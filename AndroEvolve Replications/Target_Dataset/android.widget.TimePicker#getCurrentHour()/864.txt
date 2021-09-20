package com.example.homework009;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

public class TimePickerActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tv_time;
    private TimePicker tp_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_picker);

        tv_time = (TextView) findViewById(R.id.tv_time);
        tp_time = findViewById(R.id.tp_time);
        findViewById(R.id.btn_ok).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_ok) {
            String desc = String.format("您选择的时间是：%d时%d分", tp_time.getCurrentHour(), tp_time.getCurrentMinute());
            tv_time.setText(desc);
        }
    }
}