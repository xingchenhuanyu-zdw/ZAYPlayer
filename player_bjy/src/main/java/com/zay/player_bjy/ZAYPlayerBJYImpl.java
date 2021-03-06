package com.zay.player_bjy;

import android.app.Activity;
import android.content.Context;
import android.hardware.SensorManager;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;

import com.baijiayun.constant.VideoDefinition;
import com.baijiayun.videoplayer.IBJYVideoPlayer;
import com.baijiayun.videoplayer.VideoPlayerFactory;
import com.baijiayun.videoplayer.bean.BJYVideoInfo;
import com.baijiayun.videoplayer.listeners.OnBufferingListener;
import com.baijiayun.videoplayer.player.PlayerStatus;
import com.zay.common.ZAYPlayer;
import com.zay.common.listeners.ZAYOnBufferedUpdateListener;
import com.zay.common.listeners.ZAYOnBufferingListener;
import com.zay.common.listeners.ZAYOnPlayerStatusChangeListener;
import com.zay.common.listeners.ZAYOnPlayingTimeChangeListener;
import com.zay.common.orientation.PlayerOrientationListener;
import com.zay.common.widget.ZAYPlayerView;
import com.zay.player_bjy.widget.ZAYBJYPlayerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Zdw on 2021/04/21 14:31
 */
public class ZAYPlayerBJYImpl implements ZAYPlayer {

    private static final String TAG = ZAYPlayerBJYImpl.class.getSimpleName();
    private final Map<String, Integer> mDefinitions = new HashMap<>();
    private Activity mActivity;
    private IBJYVideoPlayer mVideoPlayer;
    private final Set<ZAYOnPlayingTimeChangeListener> mZAYOnPlayingTimeChangeListenerSet = new HashSet<>();
    private final Set<ZAYOnBufferedUpdateListener> mZAYOnBufferedUpdateListenerSet = new HashSet<>();
    private final Set<ZAYOnBufferingListener> mZAYOnBufferingListenerSet = new HashSet<>();
    private final Set<ZAYOnPlayerStatusChangeListener> mZAYOnPlayerStatusChangeListenerSet = new HashSet<>();
    private PlayerOrientationListener mOrientationListener;

    @Override
    public void addOnPlayingTimeChangeListener(@NonNull ZAYOnPlayingTimeChangeListener listener) {
        mZAYOnPlayingTimeChangeListenerSet.add(listener);
    }

    @Override
    public void removeOnPlayingTimeChangeListener(@NonNull ZAYOnPlayingTimeChangeListener listener) {
        mZAYOnPlayingTimeChangeListenerSet.remove(listener);
    }

    @Override
    public void removeAllOnPlayingTimeChangeListener() {
        mZAYOnPlayingTimeChangeListenerSet.clear();
    }

    @Override
    public void addOnBufferedUpdateListener(@NonNull ZAYOnBufferedUpdateListener listener) {
        mZAYOnBufferedUpdateListenerSet.add(listener);
    }

    @Override
    public void removeOnBufferedUpdateListener(@NonNull ZAYOnBufferedUpdateListener listener) {
        mZAYOnBufferedUpdateListenerSet.remove(listener);
    }

    @Override
    public void removeAllOnBufferedUpdateListener() {
        mZAYOnBufferedUpdateListenerSet.clear();
    }

    @Override
    public void addOnBufferingListener(@NonNull ZAYOnBufferingListener listener) {
        mZAYOnBufferingListenerSet.add(listener);
    }

    @Override
    public void removeOnBufferingListener(@NonNull ZAYOnBufferingListener listener) {
        mZAYOnBufferingListenerSet.remove(listener);
    }

    @Override
    public void removeAllOnBufferingListener() {
        mZAYOnBufferingListenerSet.clear();
    }

    @Override
    public void addOnPlayerStatusChangeListener(@NonNull ZAYOnPlayerStatusChangeListener listener) {
        mZAYOnPlayerStatusChangeListenerSet.add(listener);
    }

    @Override
    public void removeOnPlayerStatusChangeListener(@NonNull ZAYOnPlayerStatusChangeListener listener) {
        mZAYOnPlayerStatusChangeListenerSet.remove(listener);
    }

    @Override
    public void removeAllOnPlayerStatusChangeListener() {
        mZAYOnPlayerStatusChangeListenerSet.clear();
    }

    @Override
    public void bindPlayerView(ZAYPlayerView playerView) {
        if (mVideoPlayer == null) return;
        if (playerView instanceof ZAYBJYPlayerView) {
            ZAYBJYPlayerView zaybjyPlayerView = (ZAYBJYPlayerView) playerView;
            mVideoPlayer.bindPlayerView(zaybjyPlayerView.getPlayerView());
            zaybjyPlayerView.setZAYPlayer(this);
        }
    }

    // ???????????????????????????????????????
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
        mVideoPlayer.setupOnlineVideoWithId(Long.parseLong(videoId), token);
    }

    // ??????????????????
    @Override
    public boolean isPlaying() {
        if (mVideoPlayer == null) return false;
        return mVideoPlayer.isPlaying();
    }

    // ????????????
    @Override
    public void start() {
        if (mVideoPlayer == null) return;
        mVideoPlayer.play();
    }

    // ????????????
    @Override
    public void pause() {
        if (mVideoPlayer == null) return;
        mVideoPlayer.pause();
    }

    // ????????????
    @Override
    public void stop() {
        if (mVideoPlayer == null) return;
        mVideoPlayer.stop();
    }

    // ???????????????
    @Override
    public void release() {
        if (mVideoPlayer == null) return;
        mVideoPlayer.release();
    }

    // ???????????????????????????
    @Override
    public void seekTo(int seekTime) {
        if (mVideoPlayer == null) return;
        mVideoPlayer.seek(seekTime);
    }

    // ??????????????????????????????
    @Override
    public int getCurrentPosition() {
        if (mVideoPlayer == null) return -1;
        return mVideoPlayer.getCurrentPosition();
    }

    // ??????????????????????????????
    @Override
    public int getDuration() {
        if (mVideoPlayer == null) return -1;
        return mVideoPlayer.getDuration();
    }

    // ?????????????????????
    @Override
    public int getDefinitionCode() {
        if (mVideoPlayer == null) return -1;
        BJYVideoInfo videoInfo = mVideoPlayer.getVideoInfo();
        if (videoInfo == null) return -1;
        return videoInfo.getDefinition().getType();
    }

    // ???????????????????????????
    @Nullable
    @Override
    public String getDefinitionName() {
        return getDefinitionName(getDefinitionCode());
    }

    // ???????????????????????????
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

    // ???????????????????????????
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

    // ?????????????????????
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

    private ZAYPlayerBJYImpl() {
        mDefinitions.put(VideoDefinition._1080P.name(), VideoDefinition._1080P.getType());
        mDefinitions.put(VideoDefinition._720P.name(), VideoDefinition._720P.getType());
        mDefinitions.put(VideoDefinition.SHD.name(), VideoDefinition.SHD.getType());
        mDefinitions.put(VideoDefinition.HD.name(), VideoDefinition.HD.getType());
        mDefinitions.put(VideoDefinition.SD.name(), VideoDefinition.SD.getType());
        mDefinitions.put(VideoDefinition.UNKNOWN.name(), VideoDefinition.UNKNOWN.getType());
    }

    private void setVideoPlayer(IBJYVideoPlayer videoPlayer) {
        mVideoPlayer = videoPlayer;
        mVideoPlayer.addOnPlayingTimeChangeListener((currentTime, duration) -> {
            for (ZAYOnPlayingTimeChangeListener listener : mZAYOnPlayingTimeChangeListenerSet) {
                if (listener != null) {
                    listener.onPlayingTimeChange(currentTime, duration);
                }
            }
        });
        mVideoPlayer.addOnBufferUpdateListener(bufferedPercentage -> {
            for (ZAYOnBufferedUpdateListener listener : mZAYOnBufferedUpdateListenerSet) {
                if (listener != null) {
                    listener.onBufferedPercentageChange(bufferedPercentage);
                }
            }
        });
        mVideoPlayer.addOnBufferingListener(new OnBufferingListener() {
            @Override
            public void onBufferingStart() {
                for (ZAYOnBufferingListener listener : mZAYOnBufferingListenerSet) {
                    if (listener != null) {
                        listener.onBufferingStart();
                    }
                }
            }

            @Override
            public void onBufferingEnd() {
                for (ZAYOnBufferingListener listener : mZAYOnBufferingListenerSet) {
                    if (listener != null) {
                        listener.onBufferingEnd();
                    }
                }
            }
        });
        mVideoPlayer.addOnPlayerStatusChangeListener(playerStatus -> {
            Log.i(TAG, "onStatusChange playerStatus: " + playerStatus);
            if (mZAYOnPlayerStatusChangeListenerSet.size() > 0) {
                if (playerStatus == PlayerStatus.STATE_PREPARED) {
                    for (ZAYOnPlayerStatusChangeListener listener : mZAYOnPlayerStatusChangeListenerSet) {
                        if (listener != null) {
                            listener.onPrepared();
                        }
                    }
                } else if (playerStatus == PlayerStatus.STATE_PAUSED) {
                    for (ZAYOnPlayerStatusChangeListener listener : mZAYOnPlayerStatusChangeListenerSet) {
                        if (listener != null) {
                            listener.onPaused();
                        }
                    }
                } else if (playerStatus == PlayerStatus.STATE_PLAYBACK_COMPLETED) {
                    for (ZAYOnPlayerStatusChangeListener listener : mZAYOnPlayerStatusChangeListenerSet) {
                        if (listener != null) {
                            listener.onCompleted();
                        }
                    }
                }
            }
        });
    }

    private void setActivity(@NonNull Activity activity) {
        mActivity = activity;
    }

    private void initOrientationListener() {
        if (mActivity != null) {
            mOrientationListener = new PlayerOrientationListener(mActivity, SensorManager.SENSOR_DELAY_UI);
            mOrientationListener.disable();
        }
    }

    @Override
    public void enableAutoOrientation() {
        if (mOrientationListener != null && mOrientationListener.canDetectOrientation()) {
            mOrientationListener.enable();
        }
    }

    @Override
    public void disableAutoOrientation() {
        if (mOrientationListener != null) {
            mOrientationListener.disable();
        }
    }

    public static class Builder {

        private boolean mSupportBackgroundAudio;
        private boolean mSupportBreakPointPlay;
        private boolean mSupportLooping;
        private Lifecycle mLifecycle;
        private final Context mContext;
        private Activity mActivity;
        private String mUserName;
        private String mUserIdentity;

        public Builder(@NonNull Context context) {
            this.mContext = context.getApplicationContext();
            if (context instanceof Activity) {
                mActivity = (Activity) context;
            }
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

        public Builder setLifecycle(@Nullable Lifecycle lifecycle) {
            this.mLifecycle = lifecycle;
            return this;
        }

        public Builder setUserInfo(@Nullable String userName, @Nullable String userIdentity) {
            this.mUserName = userName;
            this.mUserIdentity = userIdentity;
            return this;
        }

        public ZAYPlayerBJYImpl build() {
            IBJYVideoPlayer videoPlayer = new VideoPlayerFactory.Builder()
                    // ??????????????????????????????
                    .setSupportBackgroundAudio(mSupportBackgroundAudio)
                    // ????????????????????????
                    .setSupportBreakPointPlay(mSupportBreakPointPlay)
                    // ????????????????????????
                    .setSupportLooping(mSupportLooping)
                    // ??????????????????
                    .setLifecycle(mLifecycle)
                    // ???????????????????????????????????????
                    .setPreferredDefinitions(new ArrayList<VideoDefinition>() {{
                        add(VideoDefinition.UNKNOWN);
                    }})
                    .setContext(mContext)
                    .setUserInfo(mUserName, mUserIdentity)
                    .build();
            ZAYPlayerBJYImpl playerFactory = new ZAYPlayerBJYImpl();
            playerFactory.setVideoPlayer(videoPlayer);
            if (mActivity != null)
                playerFactory.setActivity(mActivity);
            playerFactory.initOrientationListener();
            return playerFactory;
        }
    }
}
