package com.zay.common.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GestureDetectorCompat;

import com.zay.common.R;
import com.zay.common.ZAYPlayer;
import com.zay.common.formatter.ITimeFormatter;

/**
 * Created by Zdw on 2021/06/18 10:21
 */
public abstract class ZAYPlayerView extends FrameLayout {

    private static final String TAG = ZAYPlayerView.class.getSimpleName();
    protected ZAYPlayer mZAYPlayer;
    protected ZAYPlayerControlView mControlView;
    private boolean mUseController = false;
    private int mControlViewShowTime = 3;
    private final Runnable mHideRunnable = this::hideControlView;

    private TextView mVideoProgressView;

    private AudioManager mAudioManager;
    private int mMinVolume, mMaxVolume;
    private int mCurrentVolume;
    private LinearLayout mLayoutVolumeProgress;
    private ProgressBar mPbVolume;

    private static final int mMinBrightness = 0;
    private static final int mMaxBrightness = 255;
    private int mCurrentBrightness;
    private LinearLayout mLayoutBrightnessProgress;
    private ProgressBar mPbBrightness;

    public ZAYPlayerView(@NonNull Context context) {
        this(context, null);
    }

    public ZAYPlayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZAYPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ZAYPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
        initThis();
    }

    protected abstract void init();

    @SuppressLint("ClickableViewAccessibility")
    private void initThis() {
        addOnAttachStateChangeListener(new OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                //Log.i(TAG, "onViewAttachedToWindow: ");
                postDelayed(mHideRunnable, mControlViewShowTime * 1000L);
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                //Log.i(TAG, "onViewDetachedFromWindow: ");
                removeCallbacks(mHideRunnable);
            }
        });

        GestureDetectorCompat gestureDetectorCompat =
                new GestureDetectorCompat(getContext(), mOnGestureListener);
        setOnTouchListener((v, event) -> {
            gestureDetectorCompat.onTouchEvent(event);
            if (event.getAction() == MotionEvent.ACTION_UP) {
                onActionUp();
            }
            return true;
        });

        mControlView.setVisibility(mUseController ? VISIBLE : GONE);

        LayoutInflater.from(getContext()).inflate(R.layout.zay_video_progress, this, true);
        mVideoProgressView = findViewById(R.id.zay_video_progress);
        mVideoProgressView.setVisibility(GONE);

        mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            mMinVolume = mAudioManager.getStreamMinVolume(AudioManager.STREAM_MUSIC);
        } else {
            mMinVolume = 0;
        }
        mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        LayoutInflater.from(getContext()).inflate(R.layout.zay_volume_progress, this, true);
        mLayoutVolumeProgress = findViewById(R.id.zay_layout_volume_progress);
        mPbVolume = findViewById(R.id.zay_pb_volume);
        mPbVolume.setMax(mMaxVolume);

        LayoutInflater.from(getContext()).inflate(R.layout.zay_brightness_progress, this, true);
        mLayoutBrightnessProgress = findViewById(R.id.zay_layout_brightness_progress);
        mPbBrightness = findViewById(R.id.zay_pb_brightness);
        mPbBrightness.setMax(mMaxBrightness);
    }

    //显示控制器
    private void showControlView() {
        if (mUseController) {
            removeCallbacks(mHideRunnable);
            mControlView.setVisibility(VISIBLE);
            if (mZAYPlayer != null && mZAYPlayer.isPlaying()) {
                postDelayed(mHideRunnable, mControlViewShowTime * 1000L);
            }
        }
    }

    //隐藏控制器
    private void hideControlView() {
        if (mUseController) {
            removeCallbacks(mHideRunnable);
            mControlView.setVisibility(GONE);
        }
    }

    //设置时间格式
    public void setTimeFormatter(@NonNull ITimeFormatter timeFormatter) {
        mControlView.setTimeFormatter(timeFormatter);
    }

    //是否使用控制器
    public void setUseController(boolean useController) {
        mUseController = useController;
    }

    //设置控制器显示时间
    public void setControlViewShowTime(@IntRange(from = 3, to = 10) int controlViewShowTime) {
        mControlViewShowTime = controlViewShowTime;
    }

    private int getWindowBrightness() {
        if (getContext() instanceof Activity) {
            Activity activity = (Activity) getContext();
            Window window = activity.getWindow();
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            if (layoutParams.screenBrightness == -1f) {
                return Settings.System.getInt(getContext().getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS, -1);
            } else {
                return (int) (layoutParams.screenBrightness * 255f);
            }
        }
        return Settings.System.getInt(getContext().getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS, -1);
    }

    private void setWindowBrightness(int brightness) {
        if (getContext() instanceof Activity) {
            Activity activity = (Activity) getContext();
            Window window = activity.getWindow();
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.screenBrightness = brightness / 255f;
            window.setAttributes(layoutParams);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        mViewWidth = w;
        mViewHeight = h;
    }

    private int mViewWidth, mViewHeight;
    private int mCurrentTime;//当前播放进度
    private int mSeekTime;//视频跳转进度
    private float mTotalDistanceX;//水平方向拖动距离
    private float mTotalDistanceY;//垂直方向拖动距离
    private float mDownX;//按下去的位置
    private int mMode = MODE_NONE;
    private static final int MODE_NONE = 0;
    private static final int MODE_PROGRESS = 1;
    private static final int MODE_VOLUME = 2;
    private static final int MODE_BRIGHTNESS = 3;

    private final GestureDetector.SimpleOnGestureListener mOnGestureListener
            = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            //Log.i(TAG, "onSingleTapUp: ");
            return super.onSingleTapUp(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.i(TAG, "onSingleTapConfirmed: " + e.toString());
            if (mControlView.getVisibility() == VISIBLE) {
                hideControlView();
            } else if (mControlView.getVisibility() == GONE) {
                showControlView();
            }
            return true;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            //Log.i(TAG, "onDoubleTapEvent: ");
            return super.onDoubleTapEvent(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.i(TAG, "onDoubleTap: " + e.toString());
            if (mZAYPlayer != null) {
                if (mZAYPlayer.isPlaying()) {
                    mZAYPlayer.pause();
                    removeCallbacks(mHideRunnable);
                } else {
                    mZAYPlayer.start();
                }
            }
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            if (mZAYPlayer == null || !mZAYPlayer.isPlaying()) return false;
            mCurrentTime = mZAYPlayer.getCurrentPosition();
            Log.i(TAG, "onDown mCurrentTime: " + mCurrentTime);
            mCurrentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            Log.i(TAG, "onDown mCurrentVolume: " + mCurrentVolume);
            mCurrentBrightness = getWindowBrightness();
            Log.i(TAG, "onDown mCurrentBrightness: " + mCurrentBrightness);
            mTotalDistanceX = 0;//拖动距离归零
            mTotalDistanceY = 0;
            mDownX = e.getX();
            //按下去的位置
            float downY = e.getY();
            Log.i(TAG, "onDown mDownX: " + mDownX + " mDownY: " + downY);
            mMode = MODE_NONE;//模式重置
            return super.onDown(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (mZAYPlayer == null || !mZAYPlayer.isPlaying()) return false;
            if (mMode == MODE_NONE) {
                if (Math.abs(distanceX) >= Math.abs(distanceY)) {//水平拖动
                    mMode = MODE_PROGRESS;//进度调节
                } else {
                    if (mDownX >= mViewWidth * 0.5f) {
                        mMode = MODE_VOLUME;//音量调节
                    } else {
                        mMode = MODE_BRIGHTNESS;//亮度调节
                    }
                }
            }

            if (mMode == MODE_PROGRESS) {
                mTotalDistanceX += distanceX;//右滑distanceX为负
                int duration = mZAYPlayer.getDuration();
                if (mViewWidth == 0 || duration == 0) return false;//除数不为0
                mSeekTime = (int) (mCurrentTime - 1.0f * duration / (mViewWidth * 2f) * mTotalDistanceX);
                mSeekTime = Math.max(0, mSeekTime);//大于等于0
                mSeekTime = Math.min(mSeekTime, duration);//小于等于duration
                Log.i(TAG, "onScroll seekTime: " + mSeekTime + " duration: " + duration);
                mVideoProgressView.setVisibility(VISIBLE);
                ITimeFormatter timeFormatter = mControlView.getTimeFormatter();
                mVideoProgressView.setText(new StringBuilder(timeFormatter.getFormattedValue(mSeekTime * 1000L))
                        .append("/").append(timeFormatter.getFormattedValue(duration * 1000L)));
            } else if (mMode == MODE_VOLUME) {
                mTotalDistanceY += distanceY;//下滑distanceY为负
                if (mViewHeight == 0) return false;//除数不为0
                int seekVolume = (int) (mCurrentVolume + 1.0f * mMaxVolume / (mViewHeight * 1f) * mTotalDistanceY);
                seekVolume = Math.max(mMinVolume, seekVolume);
                seekVolume = Math.min(seekVolume, mMaxVolume);
                mLayoutVolumeProgress.setVisibility(VISIBLE);
                mPbVolume.setProgress(seekVolume);
                if (seekVolume != mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC)) {
                    Log.i(TAG, "onScroll seekVolume: " + seekVolume);
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, seekVolume, AudioManager.FLAG_PLAY_SOUND);
                }
            } else if (mMode == MODE_BRIGHTNESS) {
                mTotalDistanceY += distanceY;//下滑distanceY为负
                if (mViewHeight == 0 || mCurrentBrightness == -1) return false;//获取亮度失败
                int seekBrightness = (int) (mCurrentBrightness + 1.0f * mMaxBrightness / (mViewHeight * 1f) * mTotalDistanceY);
                seekBrightness = Math.max(mMinBrightness, seekBrightness);
                seekBrightness = Math.min(seekBrightness, mMaxBrightness);
                mLayoutBrightnessProgress.setVisibility(VISIBLE);
                mPbBrightness.setProgress(seekBrightness);
                setWindowBrightness(seekBrightness);
                Log.i(TAG, "onScroll seekBrightness: " + seekBrightness);
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    };

    private void onActionUp() {
        if (mMode == MODE_PROGRESS) {
            Log.i(TAG, "onActionUp: MODE_PROGRESS");
            mVideoProgressView.setVisibility(GONE);
            if (mZAYPlayer == null || !mZAYPlayer.isPlaying()) return;
            mZAYPlayer.seekTo(mSeekTime);
        } else if (mMode == MODE_VOLUME) {
            Log.i(TAG, "onActionUp: MODE_VOLUME");
            mLayoutVolumeProgress.setVisibility(GONE);
        } else if (mMode == MODE_BRIGHTNESS) {
            Log.i(TAG, "onActionUp: MODE_BRIGHTNESS");
            mLayoutBrightnessProgress.setVisibility(GONE);
        }
    }
}
