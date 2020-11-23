package com.bignerdranch.android.criminalintent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.Time;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by rajohns on 7/30/13.
 */
public class TimePickerFragment extends DialogFragment {

    public static final String EXTRA_TIME = "com.bignerdranch.android.criminalintent.time";
    private Date mTime;

    public static TimePickerFragment newInstance(Date time) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_TIME, time);
        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mTime = (Date)getArguments().getSerializable(EXTRA_TIME);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mTime);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_time, null);

        @SuppressWarnings("ConstantConditions") final TimePicker timePicker = (TimePicker)v.findViewById(R.id.dialog_time_timePicker);
        timePicker.setCurrentHour(hour);
        timePicker.setCurrentMinute(minute);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int hourOfDay, int minute) {
                mTime = new GregorianCalendar(0, 0, 0, hourOfDay, minute).getTime();
                getArguments().putSerializable(EXTRA_TIME, mTime);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            NumberPicker numberPickerAmPm;
            //noinspection ConstantConditions
            numberPickerAmPm  = (NumberPicker)((ViewGroup) timePicker.getChildAt(0)).getChildAt(3);
            //noinspection ConstantConditions
            numberPickerAmPm.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    int hourOfDay = (timePicker.getCurrentHour() + 12) % 24;
                    timePicker.setCurrentHour(hourOfDay);
                }
            });
        }
        else {
            @SuppressWarnings("ConstantConditions") View amPmView  = ((ViewGroup)timePicker.getChildAt(0)).getChildAt(2);
            if(amPmView instanceof Button) {
                amPmView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if(v instanceof Button) {
                            //noinspection ConstantConditions
                            if(((Button) v).getText().equals("AM")) {
                                ((Button) v).setText("PM");
                                if (timePicker.getCurrentHour() < 12) {
                                    timePicker.setCurrentHour(timePicker.getCurrentHour() + 12);
                                }

                            }
                            else {
                                ((Button) v).setText("AM");
                                if (timePicker.getCurrentHour() >= 12) {
                                    timePicker.setCurrentHour(timePicker.getCurrentHour() - 12);
                                }
                            }
                        }

                    }
                });
            }
        }

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.time_picker_title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sendResult(Activity.RESULT_OK);
                    }
                })
                .create();
    }

    private void sendResult(int resultCode) {
        if (getTargetFragment() == null) {
            return;
        }

        Intent i = new Intent();
        i.putExtra(EXTRA_TIME, mTime);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
    }

}
