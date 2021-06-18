package com.zay.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zay.common.ZAYPlayer;
import com.zay.common.formatter.ITimeFormatter;

/**
 * Created by Zdw on 2021/06/18 10:21
 */
public abstract class ZAYPlayerView extends FrameLayout {

    protected ZAYPlayer mZAYPlayer;
    protected ZAYPlayerControlView mControlView;

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
    }

    protected abstract void init();

    public void setTimeFormatter(@NonNull ITimeFormatter timeFormatter) {
        if (mControlView != null) {
            mControlView.setTimeFormatter(timeFormatter);
        }
    }
}
