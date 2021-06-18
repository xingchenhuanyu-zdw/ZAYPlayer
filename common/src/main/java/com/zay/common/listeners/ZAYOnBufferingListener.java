package com.zay.common.listeners;

/**
 * Created by Zdw on 2021/01/19 16:18
 * // 缓冲状态监听
 */
public interface ZAYOnBufferingListener {
    void onBufferingStart();// 正在缓冲，不能播放

    void onBufferingEnd();// 缓冲完成，可以播放
}
