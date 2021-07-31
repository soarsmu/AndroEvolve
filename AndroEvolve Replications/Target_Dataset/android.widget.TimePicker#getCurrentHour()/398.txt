package com.example.maknaetest.createtimer;

import android.os.Build;
import android.widget.TimePicker;

public final class TimePickerUtil {
    // 타임피커를 통해 값을 얻어올때 사용하는 메소드의 버전을 결정해주는 클래스
    // 하위 버전에서 사용하는 메서드와 상위 버전에서 사용하는 메서드가 달라서!
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
}
