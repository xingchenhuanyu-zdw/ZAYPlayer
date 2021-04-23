package com.zay.player_cc;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.bokecc.sdk.mobile.drm.DRMServer;
import com.bokecc.sdk.mobile.play.DWMediaPlayer;
import com.zay.common.MPlayer;
import com.zay.common.listeners.OnMBufferedUpdateListener;
import com.zay.common.listeners.OnMBufferingListener;
import com.zay.common.listeners.OnMPlayerStatusChangeListener;
import com.zay.common.listeners.OnMPlayingTimeChangeListener;
import com.zay.player_cc.widget.CCPlayerView;

import java.io.IOException;
import java.util.Map;

/**
 * Created by Zdw on 2021/04/21 14:20
 */
public class MPlayerCCImpl implements MPlayer, TextureView.SurfaceTextureListener, LifecycleObserver {

    private static final String TAG = MPlayerCCImpl.class.getSimpleName();
    private boolean mSupportBackgroundAudio = true;
    private Context mContext;
    private Handler mHandler;
    private String mUserId, mApiKey, mVerificationCode;
    private boolean mAutoPlay = false;
    private boolean mIsPrepared = false;
    private boolean mIsAutoPaused = false;
    private Surface mSurface;
    private CCPlayerView mCCPlayerView;
    private DWMediaPlayer mMediaPlayer;
    private DRMServer mDRMServer;
    private int mDrmServerPort;
    private int mPreferredDefinition = DWMediaPlayer.NORMAL_DEFINITION;
    private OnMPlayingTimeChangeListener mOnMPlayingTimeChangeListener;
    private OnMBufferedUpdateListener mOnMBufferedUpdateListener;
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

    }

    @Override
    public void setOnMPlayerStatusChangeListener(OnMPlayerStatusChangeListener listener) {
        mOnMPlayerStatusChangeListener = listener;
    }

    @Override
    public void bindPlayerView(FrameLayout playerView) {
        if (mMediaPlayer == null) return;
        if (playerView instanceof CCPlayerView) {
            mCCPlayerView = (CCPlayerView) playerView;
            mCCPlayerView.setSurfaceTextureListener(this);
        }
    }

    @Override
    public void setAutoPlay(boolean autoPlay) {
        mAutoPlay = autoPlay;
    }

    @Override
    public void setupOnlineVideoWithId(@NonNull String videoId, @Nullable String token) {
        if (mMediaPlayer == null) return;
        mIsPrepared = false;
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        mMediaPlayer.setVideoPlayInfo(videoId, mUserId, mApiKey, mVerificationCode, mContext);
        mMediaPlayer.setSurface(mSurface);
        mMediaPlayer.setDefaultDefinition(mPreferredDefinition);
        mDRMServer.reset();
        mMediaPlayer.prepareAsync();
    }

    // 是否正在播放
    @Override
    public boolean isPlaying() {
        if (mMediaPlayer == null) return false;
        if (!mIsPrepared) return false;
        return mMediaPlayer.isPlaying();
    }

    // 开始播放
    @Override
    public void start() {
        if (mMediaPlayer == null) return;
        mMediaPlayer.start();
        mHandler.post(mTimeInfoRunnable);
    }

    // 暂停播放
    @Override
    public void pause() {
        if (mMediaPlayer == null) return;
        if (isPlaying()) {// 之前正在播放时
            if (mOnMPlayerStatusChangeListener != null) {
                mOnMPlayerStatusChangeListener.onPaused();
            }
        }
        mMediaPlayer.pause();
        mHandler.removeCallbacks(mTimeInfoRunnable);
    }

    // 停止播放
    @Override
    public void stop() {
        if (mMediaPlayer == null) return;
        mMediaPlayer.stop();
        mHandler.removeCallbacks(mTimeInfoRunnable);
    }

    // 释放播放器
    @Override
    public void release() {
        if (mMediaPlayer == null) return;
        mMediaPlayer.release();
    }

    // 视频跳转到，单位秒
    @Override
    public void seekTo(int seekTime) {
        if (mMediaPlayer == null) return;
        mMediaPlayer.seekTo(seekTime * 1000);
    }

    // 获取播放进度，单位秒
    @Override
    public int getCurrentPosition() {
        if (mMediaPlayer == null) return -1;
        return mMediaPlayer.getCurrentPosition() / 1000;
    }

    // 获取视频时长，单位秒
    @Override
    public int getDuration() {
        if (mMediaPlayer == null) return -1;
        return mMediaPlayer.getDuration() / 1000;
    }

    // 获取视频清晰度
    @Override
    public int getDefinitionCode() {
        if (mMediaPlayer == null) return -1;
        return mMediaPlayer.getDefinitionCode();
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
        if (mMediaPlayer == null) return null;
        Map<String, Integer> definitions = mMediaPlayer.getDefinitions();
        for (Map.Entry<String, Integer> entry : definitions.entrySet()) {
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
        if (mMediaPlayer == null) return null;
        try {
            return mMediaPlayer.getDefinitions();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 切换视频清晰度
    @Override
    public boolean changeDefinition(int definition) {
        if (mMediaPlayer == null) return false;
        if (getDefinitionCode() == definition) return true;
        if (definition != DWMediaPlayer.HIGH_DEFINITION &&
                definition != DWMediaPlayer.NORMAL_DEFINITION)
            return false;
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setSurface(mSurface);
            mDRMServer.reset();
            mPreferredDefinition = definition;
            mMediaPlayer.setDefaultDefinition(definition);
            mMediaPlayer.setDefinition(mContext, definition);
            mIsPrepared = false;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void setPreferredDefinition(int definition) {
        if (definition >= DWMediaPlayer.HIGH_DEFINITION) {
            mPreferredDefinition = DWMediaPlayer.HIGH_DEFINITION;
        } else {
            mPreferredDefinition = DWMediaPlayer.NORMAL_DEFINITION;
        }
    }

    @Override
    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
        mSurface = new Surface(surface);
        mMediaPlayer.setSurface(mSurface);
    }

    @Override
    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private void onResume() {
        if (!mSupportBackgroundAudio) {
            if (!isPlaying() && mIsPrepared && mIsAutoPaused) {
                start();
                mIsAutoPaused = false;
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    private void onPause() {
        if (!mSupportBackgroundAudio) {
            if (isPlaying()) {
                pause();
                mIsAutoPaused = true;
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private void onStop() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private void onDestroy() {
        mIsPrepared = false;
        stop();
        release();
    }

    private MPlayerCCImpl() {
    }

    private void setSupportBackgroundAudio(boolean supportBackgroundAudio) {
        mSupportBackgroundAudio = supportBackgroundAudio;
    }

    private void setLifecycle(@NonNull Lifecycle lifecycle) {
        lifecycle.addObserver(this);
    }

    private void setContext(@NonNull Context context) {
        mContext = context;
        mHandler = new Handler(Looper.getMainLooper());
    }

    private void setUserId(@NonNull String userId) {
        mUserId = userId;
    }

    private void setApiKey(@NonNull String apiKey) {
        mApiKey = apiKey;
    }

    private void setVerificationCode(String verificationCode) {
        mVerificationCode = verificationCode;
    }

    private void setMediaPlayer(DWMediaPlayer mediaPlayer) {
        mMediaPlayer = mediaPlayer;
        mMediaPlayer.setDRMServerPort(mDrmServerPort);
        // 视频缓冲进度
        mMediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                if (mOnMBufferedUpdateListener != null) {
                    mOnMBufferedUpdateListener.onBufferedPercentageChange(percent);
                }
            }
        });
        // 可以开始播放
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.e(TAG, "setOnPreparedListener");
                mIsPrepared = true;
                if (mCCPlayerView != null)
                    mCCPlayerView.onVideoSizeChanged(mp.getVideoWidth(), mp.getVideoHeight());
                if (mAutoPlay) {
                    start();
                }
                if (mOnMPlayerStatusChangeListener != null) {
                    mOnMPlayerStatusChangeListener.onPrepared();
                }
            }
        });
        // 视频播放完成
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // 未设置视频资源时，点击 CCPlayerView 会触发，排除掉
                if (mMediaPlayer.getPlayInfo() == null) return;
                if (mOnMPlayerStatusChangeListener != null) {
                    mOnMPlayerStatusChangeListener.onCompleted();
                }
            }
        });
    }

    // 启动DRMServer
    private void startDRMServer() {
        if (mDRMServer == null) {
            mDRMServer = new DRMServer();
            mDRMServer.setRequestRetryCount(20);
        }

        try {
            mDRMServer.start();
            mDrmServerPort = mDRMServer.getPort();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Runnable mTimeInfoRunnable = new Runnable() {
        @Override
        public void run() {
            if (isPlaying()) {
                if (mOnMPlayingTimeChangeListener != null) {
                    mOnMPlayingTimeChangeListener.onPlayingTimeChange(getCurrentPosition(), getDuration());
                }
                mHandler.postDelayed(mTimeInfoRunnable, 1000L);
            }
        }
    };

    public static class Builder {

        private boolean mSupportBackgroundAudio;
        private Lifecycle mLifecycle;
        private Context mContext;
        private String mUserId, mApiKey, mVerificationCode;

        public Builder() {
        }

        public Builder setSupportBackgroundAudio(boolean supportBackgroundAudio) {
            this.mSupportBackgroundAudio = supportBackgroundAudio;
            return this;
        }

        public Builder setLifecycle(@NonNull Lifecycle lifecycle) {
            this.mLifecycle = lifecycle;
            return this;
        }

        public Builder setContext(@NonNull Context context) {
            this.mContext = context.getApplicationContext();
            return this;
        }

        public Builder setUserId(@NonNull String userId) {
            mUserId = userId;
            return this;
        }

        public Builder setApiKey(@NonNull String apiKey) {
            mApiKey = apiKey;
            return this;
        }

        public Builder setVerificationCode(String verificationCode) {
            mVerificationCode = verificationCode;
            return this;
        }

        public MPlayerCCImpl build() {
            MPlayerCCImpl playerFactory = new MPlayerCCImpl();
            playerFactory.setSupportBackgroundAudio(mSupportBackgroundAudio);
            playerFactory.setLifecycle(mLifecycle);
            playerFactory.setContext(mContext);
            playerFactory.setUserId(mUserId);
            playerFactory.setApiKey(mApiKey);
            playerFactory.setVerificationCode(mVerificationCode);
            playerFactory.startDRMServer();// 在setMediaPlayer之前调用
            playerFactory.setMediaPlayer(new DWMediaPlayer());
            return playerFactory;
        }
    }
}
