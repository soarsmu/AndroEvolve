package com.example.thjen.reminderdemo;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Date;

@RequiresApi(api = Build.VERSION_CODES.N)
public class Alarm extends AppCompatActivity {

    TimePicker timePicker;
    TextView tv;
    Button btDone;
    Button btStop,
            btTime, btDate;
    DatePicker datePicker;

    AlarmManager alarmManager;
    PendingIntent pendingIntent;
    public static int position;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm);

        timePicker = (TimePicker) findViewById(R.id.timePicker);
        tv = (TextView) findViewById(R.id.tv);
        btDone = (Button) findViewById(R.id.btDone);
        btStop = (Button) findViewById(R.id.btStop);
        btTime = (Button) findViewById(R.id.btTime);
        btDate = (Button) findViewById(R.id.btDate);
        datePicker = (DatePicker) findViewById(R.id.datePicker);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        final Intent intent = new Intent(Alarm.this, AlarmReceiver.class);
        final Calendar calendar = Calendar.getInstance();
        btDone.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {

                try {

//                    calendar.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
//                    calendar.set(Calendar.MINUTE, timePicker.getCurrentMinute());
//                    calendar.set(Calendar.DATE, datePicker.getDayOfMonth());
//                    calendar.set(Calendar.MONTH, datePicker.getMonth());
//                    calendar.set(Calendar.YEAR, datePicker.getYear());

//                    int Year = getData.getIntExtra("year", 2017);
//                    int Month = getData.getIntExtra("month", 13);
//                    int Day = getData.getIntExtra("day", 3);
//                    int Hourr = getData.getIntExtra("hour", 3);
//                    int Minutee = getData.getIntExtra("minute", 5);

//                    calendar.set(Year, Month, Day, Hourr, Minutee);
                    calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), timePicker.getCurrentHour(), timePicker.getMinute());
                    //calendar.set(DateDialog.datePicker2.getYear(),DateDialog.datePicker2.getMonth(), DateDialog.datePicker2.getDayOfMonth(), TimeDialog.timePicker2.getCurrentHour(), TimeDialog.timePicker2.getCurrentMinute());

                    int hour = timePicker.getCurrentHour();
                    int minute = timePicker.getCurrentMinute();

                    String Hour = String.valueOf(hour);
                    String Minute = String.valueOf(minute);

                    if (minute < 10) {
                        Minute = "0" + Minute;
                    }

                    int date = datePicker.getDayOfMonth();
                    int month = datePicker.getMonth();
                    Toast.makeText(Alarm.this, String.valueOf(date) + " " + String.valueOf(month)  , Toast.LENGTH_SHORT).show();
                    Intent intent1 = getIntent();
                    position = intent1.getExtras().getInt("position");

                    intent.putExtra("extra", "on");

                    pendingIntent = PendingIntent.getBroadcast(Alarm.this, position, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

                    tv.setText("Time Current: " + Hour + ":" + Minute);

                } catch (Exception e ) {}

            }
        });

        btStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    /** Flag cancel current **/
                    pendingIntent = PendingIntent.getBroadcast(Alarm.this, position ,intent, PendingIntent.FLAG_CANCEL_CURRENT);
                    pendingIntent.cancel();
                    intent.putExtra("extra", "off");
                    sendBroadcast(intent);
                } catch (Exception e) {}

            }
        });

    }

}
