package com.example.nitinsharma.medicalalarmremainder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class AddAlarm extends AppCompatActivity {
    TimePicker timePicker;
    ImageButton back;
    Button done;
    EditText title;
    String alarmTitle;
    SQLiteDatabase database;
    AlarmDatabase alarmDatabase;
    AlarmManager alarmManager;
    PendingIntent pendingIntent;
    List list;
    int position;
    private Calendar calendar;
    Intent i;
    boolean update;
    ArrayList<Data> arrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        Drawable background = getResources().getDrawable(R.drawable.bg);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(android.R.color.transparent));
        window.setNavigationBarColor(getResources().getColor(android.R.color.transparent));
        window.setBackgroundDrawable(background);
        setContentView(R.layout.activity_add_alarm);
        timePicker = findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);
        back = findViewById(R.id.back);
        title = findViewById(R.id.title);
        back = findViewById(R.id.back);
        done = findViewById(R.id.done);

        arrayList = new ArrayList<>();
        list = new List(getBaseContext(), arrayList);


        alarmDatabase = new AlarmDatabase(getApplicationContext(), "Alarm Database", null, 1);
        database = alarmDatabase.getWritableDatabase();
        database = alarmDatabase.getReadableDatabase();
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddAlarm.super.onBackPressed();
            }
        });

        Intent i = getIntent();
        if (i.hasExtra("title") && i.hasExtra("hour") && i.hasExtra("minute") && i.hasExtra("position")) {
            Log.v("intent", "hit");
            title.setText(i.getStringExtra("title").trim());
            timePicker.setCurrentHour(i.getIntExtra("hour", 0));
            timePicker.setCurrentMinute(i.getIntExtra("minute", 0));
            position = i.getIntExtra("position", 0);
            update = true;

        }

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarmTitle = title.getText().toString().trim();
                if (alarmTitle.isEmpty()) {
                    title.setError("Pls Enter Title!!");
                } else {

                    calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
                    calendar.set(Calendar.MINUTE, timePicker.getCurrentMinute());
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);

                    long milliseconds = TimeUnit.SECONDS.toMillis(TimeUnit.HOURS.toSeconds(timePicker.getCurrentHour()) + TimeUnit.MINUTES.toSeconds(timePicker.getCurrentMinute()));
                    Log.v("millisecond", calendar.getTimeInMillis() + "M" + milliseconds);
                    long ab = calendar.getTimeInMillis();
                    int seconds = (int) (milliseconds / 1000) % 60;
                    int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
                    int hours = (int) ((milliseconds / (1000 * 60 * 60)) % 24);
                    Log.v("sec", "" + seconds);
                    Log.v("min", "" + minutes);
                    Log.v("hours", "" + hours);

                    if (update) {
                        ContentValues values = new ContentValues();
                        values.put("Name", alarmTitle);
                        values.put("Hour", timePicker.getCurrentHour());
                        values.put("Minute", timePicker.getCurrentMinute());
                        database.update("Alarm", values, "Column_Id=" + position, null);
                        Log.v("update", "" + position);

                    } else {
                        ContentValues values = new ContentValues();
                        values.put("Name", alarmTitle);
                        values.put("Hour", timePicker.getCurrentHour());
                        values.put("Minute", timePicker.getCurrentMinute());
                        values.put("Status", "true");
                        values.put("Time_Status", "true");
                        values.put("Title_Status", "true");
                        database.insert("Alarm", null, values);

                    }

                    Intent j = new Intent(getBaseContext(), AlarmReceiver.class);
                    pendingIntent = PendingIntent.getBroadcast(getBaseContext(), 1, j, PendingIntent.FLAG_UPDATE_CURRENT);
                    if (Build.VERSION.SDK_INT >= 23) {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    } else if (Build.VERSION.SDK_INT >= 19) {
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    } else {
                        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    }

                    if (update) {
                        Intent o = new Intent();
                        o.putExtra("Title", alarmTitle);
                        o.putExtra("Hour", timePicker.getCurrentHour());
                        o.putExtra("Min", timePicker.getCurrentMinute());
                        setResult(101, o);
                        finish();
                    } else {
                        Intent f = new Intent();
                        f.putExtra("Title", alarmTitle);
                        f.putExtra("Hour", timePicker.getCurrentHour());
                        f.putExtra("Min", timePicker.getCurrentMinute());
                        setResult(RESULT_OK, f);
                        finish();
                    }


                }
            }
        });

        list.onItemclick(new List.onClick() {
            @Override
            public void switchClick(int pos, int bc, boolean ab) {

                if (ab) {
                 /* Intent  i = new Intent(getBaseContext(), AlarmReceiver.class);
                    pendingIntent = PendingIntent.getBroadcast(getBaseContext(), 1, i, 0);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);*/


                } else {
                    Log.v("cancel", "hit");
                   /* alarmManager.cancel(pendingIntent);*/


                }


            }

            @Override
            public void itemClick(int pos) {

            }
        });


    }

    public void setIntent() {
        i = new Intent(getBaseContext(), AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(getBaseContext(), 1, i, 0);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);


    }
}
