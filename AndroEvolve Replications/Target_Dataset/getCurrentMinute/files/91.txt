package com.example.mdpiyalhasan.piyalalarmclock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class piyal extends AppCompatActivity {
    public Button alarm_on;
    public Button alarm_off;
    TimePicker alarm_timepicker;
    TextView tx;
    Context contex;
    Intent intent;
    AlarmManager alarmManager;
    PendingIntent pendingIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piyal);
        this.contex=this;
        alarm();
    }
    public void alarm()
    {
        alarm_on=(Button)findViewById(R.id.button);
        alarm_off=(Button)findViewById(R.id.button2);
        //tx=(TextView)findViewById(R.id.textView);
        alarm_timepicker=(TimePicker)findViewById(R.id.timePicker);
        alarmManager=(AlarmManager) getSystemService(ALARM_SERVICE);
        alarm_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Toast.makeText(getBaseContext(),"Alarm off",Toast.LENGTH_LONG).show();
                intent.putExtra("extra", "alarm off");
                alarmManager.cancel(pendingIntent);
                sendBroadcast(intent);

            }
        });

        //set the alarm on button
        intent=new Intent(piyal.this,Alarm_Receiver.class);
        final Calendar cal=Calendar.getInstance();
        alarm_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(),"Alarm set to"+alarm_timepicker.getCurrentHour()+ ":"+alarm_timepicker.getCurrentMinute(),Toast.LENGTH_SHORT).show();
                cal.set(Calendar.HOUR_OF_DAY,alarm_timepicker.getCurrentHour());
                cal.set(Calendar.MINUTE,alarm_timepicker.getCurrentMinute());
                intent.putExtra("extra","alarm on");
                pendingIntent=PendingIntent.getBroadcast(piyal.this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.set(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(),pendingIntent);
            }
        });
    }
}
