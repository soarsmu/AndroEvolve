package com.example.alarm_app;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    TextView timetve;
    TimePicker timepicker;
    Button alarm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        timepicker=findViewById(R.id.timePicker);

        alarm=findViewById(R.id.button1);
        timetve = findViewById(R.id.timetv);

        alarm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                cal.set(timepicker.getCurrentHour(),timepicker.getCurrentMinute(),00);

                Toast.makeText(MainActivity.this,"Alarm set at:"+timepicker.getCurrentHour()+":"+timepicker.getCurrentMinute(),Toast.LENGTH_LONG).show();

                Intent intent = new Intent(MainActivity.this,Mybroadcastreceiver.class);
                PendingIntent pi = PendingIntent.getBroadcast(MainActivity.this,1234,intent,PendingIntent.FLAG_ONE_SHOT);
                AlarmManager amp = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                amp.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);

                String currentTime="Alarm Set: "+timepicker.getCurrentHour()+":"+timepicker.getCurrentMinute();
                timetve.setText(currentTime);

            }
        });
    }
}