package com.antol.batterymanager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by CSAntol on 9/7/13.
 */
public class MainActivity extends Activity {

    private static final String TAG = AlarmReceiver.class.getSimpleName();
    private static final String SLEEP_START_HOUR = "sleepstarthour";
    private static final String SLEEP_END_HOUR = "sleependhour";
    private static final String WORK_START_HOUR = "workstarthour";
    private static final String WORK_END_HOUR = "workendhour";
    private static final String SLEEP_START_MIN = "sleepstartmin";
    private static final String SLEEP_END_MIN = "sleependmin";
    private static final String WORK_START_MIN = "workstartmin";
    private static final String WORK_END_MIN = "workendmin";
    protected static final String DISABLE_WEEKENDS = "disableweekends";
    protected static final int REQUEST_CODE_ENABLE_NETWORK = 98765;
    protected static final int REQUEST_CODE_ENABLE_RINGER = 98766;
    protected static final int REQUEST_CODE_DISABLE_NETWORK = 98767;
    protected static final int REQUEST_CODE_DISABLE_RINGER = 98768;
    private static AlarmReceiver alarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alarm = new AlarmReceiver();
        final Context context = this;
        final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Button start = (Button) findViewById(R.id.start_service);
        Button stop = (Button) findViewById(R.id.stop_service);
        Button updateSleepStart = (Button) findViewById(R.id.update_sleep_start);
        Button updateSleepEnd = (Button) findViewById(R.id.update_sleep_end);
        Button updateWorkStart = (Button) findViewById(R.id.update_work_start);
        Button updateWorkEnd = (Button) findViewById(R.id.update_work_end);
        final TimePicker timePicker = (TimePicker) findViewById(R.id.timePicker);
        CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox);

        // Fill out existing times
        final SharedPreferences sp = getSharedPreferences(TAG, Activity.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sp.edit();

        // sleep start
        int sleepStartHr = sp.getInt(SLEEP_START_HOUR, 0);
        int sleepStartMin = sp.getInt(SLEEP_START_MIN, 0);
        final TextView sleepStartTv = (TextView) findViewById(R.id.sleep_start_text);
        updateTimeTextView(sleepStartTv, sleepStartHr, sleepStartMin);

        // sleep end
        int sleepEndHr = sp.getInt(SLEEP_END_HOUR, 0);
        int sleepEndMin = sp.getInt(SLEEP_END_MIN, 0);
        final TextView sleepEndTv = (TextView) findViewById(R.id.sleep_end_text);
        updateTimeTextView(sleepEndTv, sleepEndHr, sleepEndMin);

        // work start
        int workStartHr = sp.getInt(WORK_START_HOUR, 0);
        int workStartMin = sp.getInt(WORK_START_MIN, 0);
        final TextView workStartTv = (TextView) findViewById(R.id.work_start_text);
        updateTimeTextView(workStartTv, workStartHr, workStartMin);

        // work end
        int workEndHr = sp.getInt(WORK_END_HOUR, 0);
        int workEndMin = sp.getInt(WORK_END_MIN, 0);
        final TextView workEndTv = (TextView) findViewById(R.id.work_end_text);
        updateTimeTextView(workEndTv, workEndHr, workEndMin);

        // manage checkbox
        boolean isChecked = sp.getBoolean(DISABLE_WEEKENDS, true);
        checkBox.setChecked(isChecked);

        updateSleepStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putInt(SLEEP_START_HOUR, timePicker.getCurrentHour());
                editor.putInt(SLEEP_START_MIN, timePicker.getCurrentMinute());
                editor.commit();
                updateTimeTextView(sleepStartTv, timePicker.getCurrentHour(), timePicker.getCurrentMinute());
            }
        });
        updateSleepEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putInt(SLEEP_END_HOUR, timePicker.getCurrentHour());
                editor.putInt(SLEEP_END_MIN, timePicker.getCurrentMinute());
                editor.commit();
                updateTimeTextView(sleepEndTv, timePicker.getCurrentHour(), timePicker.getCurrentMinute());
            }
        });
        updateWorkStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putInt(WORK_START_HOUR, timePicker.getCurrentHour());
                editor.putInt(WORK_START_MIN, timePicker.getCurrentMinute());
                editor.commit();
                updateTimeTextView(workStartTv, timePicker.getCurrentHour(), timePicker.getCurrentMinute());
            }
        });
        updateWorkEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putInt(WORK_END_HOUR, timePicker.getCurrentHour());
                editor.putInt(WORK_END_MIN, timePicker.getCurrentMinute());
                editor.commit();
                updateTimeTextView(workEndTv, timePicker.getCurrentHour(), timePicker.getCurrentMinute());
            }
        });
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                editor.putBoolean(DISABLE_WEEKENDS, b);
                editor.commit();
            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!setAlarm(context))
                    Toast.makeText(context, context.getString(R.string.cancel_alarm), Toast.LENGTH_SHORT).show();
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alarm.cancelAlarm(context, alarmManager);
                Toast.makeText(context, context.getString(R.string.cancel_alarm), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateTimeTextView(TextView textView, int startHr, int startMin) {
        String noonStatus = startHr > 11 ? "pm" : "am";
        String hr;
        if (startHr == 0) {
            hr = Integer.toString(12);
        } else if (startHr > 12) {
            hr = Integer.toString(startHr - 12);
        } else {
            hr = Integer.toString(startHr);
        }
        textView.setText(String.format("%s:%s:00%s", hr, String.format("%s%d", startMin < 10 ? Integer.toString(0) : "", startMin), noonStatus));
    }

    private static Calendar setCalendar(int hour, int min) {
        Calendar calendar = Calendar.getInstance();

        // add a day if the requested time is in the past
        if (calendar.get(Calendar.HOUR_OF_DAY) > hour ||
                (calendar.get(Calendar.HOUR_OF_DAY) == hour && calendar.get(Calendar.MINUTE) >= min)) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, 0);

        return calendar;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, Credits.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected static boolean setAlarm(Context context) {
        Calendar cal;
        Intent intent;
        PendingIntent pendingIntent;
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        final SharedPreferences sp = context.getSharedPreferences(TAG, Activity.MODE_PRIVATE);

        boolean noSleep = false, noWork = false;

        if (!(sp.getInt(SLEEP_END_HOUR, 0) == sp.getInt(SLEEP_START_HOUR, 0) && sp.getInt(SLEEP_END_MIN, 0) == sp.getInt(SLEEP_START_MIN, 0))) {

            // Turn on network alarm
            intent = new Intent(context, AlarmReceiver.class);
            intent.putExtra(AlarmReceiver.ALTER, AlarmReceiver.Alteration.ENABLE_NETWORK.name());
            pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE_ENABLE_NETWORK, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            cal = setCalendar(sp.getInt(SLEEP_END_HOUR, 0), sp.getInt(SLEEP_END_MIN, 0));
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

            // Turn off network alarm
            intent = new Intent(context, AlarmReceiver.class);
            intent.putExtra(AlarmReceiver.ALTER, AlarmReceiver.Alteration.DISABLE_NETWORK.name());
            pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE_DISABLE_NETWORK, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            cal = setCalendar(sp.getInt(SLEEP_START_HOUR, 0), sp.getInt(SLEEP_START_MIN, 0));
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        } else {
            noSleep = true;
            alarm.cancelSleepAlarm(context, alarmManager);
        }

        if (!(sp.getInt(WORK_END_HOUR, 0) == sp.getInt(WORK_START_HOUR, 0) && sp.getInt(WORK_END_MIN, 0) == sp.getInt(WORK_START_MIN, 0))) {

            // Turn off ringer alarm
            intent = new Intent(context, AlarmReceiver.class);
            intent.putExtra(AlarmReceiver.ALTER, AlarmReceiver.Alteration.DISABLE_RINGER.name());
            pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE_DISABLE_RINGER, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            cal = setCalendar(sp.getInt(WORK_START_HOUR, 0), sp.getInt(WORK_START_MIN, 0));
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

            // Turn on ringer alarm
            intent = new Intent(context, AlarmReceiver.class);
            intent.putExtra(AlarmReceiver.ALTER, AlarmReceiver.Alteration.ENABLE_RINGER.name());
            pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE_ENABLE_RINGER, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            cal = setCalendar(sp.getInt(WORK_END_HOUR, 0), sp.getInt(WORK_END_MIN, 0));
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        } else {
            noWork = true;
            alarm.cancelWorkAlarm(context, alarmManager);
        }

        if (noSleep && noWork) {
            return false;
        }
        Toast.makeText(context, context.getString(R.string.set_alarm), Toast.LENGTH_SHORT).show();
        return true;
    }
    
}
