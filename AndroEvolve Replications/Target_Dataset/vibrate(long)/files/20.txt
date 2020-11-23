package ls.example.t.zero2line.util;

import android.content.Context;
import android.os.Vibrator;
import android.support.annotation.RequiresPermission;

import static android.Manifest.permission.VIBRATE;

/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 2016/09/29
 *     desc  : utils about vibrate
 * </pre>
 */
public final class VibrateUtils {

    private static android.os.Vibrator vibrator;

    private VibrateUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * Vibrate.
     * <p>Must hold {@code <uses-permission android:name="android.permission.VIBRATE" />}</p>
     *
     * @param milliseconds The number of milliseconds to vibrate.
     */
    @android.support.annotation.RequiresPermission(VIBRATE)
    public static void vibrate(final long milliseconds) {
        android.os.Vibrator vibrator = getVibrator();
        if (vibrator == null) return;
        vibrator.vibrate(milliseconds);
    }

    /**
     * Vibrate.
     * <p>Must hold {@code <uses-permission android:name="android.permission.VIBRATE" />}</p>
     *
     * @param pattern An array of longs of times for which to turn the vibrator on or off.
     * @param repeat  The index into pattern at which to repeat, or -1 if you don't want to repeat.
     */
    @android.support.annotation.RequiresPermission(VIBRATE)
    public static void vibrate(final long[] pattern, final int repeat) {
        android.os.Vibrator vibrator = getVibrator();
        if (vibrator == null) return;
        vibrator.vibrate(pattern, repeat);
    }

    /**
     * Cancel vibrate.
     * <p>Must hold {@code <uses-permission android:name="android.permission.VIBRATE" />}</p>
     */
    @android.support.annotation.RequiresPermission(VIBRATE)
    public static void cancel() {
        android.os.Vibrator vibrator = getVibrator();
        if (vibrator == null) return;
        vibrator.cancel();
    }

    private static android.os.Vibrator getVibrator() {
        if (vibrator == null) {
            vibrator = (android.os.Vibrator) Utils.getApp().getSystemService(android.content.Context.VIBRATOR_SERVICE);
        }
        return vibrator;
    }
}
