package com.cea.celibrary.utils;

import android.app.Activity;
import android.app.Service;
import android.os.Vibrator;

public class VibratorUtils {
    /**
     * long milliseconds ：震动的时长，单位是毫秒
     * long[] pattern ：自定义震动模式 。数组中数字的含义依次是[静止时长，震动时长，          * 静止时长，震动时长。。。]时长的单位是毫秒
     * boolean isRepeat ： 是否反复震动，如果是true，反复震动，如果是false，只震动一
     * 次
     */

    public static void Vibrate(final Activity activity, long milliseconds) {
        Vibrator vib = (Vibrator) activity
                .getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(milliseconds);
    }

    public static void Vibrate(final Activity activity, long[] pattern,
                               boolean isRepeat) {
        Vibrator vib = (Vibrator) activity
                .getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(pattern, isRepeat ? 1 : -1);
    }
}