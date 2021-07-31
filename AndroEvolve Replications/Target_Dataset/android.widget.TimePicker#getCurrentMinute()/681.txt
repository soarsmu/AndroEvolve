
package com.jovision.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

import cn.tmway.temsee.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Pattern;

public class DateTimePickDialogUtil implements OnDateChangedListener,
        OnTimeChangedListener {
    private DatePicker datePicker;
    private TimePicker timePicker;
    private AlertDialog ad;
    private String dateTime;
    private String initDate;
    private String initTime;
    private Activity activity;
    private String preTitle;
    private String setting, cancel;

    public DateTimePickDialogUtil(Activity activity, String initDate,
            String initTime, int flag) {
        this.activity = activity;
        this.initDate = initDate;
        this.initTime = initTime;
        this.setting = activity.getResources().getString(R.string.setting);
        this.cancel = activity.getResources().getString(R.string.cancel);

        switch (flag) {
            case 0:
                preTitle = activity.getResources().getString(
                        R.string.str_start_time);
                break;
            case 1:
                preTitle = activity.getResources().getString(R.string.str_end_time);
                break;
            default:
                preTitle = activity.getResources().getString(
                        R.string.str_setting_time);
                break;
        }
    }

    public void init(DatePicker datePicker, TimePicker timePicker) {
        Calendar calendar = Calendar.getInstance();
        if (!(null == initTime || "".equals(initTime))) {
            calendar = this.getCalendarByInintTime(initTime);
        } else {
            initTime = calendar.get(Calendar.HOUR_OF_DAY) + ":"
                    + calendar.get(Calendar.MINUTE);
        }

        // datePicker.init(calendar.get(Calendar.YEAR),
        // calendar.get(Calendar.MONTH),
        // calendar.get(Calendar.DAY_OF_MONTH), this);
        timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
        timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
    }

    public AlertDialog dateTimePicKDialog(final TextView setTime) {
        RelativeLayout dateTimeLayout = (RelativeLayout) activity
                .getLayoutInflater().inflate(R.layout.common_datetime, null);
        datePicker = (DatePicker) dateTimeLayout.findViewById(R.id.datepicker);
        timePicker = (TimePicker) dateTimeLayout.findViewById(R.id.timepicker);
        timePicker.setIs24HourView(true);
        init(datePicker, timePicker);
        timePicker.setOnTimeChangedListener(this);

        ad = new AlertDialog.Builder(activity)
                .setTitle(
                        activity.getResources().getString(
                                R.string.str_setting_time))
                .setView(dateTimeLayout)
                .setPositiveButton(setting,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int whichButton) {
                                setTime.setText(dateTime);
                            }
                        })
                .setNegativeButton(cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int whichButton) {
                            }
                        }).show();

        onTimeChanged(null, 0, 0);
        return ad;
    }

    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {

        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
        calendar.set(Calendar.MINUTE, timePicker.getCurrentMinute());
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        dateTime = sdf.format(calendar.getTime());
        ad.setTitle(preTitle + " " + dateTime);
    }

    public void onDateChanged(DatePicker view, int year, int monthOfYear,
            int dayOfMonth) {

        Calendar calendar = Calendar.getInstance();

        calendar.set(timePicker.getCurrentHour(), timePicker.getCurrentMinute());
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        dateTime = sdf.format(calendar.getTime());
        ad.setTitle(preTitle + " " + dateTime);
    }

    private Calendar getCalendarByInintTime(String initTime) {
        Calendar calendar = Calendar.getInstance();
        try {
            Pattern pattern = Pattern.compile(":");
            String[] strs = pattern.split(initTime);
            String hour = strs[0].trim();
            String minute = strs[1].trim();

            int currentHour = Integer.valueOf((null == hour) ? "0" : hour)
                    .intValue();
            int currentMinute = Integer
                    .valueOf((null == minute) ? "0" : minute).intValue();

            // int currentHour = Integer.valueOf(null).intValue();
            // int currentMinute = Integer.valueOf(null).intValue();

            calendar.set(Calendar.HOUR_OF_DAY, currentHour);
            calendar.set(Calendar.MINUTE, currentMinute);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return calendar;
    }

}
