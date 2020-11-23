package com.example.guc200.meet;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;


/**
 * Created by guc200 on 5/12/2017.
 */


public class AddEventActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addevent_activity);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abs_layout);
        Button button = (Button) findViewById(R.id.action_bar_forward);
        button.setVisibility(View.GONE);
    }

    public void closeEvent(View view){

    }


    public void pickDate(View view){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.your_placeholder, new dateFragment()).addToBackStack(null);
        ft.commit();
    }


    public void pickTime(View view){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.your_placeholder, new timeFragment()).addToBackStack(null);
        ft.commit();
    }

    public void confirmDate(View view){
        String dayOfWeek = "";
        TextView dateTextField;
        DatePicker datePicker;

        datePicker = (DatePicker) findViewById(R.id.datePicker);
        dateTextField= (TextView) findViewById(R.id.dateTextField);

        Calendar cal = new GregorianCalendar(datePicker.getYear(),datePicker.getMonth(),datePicker.getDayOfMonth());
        String month = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        String weekDay = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
        String dayOfMonth = String.valueOf(cal.get(Calendar.DATE));
        String year = String.valueOf(cal.get(Calendar.YEAR));

        dateTextField.setText(weekDay+", "+month+ " " +dayOfMonth+" of "+year);
        getSupportFragmentManager().popBackStack();

    }

    public void confirmTime( View view){
        TimePicker timePicker;
        TextView timeTextField;
        int hour;
        int minute;
        String AMorPM;

        timeTextField= (TextView) findViewById(R.id.timeTextField);
        timePicker = (TimePicker) findViewById(R.id.timePicker);

        if (Build.VERSION.SDK_INT >= 23) {
            int tempHour = timePicker.getHour();
            if (timePicker.getHour() >= 12){
                tempHour = tempHour - 12;
            }
            else if (timePicker.getHour() == 12 || timePicker.getHour() == 0){
                tempHour = 12;
            }
            if (timePicker.getHour() >= 12){
                AMorPM = "PM";
            }
            else{
                AMorPM = "AM";
            }
            hour = tempHour;
            minute = timePicker.getMinute();
        }
        else {
            int tempHour = timePicker.getCurrentHour();
            if (timePicker.getCurrentHour() > 12){
                tempHour = tempHour - 12;
            }
            else if (timePicker.getCurrentHour() == 12 || timePicker.getCurrentHour() == 0){
                tempHour = 12;
            }

            if (timePicker.getCurrentHour() >= 12){
                AMorPM = "PM";
            }
            else{
                AMorPM = "AM";
            }
            hour = tempHour;
            minute = timePicker.getCurrentMinute();
        }



        timeTextField.setText(String.format("%02d:%02d %s", hour, minute,AMorPM ));

        //timeTextField.setText(hour + ":"+ minute+" "+AMorPM);
        getSupportFragmentManager().popBackStack();

    }

    public void closeDatePicker(View view){
        getSupportFragmentManager().popBackStack();
    }

}



