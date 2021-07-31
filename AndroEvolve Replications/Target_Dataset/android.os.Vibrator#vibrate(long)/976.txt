package com.bon.util;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.os.Vibrator;

import com.bon.logger.Logger;

/**
 * Created by dangpp on 9/11/2017.
 */

public class VibratorUtils {
    private static final String TAG = VibratorUtils.class.getSimpleName();

    // const
    private static final int VIBRATE_TIME = 1000;
    private static Vibrator vibrator = null;

    private static Vibrator getInstance(Context context) {
        if (vibrator == null) {
            synchronized (VibratorUtils.class) {
                if (vibrator == null) {
                    vibrator = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
                }
            }
        }

        return vibrator;
    }

    /**
     * Vibrate constantly in 1000ms
     *
     * @param context
     */
    public static void vibrate(Context context) {
        vibrate(context, VIBRATE_TIME);
    }

    /**
     * Vibrate constantly for the specified period of time.
     *
     * @param context
     * @param milliseconds
     */
    @SuppressLint("MissingPermission")
    public static void vibrate(Context context, long milliseconds) {
        try {
            Vibrator vibrator = getInstance(context);
            vibrator.vibrate(milliseconds);
        } catch (Exception e) {
            Logger.e(TAG, e);
        }
    }

    /**
     * Vibrate with a given pattern.
     *
     * @param context
     * @param pattern
     * @param repeat
     */
    @SuppressLint("MissingPermission")
    public static void vibrate(Context context, long[] pattern, int repeat) {
        try {
            Vibrator vibrator = getInstance(context);
            vibrator.vibrate(pattern, repeat);
        } catch (Exception e) {
            Logger.e(TAG, e);
        }
    }

    /**
     * cancel vibrate
     *
     * @param context
     */
    @SuppressLint("MissingPermission")
    public static void cancel(Context context) {
        try {
            Vibrator vibrator = getInstance(context);
            vibrator.cancel();
        } catch (Exception e) {
            Logger.e(TAG, e);
        }
    }
}
