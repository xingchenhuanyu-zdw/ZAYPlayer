package com.zay.mediaplayer;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.baijiayun.constant.VideoDefinition;
import com.bokecc.sdk.mobile.play.DWMediaPlayer;
import com.zay.common.PlayerMode;
import com.zay.common.ZAYPlayer;
import com.zay.common.formatter.DefaultTimeFormatter;
import com.zay.mediaplayer.databinding.ActivityMain2Binding;
import com.zay.player_bjy.ZAYPlayerBJYImpl;
import com.zay.player_cc.ZAYPlayerCCImpl;
import com.zay.player_exo.ZAYPlayerExoImpl;

public class Main2Activity extends AppCompatActivity {

    private ZAYPlayer mZAYPlayer;
    private ActivityMain2Binding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMain2Binding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        initPlayerWrapper();
        if (ZAYApplication.getContext().getPlayerMode() == PlayerMode.MODE_CC) {
            mZAYPlayer.setAutoPlay(true);
            mZAYPlayer.setPreferredDefinition(DWMediaPlayer.HIGH_DEFINITION);
            mZAYPlayer.setupOnlineVideoWithId("4BCED300260AB2BD9C33DC5901307461", null);
        } else if (ZAYApplication.getContext().getPlayerMode() == PlayerMode.MODE_BJY) {
            mZAYPlayer.setAutoPlay(true);
            mZAYPlayer.setPreferredDefinition(VideoDefinition._1080P.getType());
            mZAYPlayer.setupOnlineVideoWithId("74480736", "aSt0EqnVkzo3Gu_x5aiohbwdvr_pkgGjgIDMTJGst8-4HK5GTcos1you22apgAA0");
        } else if (ZAYApplication.getContext().getPlayerMode() == PlayerMode.MODE_EXO) {
            mZAYPlayer.setAutoPlay(true);
            mZAYPlayer.setupOnlineVideoWithId("https://cz-video-view.oss-cn-beijing.aliyuncs.com/20201217/ca6d1674a038a786ab265d3fb1836a9c.mp4", null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mZAYPlayer.removeAllOnPlayingTimeChangeListener();
        mZAYPlayer.removeAllOnBufferedUpdateListener();
        mZAYPlayer.removeAllOnBufferingListener();
        mZAYPlayer.removeAllOnPlayerStatusChangeListener();
    }

    private void initPlayerWrapper() {
        if (ZAYApplication.getContext().getPlayerMode() == PlayerMode.MODE_CC) {
            mZAYPlayer = new ZAYPlayerCCImpl.Builder(this)
                    .setSupportBackgroundAudio(false)
                    .setLifecycle(getLifecycle())
                    .setUserId("055AE5CC4411D5CB")
                    .setApiKey("BnPoazIUwhRa1Sk5AVBP7hk8dDk7a3Aw")
                    .setVerificationCode(null)
                    .build();
            mZAYPlayer.bindPlayerView(mBinding.ccPlayerView);
            mBinding.ccPlayerView.setTimeFormatter(new DefaultTimeFormatter());// 自定义时间格式
            mBinding.ccPlayerView.setVisibility(View.VISIBLE);
            mBinding.bjyPlayerView.setVisibility(View.GONE);
            mBinding.exoPlayerView.setVisibility(View.GONE);
        } else if (ZAYApplication.getContext().getPlayerMode() == PlayerMode.MODE_BJY) {
            mZAYPlayer = new ZAYPlayerBJYImpl.Builder(this)
                    .setSupportBackgroundAudio(false)
                    .setSupportBreakPointPlay(false)
                    .setSupportLooping(false)
                    .setLifecycle(getLifecycle())
                    .setUserInfo(null, null)
                    .build();
            mZAYPlayer.bindPlayerView(mBinding.bjyPlayerView);
            mBinding.bjyPlayerView.setTimeFormatter(new DefaultTimeFormatter());// 自定义时间格式
            mBinding.bjyPlayerView.setVisibility(View.VISIBLE);
            mBinding.ccPlayerView.setVisibility(View.GONE);
            mBinding.exoPlayerView.setVisibility(View.GONE);
        } else if (ZAYApplication.getContext().getPlayerMode() == PlayerMode.MODE_EXO) {
            mZAYPlayer = new ZAYPlayerExoImpl.Builder(this)
                    .setSupportBackgroundAudio(false)
                    .setLifecycle(getLifecycle())
                    .build();
            mZAYPlayer.bindPlayerView(mBinding.exoPlayerView);
            mBinding.exoPlayerView.setTimeFormatter(new DefaultTimeFormatter());// 自定义时间格式
            mBinding.exoPlayerView.setVisibility(View.VISIBLE);
            mBinding.ccPlayerView.setVisibility(View.GONE);
            mBinding.bjyPlayerView.setVisibility(View.GONE);
        }
    }
}
