package com.lnt.chthp00109.servicesapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class Sceduletask extends AppCompatActivity {
    SaveLogFiles logFiles = new SaveLogFiles();
    private static final String TAG = Sceduletask.class.getSimpleName();
    TimePicker timePicker;

    String amorpm = "";
    String times = "";

    int TimES;

    TextView scdullertime;
    Switch disableclock;

    PendingIntent pi;
    Intent pendingintent;

    AlarmManager am;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sceduletask);
        timePicker = findViewById(R.id.TimePicker);
        disableclock = findViewById(R.id.Disableclock);
        am = (AlarmManager) getApplicationContext().getSystemService(ALARM_SERVICE);
        pendingintent = new Intent(this, MyAlarm.class);

        scdullertime = findViewById(R.id.scheduletime);
        pi = PendingIntent.getBroadcast(this, 0, pendingintent, PendingIntent.FLAG_ONE_SHOT);

        disableclock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Log.d("ScedulerTask", "Desableing the alarm");
                    disablealarm();
                }
            }
        });

    }

    public void SetAlarm(View view) {
        Calendar calendar = Calendar.getInstance();
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            logFiles.Senddata(TAG + "-->Api level  >= 23!");
            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                    timePicker.getHour(), timePicker.getMinute(), 0);
            amorpm = timePicker.getHour() >= 12 ? "PM" : "AM";

            times = String.valueOf(TimES = timePicker.getHour() > 12 ? timePicker.getHour() - 12 :
                    timePicker.getHour()) + ":" + String.valueOf(timePicker.getMinute() < 10 ? String.valueOf
                    ("0" + timePicker.getMinute()) : timePicker.getMinute()) + " " + amorpm;
        } else {
            logFiles.Senddata(TAG + "-->:API LESS THAN 23!");

            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                    timePicker.getCurrentHour(), timePicker.getCurrentMinute(), 0);
            amorpm = timePicker.getCurrentHour() >= 12 ? "PM" : "AM";
            times = String.valueOf(TimES = timePicker.getCurrentHour() > 12 ? timePicker.getCurrentHour() - 12 :
                    timePicker.getCurrentHour()) + String.valueOf(timePicker.getCurrentMinute() < 10 ? String.valueOf
                    ("0" + timePicker.getCurrentMinute()) : timePicker.getCurrentMinute()) + " " + amorpm;
        }
        setTask(calendar.getTimeInMillis(), times);
    }

    public void setTask(long time, String s) {
        //getting the alarm manager
        // am.setTime(time);
        am.setRepeating(AlarmManager.RTC, time, AlarmManager.INTERVAL_DAY, pi);
        logFiles.Senddata(TAG + "-->Alarmset using alarmmanager!");
        scdullertime.setText(s);
    }

    public void disablealarm() {
        if (am != null) {
            am.cancel(pi);
            scdullertime.setText("--:--");
            logFiles.Senddata(TAG + "-->canceled alarm!");
            Log.d("SceduleTask", "Disable successfully");
        } else
            Toast.makeText(this, "NO alarm is set", Toast.LENGTH_SHORT).show();
    }

}

