package com.zay.mediaplayer;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.baijiayun.constant.VideoDefinition;
import com.baijiayun.videoplayer.widget.BJYPlayerView;
import com.bokecc.sdk.mobile.play.DWMediaPlayer;
import com.zay.common.MPlayer;
import com.zay.common.PlayerMode;
import com.zay.common.listeners.OnMPlayerStatusChangeListener;
import com.zay.player_bjy.MPlayerBJYImpl;
import com.zay.player_cc.MPlayerCCImpl;
import com.zay.player_cc.widget.CCPlayerView;
import com.zay.player_exo.MPlayerExoImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class Main2Activity extends AppCompatActivity {

    private static final String TAG = Main2Activity.class.getSimpleName();
    private MPlayer mMPlayer;
    private Unbinder mUnbinder;
    @BindView(R.id.cc_player_view)
    CCPlayerView mCcPlayerView;
    @BindView(R.id.bjy_player_view)
    BJYPlayerView mBjyPlayerView;
    @BindView(R.id.iv_start_pause)
    ImageView mIvStartPause;
    @BindView(R.id.tv_current_time)
    TextView mTvCurrentTime;
    @BindView(R.id.sb_progress)
    SeekBar mSbProgress;
    @BindView(R.id.tv_duration)
    TextView mTvDuration;
    @BindView(R.id.tv_definition)
    TextView mTvDefinition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        initViews();
        initPlayerWrapper();
        if (ZAYApplication.getContext().getPlayerMode() == PlayerMode.MODE_CC) {
            mMPlayer.setAutoPlay(true);
            mMPlayer.setPreferredDefinition(DWMediaPlayer.HIGH_DEFINITION);
            mMPlayer.setupOnlineVideoWithId("4BCED300260AB2BD9C33DC5901307461", null);
        } else if (ZAYApplication.getContext().getPlayerMode() == PlayerMode.MODE_BJY) {
            mMPlayer.setAutoPlay(true);
            mMPlayer.setPreferredDefinition(VideoDefinition._1080P.getType());
            mMPlayer.setupOnlineVideoWithId("69115957", "zUOmwxiqv_U3Gu_x5aiohdaJqbP-km6stv7k6_ZZMWiKVcCqpqYKMSou22apgAA0");
        } else if (ZAYApplication.getContext().getPlayerMode() == PlayerMode.MODE_EXO) {
            mMPlayer.setAutoPlay(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUnbinder != null) {
            mUnbinder.unbind();
            mUnbinder = null;
        }
    }

    private void initViews() {
        mUnbinder = ButterKnife.bind(this);
        mSbProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mMPlayer != null && mMPlayer.isPlaying()) {
                    int seekTime = (int) (1.0d * seekBar.getProgress() / seekBar.getMax() * mMPlayer.getDuration());
                    mMPlayer.seekTo(seekTime);
                }
            }
        });
    }

    private void initPlayerWrapper() {
        if (ZAYApplication.getContext().getPlayerMode() == PlayerMode.MODE_CC) {
            mMPlayer = new MPlayerCCImpl.Builder(this)
                    .setSupportBackgroundAudio(false)
                    .setLifecycle(getLifecycle())
                    .setUserId("055AE5CC4411D5CB")
                    .setApiKey("BnPoazIUwhRa1Sk5AVBP7hk8dDk7a3Aw")
                    .setVerificationCode(null)
                    .build();
            mMPlayer.bindPlayerView(mCcPlayerView);
            mCcPlayerView.setVisibility(View.VISIBLE);
            mBjyPlayerView.setVisibility(View.GONE);
        } else if (ZAYApplication.getContext().getPlayerMode() == PlayerMode.MODE_BJY) {
            mMPlayer = new MPlayerBJYImpl.Builder(this)
                    .setSupportBackgroundAudio(false)
                    .setSupportBreakPointPlay(false)
                    .setSupportLooping(true)
                    .setLifecycle(getLifecycle())
                    .setUserInfo(null, null)
                    .build();
            mMPlayer.bindPlayerView(mBjyPlayerView);
            mBjyPlayerView.setVisibility(View.VISIBLE);
            mCcPlayerView.setVisibility(View.GONE);
        } else if (ZAYApplication.getContext().getPlayerMode() == PlayerMode.MODE_EXO) {
            mMPlayer = new MPlayerExoImpl.Builder(this)
                    .setSupportBackgroundAudio(false)
                    .setLifecycle(getLifecycle())
                    .build();
        }
        mMPlayer.setOnMPlayingTimeChangeListener((currentTime, duration) -> {
            mTvCurrentTime.setText(millSecondsToStr(currentTime * 1000));
            mTvDuration.setText(millSecondsToStr(duration * 1000));
            mSbProgress.setProgress(mSbProgress.getMax() * currentTime / duration);
            mIvStartPause.setImageResource(R.drawable.small_stop_ic);//播放中
        });
        mMPlayer.setOnMBufferedUpdateListener(bufferedPercentage -> {
            //视频缓冲进度
            mSbProgress.setSecondaryProgress(bufferedPercentage);
        });
        mMPlayer.setOnMBufferingListener(null);
        mMPlayer.setOnMPlayerStatusChangeListener(new OnMPlayerStatusChangeListener() {
            @Override
            public void onPrepared() {//视频准备完成
                if (mMPlayer.isPlaying()) {//自动播放
                    mIvStartPause.setImageResource(R.drawable.small_stop_ic);
                } else {//不自动播放
                    mIvStartPause.setImageResource(R.drawable.small_begin_ic);
                }
                mTvDefinition.setText(mMPlayer.getDefinitionName());
            }

            @Override
            public void onPaused() {//视频被暂停
                mIvStartPause.setImageResource(R.drawable.small_begin_ic);
            }

            @Override
            public void onCompleted() {//视频播放完成
                mIvStartPause.setImageResource(R.drawable.small_begin_ic);
            }
        });
    }

    @OnClick({R.id.iv_start_pause, R.id.tv_definition})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_start_pause:
                if (mMPlayer != null) {
                    if (mMPlayer.isPlaying()) {
                        mMPlayer.pause();
                    } else {
                        mMPlayer.start();
                    }
                }
                break;
            case R.id.tv_definition:
                Map<String, Integer> supportedDefinitions = mMPlayer.getSupportedDefinitions();
                if (supportedDefinitions == null || supportedDefinitions.size() <= 1) return;
                List<String> definitionNames = new ArrayList<>(supportedDefinitions.keySet());
                new AlertDialog.Builder(this).setTitle("清晰度")
                        .setItems(definitionNames.toArray(new String[]{}), (dialog, which) -> {
                            Integer definition = supportedDefinitions.get(definitionNames.get(which));
                            if (definition != null) {
                                boolean result = mMPlayer.changeDefinition(definition);
                                Log.e(TAG, "changeDefinition: " + result);
                            }
                        }).create().show();
                break;
        }
    }

    public static String millSecondsToStr(int millSeconds) {
        int seconds = millSeconds / 1000;
        String result = "";
        int hour, min, second;
        hour = seconds / 3600;
        min = (seconds - hour * 3600) / 60;
        second = seconds - hour * 3600 - min * 60;
        if (hour < 10) {
            result += "0" + hour + ":";
        } else {
            result += hour + ":";
        }
        if (min < 10) {
            result += "0" + min + ":";
        } else {
            result += min + ":";
        }
        if (second < 10) {
            result += "0" + second;
        } else {
            result += second;
        }
        return result;
    }
}
