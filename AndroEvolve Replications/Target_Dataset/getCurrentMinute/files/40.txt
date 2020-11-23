package newest.box.smart.com.smartchargeboxnewest.modals;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import newest.box.smart.com.smartchargeboxnewest.R;
import newest.box.smart.com.smartchargeboxnewest.events.TimeRangeSelectedEvent;

/**
 * Created by Deividas on 2017-09-10.
 */

public class TimeRangePickerDialog extends DialogFragment implements View.OnClickListener, TabHost.OnTabChangeListener {
    private TimePicker startTimePicker, endTimePicker;
    private boolean is24HourMode = true;
    private boolean isSecondSelected = false;

    private int startDay;
    private int startMonth;
    private int startYear;

    private int endDay;
    private int endMonth;
    private int endYear;
    private boolean secondDay = false;
    private String id;
    private boolean validTime = true;

    Calendar startCalendar;
    Calendar endCalendar;
    private TextView startTime;
    private TextView endTime;

    public static TimeRangePickerDialog newInstance() {
        TimeRangePickerDialog ret = new TimeRangePickerDialog();
        ret.initialize();
        return ret;
    }

    public void initialize() {
        this.is24HourMode = true;
    }

    public void setCalendarValues(String year, String month, String day, String connectorId) {
        this.startYear = Integer.valueOf(year);
        this.startMonth = Integer.valueOf(month);
        this.startDay = Integer.valueOf(day);

        Log.d("SDSIGDSD", startYear + " " + startMonth + " " + startDay);

        endYear = this.startYear;
        endMonth = this.startMonth;
        endDay = this.startDay;

        this.id = connectorId;
    }

    @Override
    public void onTabChanged(String s) {
        dateColor(s);
    }

    private void dateColor(String s) {
        if("1".equals(s)) {
            startTime.setTextColor(Color.RED);
            endTime.setTextColor(Color.GRAY);
        } else if ("2".equals(s)) {
            endTime.setTextColor(Color.RED);
            startTime.setTextColor(Color.GRAY);
            isSecondSelected = true;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_time_range_picker_dialog, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        startTimePicker = (TimePicker) root.findViewById(R.id.startTimePicker);
        endTimePicker = (TimePicker) root.findViewById(R.id.endTimePicker);

        TabHost tabs = (TabHost) root.findViewById(R.id.tabHost);
        final Button setTimeRange = (Button) root.findViewById(R.id.set_time);
        Button cancelButton = (Button) root.findViewById(R.id.cancel);
        startTimePicker.setIs24HourView(is24HourMode);
        endTimePicker.setIs24HourView(is24HourMode);
        setTimeRange.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        tabs.findViewById(R.id.tabHost);
        tabs.setup();
        TabHost.TabSpec tabpage1 = tabs.newTabSpec("1");
        tabpage1.setContent(R.id.startTimeGroup);
        tabpage1.setIndicator("Pradžios laikas");

        TabHost.TabSpec tabpage2 = tabs.newTabSpec("2");
        tabpage2.setContent(R.id.endTimeGroup);
        tabpage2.setIndicator("Pabaigos laikas");

        tabs.addTab(tabpage1);
        tabs.addTab(tabpage2);

        tabs.setOnTabChangedListener(this);

        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        startCalendar = Calendar.getInstance();
        endCalendar = Calendar.getInstance();
        startCalendar.set(startYear, startMonth - 1, startDay, startTimePicker.getCurrentHour(), startTimePicker.getCurrentMinute());

        startTime = (TextView) root.findViewById(R.id.stat_time);
        endTime = (TextView) root.findViewById(R.id.end_time);

        dateColor("1");


        //startTime.setText(dateFormat.format(startCalendar.getTime().getTime() - 60000));
        startTime.setText(dateFormat.format(startCalendar.getTime().getTime()));
        startTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                Calendar calendar = Calendar.getInstance();

                startCalendar.set(startYear, startMonth - 1, startDay, timePicker.getCurrentHour(), timePicker.getCurrentMinute());
                startTime.setText(dateFormat.format(startCalendar.getTime().getTime()));
                if (startCalendar.getTimeInMillis() > calendar.getTimeInMillis()) {
                    setTimeRange.setTextColor(Color.parseColor("#2e9df2"));
                    validTime = true;
                } else {
                    setTimeRange.setTextColor(Color.RED);
                    validTime = false;
                }
            }
        });


        endCalendar.set(endYear, endMonth - 1, endDay, endTimePicker.getCurrentHour(), endTimePicker.getCurrentMinute());
        endTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {
//                endCalendar.set(endYear, endMonth - 1, tmpDay, timePicker.getCurrentHour(), timePicker.getCurrentMinute() + 1);
//                if(startCalendar.getTime().getTime() > endCalendar.getTime().getTime()) {
//                    tmpDay += 1;
//                    endCalendar.set(endYear, endMonth - 1, tmpDay, timePicker.getCurrentHour(), timePicker.getCurrentMinute() + 1);
//                } else {
//                    if(Math.abs(tmpDay - endDay) == 1) {
//                        tmpDay -= 1;
//                    } else {
//                        tmpDay = endDay;
//                    }
//                    endCalendar.set(endYear, endMonth - 1, tmpDay, timePicker.getCurrentHour(), timePicker.getCurrentMinute() + 1);
//                }
                endCalendar.set(endYear, endMonth - 1, endDay, endTimePicker.getCurrentHour(), endTimePicker.getCurrentMinute());
                if(startCalendar.getTime().getTime() > endCalendar.getTime().getTime()) {
                    secondDay = true;
                } else {
                    secondDay = false;
                }
                if(secondDay) {
                    endTime.setText(dateFormat.format(endCalendar.getTime().getTime() + 86400000));
                } else {
                    endTime.setText(dateFormat.format(endCalendar.getTime().getTime()));
                }

            }
        });

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getDialog() == null)
            return;
        getDialog().getWindow().setLayout(950, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.set_time) {
            if(!validTime) {
                Toast.makeText(getContext(), "Pasirinktas blogas laikas", Toast.LENGTH_SHORT).show();
                return;
            }
            if (isSecondSelected) {
                long endTime = endCalendar.getTimeInMillis();
                if(secondDay) {
                    endTime += 86400000;
                }
                if(Math.abs(endTime - startCalendar.getTimeInMillis()) < 900000) {
                    Toast.makeText(getContext(), "Krovimo laikas turi būti didesnis už 15 min.", Toast.LENGTH_SHORT).show();
                    return;
                }
                dismiss();
                int startHour = startTimePicker.getCurrentHour();
                int startMin = startTimePicker.getCurrentMinute();
                int endHour = endTimePicker.getCurrentHour();
                int endMin = endTimePicker.getCurrentMinute();
                if(secondDay) {
                    endDay += 1;
                }

                Log.d("SDSUYDSDSDSDDSDSD", startYear + " " + startMonth + " " + startDay + " " + startHour + " " + startMin);


                EventBus.getDefault().post(new TimeRangeSelectedEvent(String.valueOf(startYear),
                        String.valueOf(startMonth), String.valueOf(startDay), startHour, startMin,
                        String.valueOf(endYear), String.valueOf(endMonth), String.valueOf(endDay),
                        endHour, endMin, id));
            } else {
                Toast.makeText(getContext(), "Pabaigos laikas nenustatytas!", Toast.LENGTH_SHORT).show();
            }
        } else if (v.getId() == R.id.cancel) {
            getDialog().dismiss();
        }
    }
}
