package com.zay.common.formatter;

/**
 * Created by Zdw on 2021/06/18 13:50
 */

public class DefaultTimeFormatter implements ITimeFormatter {
    @Override
    public String getFormattedValue(long millSeconds) {
        long seconds = millSeconds / 1000;
        String result = "";
        long hour, min, second;
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
