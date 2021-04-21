package com.zay.player_cc.widget;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by Zdw on 2021/03/22 11:14
 */
public class CCPlayerView extends FrameLayout implements TextureView.SurfaceTextureListener {

    private static final String TAG = "CCPlayerView";
    private TextureView mTextureView;
    private TextureView.SurfaceTextureListener mSurfaceTextureListener;
    private int mVideoWidth;
    private int mVideoHeight;
    private int mViewWidth;
    private int mViewHeight;

    public CCPlayerView(@NonNull Context context) {
        this(context, null);
    }

    public CCPlayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CCPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public CCPlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void setSurfaceTextureListener(TextureView.SurfaceTextureListener surfaceTextureListener) {
        mSurfaceTextureListener = surfaceTextureListener;
    }

    private void init() {
        mTextureView = new TextureView(getContext());
        mTextureView.setSurfaceTextureListener(this);
        addView(mTextureView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    private void updateTextureViewSize() {
        if (mTextureView == null) return;
        if (mVideoWidth <= 0 || mVideoHeight <= 0) return;
        if (mViewWidth <= 0 || mViewHeight <= 0) return;
        final double videoRatio = 1.0d * mVideoWidth / mVideoHeight;
        Log.i(TAG, "updateTextureViewSize===" + "videoWidth=" + mVideoWidth + ", videoHeight=" + mVideoHeight + ", videoRatio=" + videoRatio);
        final double viewRatio = 1.0d * mViewWidth / mViewHeight;
        Log.i(TAG, "updateTextureViewSize===" + "viewWidth=" + mViewWidth + ", viewHeight=" + mViewHeight + ", viewRatio=" + viewRatio);
        //必须放在 post 里面执行
        post(new Runnable() {
            @Override
            public void run() {
                LayoutParams lp = (LayoutParams) mTextureView.getLayoutParams();
                lp.gravity = Gravity.CENTER;
                lp.width = mViewWidth;
                lp.height = mViewHeight;
                if (videoRatio > viewRatio) {
                    lp.height = (int) (mViewWidth / videoRatio);
                } else {
                    lp.width = (int) (mViewHeight * videoRatio);
                }
                mTextureView.setLayoutParams(lp);
            }
        });
    }

    public void onVideoSizeChanged(int width, int height) {
        Log.i(TAG, "onVideoSizeChanged===width===" + width);
        Log.i(TAG, "onVideoSizeChanged===height===" + height);
        mVideoWidth = width;
        mVideoHeight = height;
        updateTextureViewSize();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        mViewWidth = w;
        mViewHeight = h;
        updateTextureViewSize();
    }

    @Override
    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
        Log.i(TAG, "onSurfaceTextureAvailable===width===" + width);
        Log.i(TAG, "onSurfaceTextureAvailable===height===" + height);
        if (mSurfaceTextureListener != null)
            mSurfaceTextureListener.onSurfaceTextureAvailable(surface, width, height);
    }

    @Override
    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {
        Log.i(TAG, "onSurfaceTextureSizeChanged===width===" + width);
        Log.i(TAG, "onSurfaceTextureSizeChanged===height===" + height);
        if (mSurfaceTextureListener != null)
            mSurfaceTextureListener.onSurfaceTextureSizeChanged(surface, width, height);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
        if (mSurfaceTextureListener != null)
            mSurfaceTextureListener.onSurfaceTextureDestroyed(surface);
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {
        if (mSurfaceTextureListener != null)
            mSurfaceTextureListener.onSurfaceTextureUpdated(surface);
    }
}
