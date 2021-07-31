package com.example.tuan.alarm;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TimePicker;

import java.util.ArrayList;
public class AddActivity extends AppCompatActivity {
    //constants
    public static final String ADD_HOUR = "ADD_HOUR";
    public static final String ADD_MINUTE = "ADD_MINUTE";
    public static final String ADD_AMPM = "ADD_AMPM";
    public static final String ADD_BUNDLE = "ADD_BUNDLE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        Toolbar toolbar = findViewById(R.id.toolbar_add);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        add();
        //back to main activity when press "<"
        ImageView editBack = findViewById(R.id.add_back);
        editBack.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                }
        );
    }

    //add new item
    public void add(){
        final TimePicker timePicker = (TimePicker)findViewById(R.id.timepickerAdd);
        Button btnAdd = (Button)findViewById(R.id.btn_add);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String hour, minute, ampm;
                if (timePicker.getCurrentHour()>11){
                    hour = "" + (timePicker.getCurrentHour()-12);
                    ampm = "PM";
                } else {
                    hour = "" + timePicker.getCurrentHour();
                    ampm = "AM";
                }
                if (timePicker.getCurrentMinute()<10){
                    minute = "0" + timePicker.getCurrentMinute();
                } else {
                    minute = "" + timePicker.getCurrentMinute();
                }
               if (hour.equals("0") && ampm.equals("PM") ){
                hour = ""+12;
                }
                Intent intent = getIntent();
                Bundle bundle = new Bundle();
                bundle.putString(ADD_HOUR,hour);
                bundle.putString(ADD_MINUTE,minute);
                bundle.putString(ADD_AMPM, ampm);
                intent.putExtra(ADD_BUNDLE, bundle);
                setResult(MainActivity.RESULT_CODE_ADD,intent);
                finish();
            }
        });
    }
}
