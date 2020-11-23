package com.mycompany.workpercent;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.Calendar;

public class Preferences extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        TimePicker timePicker1 = (TimePicker) findViewById(R.id.timePicker1);
        TimePicker timePicker2 = (TimePicker) findViewById(R.id.timePicker2);
        EditText wageText = (EditText) findViewById(R.id.wage);
        SharedPreferences shiftDetails = getSharedPreferences("shiftDetails", MODE_PRIVATE);
        DecimalFormat df = new DecimalFormat("#.00");

        int begHour = shiftDetails.getInt("beginHour", Calendar.HOUR);
        int begMin = shiftDetails.getInt("beginMinute", Calendar.MINUTE);
        int endHour = shiftDetails.getInt("endHour", Calendar.HOUR);
        int endMin = shiftDetails.getInt("endMinute", Calendar.MINUTE);
        float wage = shiftDetails.getFloat("hourlyWage", 1);
        timePicker1.setCurrentHour(begHour);
        timePicker1.setCurrentMinute(begMin);
        timePicker2.setCurrentHour(endHour);
        timePicker2.setCurrentMinute(endMin);
        wageText.setText("" + df.format((double) wage));
    }

    public void calculateIntent (View view)
    {
        SharedPreferences shiftDetails = getSharedPreferences("shiftDetails", MODE_PRIVATE);
        EditText wage = (EditText) findViewById(R.id.wage);
        TimePicker beg = (TimePicker) findViewById(R.id.timePicker1);
        TimePicker end = (TimePicker) findViewById(R.id.timePicker2);
        SharedPreferences.Editor edit = shiftDetails.edit();
        edit.clear();
        System.out.println("beg: " + beg.getCurrentHour() + " end: " + end.getCurrentHour());
        edit.putInt("beginHour", beg.getCurrentHour());
        edit.putInt("beginMinute", beg.getCurrentMinute());
        edit.putInt("endHour", end.getCurrentHour());
        edit.putInt("endMinute", end.getCurrentMinute());
        if(wage.getText().toString().equals(""))
        {
            edit.putFloat("hourlyWage", 5);
        }
        else
        {
            edit.putFloat("hourlyWage", Float.valueOf(wage.getText().toString()));
        }
        edit.commit();
        Toast.makeText(getApplicationContext(), "Shift details are saved..", Toast.LENGTH_LONG).show();
        Intent in = new Intent(getApplicationContext(), DisplayActivity.class);
        startActivity(in);
    }
}
