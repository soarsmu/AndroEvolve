package com.nghiatl.common.view;

import android.os.Build;
import android.widget.TimePicker;

/**
 * Created by Nghia on 2/19/2018.
 */
public class TimePickerUtil {
    public static void SetHour(TimePicker view, int hour) {
        if (Build.VERSION.SDK_INT < 23 )
            view.setCurrentHour(hour);
        else
            view.setHour(hour);
    }

    public static void SetMinute(TimePicker view, int minute) {
        if (Build.VERSION.SDK_INT < 23 )
            view.setCurrentMinute(minute);
        else
            view.setMinute(minute);
    }

    public static int GetMinute(TimePicker view) {
        if (Build.VERSION.SDK_INT < 23 )
            return view.getCurrentMinute();
        return view.getMinute();
    }

    public static int GetHour(TimePicker view) {
        if (Build.VERSION.SDK_INT < 23 )
            return view.getCurrentHour();
        return view.getHour();
    }
}
