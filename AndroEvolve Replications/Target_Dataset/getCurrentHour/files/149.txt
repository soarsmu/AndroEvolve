package momen.alarme;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    TimePicker timePicker;
    AlarmManager alarmManager;
    Calendar calendar1,calendar2;
    Intent intent;
    PendingIntent pendingIntent1,pendingIntent2;
    Spinner spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        timePicker = (TimePicker) findViewById(R.id.timePicker);
        spinner = (Spinner) findViewById(R.id.spinner);
        calendar1 = Calendar.getInstance();
        calendar2 = Calendar.getInstance();
        NotificationManager notificationManager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(0);
    }
    public void a_on(View view)
    {
        int getHour = timePicker.getCurrentHour();
        int getMinute = timePicker.getCurrentMinute();
        if(String.valueOf(spinner.getSelectedItem()).matches("التنبيه الاول")) {
            calendar1.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
            calendar1.set(Calendar.MINUTE, timePicker.getCurrentMinute());
            calendar1.set(Calendar.SECOND,0);
            Toast.makeText(MainActivity.this,String.valueOf(spinner.getSelectedItem()),Toast.LENGTH_SHORT).show();
            intent = new Intent(MainActivity.this, Alarm_Receiver.class);
            pendingIntent1 = PendingIntent.getBroadcast(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar1.getTimeInMillis(), pendingIntent1);
        }
        else if(String.valueOf(spinner.getSelectedItem()).matches("التنبيه الثانى")) {
            calendar2.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
            calendar2.set(Calendar.MINUTE, timePicker.getCurrentMinute());
            calendar2.set(Calendar.SECOND,0);
            Toast.makeText(MainActivity.this,String.valueOf(spinner.getSelectedItem()),Toast.LENGTH_SHORT).show();
            intent = new Intent(MainActivity.this, Alarm_Receiver.class);
            pendingIntent2 = PendingIntent.getBroadcast(MainActivity.this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar2.getTimeInMillis(), pendingIntent2);
        }

    }
    public void a_off(View view)
    {
        alarmManager.cancel(pendingIntent1);
        alarmManager.cancel(pendingIntent2);
    }
}
