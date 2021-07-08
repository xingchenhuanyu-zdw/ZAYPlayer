package com.zay.common.orientation;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.util.Log;
import android.view.OrientationEventListener;

import androidx.annotation.NonNull;

/**
 * Created by Zdw on 2021/07/07 16:06
 */
public class PlayerOrientationListener extends OrientationEventListener {

    private static final String TAG = PlayerOrientationListener.class.getSimpleName();
    private final Context mContext;

    public PlayerOrientationListener(@NonNull Context context, int rate) {
        super(context, rate);
        mContext = context;
    }

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    public void onOrientationChanged(int orientation) {
        int threshold = 5;
        if (Math.abs(orientation) < threshold) orientation = 0;
        else if (Math.abs(orientation - 90) < threshold) orientation = 90;
        else if (Math.abs(orientation - 180) < threshold) orientation = 180;
        else if (Math.abs(orientation - 270) < threshold) orientation = 270;

        Activity activity;
        if (mContext instanceof Activity) {
            activity = (Activity) mContext;
        } else {
            return;
        }

        switch (orientation) {
            case 0:
                Log.i(TAG, "onOrientationChanged orientation:" + orientation);
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            case 90:
                Log.i(TAG, "onOrientationChanged orientation:" + orientation);
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                break;
            case 180:
                Log.i(TAG, "onOrientationChanged orientation:" + orientation);
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                break;
            case 270:
                Log.i(TAG, "onOrientationChanged orientation:" + orientation);
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
        }
    }
}
