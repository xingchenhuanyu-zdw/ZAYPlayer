package com.zay.mediaplayer;

import android.app.Application;
import android.content.Context;

import com.baijiayun.BJYPlayerSDK;
import com.zay.common.Constant;
import com.zay.common.PlayerMode;
import com.zay.player_cc.CCPlayerHelper;

/**
 * Created by Zdw on 2021/04/21 14:28
 */
public class ZAYApplication extends Application {

    private static ZAYApplication sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init() {
        if (getPlayerMode() == PlayerMode.MODE_CC) {
            CCPlayerHelper.initDWStorage(getContext());
        } else if (getPlayerMode() == PlayerMode.MODE_BJY) {
            initBJYPlayerSDK();
        }
    }

    // 初始化百家云配置
    private void initBJYPlayerSDK() {
        new BJYPlayerSDK.Builder(this)
                .setDevelopMode(true)
                // 如果没有个性域名请注释
                .setCustomDomain("e66539824.at.baijiayun.com")
                .setEncrypt(true)
                .build();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        sContext = this;
    }

    public static ZAYApplication getContext() {
        return sContext;
    }

    @Constant.PlayerMode
    public int getPlayerMode() {
        return PlayerMode.MODE_CC;
    }
}
