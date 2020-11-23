package com.example.bianguojian.project;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;


import java.util.List;


/**
 * Created by Administrator on 2016/12/12.
 */

public class DateTimePickDialogUtil implements DatePicker.OnDateChangedListener, TimePicker.OnTimeChangedListener {
    private TimePicker timePickerBegin;
    private TimePicker timePickerEnd;
    private EditText eventNamePicker;
    private AlertDialog ad;
    private String dateTime;
    private String initDateTime;
    private Activity activity;

    public DateTimePickDialogUtil(Activity activity, String initDateTime) {
        this.activity = activity;
        this.initDateTime = initDateTime;
    }

    public void init(TimePicker timePickerBegin, TimePicker timePickerEnd) {
        timePickerBegin.setCurrentHour(0);
        timePickerBegin.setCurrentMinute(0);
        timePickerEnd.setCurrentHour(0);
        timePickerEnd.setCurrentMinute(0);
    }

    public void dateTimePicKDialog(final List<String[]> listString, final MyAdapter2 myAdapter2, final Context context, final int year, final int month, final int day, final myDB myDatabase) {

        LinearLayout dateTimeLayout = (LinearLayout) activity
                .getLayoutInflater().inflate(R.layout.common_datetime, null);
        timePickerBegin = (TimePicker) dateTimeLayout.findViewById(R.id.timepicker_begin);
        timePickerEnd = (TimePicker) dateTimeLayout.findViewById(R.id.timepicker_end);
        eventNamePicker = (EditText) dateTimeLayout.findViewById(R.id.edit_event_name);
        init(timePickerBegin, timePickerEnd);
        timePickerBegin.setIs24HourView(true);
        timePickerEnd.setIs24HourView(true);
        timePickerBegin.setOnTimeChangedListener(this);
        timePickerEnd.setOnTimeChangedListener(this);

        ad = new AlertDialog.Builder(activity)
                .setTitle(initDateTime)
                .setView(dateTimeLayout)
                .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if((eventNamePicker.getText()).toString().equals("")) {
                            Toast.makeText(context, "事件不能为空", Toast.LENGTH_SHORT).show();
                        } else {
                            dateTime = "";
                            if (timePickerBegin.getCurrentHour() < 10) dateTime += "0" + timePickerBegin.getCurrentHour();
                            else dateTime += "" + timePickerBegin.getCurrentHour();
                            if (timePickerBegin.getCurrentMinute() < 10) dateTime += ":0" + timePickerBegin.getCurrentMinute();
                            else dateTime += ":" + timePickerBegin.getCurrentMinute();
                            if (timePickerEnd.getCurrentHour() < 10) dateTime += "-0" + timePickerEnd.getCurrentHour();
                            else dateTime += "-" + timePickerEnd.getCurrentHour();
                            if (timePickerEnd.getCurrentMinute() < 10) dateTime += ":0" + timePickerEnd.getCurrentMinute();
                            else dateTime += ":" + timePickerEnd.getCurrentMinute();
                            listString.add(new String[]{dateTime, (eventNamePicker.getText()).toString()});
                            myAdapter2.notifyDataSetChanged();
                            myDatabase.insertToDB(year, month, day, dateTime, (eventNamePicker.getText()).toString());
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                }).show();

        onDateChanged(null, 0, 0, 0);
    }

    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        onDateChanged(null, 0, 0, 0);
    }

    public void onDateChanged(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
        dateTime = timePickerBegin.getCurrentHour() + ":" + timePickerBegin.getCurrentMinute() + "-" + timePickerEnd.getCurrentHour() + ":" + timePickerEnd.getCurrentMinute();
    }

}
