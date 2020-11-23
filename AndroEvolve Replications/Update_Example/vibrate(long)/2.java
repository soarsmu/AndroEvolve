package com.ankhrom.coinmarketcap.common;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;

/**
 * Created by R' on 1/2/2018.
 */

public final class AppVibrator {

    private static final int DURATION = 50;
    private static final int AMPLITUDE = 175;

    public static void itemActivated(@NonNull Context context) {

        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        if (vibrator != null && vibrator.hasVibrator()) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O && vibrator.hasAmplitudeControl()) {
                long milliseconds = DURATION;
            	int amplitude = AMPLITUDE;
            	VibrationEffect effect = VibrationEffect.createOneShot(milliseconds, amplitude);
                vibrator.vibrate(effect);
            } else {
                long milliseconds = DURATION;
                vibrator.vibrate(milliseconds);
            }
        }
    }
}
