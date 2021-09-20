package com.tutu.lib_fingerprint.utils;

import android.app.Activity;
import android.app.Service;
import android.os.Vibrator;

/**
 * @author ：wuchao on 2020/8/19 11：05
 * @describe ：震动处理
 * email: 519510228@qq.com
 */

public class ViUtils {

    //震动milliseconds毫秒
    public static void vibrate(final Activity activity, long milliseconds) {
        Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(milliseconds);
    }
    //以pattern[]方式震动
    public static void vibrate(final Activity activity, long[] pattern, int repeat){
        Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(pattern,repeat);
    }
    //取消震动
    public static void virateCancle(final Activity activity){
        Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
        vib.cancel();
    }

}
