package com.lixtracking.lt.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.lixtracking.lt.R;


public class SetCustomDateTime extends Activity implements DatePicker.OnDateChangedListener{
    private TimePicker timePicker = null;
    private DatePicker datePicker = null;
    private Button set = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.date_time_dialog);

        Intent intent = this.getIntent();
        String from = intent.getStringExtra("FROM");
        String to = intent.getStringExtra("TO");

        timePicker = (TimePicker)findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);
        datePicker = (DatePicker)findViewById(R.id.datePicker);

        if(intent.getBooleanExtra("START",false)) {
            String y = from.substring(0,4);
            String m = from.substring(5,7);
            String d = from.substring(8,10);
            String h = from.substring(11,13);
            String min = from.substring(14,16);
            datePicker.init(Integer.parseInt(y), Integer.parseInt(m) - 1, Integer.parseInt(d), this);
            timePicker.setCurrentHour(Integer.parseInt(h));
            timePicker.setCurrentMinute(Integer.parseInt(min));
        }else if(intent.getBooleanExtra("END",false)) {
            String y = to.substring(0,4);
            String m = to.substring(5,7);
            String d = to.substring(8,10);
            String h = to.substring(11,13);
            String min = to.substring(14,16);
            datePicker.init(Integer.parseInt(y),Integer.parseInt(m)-1,Integer.parseInt(d),this);
            timePicker.setCurrentHour(Integer.parseInt(h));
            timePicker.setCurrentMinute(Integer.parseInt(min));
        }
        set = (Button)findViewById(R.id.button);
        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();

                String year = Integer.toString(datePicker.getYear());
                String month = Integer.toString(datePicker.getMonth());
                if(datePicker.getMonth() < 10) {
                    month = "0" + Integer.toString(datePicker.getMonth() + 1);
                }

                String day = Integer.toString(datePicker.getDayOfMonth());
                if(datePicker.getDayOfMonth() < 10) {
                    day = "0" + Integer.toString(datePicker.getDayOfMonth());
                }

                String hour = Integer.toString(timePicker.getCurrentHour());
                if(timePicker.getCurrentHour() < 10) {
                    hour = "0" + Integer.toString(timePicker.getCurrentHour());
                }

                String minute = Integer.toString(timePicker.getCurrentMinute());
                if(timePicker.getCurrentMinute() < 10) {
                    minute = "0" + Integer.toString(timePicker.getCurrentMinute());
                }
                String second = "00";

                String result = year + "/" + month + "/" + day + " " + hour + ":" + minute + ":" + second;
                intent.putExtra("DATE_TIME",result);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }

    @Override
    public void onDateChanged(DatePicker datePicker, int i, int i2, int i3) {

    }
}
