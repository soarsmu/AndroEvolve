package com.example.nz.timepicker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    TimePicker timepicker;
    Button button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timepicker = (TimePicker) findViewById(R.id.timePicker);
        button = (Button) findViewById(R.id.btn);
        timepicker.setIs24HourView(true);
        timepicker.getCurrentHour();
        timepicker.getCurrentMinute();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"Current time : "+timepicker.getCurrentHour()+" : "+timepicker.getCurrentMinute(),Toast.LENGTH_SHORT).show();

            }
        });

    }
}
