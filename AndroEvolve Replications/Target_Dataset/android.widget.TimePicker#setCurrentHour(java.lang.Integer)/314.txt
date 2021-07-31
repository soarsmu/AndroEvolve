package com.alr.tasks;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

public class TimeActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.time);
        boolean f24 = DateFormat.is24HourFormat(getApplicationContext());
        final TimePicker t = (TimePicker)findViewById(R.id.timePicker);
        t.setIs24HourView(f24);
        Button b0 = (Button)findViewById(R.id.buttonTime0);
        Button b6 = (Button)findViewById(R.id.buttonTime6);
        Button b12 = (Button)findViewById(R.id.buttonTime12);
        Button b18 = (Button)findViewById(R.id.buttonTime18);
        if (f24) {
            b0.setText("00:00");
            b6.setText("06:00");
            b12.setText("12:00");
            b18.setText("18:00");
        } else {
            b0.setText("0:00 AM");
            b6.setText("6:00 AM");
            b12.setText("0:00 PM");
            b18.setText("6:00 PM");
        }
        MyTask ts = (MyTask)getIntent().getSerializableExtra("task");
        ts.setContext(getApplicationContext());
        readTs(ts);
    }

    public void bNowClick(View v) {
        TimePicker t = (TimePicker)findViewById(R.id.timePicker);
        Time tm = new Time();
        t.setCurrentHour(tm.hour);
        t.setCurrentMinute(tm.minute);
    }
    public void b0click(View v) {
        TimePicker t = (TimePicker)findViewById(R.id.timePicker);
        t.setCurrentHour(0);
        t.setCurrentMinute(0);
    }
    public void b60click(View v) {
        TimePicker t = (TimePicker)findViewById(R.id.timePicker);
        t.setCurrentHour(6);
        t.setCurrentMinute(0);
    }
    public void b12click(View v) {
        TimePicker t = (TimePicker)findViewById(R.id.timePicker);
        t.setCurrentHour(12);
        t.setCurrentMinute(0);
    }
    public void b18click(View v) {
        TimePicker t = (TimePicker)findViewById(R.id.timePicker);
        t.setCurrentHour(18);
        t.setCurrentMinute(0);
    }

    private void readTs(MyTask ts) {
        TimePicker t = (TimePicker)findViewById(R.id.timePicker);
        t.setCurrentHour(ts.hour);
        t.setCurrentMinute(ts.minute);
    }

    public void timeOkButtonClick(View v) {
        Intent intent = new Intent();
        TimePicker t = (TimePicker)findViewById(R.id.timePicker);
        intent.putExtra("hour", t.getCurrentHour());
        intent.putExtra("min", t.getCurrentMinute());
        setResult(RESULT_OK, intent);
        finish();
    }
}
