package com.dodsoneng.scheduler;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private String TAG = "DESCHED.MainAct";
    Button  startBtn;
    Button  stopBtn;
    Context _this;
    final MyAlarm _alarm = new MyAlarm();

    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d (TAG, "onCreate()");
        setContentView(R.layout.activity_main);

        _this = this;

        final TimePicker bgnTimeView = (TimePicker) findViewById(R.id.timePicker1);
        final TimePicker endTimeView = (TimePicker) findViewById(R.id.timePicker2);

        startBtn = (Button) findViewById(R.id.button1);
        stopBtn = (Button) findViewById(R.id.button2);

        _alarm.setOnReceiveListener(new MyAlarm.onReceiveListener() {
            public void doAction () {
                Log.d (TAG, "MyAlarm.onReceiveListener().doAction");
            }
            /* FOR MY EXPERIENCE ONLY or FUTURE USE IF NEEDED (S.ENG)
            public void doAction (int arg) {
                Log.d (TAG, "MyAlarm.onReceiveListener().doAction: arg="+arg);
            }
            */
        });

        startBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                long bgnTimeMS = _alarm.getTimeInMilisecs(bgnTimeView.getCurrentHour(), bgnTimeView.getCurrentMinute() );
                long endTimeMS   = _alarm.getTimeInMilisecs(endTimeView.getCurrentHour(), endTimeView.getCurrentMinute() );
                long dt = bgnTimeMS-System.currentTimeMillis();

                Log.d (TAG, "onClick(start)");
                Log.d (TAG, "onClick(begin time): " + bgnTimeView.getCurrentHour() + ":" + bgnTimeView.getCurrentMinute());
                Log.d (TAG, "onClick(end time)  : " + endTimeView.getCurrentHour() + ":" + endTimeView.getCurrentMinute());
                Log.d (TAG, "onClick(begin time): tmsecs=" + bgnTimeMS + " systime=" + System.currentTimeMillis() + " dt="+ dt);

                _alarm.setAlarm(_this, bgnTimeMS, endTimeMS);
            }
        });

        stopBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d (TAG, "onClick(stop)");
                // TODO Auto-generated method stub
                _alarm.stopAlarm();
            }
        });

    }
}
