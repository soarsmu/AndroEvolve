package edu.tjhsst.sleepn;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class AlarmCreator extends AppCompatActivity {

    private TimePicker timePicker;
    private EditText mEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_creator);

        timePicker = (TimePicker) findViewById(R.id.timePicker);
        int hour = timePicker.getCurrentHour();
        int minute = timePicker.getCurrentMinute();

        findViewById(R.id.creationMainLayout).requestFocus();

        Button creationButton = (Button) findViewById(R.id.creationButton);
        Button cancelCreationButton = (Button) findViewById(R.id.cancelCreationButton);
        mEditText = (EditText) findViewById(R.id.nameInput);

        creationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PendingIntent id = setAlarm(timePicker.getCurrentHour(), timePicker.getCurrentMinute());

                prepareIntent(id);

                finish();
            }
        });
        cancelCreationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //Create a new PendingIntent and add it to the AlarmManager
    }

    private PendingIntent setAlarm(int Hour, int Minute) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, Hour);
        cal.set(Calendar.MINUTE, Minute);
        cal.set(Calendar.SECOND, 0);

        //in case of choosing a previous hour, then set alarm to next day
        if (cal.getTimeInMillis() < System.currentTimeMillis())
            cal.set(Calendar.HOUR_OF_DAY, Hour + 24);



        Intent intent = new Intent(this, AlarmReceiverActivity.class);
        System.out.println(mEditText.getText());
        if(!mEditText.getText().equals(""))
        {
            intent.putExtra("alarmName", mEditText.getText().toString());
        }
        else
        {
            intent.putExtra("alarmName", "Alarm");
        }
        int intentId = UUID.randomUUID().hashCode();
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                intentId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager am =
                (AlarmManager)getSystemService(Activity.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                pendingIntent);

        cal.add(Calendar.SECOND, 10);
        return pendingIntent;
    }

    private void prepareIntent(PendingIntent pIntent)
    {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        if(!mEditText.getText().equals(""))
        {
            intent.putExtra("alarmName", mEditText.getText().toString());
        }
        else
        {
            intent.putExtra("alarmName", "Alarm");
        }
        String time = "";
        if(timePicker.getCurrentHour() > 12)
        {
            int hour = timePicker.getCurrentHour() - 12;
            time = String.valueOf(hour) + ":" + String.valueOf(timePicker.getCurrentMinute()) + " PM";
        }
        else
        {
            time = String.valueOf(timePicker.getCurrentHour()) + ":" + String.valueOf(timePicker.getCurrentMinute()) + " PM";
        }
        intent.putExtra("alarmTime", time);
        intent.putExtra("intent", pIntent);
    }
}
