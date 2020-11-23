package com.hackthenorth.lockmeout.app.LockPhone;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.hackthenorth.lockmeout.app.R;
import com.hackthenorth.lockmeout.app.Timer.TimerService;

import java.lang.reflect.Field;
import java.util.Calendar;

/**
 * Created by Berries on 2014-09-19.
 */
public class EndTime extends LockPhoneFragment {

    RelativeLayout layout;
    private OnLockSelectedListener listener;
    private int hour;
    private int minute;
    private int day;
    private int month;
    public long startTime;
    public Context ctx;
    public String startTimeFile = "com.hackthenorth.lockmeout.startTime";
    public String endTimeFile = "com.hackthenorth.lockmeout.endTime";
    public SharedPreferences prefs;

    private TimePicker timePicker;
    private DatePicker datePicker;
    private Button nextButton;
    private AlertDialog.Builder pickDateDialog;
    private AlertDialog.Builder pickTimeDialog;
    private View timeLayout;
    private View dateLayout;
    private Button timeBtnDialog;
    private Button dateBtnDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = (View) inflater.inflate(
                R.layout.fragment_endtime, container, false);
        prefs = ctx.getSharedPreferences("com.hackthenorth.lockmeout", Context.MODE_PRIVATE);

        if(timeLayout == null) {
            timeLayout = inflater.inflate(R.layout.time_dialog, null);
        }

        if(dateLayout == null){
            dateLayout = inflater.inflate(R.layout.date_dialog, null);
        }

        pickTimeDialog.setView(timeLayout);
        pickDateDialog.setView(dateLayout);
        datePicker = (DatePicker) dateLayout.findViewById(R.id.date_picker);
        timePicker = (TimePicker) timeLayout.findViewById(R.id.time_picker);

        try {
            Field f[] = datePicker.getClass().getDeclaredFields();
            for (Field field : f) {
                if (field.getName().equals("mYearPicker") || field.getName().equals("mYearSpinner")) {
                    field.setAccessible(true);
                    Object yearPicker = new Object();
                    yearPicker = field.get(datePicker);
                    ((View) yearPicker).setVisibility(View.GONE);
                }
            }
        }
        catch (SecurityException e) {
            Log.d("ERROR", e.getMessage());
        }
        catch (IllegalArgumentException e) {
            Log.d("ERROR", e.getMessage());
        }
        catch (IllegalAccessException e) {
            Log.d("ERROR", e.getMessage());
        }
        Button setDateBtn = (Button) view.findViewById(R.id.btn_set_date_end);
        setDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pickDateDialog.show();

            }
        });

        Button setTimeBtn = (Button) view.findViewById(R.id.btn_set_time_end);
        setTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pickTimeDialog.show();

            }
        });
        Button backButton = (Button) view.findViewById(R.id.back_button);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveClicked(timePicker.getCurrentMinute(), timePicker.getCurrentHour(), datePicker.getDayOfMonth(), datePicker.getMonth(), "back");
                saveClicked( minute, hour, day, month, "back");
            }
        });

        Button lockButton = (Button) view.findViewById(R.id.lock_button);

        lockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveClicked(minute, hour, day, month, "");
                Toast.makeText(ctx, "TESTING", Toast.LENGTH_LONG).show();
                java.util.Calendar calendar = java.util.Calendar.getInstance();

                Calendar cl = Calendar.getInstance();
                Calendar cl2 = Calendar.getInstance();

                switch ( month ) {
                    case 0:
                        calendar.set(datePicker.getYear(), Calendar.JANUARY, datePicker.getDayOfMonth(),
                                timePicker.getCurrentHour(), timePicker.getCurrentMinute(), 0);
                        break;
                    case 1:
                        calendar.set(datePicker.getYear(), Calendar.FEBRUARY, datePicker.getDayOfMonth(),
                                timePicker.getCurrentHour(), timePicker.getCurrentMinute(), 0);
                        break;
                    case 2:
                        calendar.set(datePicker.getYear(), Calendar.MARCH, datePicker.getDayOfMonth(),
                                timePicker.getCurrentHour(), timePicker.getCurrentMinute(), 0);
                        break;
                    case 3:
                        calendar.set(datePicker.getYear(), Calendar.APRIL, datePicker.getDayOfMonth(),
                                timePicker.getCurrentHour(), timePicker.getCurrentMinute(), 0);
                        break;
                    case 4:
                        calendar.set(datePicker.getYear(), Calendar.MAY, datePicker.getDayOfMonth(),
                                timePicker.getCurrentHour(), timePicker.getCurrentMinute(), 0);
                        break;
                    case 5:
                        calendar.set(datePicker.getYear(), Calendar.JUNE, datePicker.getDayOfMonth(),
                                timePicker.getCurrentHour(), timePicker.getCurrentMinute(), 0);
                        break;
                    case 6:
                        calendar.set(datePicker.getYear(), Calendar.JULY, datePicker.getDayOfMonth(),
                                timePicker.getCurrentHour(), timePicker.getCurrentMinute(), 0);
                        break;
                    case 7:
                        calendar.set(datePicker.getYear(), Calendar.AUGUST, datePicker.getDayOfMonth(),
                                timePicker.getCurrentHour(), timePicker.getCurrentMinute(), 0);
                        break;
                    case 8:
                        calendar.set(datePicker.getYear(), Calendar.SEPTEMBER, datePicker.getDayOfMonth(),
                                timePicker.getCurrentHour(), timePicker.getCurrentMinute(), 0);
                        break;
                    case 9:
                        calendar.set(datePicker.getYear(), Calendar.OCTOBER, datePicker.getDayOfMonth(),
                                timePicker.getCurrentHour(), timePicker.getCurrentMinute(), 0);
                        break;
                    case 10:
                        calendar.set(datePicker.getYear(), Calendar.NOVEMBER, datePicker.getDayOfMonth(),
                                timePicker.getCurrentHour(), timePicker.getCurrentMinute(), 0);
                        break;
                    case 11:
                        calendar.set(datePicker.getYear(), Calendar.DECEMBER, datePicker.getDayOfMonth(),
                                timePicker.getCurrentHour(), timePicker.getCurrentMinute(), 0);
                        break;
                    default:
                        break;
                }
                long endTime = calendar.getTimeInMillis();
                Log.d("endTime", "The very first endTIme: " + endTime);
                long startTime = prefs.getLong(startTimeFile, 0);

                if (endTime < startTime) {
                    switch (datePicker.getMonth()) {
                        case 0:
                            calendar.set(datePicker.getYear() + 1, Calendar.JANUARY, datePicker.getDayOfMonth(),
                                    timePicker.getCurrentHour(), timePicker.getCurrentMinute(), 0);
                            break;
                        case 1:
                            calendar.set(datePicker.getYear() + 1, Calendar.FEBRUARY, datePicker.getDayOfMonth(),
                                    timePicker.getCurrentHour(), timePicker.getCurrentMinute(), 0);
                            break;
                        case 2:
                            calendar.set(datePicker.getYear() + 1, Calendar.MARCH, datePicker.getDayOfMonth(),
                                    timePicker.getCurrentHour(), timePicker.getCurrentMinute(), 0);
                            break;
                        case 3:
                            calendar.set(datePicker.getYear() + 1, Calendar.APRIL, datePicker.getDayOfMonth(),
                                    timePicker.getCurrentHour(), timePicker.getCurrentMinute(), 0);
                            break;
                        case 4:
                            calendar.set(datePicker.getYear() + 1, Calendar.MAY, datePicker.getDayOfMonth(),
                                    timePicker.getCurrentHour(), timePicker.getCurrentMinute(), 0);
                            break;
                        case 5:
                            calendar.set(datePicker.getYear() + 1, Calendar.JUNE, datePicker.getDayOfMonth(),
                                    timePicker.getCurrentHour(), timePicker.getCurrentMinute(), 0);
                            break;
                        case 6:
                            calendar.set(datePicker.getYear() + 1, Calendar.JULY, datePicker.getDayOfMonth(),
                                    timePicker.getCurrentHour(), timePicker.getCurrentMinute(), 0);
                            break;
                        case 7:
                            calendar.set(datePicker.getYear() + 1, Calendar.AUGUST, datePicker.getDayOfMonth(),
                                    timePicker.getCurrentHour(), timePicker.getCurrentMinute(), 0);
                            break;
                        case 8:
                            calendar.set(datePicker.getYear() + 1, Calendar.SEPTEMBER, datePicker.getDayOfMonth(),
                                    timePicker.getCurrentHour(), timePicker.getCurrentMinute(), 0);
                            break;
                        case 9:
                            calendar.set(datePicker.getYear() + 1, Calendar.OCTOBER, datePicker.getDayOfMonth(),
                                    timePicker.getCurrentHour(), timePicker.getCurrentMinute(), 0);
                            break;
                        case 10:
                            calendar.set(datePicker.getYear() + 1, Calendar.NOVEMBER, datePicker.getDayOfMonth(),
                                    timePicker.getCurrentHour(), timePicker.getCurrentMinute(), 0);
                            break;
                        case 11:
                            calendar.set(datePicker.getYear() + 1, Calendar.DECEMBER, datePicker.getDayOfMonth(),
                                    timePicker.getCurrentHour(), timePicker.getCurrentMinute(), 0);
                            break;
                        default:
                            break;
                    }

                    endTime = calendar.getTimeInMillis();
                    Log.d("endTime", "The very second endTIme: " + endTime);
                }
                prefs.edit().putLong(endTimeFile, endTime).apply();
                long tmpEndTime = prefs.getLong(endTimeFile, 0);

                listener.promptLock();
            }
        });

        return view;
    }


    public interface OnLockSelectedListener{
        public void promptLock();
    }

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);

        ctx = activity.getApplicationContext();
        if (activity instanceof OnLockSelectedListener) {
            listener = (OnLockSelectedListener) activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implement MyListFragment.OnItemSelectedListener");
        }

        pickDateDialog = new AlertDialog.Builder(activity)
                .setTitle("Set Date")
                .setMessage("Select the date you want to start.")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        day = datePicker.getDayOfMonth();
                        month = datePicker.getMonth();

                        Toast toast = Toast.makeText(activity.getApplicationContext(), "Set Month: " + month + " Days: " + day, Toast.LENGTH_LONG);
                        toast.show();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        pickTimeDialog = new AlertDialog.Builder(activity)
                .setTitle("Set Time")
                .setMessage("Select the time you want to start.")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        minute = timePicker.getCurrentMinute();
                        hour = timePicker.getCurrentHour();

                        Toast toast = Toast.makeText(activity.getApplicationContext(), "Set Hour: " + hour + " Minutes: " + minute, Toast.LENGTH_LONG);
                        toast.show();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}