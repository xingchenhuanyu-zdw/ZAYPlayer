package com.zay.player_exo;

import android.content.Context;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;

import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.zay.common.MPlayer;
import com.zay.common.listeners.OnMBufferedUpdateListener;
import com.zay.common.listeners.OnMBufferingListener;
import com.zay.common.listeners.OnMPlayerStatusChangeListener;
import com.zay.common.listeners.OnMPlayingTimeChangeListener;

import java.util.Map;

/**
 * Created by Zdw on 2021/04/25 9:54
 */
public class MPlayerExoImpl implements MPlayer, LifecycleObserver {

    private static final String TAG = MPlayerExoImpl.class.getSimpleName();
    private boolean mSupportBackgroundAudio = true;
    private Context mContext;
    private SimpleExoPlayer mExoPlayer;

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
    public void start() {

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
    public int getDefinitionCode() {
        return 0;
    }

    @Nullable
    @Override
    public String getDefinitionName() {
        return null;
    }

    @Nullable
    @Override
    public String getDefinitionName(int definition) {
        return null;
    }

    @Nullable
    @Override
    public Map<String, Integer> getSupportedDefinitions() {
        return null;
    }

    @Override
    public boolean changeDefinition(int definition) {
        return false;
    }

    @Override
    public void setPreferredDefinition(int definition) {

    }

    private MPlayerExoImpl() {
    }

    private void setSupportBackgroundAudio(boolean supportBackgroundAudio) {
        mSupportBackgroundAudio = supportBackgroundAudio;
    }

    private void setLifecycle(@NonNull Lifecycle lifecycle) {
        lifecycle.addObserver(this);
    }

    private void setContext(@NonNull Context context) {
        mContext = context;
    }

    private void setExoPlayer(ExoPlayer exoPlayer) {
        if (exoPlayer instanceof SimpleExoPlayer) {
            mExoPlayer = (SimpleExoPlayer) exoPlayer;
        }
    }

    public static class Builder {

        private boolean mSupportBackgroundAudio;
        private Lifecycle mLifecycle;
        private Context mContext;

        public Builder(@NonNull Context context) {
            this.mContext = context.getApplicationContext();
        }

        public Builder setSupportBackgroundAudio(boolean supportBackgroundAudio) {
            this.mSupportBackgroundAudio = supportBackgroundAudio;
            return this;
        }

        public Builder setLifecycle(@Nullable Lifecycle lifecycle) {
            this.mLifecycle = lifecycle;
            return this;
        }

        public MPlayerExoImpl build() {
            MPlayerExoImpl playerFactory = new MPlayerExoImpl();
            playerFactory.setSupportBackgroundAudio(mSupportBackgroundAudio);
            if (mLifecycle != null)
                playerFactory.setLifecycle(mLifecycle);
            playerFactory.setContext(mContext);
            @DefaultRenderersFactory.ExtensionRendererMode
            int extensionRendererMode = DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF;
            RenderersFactory renderersFactory = new DefaultRenderersFactory(mContext).setExtensionRendererMode(extensionRendererMode);
            SimpleExoPlayer exoPlayer = new SimpleExoPlayer.Builder(mContext, renderersFactory).build();
            playerFactory.setExoPlayer(exoPlayer);
            return playerFactory;
        }
    }
}
