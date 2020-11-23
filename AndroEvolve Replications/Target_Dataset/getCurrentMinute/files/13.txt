package com.example.alarmapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    TextView timetv;
    TimePicker timePicker;
    Button alarm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timePicker = findViewById(R.id.timepk);
        alarm = findViewById(R.id.btnAlarm);
        timetv = findViewById(R.id.timetv);


alarm.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(timePicker.getCurrentHour(),timePicker.getCurrentMinute(),0);

        Toast.makeText(MainActivity.this,"Alarm set at :"+timePicker.getCurrentHour()+":"+timePicker.getCurrentMinute(),Toast.LENGTH_SHORT).show();

        Intent alarmIntent = new Intent(MainActivity.this, MyAlarmBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 8092, alarmIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        String ct = "Alarm set at :"+timePicker.getCurrentHour()+":"+timePicker.getCurrentMinute();
        timetv.setText(ct);

    }
});

    }
}
