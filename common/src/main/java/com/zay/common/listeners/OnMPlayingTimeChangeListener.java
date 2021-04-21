package com.zay.common.listeners;

/**
 * Created by Zdw on 2021/01/19 16:05
 * // 播放进度监听，单位为秒
 */
public interface OnMPlayingTimeChangeListener {
    void onPlayingTimeChange(int currentTime, int duration);
}
