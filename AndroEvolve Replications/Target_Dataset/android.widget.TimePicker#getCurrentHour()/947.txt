package com.learntodroid.simplealarmclock.createalarm;

import android.os.Build;
import android.widget.TimePicker;

public final class TimePickerUtil {
    public static int getTimePickerHour(TimePicker tp) {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) ? tp.getHour() : tp.getCurrentHour();

    }

    public static int getTimePickerMinute(TimePicker tp) {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) ? tp.getMinute() : tp.getCurrentMinute();
    }
}
