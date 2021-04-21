package com.zay.common.listeners;

/**
 * Created by Zdw on 2021/01/19 16:27
 * // 播放器状态监听
 */
public interface OnMPlayerStatusChangeListener {
    void onPrepared();

    void onPaused();

    void onCompleted();
}
