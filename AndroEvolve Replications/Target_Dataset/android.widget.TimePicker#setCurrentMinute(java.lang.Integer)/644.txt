package com.yuntongxun.ecdemo.ui.xiaomi;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

import com.yuntongxun.ecdemo.R;

public class TimeIntervalDialog extends Dialog implements OnTimeChangedListener{

    private TimeIntervalInterface timeIntervalInterface;
    private Context mContext;
    private TimePicker startTimePicker, endTimePicker;
    private Button applyBtn, cancelBtn;
    private int startHour, startMinute, endHour, endMinute;

    public TimeIntervalDialog(Context context, TimeIntervalInterface timeIntervalInterface, int startHour, int startMinute, int endHour, int endMinute) {
        super(context);
        mContext = context;
        this.timeIntervalInterface = timeIntervalInterface;
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.endHour = endHour;
        this.endMinute = endMinute;
    }

    public TimeIntervalDialog(Context context, TimeIntervalInterface timeIntervalInterface) {
        this(context, timeIntervalInterface, 0, 0, 23, 59);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_time_dialog);
        setCancelable(true);
        setTitle(mContext.getString(R.string.set_accept_time));
        startTimePicker = (TimePicker) findViewById(R.id.startTimePicker);
        startTimePicker.setIs24HourView(true);
        startTimePicker.setCurrentHour(startHour);
        startTimePicker.setCurrentMinute(startMinute);
        startTimePicker.setOnTimeChangedListener(this);
        endTimePicker = (TimePicker) findViewById(R.id.endTimePicker);
        endTimePicker.setIs24HourView(true);
        endTimePicker.setCurrentHour(endHour);
        endTimePicker.setCurrentMinute(endMinute);
        endTimePicker.setOnTimeChangedListener(this);
        applyBtn = (Button) findViewById(R.id.apply);
        applyBtn.setOnClickListener(clickListener);
        cancelBtn = (Button) findViewById(R.id.cancel);
        cancelBtn.setOnClickListener(clickListener);
    }


    private Button.OnClickListener clickListener=new Button.OnClickListener(){

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.apply:
                    dismiss();
                    timeIntervalInterface.apply(startHour, startMinute, endHour, endMinute);
                    break;
                case R.id.cancel:
                    dismiss();
                    timeIntervalInterface.cancel();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        if(view == startTimePicker) {
            startHour = hourOfDay;
            startMinute = minute;
        } else if (view == endTimePicker) {
            endHour = hourOfDay;
            endMinute = minute;
        }
    }

    public static interface TimeIntervalInterface {
        public void apply(int startHour, int startMin, int endHour, int endMin);
        public void cancel();
    }
}
