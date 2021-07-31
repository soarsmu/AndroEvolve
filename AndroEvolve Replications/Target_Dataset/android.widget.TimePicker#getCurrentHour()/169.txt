package com.acadgild.dateandtimeapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

public class TimeActivity extends AppCompatActivity {
    Button timeButton;
    TimePicker timer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        timeButton=(Button) findViewById(R.id.getTime);
        timer=(TimePicker)findViewById(R.id.timePicker);

        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean pm=timer.getCurrentHour()>12;

                Snackbar.make(v,"Time: "+(timer.getCurrentHour()%12)+":"+timer.getCurrentMinute()+" "+(pm?"PM":"AM"),Snackbar.LENGTH_LONG).show();
            }
        });

    }

}
