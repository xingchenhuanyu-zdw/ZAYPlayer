package com.zay.mediaplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.baijiayun.constant.VideoDefinition;
import com.baijiayun.videoplayer.widget.BJYPlayerView;
import com.zay.common.MPlayer;
import com.zay.common.PlayerMode;
import com.zay.player_bjy.MPlayerBJYImpl;
import com.zay.player_cc.MPlayerCCImpl;
import com.zay.player_cc.widget.CCPlayerView;

import java.util.ArrayList;

public class Main2Activity extends AppCompatActivity {

    private MPlayer mMPlayer;
    private CCPlayerView mCCPlayerView;
    private BJYPlayerView mBJYPlayerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        mCCPlayerView = findViewById(R.id.cc_player_view);
        mBJYPlayerView = findViewById(R.id.bjy_player_view);
        initPlayerWrapper();
        mMPlayer.setAutoPlay(true);
        mMPlayer.setupOnlineVideoWithId("4BCED300260AB2BD9C33DC5901307461", null);
    }

    private void initPlayerWrapper() {
        if (ZAYApplication.getContext().getPlayerMode() == PlayerMode.MODE_CC) {
            mMPlayer = new MPlayerCCImpl.Builder()
                    .setSupportBackgroundAudio(false)
                    .setLifecycle(getLifecycle())
                    .setContext(this)
                    .setUserId("055AE5CC4411D5CB")
                    .setApiKey("BnPoazIUwhRa1Sk5AVBP7hk8dDk7a3Aw")
                    .setVerificationCode(null)
                    .build();
            mMPlayer.bindPlayerView(mCCPlayerView);
        } else if (ZAYApplication.getContext().getPlayerMode() == PlayerMode.MODE_BJY) {
            mMPlayer = new MPlayerBJYImpl.Builder()
                    .setSupportBackgroundAudio(false)
                    .setSupportBreakPointPlay(false)
                    .setSupportLooping(false)
                    .setLifecycle(getLifecycle())
                    .setPreferredDefinitions(new ArrayList<VideoDefinition>() {{
                        add(VideoDefinition.UNKNOWN);
                    }})
                    .setContext(this)
                    .setUserInfo(null, null)
                    .build();
            mMPlayer.bindPlayerView(mBJYPlayerView);
        }
    }
}
