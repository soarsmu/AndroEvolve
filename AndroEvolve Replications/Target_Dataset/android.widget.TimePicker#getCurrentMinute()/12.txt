package com.pplive.liveplatform.widget;

import java.util.Calendar;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

import com.pplive.liveplatform.Constants;
import com.pplive.liveplatform.R;

public class DateTimePicker extends LinearLayout {
    
    private static final String TAG = DateTimePicker.class.getSimpleName();

    private DatePicker mDatePicker;
    private TimePicker mTimePicker;
    private OnDateTimeChangedListener mOnDateTimeChangedListener;
    
    private Calendar mCalendar;
    
    public DateTimePicker(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Service.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.widget_datetime_picker, this, true);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mDatePicker = (DatePicker) findViewById(R.id.date_picker);
        mTimePicker = (TimePicker) findViewById(R.id.time_picker);

        mCalendar = Calendar.getInstance();
        
        mCalendar.add(Calendar.MINUTE, 20);
        
        init();
    }
    
    private void init() {
        mDatePicker.init(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH), new OnDateChangedListener() {

            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Log.d(TAG, "onDateChanged");
                onDateTimeChanged();
            }
        });

        mTimePicker.setIs24HourView(true);
        mTimePicker.setCurrentHour(mCalendar.get(Calendar.HOUR_OF_DAY));
        mTimePicker.setCurrentMinute(mCalendar.get(Calendar.MINUTE));
        mTimePicker.setOnTimeChangedListener(new OnTimeChangedListener() {

            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                Log.d(TAG, "onTimeChanged");
                onDateTimeChanged();
            }
        });
    }
    
    public void setOnDateTimeChanged(OnDateTimeChangedListener listener) {
        mOnDateTimeChangedListener = listener;

//        onDateTimeChanged();
    }

    private void onDateTimeChanged() {
        if (null != mOnDateTimeChangedListener) {
            mOnDateTimeChangedListener.onDateTimeChanged(getYear(), getMonth(), getDayOfMonth(), getCurrentHour(), getCurrentMinute());
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void setMaxDate(long maxDate) {
        if (Constants.LARGER_THAN_OR_EQUAL_HONEYCOMB) {
            mDatePicker.setMaxDate(maxDate);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void setMinDate(long minDate) {
        if (Constants.LARGER_THAN_OR_EQUAL_HONEYCOMB) {
            mDatePicker.setMinDate(minDate);
        }
    }

    public int getYear() {
        return mDatePicker.getYear();
    }

    public int getMonth() {
        return mDatePicker.getMonth() + 1;
    }

    public int getDayOfMonth() {
        return mDatePicker.getDayOfMonth();
    }

    public int getCurrentHour() {
        return mTimePicker.getCurrentHour();
    }

    public int getCurrentMinute() {
        return mTimePicker.getCurrentMinute();
    }
    
    public long getTimeInMillis() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(getYear(), getMonth() - 1, getDayOfMonth(), getCurrentHour(), getCurrentMinute());
        
        return calendar.getTimeInMillis();
    }

    public interface OnDateTimeChangedListener {

        void onDateTimeChanged(int year, int month, int day, int hour, int minute);

    }
}
