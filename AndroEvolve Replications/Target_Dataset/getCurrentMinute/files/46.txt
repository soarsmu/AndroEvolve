package com.example.john.oneway.Filter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.john.oneway.Driver;
import com.example.john.oneway.R;

public class TimeActivity extends AppCompatActivity implements Serializable {
    public static final String EXTRA = "TimeExtra";

    TimePicker timePicker;
    Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time);

        // Time Picker
        timePicker=(TimePicker)findViewById(R.id.timePicker);
       timePicker.setIs24HourView(true );


    }

    public void btnClick(View v){
        NumberFormat formatter = new DecimalFormat("00");
        Toast.makeText(getBaseContext(), "Time selected:" +timePicker.getCurrentHour() +
                ":" + formatter.format(timePicker.getCurrentMinute()),Toast.LENGTH_SHORT).show();

        Driver driver = new Driver();

        driver.setmTimePicker("Service time is: " + String.valueOf(timePicker.getCurrentHour()) + ":" + (String.valueOf(formatter.format(timePicker.getCurrentMinute()))));

        Intent returnIntent = new Intent();

        returnIntent.putExtra(EXTRA,  driver);
       // returnIntent.putExtra(EXTRA,timePicker.getCurrentMinute());
        setResult(RESULT_OK, returnIntent);
        finish();



    }
}

