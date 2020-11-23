package com.example.admin.mezamasi;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import static com.example.admin.mezamasi.Constants.FILE_NAME;

/**
 * Created by admin on 2017/02/28.
 */

public class AddActivity extends Activity {
    MyAlarmManager mam;
    TimePicker timePicker;
    EditText editText;
    private UrlDto dto;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        load();
        setContentView(R.layout.activity_add);
        editText = (EditText) findViewById(R.id.memo);
        editText.setText(dto.getMemo());
        timePicker = (TimePicker)findViewById(R.id.time_picker);
        timePicker.setIs24HourView(true);
        mam = new MyAlarmManager(this);

        Button button5 =(Button)findViewById(R.id.btn5);
        button5.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                    int newMinute = timePicker.getMinute() + 5;
                    if (newMinute > 59){
                        timePicker.setHour(timePicker.getHour() + 1);
                    }
                    timePicker.setMinute(newMinute);
                } else {
                    int newMinute = timePicker.getCurrentMinute() + 5;
                    if (newMinute > 59){
                        timePicker.setCurrentHour(timePicker.getCurrentHour() + 1);
                    }
                    timePicker.setCurrentMinute(newMinute);
                }
            }
        });

        Button button_5 =(Button)findViewById(R.id.btn_5);
        button_5.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                    int newMinute = timePicker.getMinute() - 5;
                    if (newMinute < 0){
                        timePicker.setHour(timePicker.getHour() - 1);
                    }
                    timePicker.setMinute(newMinute);
                } else {
                    int newMinute = timePicker.getCurrentMinute() - 5;
                    if (newMinute < 0){
                        timePicker.setCurrentHour(timePicker.getCurrentHour() - 1);
                    }
                    timePicker.setCurrentMinute(newMinute);
                }
            }
        });

        Button button15 =(Button)findViewById(R.id.btn15);
        button15.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                    int newMinute = timePicker.getMinute() + 15;
                    if (newMinute > 59){
                        timePicker.setHour(timePicker.getHour() + 1);
                    }
                    timePicker.setMinute(newMinute);
                } else {
                    int newMinute = timePicker.getCurrentMinute() + 15;
                    if (newMinute > 59){
                        timePicker.setCurrentHour(timePicker.getCurrentHour() + 1);
                    }
                    timePicker.setCurrentMinute(newMinute);
                }
            }
        });

        Button button_15 =(Button)findViewById(R.id.btn_15);
        button_15.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                    int newMinute = timePicker.getMinute() - 15;
                    if (newMinute < 0){
                        timePicker.setHour(timePicker.getHour() - 1);
                    }
                    timePicker.setMinute(newMinute);
                } else {
                    int newMinute = timePicker.getCurrentMinute() - 15;
                    if (newMinute < 0){
                        timePicker.setCurrentHour(timePicker.getCurrentHour() - 1);
                    }
                    timePicker.setCurrentMinute(newMinute);
                }
            }
        });

        Button button =(Button)findViewById(R.id.btn);
        button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                int hour;
                int minute;
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                    hour = timePicker.getHour();
                    minute = timePicker.getMinute();
                } else {
                    hour = timePicker.getCurrentHour();
                    minute = timePicker.getCurrentMinute();
                }
                mam.addAlarm(hour,minute);
                Intent intent = new Intent(AddActivity.this, MainActivity.class);
                intent.putExtra("hour", String.format("%02d", hour));
                intent.putExtra("minute", String.format("%02d", minute));
                intent.putExtra("memo", editText.getText().toString());
                startActivity(intent);
            }
        });

        Button cancelButton =(Button)findViewById(R.id.cancel_btn);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void load() {
        // ファイルに保存したデータを読み込む
        Object o = TempDataUtil.load(this, FILE_NAME);
        if (o == null)
            dto = new UrlDto("","");
        else {
            dto = (UrlDto) o;
        }
        // 読み込んだデータを画面に反映する
    }
}
