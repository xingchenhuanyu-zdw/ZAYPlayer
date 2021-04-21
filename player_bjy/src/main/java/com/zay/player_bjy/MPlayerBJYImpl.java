package com.zay.player_bjy;

import android.content.Context;
import android.widget.FrameLayout;

import androidx.lifecycle.Lifecycle;

import com.baijiayun.constant.VideoDefinition;
import com.baijiayun.videoplayer.IBJYVideoPlayer;
import com.baijiayun.videoplayer.VideoPlayerFactory;
import com.zay.common.MPlayer;
import com.zay.common.listeners.OnMBufferedUpdateListener;
import com.zay.common.listeners.OnMBufferingListener;
import com.zay.common.listeners.OnMPlayerStatusChangeListener;
import com.zay.common.listeners.OnMPlayingTimeChangeListener;

import java.util.List;

/**
 * Created by Zdw on 2021/04/21 14:31
 */
public class MPlayerBJYImpl implements MPlayer {

    private void setVideoPlayer(IBJYVideoPlayer videoPlayer) {

    }

    @Override
    public void setOnMPlayingTimeChangeListener(OnMPlayingTimeChangeListener listener) {

    }

    @Override
    public void setOnMBufferedUpdateListener(OnMBufferedUpdateListener listener) {

    }

    @Override
    public void setOnMBufferingListener(OnMBufferingListener listener) {

    }

    @Override
    public void setOnMPlayerStatusChangeListener(OnMPlayerStatusChangeListener listener) {

    }

    @Override
    public void bindPlayerView(FrameLayout playerView) {

    }

    @Override
    public void setAutoPlay(boolean autoPlay) {

    }

    @Override
    public void setupOnlineVideoWithId(String videoId, String token) {

    }

    @Override
    public boolean isPlaying() {
        return false;
    }

    @Override
    public void play() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void release() {

    }

    @Override
    public void seekTo(int seekTime) {

    }

    @Override
    public int getCurrentPosition() {
        return 0;
    }

    @Override
    public int getDuration() {
        return 0;
    }

    @Override
    public int getDefinition() {
        return 0;
    }

    @Override
    public List<Integer> getSupportedDefinitionList() {
        return null;
    }

    @Override
    public boolean changeDefinition(int definition) {
        return false;
    }

    @Override
    public void setPreferredDefinition(int definition) {

    }

    private MPlayerBJYImpl() {
    }

    public static class Builder {

        private boolean mSupportBackgroundAudio;
        private boolean mSupportBreakPointPlay;
        private boolean mSupportLooping;
        private Lifecycle mLifecycle;
        private List<VideoDefinition> mPreferredDefinitions;
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

        public Builder setPreferredDefinitions(List<VideoDefinition> preferredDefinitions) {
            this.mPreferredDefinitions = preferredDefinitions;
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
                    .setPreferredDefinitions(mPreferredDefinitions)
                    .setContext(mContext)
                    .setUserInfo(mUserName, mUserIdentity)
                    .build();
            MPlayerBJYImpl playerFactory = new MPlayerBJYImpl();
            playerFactory.setVideoPlayer(videoPlayer);
            return playerFactory;
        }
    }
}
