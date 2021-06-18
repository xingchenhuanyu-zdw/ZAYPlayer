package com.zay.player_bjy.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.baijiayun.videoplayer.widget.BJYPlayerView;
import com.zay.common.ZAYPlayer;
import com.zay.common.widget.ZAYPlayerControlView;
import com.zay.common.widget.ZAYPlayerView;

/**
 * Created by Zdw on 2021/06/18 15:01
 */
public class ZAYBJYPlayerView extends ZAYPlayerView {

    private BJYPlayerView mPlayerView;

    public BJYPlayerView getPlayerView() {
        return mPlayerView;
    }

    public void setZAYPlayer(@NonNull ZAYPlayer zayPlayer) {
        mZAYPlayer = zayPlayer;
        mControlView.setZAYPlayer(zayPlayer);
    }

    public ZAYBJYPlayerView(@NonNull Context context) {
        this(context, null);
    }

    public ZAYBJYPlayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZAYBJYPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ZAYBJYPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void init() {
        mPlayerView = new BJYPlayerView(getContext());
        addView(mPlayerView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mControlView = new ZAYPlayerControlView(getContext());
        LayoutParams layoutParams = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.BOTTOM;
        addView(mControlView, layoutParams);
    }
}
