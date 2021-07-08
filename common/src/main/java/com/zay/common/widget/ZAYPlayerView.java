package com.zay.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

    private void initThis() {
        addOnAttachStateChangeListener(new OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                Log.i(TAG, "onViewAttachedToWindow: ");
                postDelayed(mHideRunnable, mControlViewShowTime * 1000L);
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                Log.i(TAG, "onViewDetachedFromWindow: ");
                removeCallbacks(mHideRunnable);
            }
        });

        // TODO: 2021/07/06 替换为手势
        setOnClickListener(v -> {
            if (mControlView.getVisibility() == VISIBLE) {
                hideControlView();
            } else if (mControlView.getVisibility() == GONE) {
                showControlView();
            }
        });

        mControlView.setVisibility(mUseController ? VISIBLE : GONE);
    }

    //显示控制器
    private void showControlView() {
        if (mUseController) {
            removeCallbacks(mHideRunnable);
            mControlView.setVisibility(VISIBLE);
            postDelayed(mHideRunnable, mControlViewShowTime * 1000L);
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
}
