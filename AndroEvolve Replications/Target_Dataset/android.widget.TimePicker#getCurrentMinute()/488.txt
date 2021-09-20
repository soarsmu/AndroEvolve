package com.vicdoz.alarmsheduler;

import android.content.Intent;
import android.provider.AlarmClock;
import android.widget.TimePicker;

public class Scheduler extends Settings {
    private int from_hour;
    private int from_minute;
    private int to_hour;
    private int to_minute;

    private int max_alarms = 15;
    private  int interval = 3;


    public Scheduler(TimePicker from_timePicker, TimePicker to_timePicker) {
        from_hour = from_timePicker.getCurrentHour();
        from_minute = from_timePicker.getCurrentMinute();
        to_hour = to_timePicker.getCurrentHour();
        to_minute = to_timePicker.getCurrentMinute();
    }


    public boolean schedule() {

        int minutes = from_minute;
        int hour =from_hour;
        String message = "";
        for (int i = 0; i < max_alarms; i++, minutes += interval) {
            if (minutes >= 60) {
                minutes = minutes - 60;
                hour += 1;
            }

            if (hour == to_hour && minutes >= to_minute) {
                break;
            }
            createAlarm(message, hour, minutes);
        }
        return true;

    }

    protected void createAlarm(String message, int hour, int minutes) {
        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM)
                .putExtra(AlarmClock.EXTRA_MESSAGE, message)
                .putExtra(AlarmClock.EXTRA_HOUR, hour)
                .putExtra(AlarmClock.EXTRA_MINUTES, minutes);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}