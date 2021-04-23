package com.zay.player_bjy;

import android.content.Context;
import android.text.TextUtils;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;

import com.baijiayun.constant.VideoDefinition;
import com.baijiayun.videoplayer.IBJYVideoPlayer;
import com.baijiayun.videoplayer.VideoPlayerFactory;
import com.baijiayun.videoplayer.bean.BJYVideoInfo;
import com.baijiayun.videoplayer.listeners.OnBufferedUpdateListener;
import com.baijiayun.videoplayer.listeners.OnBufferingListener;
import com.baijiayun.videoplayer.listeners.OnPlayerStatusChangeListener;
import com.baijiayun.videoplayer.listeners.OnPlayingTimeChangeListener;
import com.baijiayun.videoplayer.player.PlayerStatus;
import com.baijiayun.videoplayer.widget.BJYPlayerView;
import com.zay.common.MPlayer;
import com.zay.common.listeners.OnMBufferedUpdateListener;
import com.zay.common.listeners.OnMBufferingListener;
import com.zay.common.listeners.OnMPlayerStatusChangeListener;
import com.zay.common.listeners.OnMPlayingTimeChangeListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Zdw on 2021/04/21 14:31
 */
public class MPlayerBJYImpl implements MPlayer {

    private Map<String, Integer> mDefinitions = new HashMap<>();
    private IBJYVideoPlayer mVideoPlayer;
    private OnMPlayingTimeChangeListener mOnMPlayingTimeChangeListener;
    private OnMBufferedUpdateListener mOnMBufferedUpdateListener;
    private OnMBufferingListener mOnMBufferingListener;
    private OnMPlayerStatusChangeListener mOnMPlayerStatusChangeListener;

    @Override
    public void setOnMPlayingTimeChangeListener(OnMPlayingTimeChangeListener listener) {
        mOnMPlayingTimeChangeListener = listener;
    }

    @Override
    public void setOnMBufferedUpdateListener(OnMBufferedUpdateListener listener) {
        mOnMBufferedUpdateListener = listener;
    }

    @Override
    public void setOnMBufferingListener(OnMBufferingListener listener) {
        mOnMBufferingListener = listener;
    }

    @Override
    public void setOnMPlayerStatusChangeListener(OnMPlayerStatusChangeListener listener) {
        mOnMPlayerStatusChangeListener = listener;
    }

    @Override
    public void bindPlayerView(FrameLayout playerView) {
        if (mVideoPlayer == null) return;
        if (playerView instanceof BJYPlayerView) {
            mVideoPlayer.bindPlayerView((BJYPlayerView) playerView);
        }
    }

    // 是否自动播放，默认自动播放
    @Override
    public void setAutoPlay(boolean autoPlay) {
        if (mVideoPlayer == null) return;
        mVideoPlayer.setAutoPlay(autoPlay);
    }

    @Override
    public void setupOnlineVideoWithId(@NonNull String videoId, @NonNull String token) {
        if (TextUtils.isEmpty(videoId) || TextUtils.isEmpty(token)) return;
        if (!TextUtils.isDigitsOnly(videoId)) return;
        if (mVideoPlayer == null) return;
        mVideoPlayer.setupOnlineVideoWithId(Long.valueOf(videoId), token);
    }

    // 是否正在播放
    @Override
    public boolean isPlaying() {
        if (mVideoPlayer == null) return false;
        return mVideoPlayer.isPlaying();
    }

    // 开始播放
    @Override
    public void start() {
        if (mVideoPlayer == null) return;
        mVideoPlayer.play();
    }

    // 暂停播放
    @Override
    public void pause() {
        if (mVideoPlayer == null) return;
        mVideoPlayer.pause();
    }

    // 停止播放
    @Override
    public void stop() {
        if (mVideoPlayer == null) return;
        mVideoPlayer.stop();
    }

    // 释放播放器
    @Override
    public void release() {
        if (mVideoPlayer == null) return;
        mVideoPlayer.release();
    }

    // 视频跳转到，单位秒
    @Override
    public void seekTo(int seekTime) {
        if (mVideoPlayer == null) return;
        mVideoPlayer.seek(seekTime);
    }

    // 获取播放进度，单位秒
    @Override
    public int getCurrentPosition() {
        if (mVideoPlayer == null) return -1;
        return mVideoPlayer.getCurrentPosition();
    }

    // 获取视频时长，单位秒
    @Override
    public int getDuration() {
        if (mVideoPlayer == null) return -1;
        return mVideoPlayer.getDuration();
    }

    // 获取视频清晰度
    @Override
    public int getDefinitionCode() {
        if (mVideoPlayer == null) return -1;
        BJYVideoInfo videoInfo = mVideoPlayer.getVideoInfo();
        if (videoInfo == null) return -1;
        return videoInfo.getDefinition().getType();
    }

    // 获取视频清晰度名称
    @Nullable
    @Override
    public String getDefinitionName() {
        return getDefinitionName(getDefinitionCode());
    }

    // 获取视频清晰度名称
    @Nullable
    @Override
    public String getDefinitionName(int definition) {
        if (definition >= VideoDefinition.Audio.getType() ||
                definition <= VideoDefinition.UNKNOWN.getType())
            return null;
        for (Map.Entry<String, Integer> entry : mDefinitions.entrySet()) {
            if (definition == entry.getValue()) {
                return entry.getKey();
            }
        }
        return null;
    }

    // 获取视频清晰度集合
    @Nullable
    @Override
    public Map<String, Integer> getSupportedDefinitions() {
        if (mVideoPlayer == null) return null;
        BJYVideoInfo videoInfo = mVideoPlayer.getVideoInfo();
        if (videoInfo == null || videoInfo.getSupportedDefinitionList() == null) return null;
        Map<String, Integer> supportedDefinitions = new HashMap<>();
        for (VideoDefinition videoDefinition : videoInfo.getSupportedDefinitionList()) {
            supportedDefinitions.put(videoDefinition.name(), videoDefinition.getType());
        }
        return supportedDefinitions;
    }

    // 切换视频清晰度
    @Override
    public boolean changeDefinition(int definition) {
        if (mVideoPlayer == null) return false;
        if (getDefinitionCode() == definition) return true;
        Map<String, Integer> supportedDefinitions = getSupportedDefinitions();
        if (supportedDefinitions == null) return false;
        for (Map.Entry<String, Integer> entry : supportedDefinitions.entrySet()) {
            if (definition == entry.getValue()) {
                return mVideoPlayer.changeDefinition(VideoDefinition.valueOf(entry.getKey()));
            }
        }
        return false;
    }

    @Override
    public void setPreferredDefinition(int definition) {
        if (mVideoPlayer == null) return;
        List<VideoDefinition> preferredDefinitions = new ArrayList<>();
        if (definition >= VideoDefinition.UNKNOWN.getType())
            preferredDefinitions.add(0, VideoDefinition.UNKNOWN);
        if (definition >= VideoDefinition.SD.getType())
            preferredDefinitions.add(0, VideoDefinition.SD);
        if (definition >= VideoDefinition.HD.getType())
            preferredDefinitions.add(0, VideoDefinition.HD);
        if (definition >= VideoDefinition.SHD.getType())
            preferredDefinitions.add(0, VideoDefinition.SHD);
        if (definition >= VideoDefinition._720P.getType())
            preferredDefinitions.add(0, VideoDefinition._720P);
        if (definition >= VideoDefinition._1080P.getType())
            preferredDefinitions.add(0, VideoDefinition._1080P);
        if (preferredDefinitions.isEmpty()) {
            mVideoPlayer.setPreferredDefinitions(new ArrayList<VideoDefinition>() {{
                add(VideoDefinition.UNKNOWN);
            }});
        } else {
            mVideoPlayer.setPreferredDefinitions(preferredDefinitions);
        }
    }

    private MPlayerBJYImpl() {
        mDefinitions.put(VideoDefinition._1080P.name(), VideoDefinition._1080P.getType());
        mDefinitions.put(VideoDefinition._720P.name(), VideoDefinition._720P.getType());
        mDefinitions.put(VideoDefinition.SHD.name(), VideoDefinition.SHD.getType());
        mDefinitions.put(VideoDefinition.HD.name(), VideoDefinition.HD.getType());
        mDefinitions.put(VideoDefinition.SD.name(), VideoDefinition.SD.getType());
        mDefinitions.put(VideoDefinition.UNKNOWN.name(), VideoDefinition.UNKNOWN.getType());
    }

    private void setVideoPlayer(IBJYVideoPlayer videoPlayer) {
        mVideoPlayer = videoPlayer;
        mVideoPlayer.addOnPlayingTimeChangeListener(new OnPlayingTimeChangeListener() {
            @Override
            public void onPlayingTimeChange(int currentTime, int duration) {
                if (mOnMPlayingTimeChangeListener != null)
                    mOnMPlayingTimeChangeListener.onPlayingTimeChange(currentTime, duration);
            }
        });
        mVideoPlayer.addOnBufferUpdateListener(new OnBufferedUpdateListener() {
            @Override
            public void onBufferedPercentageChange(int bufferedPercentage) {
                if (mOnMBufferedUpdateListener != null)
                    mOnMBufferedUpdateListener.onBufferedPercentageChange(bufferedPercentage);
            }
        });
        mVideoPlayer.addOnBufferingListener(new OnBufferingListener() {
            @Override
            public void onBufferingStart() {
                if (mOnMBufferingListener != null)
                    mOnMBufferingListener.onBufferingStart();
            }

            @Override
            public void onBufferingEnd() {
                if (mOnMBufferingListener != null)
                    mOnMBufferingListener.onBufferingEnd();
            }
        });
        mVideoPlayer.addOnPlayerStatusChangeListener(new OnPlayerStatusChangeListener() {
            @Override
            public void onStatusChange(PlayerStatus playerStatus) {
                if (mOnMPlayerStatusChangeListener != null) {
                    if (playerStatus == PlayerStatus.STATE_PREPARED) {
                        mOnMPlayerStatusChangeListener.onPrepared();
                    } else if (playerStatus == PlayerStatus.STATE_PAUSED) {
                        mOnMPlayerStatusChangeListener.onPaused();
                    } else if (playerStatus == PlayerStatus.STATE_PLAYBACK_COMPLETED) {
                        mOnMPlayerStatusChangeListener.onCompleted();
                    }
                }
            }
        });
    }

    public static class Builder {

        private boolean mSupportBackgroundAudio;
        private boolean mSupportBreakPointPlay;
        private boolean mSupportLooping;
        private Lifecycle mLifecycle;
        private Context mContext;
        private String mUserName;
        private String mUserIdentity;

        public Builder() {
        }

        public Builder setSupportBackgroundAudio(boolean supportBackgroundAudio) {
            this.mSupportBackgroundAudio = supportBackgroundAudio;
            return this;
        }

        public Builder setSupportBreakPointPlay(boolean supportBreakPointPlay) {
            this.mSupportBreakPointPlay = supportBreakPointPlay;
            return this;
        }

        public Builder setSupportLooping(boolean supportLooping) {
            this.mSupportLooping = supportLooping;
            return this;
        }

        public Builder setLifecycle(Lifecycle lifecycle) {
            this.mLifecycle = lifecycle;
            return this;
        }

        public Builder setContext(Context context) {
            this.mContext = context.getApplicationContext();
            return this;
        }

        public Builder setUserInfo(String userName, String userIdentity) {
            this.mUserName = userName;
            this.mUserIdentity = userIdentity;
            return this;
        }

        public MPlayerBJYImpl build() {
            IBJYVideoPlayer videoPlayer = new VideoPlayerFactory.Builder()
                    // 是否开启后台音频播放
                    .setSupportBackgroundAudio(mSupportBackgroundAudio)
                    // 是否开启记忆播放
                    .setSupportBreakPointPlay(mSupportBreakPointPlay)
                    // 是否开启循环播放
                    .setSupportLooping(mSupportLooping)
                    // 绑定生命周期
                    .setLifecycle(mLifecycle)
                    // 设置在线播放清晰度匹配规则
                    .setPreferredDefinitions(new ArrayList<VideoDefinition>() {{
                        add(VideoDefinition.UNKNOWN);
                    }})
                    .setContext(mContext)
                    .setUserInfo(mUserName, mUserIdentity)
                    .build();
            MPlayerBJYImpl playerFactory = new MPlayerBJYImpl();
            playerFactory.setVideoPlayer(videoPlayer);
            return playerFactory;
        }
    }
}
