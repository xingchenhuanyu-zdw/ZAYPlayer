package com.zay.player_cc;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.bokecc.sdk.mobile.drm.DRMServer;
import com.bokecc.sdk.mobile.play.DWMediaPlayer;
import com.zay.common.ZAYPlayer;
import com.zay.common.listeners.ZAYOnBufferedUpdateListener;
import com.zay.common.listeners.ZAYOnBufferingListener;
import com.zay.common.listeners.ZAYOnPlayerStatusChangeListener;
import com.zay.common.listeners.ZAYOnPlayingTimeChangeListener;
import com.zay.common.widget.ZAYPlayerView;
import com.zay.player_cc.widget.ZAYCCPlayerView;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Zdw on 2021/04/21 14:20
 */
public class ZAYPlayerCCImpl implements ZAYPlayer, TextureView.SurfaceTextureListener, LifecycleObserver {

    private static final String TAG = ZAYPlayerCCImpl.class.getSimpleName();
    private boolean mSupportBackgroundAudio = true;
    private Context mContext;
    private Handler mHandler;
    private String mUserId, mApiKey, mVerificationCode;
    private boolean mAutoPlay = false;
    private boolean mIsPrepared = false;
    private boolean mIsAutoPaused = false;
    private Surface mSurface;
    private ZAYCCPlayerView mZAYCCPlayerView;
    private DWMediaPlayer mMediaPlayer;
    private DRMServer mDRMServer;
    private int mDrmServerPort;
    private int mCurrentPosition = -1;
    private int mPreferredDefinition = DWMediaPlayer.NORMAL_DEFINITION;
    private Set<ZAYOnPlayingTimeChangeListener> mZAYOnPlayingTimeChangeListenerSet = new HashSet<>();
    private Set<ZAYOnBufferedUpdateListener> mZAYOnBufferedUpdateListenerSet = new HashSet<>();
    private Set<ZAYOnBufferingListener> mZAYOnBufferingListenerSet = new HashSet<>();
    private Set<ZAYOnPlayerStatusChangeListener> mZAYOnPlayerStatusChangeListenerSet = new HashSet<>();

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
        if (mMediaPlayer == null) return;
        if (playerView instanceof ZAYCCPlayerView) {
            mZAYCCPlayerView = (ZAYCCPlayerView) playerView;
            mZAYCCPlayerView.setSurfaceTextureListener(this);
            mZAYCCPlayerView.setZAYPlayer(this);
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
            for (ZAYOnPlayerStatusChangeListener listener : mZAYOnPlayerStatusChangeListenerSet) {
                if (listener != null) {
                    listener.onPaused();
                }
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
            mCurrentPosition = getCurrentPosition();
            mMediaPlayer.reset();
            mMediaPlayer.setSurface(mSurface);
            mDRMServer.reset();
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

    private ZAYPlayerCCImpl() {
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
        mMediaPlayer.setScreenOnWhilePlaying(true);
        // 视频缓冲状态
        mMediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                Log.i(TAG, "onInfo what: " + what);
                if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                    for (ZAYOnBufferingListener listener : mZAYOnBufferingListenerSet) {
                        if (listener != null) {
                            listener.onBufferingStart();
                        }
                    }
                } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                    for (ZAYOnBufferingListener listener : mZAYOnBufferingListenerSet) {
                        if (listener != null) {
                            listener.onBufferingEnd();
                        }
                    }
                }
                return false;
            }
        });
        // 视频缓冲进度
        mMediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                for (ZAYOnBufferedUpdateListener listener : mZAYOnBufferedUpdateListenerSet) {
                    if (listener != null) {
                        listener.onBufferedPercentageChange(percent);
                    }
                }
            }
        });
        // 可以开始播放
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.i(TAG, "setOnPreparedListener");
                mIsPrepared = true;
                if (mZAYCCPlayerView != null)
                    mZAYCCPlayerView.onVideoSizeChanged(mp.getVideoWidth(), mp.getVideoHeight());
                if (mAutoPlay) {
                    start();
                }
                if (mCurrentPosition > 0) {// 切换清晰度时保持播放进度
                    seekTo(mCurrentPosition);
                    mCurrentPosition = -1;
                }
                for (ZAYOnPlayerStatusChangeListener listener : mZAYOnPlayerStatusChangeListenerSet) {
                    if (listener != null) {
                        listener.onPrepared();
                    }
                }
            }
        });
        // 视频播放完成
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // 未设置视频资源时，点击 CCPlayerView 会触发，排除掉
                if (mMediaPlayer.getPlayInfo() == null) return;
                for (ZAYOnPlayerStatusChangeListener listener : mZAYOnPlayerStatusChangeListenerSet) {
                    if (listener != null) {
                        listener.onCompleted();
                    }
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
                for (ZAYOnPlayingTimeChangeListener listener : mZAYOnPlayingTimeChangeListenerSet) {
                    if (listener != null) {
                        listener.onPlayingTimeChange(getCurrentPosition(), getDuration());
                    }
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

        public Builder setUserId(@NonNull String userId) {
            mUserId = userId;
            return this;
        }

        public Builder setApiKey(@NonNull String apiKey) {
            mApiKey = apiKey;
            return this;
        }

        public Builder setVerificationCode(@Nullable String verificationCode) {
            mVerificationCode = verificationCode;
            return this;
        }

        public ZAYPlayerCCImpl build() {
            ZAYPlayerCCImpl playerFactory = new ZAYPlayerCCImpl();
            playerFactory.setSupportBackgroundAudio(mSupportBackgroundAudio);
            if (mLifecycle != null)
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
