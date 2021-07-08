package com.zay.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.zay.common.R;
import com.zay.common.ZAYPlayer;
import com.zay.common.formatter.DefaultTimeFormatter;
import com.zay.common.formatter.ITimeFormatter;
import com.zay.common.listeners.ZAYOnBufferingListener;
import com.zay.common.listeners.ZAYOnPlayerStatusChangeListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Zdw on 2021/06/18 10:21
 */
public class ZAYPlayerControlView extends FrameLayout implements View.OnClickListener {

    private static final String TAG = ZAYPlayerControlView.class.getSimpleName();
    private ZAYPlayer mZAYPlayer;

    private ImageView mIvPause;
    private ImageView mIvStart;
    private TextView mTvDefinition;
    private TextView mTvCurrentTime;
    private TextView mTvDuration;
    private SeekBar mSbProgress;

    private ITimeFormatter mTimeFormatter = new DefaultTimeFormatter();

    public void setZAYPlayer(@NonNull ZAYPlayer zayPlayer) {
        mZAYPlayer = zayPlayer;
        setListeners();
    }

    public void setTimeFormatter(@NonNull ITimeFormatter timeFormatter) {
        mTimeFormatter = timeFormatter;
    }

    public ZAYPlayerControlView(@NonNull Context context) {
        this(context, null);
    }

    public ZAYPlayerControlView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZAYPlayerControlView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public ZAYPlayerControlView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    protected void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.zay_player_control_view, this, true);
        mIvPause = findViewById(R.id.zay_iv_pause);
        mIvPause.setOnClickListener(this);
        mIvStart = findViewById(R.id.zay_iv_start);
        mIvStart.setOnClickListener(this);
        mTvDefinition = findViewById(R.id.zay_tv_definition);
        mTvDefinition.setOnClickListener(this);
        mTvCurrentTime = findViewById(R.id.zay_tv_current_time);
        mTvDuration = findViewById(R.id.zay_tv_duration);
        mSbProgress = findViewById(R.id.zay_sb_progress);
        mSbProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mZAYPlayer != null && mZAYPlayer.isPlaying()) {
                    int seekTime = (int) (1.0d * seekBar.getProgress() / seekBar.getMax() * mZAYPlayer.getDuration());
                    mZAYPlayer.seekTo(seekTime);
                }
            }
        });
        showStart();
    }

    private void setListeners() {
        mZAYPlayer.addOnPlayingTimeChangeListener((currentTime, duration) -> {
            if (mTimeFormatter == null)
                mTimeFormatter = new DefaultTimeFormatter();
            mTvCurrentTime.setText(mTimeFormatter.getFormattedValue(currentTime * 1000L));
            mTvDuration.setText(mTimeFormatter.getFormattedValue(duration * 1000L));
            mSbProgress.setProgress(mSbProgress.getMax() * currentTime / duration);
            if (mZAYPlayer.isPlaying()) {
                showPause();
            }
        });
        mZAYPlayer.addOnBufferedUpdateListener(bufferedPercentage -> mSbProgress.setSecondaryProgress(bufferedPercentage));
        mZAYPlayer.addOnBufferingListener(new ZAYOnBufferingListener() {
            @Override
            public void onBufferingStart() {
                Log.i(TAG, "onBufferingStart: ");
            }

            @Override
            public void onBufferingEnd() {
                Log.i(TAG, "onBufferingEnd: ");
            }
        });
        mZAYPlayer.addOnPlayerStatusChangeListener(new ZAYOnPlayerStatusChangeListener() {
            @Override
            public void onPrepared() {
                Log.i(TAG, "onPrepared: ");
                if (mZAYPlayer.isPlaying()) {
                    showPause();
                } else {
                    showStart();
                }
                mTvDefinition.setText(mZAYPlayer.getDefinitionName());
            }

            @Override
            public void onPaused() {
                Log.i(TAG, "onPaused: ");
                showStart();
            }

            @Override
            public void onCompleted() {
                Log.i(TAG, "onCompleted: ");
                showStart();
            }
        });
    }

    private void showStart() {
        mIvPause.setVisibility(GONE);
        mIvStart.setVisibility(VISIBLE);
    }

    private void showPause() {
        mIvPause.setVisibility(VISIBLE);
        mIvStart.setVisibility(GONE);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.zay_iv_pause) {
            if (mZAYPlayer != null && mZAYPlayer.isPlaying()) {
                mZAYPlayer.pause();
            }
        } else if (v.getId() == R.id.zay_iv_start) {
            if (mZAYPlayer != null && !mZAYPlayer.isPlaying()) {
                mZAYPlayer.start();
            }
        } else if (v.getId() == R.id.zay_tv_definition) {
            final Map<String, Integer> supportedDefinitions = mZAYPlayer.getSupportedDefinitions();
            if (supportedDefinitions == null || supportedDefinitions.size() <= 1) return;
            final List<String> definitionNames = new ArrayList<>(supportedDefinitions.keySet());
            new AlertDialog.Builder(getContext()).setTitle("清晰度")
                    .setItems(definitionNames.toArray(new String[]{}), (dialog, which) -> {
                        Integer definition = supportedDefinitions.get(definitionNames.get(which));
                        if (definition != null) {
                            boolean result = mZAYPlayer.changeDefinition(definition);
                            Log.i(TAG, "changeDefinition: " + result);
                        }
                    }).create().show();
        }
    }
}
