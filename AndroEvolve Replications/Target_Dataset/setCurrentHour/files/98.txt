package com.example.tagweck.tagweck;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.lang.*;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.widget.Button;
import android.widget.TextView;
import android.view.View;
import android.widget.TimePicker;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {

    public static TimePicker timePicker;
    public static ToggleButton alarm_toggle;
    public static Button button_ok;

    public static TextView seconds_text;
    public static TextView minutes_text;
    public static TextView hours_text;
    public static TextView weckzeit;

    public static int seconds;
    public static int minutes;
    public static int hours;

    static final String seconds_string = "Sekunden";
    static final String minutes_string = "Minuten";
    static final String hours_string = "Stunden";
    static final String hour_string = "Stunde";
    static final String minute_string = "Minute";

    public static int hour;
    public static int minute;

    Calendar cWeckzeit = new GregorianCalendar();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        seconds_text = (TextView)findViewById(R.id.seconds_text);
        minutes_text = (TextView)findViewById(R.id.minutes_text);
        hours_text = (TextView)findViewById(R.id.hours_text);
        weckzeit = (TextView)findViewById(R.id.weckzeit);
        timePicker = (TimePicker)findViewById(R.id.timePicker);
        alarm_toggle = (ToggleButton)findViewById(R.id.alarm_toogle);
        button_ok = (Button)findViewById(R.id.button_ok);

        timePicker.setIs24HourView(true);
        timePicker.setCurrentHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));

        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);

        seconds_text.setText(Integer.toString(prefs.getInt(seconds_string, getseconds())));
        minutes_text.setText(Integer.toString(prefs.getInt(minutes_string, getminutes())));
        hours_text.setText(Integer.toString(prefs.getInt(hours_string, gethours())));
        hour = prefs.getInt(hour_string, 0);
        minute = prefs.getInt(minute_string, 0);
        cWeckzeit.set(Calendar.HOUR_OF_DAY, hour);
        cWeckzeit.set(Calendar.MINUTE, minute);
        weckzeit.setText(cWeckzeit.get(Calendar.HOUR_OF_DAY) + ":" + cWeckzeit.get(Calendar.MINUTE));
    }

    public void onPause() {
        super.onPause();

        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(seconds_string, seconds);
        editor.putInt(minutes_string, minutes);
        editor.putInt(hours_string, hours);
        editor.putInt(hour_string, hour);
        editor.putInt(minute_string, minute);
        editor.putBoolean("initialisiert", true);
        editor.commit();
    }

    private int getseconds() {
        Calendar c = Calendar.getInstance();
        int sec = c.get(Calendar.SECOND);
        seconds = sec;
        return sec;
    }

    private int getminutes() {
        Calendar c = Calendar.getInstance();
        int min = c.get(Calendar.MINUTE);
        minutes = min;
        return min;
    }

    private int gethours() {
        Calendar c = Calendar.getInstance();
        int h = c.get(Calendar.HOUR_OF_DAY);
        hours = h;
        return h;
    }

    public void refresh(View view) {
        seconds_text.setText(Integer.toString(getseconds()));
        minutes_text.setText(Integer.toString(getminutes()));
        hours_text.setText(Integer.toString(gethours()));
    }

    public void weckzeit_setzen(View view) {
        hour = timePicker.getCurrentHour();
        minute = timePicker.getCurrentMinute();
        cWeckzeit.set(Calendar.HOUR_OF_DAY, hour);
        cWeckzeit.set(Calendar.MINUTE, minute);
        weckzeit.setText(cWeckzeit.get(Calendar.HOUR_OF_DAY) + ":" + cWeckzeit.get(Calendar.MINUTE));
    }
}
