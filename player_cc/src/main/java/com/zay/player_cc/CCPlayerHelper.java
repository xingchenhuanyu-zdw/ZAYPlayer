package com.zay.player_cc;

import android.content.Context;
import android.content.SharedPreferences;

import com.bokecc.sdk.mobile.util.DWSdkStorage;
import com.bokecc.sdk.mobile.util.DWStorageUtil;

/**
 * Created by Zdw on 2021/04/21 15:25
 */
public class CCPlayerHelper {

    /**
     * 播放加密视频调用此方法
     *
     * @param context 上下文
     */
    public static void initDWStorage(Context context) {
        final SharedPreferences sp = context
                .getSharedPreferences("DWSdkStorage", Context.MODE_PRIVATE);
        DWSdkStorage myDWSdkStorage = new DWSdkStorage() {
            @Override
            public String get(String key) {
                return sp.getString(key, "");
            }

            @Override
            public void put(String key, String value) {
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(key, value);
                editor.apply();
            }
        };
        DWStorageUtil.setDWSdkStorage(myDWSdkStorage);
    }
}
