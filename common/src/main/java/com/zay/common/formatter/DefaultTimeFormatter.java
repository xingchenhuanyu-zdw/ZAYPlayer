package com.zay.common.formatter;

/**
 * Created by Zdw on 2021/06/18 13:50
 */

public class DefaultTimeFormatter implements ITimeFormatter {
    @Override
    public String getFormattedValue(long millSeconds) {
        long seconds = millSeconds / 1000;
        String result = "";
        long min, second;
        min = seconds / 60;
        second = seconds - min * 60;
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
