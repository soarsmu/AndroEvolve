package com.daeseong.timepicker_test;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

public class TimePickerDialogEx extends Dialog {

    private static final String TAG = TimePickerDialogEx.class.getSimpleName();

    public int BUTTON_TYPE = Dialog.BUTTON_NEGATIVE;

    private Button button1, button2;
    private TimePicker timepicker1;
    private int nhour, nMinute;

    public TimePickerDialogEx(Context context){
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timepicker_dialog);

        button1 = findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BUTTON_TYPE = Dialog.BUTTON_NEGATIVE;
                dismiss();
            }
        });

        button2 = findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BUTTON_TYPE = Dialog.BUTTON_POSITIVE;
                dismiss();
            }
        });

        timepicker1 = (TimePicker)findViewById(R.id.timepicker1);
        timepicker1.setIs24HourView(true);
        timepicker1.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    nhour = timepicker1.getHour();
                    nMinute = timepicker1.getMinute();
                } else {
                    nhour = timepicker1.getCurrentHour();
                    nMinute = timepicker1.getCurrentMinute();
                }

                Log.e(TAG, "nhour:" + nhour + " nMinute:" + nMinute);
                Log.e(TAG, "hourOfDay:" + hourOfDay + " minute:" + minute);

            }
        });

    }

    public String getHour(){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            nhour = timepicker1.getHour();
            nMinute = timepicker1.getMinute();
        } else {
            nhour = timepicker1.getCurrentHour();
            nMinute = timepicker1.getCurrentMinute();
        }

        return Integer.toString(nhour);
    }

    public String getMinute(){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            nhour = timepicker1.getHour();
            nMinute = timepicker1.getMinute();
        } else {
            nhour = timepicker1.getCurrentHour();
            nMinute = timepicker1.getCurrentMinute();
        }

        return Integer.toString(nMinute);
    }
}
