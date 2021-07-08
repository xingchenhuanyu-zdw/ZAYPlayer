package com.zay.player_exo;

import android.app.Activity;
import android.content.Context;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.zay.common.ZAYPlayer;
import com.zay.common.listeners.ZAYOnBufferedUpdateListener;
import com.zay.common.listeners.ZAYOnBufferingListener;
import com.zay.common.listeners.ZAYOnPlayerStatusChangeListener;
import com.zay.common.listeners.ZAYOnPlayingTimeChangeListener;
import com.zay.common.orientation.PlayerOrientationListener;
import com.zay.common.widget.ZAYPlayerView;
import com.zay.player_exo.widget.ZAYEXOPlayerView;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Zdw on 2021/04/25 9:54
 */
public class ZAYPlayerExoImpl implements ZAYPlayer, LifecycleObserver {

    private static final String TAG = ZAYPlayerExoImpl.class.getSimpleName();
    private boolean mSupportBackgroundAudio = true;
    private final Handler mHandler;
    private Activity mActivity;
    private boolean mIsPrepared = false;
    private boolean mIsAutoPaused = false;
    private SimpleExoPlayer mExoPlayer;
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
        if (playerView instanceof ZAYEXOPlayerView) {
            ZAYEXOPlayerView zayexoPlayerView = (ZAYEXOPlayerView) playerView;
            zayexoPlayerView.getPlayerView().setPlayer(mExoPlayer);
            zayexoPlayerView.setZAYPlayer(this);
        }
    }

    @Override
    public void setAutoPlay(boolean autoPlay) {
        if (mExoPlayer == null) return;
        mExoPlayer.setPlayWhenReady(autoPlay);
    }

    /**
     * 开始播放视频
     *
     * @param videoId 非加密视频网络链接
     * @param token   null
     */
    @Override
    public void setupOnlineVideoWithId(@NonNull String videoId, @Nullable String token) {
        if (mExoPlayer == null) return;
        mIsPrepared = false;
        Uri uri = Uri.parse(videoId);
        MediaItem mediaItem = MediaItem.fromUri(uri);
        mExoPlayer.setMediaItem(mediaItem);
        //mExoPlayer.addMediaItem(mediaItem);// 不起用列表
        mExoPlayer.prepare();
    }

    // 是否正在播放
    @Override
    public boolean isPlaying() {
        if (mExoPlayer == null) return false;
        if (!mIsPrepared) return false;
        return mExoPlayer.isPlaying();
    }

    // 开始播放
    @Override
    public void start() {
        if (mExoPlayer == null) return;
        if (mPlaybackState == Player.STATE_ENDED) {
            if (mExoPlayer.getCurrentMediaItem() != null) {
                mExoPlayer.setMediaItem(mExoPlayer.getCurrentMediaItem());
                mExoPlayer.prepare();
                mExoPlayer.play();
            }
        } else if (mPlaybackState == Player.STATE_READY) {
            mExoPlayer.play();
            mHandler.post(mTimeInfoRunnable);
        }
    }

    // 暂停播放
    @Override
    public void pause() {
        if (mExoPlayer == null) return;
        if (isPlaying()) {// 之前正在播放时
            for (ZAYOnPlayerStatusChangeListener listener : mZAYOnPlayerStatusChangeListenerSet) {
                if (listener != null) {
                    listener.onPaused();
                }
            }
        }
        mExoPlayer.pause();
        mHandler.removeCallbacks(mTimeInfoRunnable);
    }

    // 停止播放
    @Override
    public void stop() {
        if (mExoPlayer == null) return;
        mExoPlayer.stop();
        mHandler.removeCallbacks(mTimeInfoRunnable);
        mHandler.removeCallbacks(mBufferedPercentageRunnable);
    }

    // 释放播放器
    @Override
    public void release() {
        if (mExoPlayer == null) return;
        mExoPlayer.release();
    }

    // 视频跳转到，单位秒
    @Override
    public void seekTo(int seekTime) {
        if (mExoPlayer == null) return;
        mExoPlayer.seekTo(mExoPlayer.getCurrentWindowIndex(), seekTime * 1000);
    }

    // 获取播放进度，单位秒
    @Override
    public int getCurrentPosition() {
        if (mExoPlayer == null) return -1;
        return (int) (mExoPlayer.getCurrentPosition() / 1000);
    }

    // 获取视频时长，单位秒
    @Override
    public int getDuration() {
        if (mExoPlayer == null) return -1;
        return (int) (mExoPlayer.getDuration() / 1000);
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

    private ZAYPlayerExoImpl() {
        mHandler = new Handler(Looper.getMainLooper());
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

    private void setSupportBackgroundAudio(boolean supportBackgroundAudio) {
        mSupportBackgroundAudio = supportBackgroundAudio;
    }

    private void setLifecycle(@NonNull Lifecycle lifecycle) {
        lifecycle.addObserver(this);
    }

    private int mPlaybackState;

    private void setExoPlayer(ExoPlayer exoPlayer) {
        if (exoPlayer instanceof SimpleExoPlayer) {
            mExoPlayer = (SimpleExoPlayer) exoPlayer;
            mExoPlayer.setRepeatMode(Player.REPEAT_MODE_OFF);// 不开启循环
            mExoPlayer.setPauseAtEndOfMediaItems(true);// 每个视频播放完后暂停
            mExoPlayer.addListener(new Player.EventListener() {
                @Override
                public void onPlaybackStateChanged(int state) {
                    mPlaybackState = state;
                    if (state == Player.STATE_IDLE) {
                        Log.i(TAG, "onPlaybackStateChanged state: STATE_IDLE");
                    } else if (state == Player.STATE_BUFFERING) {// 正在缓冲
                        Log.i(TAG, "onPlaybackStateChanged state: STATE_BUFFERING");
                        mHandler.post(mBufferedPercentageRunnable);
                        for (ZAYOnBufferingListener listener : mZAYOnBufferingListenerSet) {
                            if (listener != null) {
                                listener.onBufferingStart();
                            }
                        }
                    } else if (state == Player.STATE_READY) {// 缓冲完成，可以播放
                        Log.i(TAG, "onPlaybackStateChanged state: STATE_READY");
                        mIsPrepared = true;
                        if (mExoPlayer.getPlayWhenReady()) {
                            mHandler.post(mTimeInfoRunnable);
                        }
                        for (ZAYOnBufferingListener listener : mZAYOnBufferingListenerSet) {
                            if (listener != null) {
                                listener.onBufferingEnd();
                            }
                        }
                        for (ZAYOnPlayerStatusChangeListener listener : mZAYOnPlayerStatusChangeListenerSet) {
                            if (listener != null) {
                                listener.onPrepared();
                            }
                        }
                    } else if (state == Player.STATE_ENDED) {// 播放结束，全部视频播放完时才会触发
                        Log.i(TAG, "onPlaybackStateChanged state: STATE_ENDED");
                        for (ZAYOnPlayerStatusChangeListener listener : mZAYOnPlayerStatusChangeListenerSet) {
                            if (listener != null) {
                                listener.onCompleted();
                            }
                        }
                    }
                }
            });
        }
    }

    private final Runnable mTimeInfoRunnable = new Runnable() {
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

    private int mBufferedPercentage;
    private final Runnable mBufferedPercentageRunnable = new Runnable() {
        @Override
        public void run() {
            if (mExoPlayer != null) {
                if (mZAYOnBufferedUpdateListenerSet.size() > 0) {
                    int bufferedPercentage = mExoPlayer.getBufferedPercentage();
                    if (mBufferedPercentage != bufferedPercentage) {
                        mBufferedPercentage = bufferedPercentage;
                        for (ZAYOnBufferedUpdateListener listener : mZAYOnBufferedUpdateListenerSet) {
                            if (listener != null) {
                                listener.onBufferedPercentageChange(mBufferedPercentage);
                            }
                        }
                        Log.i(TAG, "mBufferedPercentageRunnable mBufferedPercentage: " + mBufferedPercentage);
                    }
                }
                mHandler.postDelayed(mBufferedPercentageRunnable, 1000L);
            }
        }
    };

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
        private Lifecycle mLifecycle;
        private final Context mContext;
        private Activity mActivity;

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

        public Builder setLifecycle(@Nullable Lifecycle lifecycle) {
            this.mLifecycle = lifecycle;
            return this;
        }

        public ZAYPlayerExoImpl build() {
            ZAYPlayerExoImpl playerFactory = new ZAYPlayerExoImpl();
            playerFactory.setSupportBackgroundAudio(mSupportBackgroundAudio);
            if (mLifecycle != null)
                playerFactory.setLifecycle(mLifecycle);
            @DefaultRenderersFactory.ExtensionRendererMode
            int extensionRendererMode = DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF;
            RenderersFactory renderersFactory = new DefaultRenderersFactory(mContext).setExtensionRendererMode(extensionRendererMode);
            SimpleExoPlayer exoPlayer = new SimpleExoPlayer.Builder(mContext, renderersFactory).build();
            playerFactory.setExoPlayer(exoPlayer);
            if (mActivity != null)
                playerFactory.setActivity(mActivity);
            playerFactory.initOrientationListener();
            return playerFactory;
        }
    }
}
