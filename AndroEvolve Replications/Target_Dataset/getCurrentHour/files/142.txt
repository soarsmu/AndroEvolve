package com.example.analia.apptest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.content.Intent;
import android.widget.TextView;
import android. widget.TimePicker;
import android.os.CountDownTimer;

public class TimerActivity extends AppCompatActivity {
    private boolean timerStart = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

    }

    public void doButtonClick(View e){
        TextView displayTime = (TextView) findViewById(R.id.textView2);
        TimePicker timePicker = (TimePicker) findViewById(R.id.timePicker);
        displayTime.setText(timeToString(timePicker));
    }

    private String timeToString(TimePicker tp){
        int currHour = tp.getCurrentHour();
        int currMinute = tp.getCurrentMinute();
        if(currMinute < 40){
            currMinute += 20;
        }
        else{
            int diff = 60 - currMinute;
            currHour += 1;
            currMinute = 20 - diff;
        }
        String currMinuteS = Integer.toString(currMinute);
        if(currHour > 12){
            currHour -= 12;
        }
        if(currMinuteS.length() < 2){
            currMinuteS = "0" + currMinuteS;
        }
        return Integer.toString(currHour) + ":" + currMinuteS;
    }

    /*
    public void onClick(View v) {
        TimePicker timePicker = (TimePicker) findViewById(R.id.timePicker);
        int futrHour = timePicker.getCurrentHour();
        int futrMinute = timePicker.getCurrentMinute();
        if(futrMinute < 40){
            futrMinute += 20;
        }
        else{
            int diff = 60 - futrMinute;
            futrHour += 1;
            futrMinute = 20 - diff;
        }
        while(timePicker.getCurrentHour() != futrHour || timePicker.getCurrentMinute() != futrMinute){
            if(timePicker.getCurrentMinute() < 59){
                timePicker.setCurrentMinute(timePicker.getCurrentMinute() + 1);
            }
            else{
                timePicker.setCurrentMinute(0);
                timePicker.setCurrentHour(timePicker.getCurrentHour() + 1);
            }
        }

    }*/

    public void onClick(View v) {
        CountDownTimer timer = new myCountDownTimer(120000, 60000);
        if(!timerStart){
            timer.start();
            timerStart = true;
        }
        else{
            timer.cancel();
            timerStart = false;
        }

    }
    public class myCountDownTimer extends CountDownTimer{
        public myCountDownTimer(long start, long interval){
            super(start, interval);
        }
        @Override
        public void onTick(long millisUntilFinished) {
            TimePicker timePicker = (TimePicker) findViewById(R.id.timePicker);
            if(timePicker.getCurrentMinute() < 59){
                timePicker.setCurrentMinute(timePicker.getCurrentMinute() + 1);
            }
            else{
                timePicker.setCurrentMinute(0);
                timePicker.setCurrentHour(timePicker.getCurrentHour() + 1);
            }
        }

        @Override
        public void onFinish() {
            TextView finish = (TextView) findViewById(R.id.textView3);
            finish.setText("Done!");
        }
    }
}

