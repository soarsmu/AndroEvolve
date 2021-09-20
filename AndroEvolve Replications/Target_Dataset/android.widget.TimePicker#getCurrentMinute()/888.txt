package id.ac.ui.cs.mobileprogramming.kace.kcclock.alarm.util;

import android.os.Build;
import android.widget.TimePicker;

/**
 * Based on learntodroid's https://gist.github.com/learntodroid/c1637794bebb92241fafe2d183a3349b#file-timepickerutil-java */
public class TimePickerUtil {

    public static int getTimePickerHour(TimePicker tp) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return tp.getHour();
        } else {
            return tp.getCurrentHour();
        }
    }

    public static int getTimePickerMinute(TimePicker tp) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return tp.getMinute();
        } else {
            return tp.getCurrentMinute();
        }
    }

    public static void setTimePickerHour(TimePicker tp, int hour) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tp.setHour(hour);
        } else {
            tp.setCurrentHour(hour);
        }
    }

    public static void setTimePickerMinute(TimePicker tp, int minute) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tp.setMinute(minute);
        } else {
            tp.setCurrentMinute(minute);
        }
    }
}
