package com.zay.common;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Zdw on 2021/04/21 14:41
 */
public class Constant {

    @IntDef({com.zay.common.PlayerMode.MODE_CC, com.zay.common.PlayerMode.MODE_BJY, com.zay.common.PlayerMode.MODE_ORIGINAL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface PlayerMode {
    }
}
