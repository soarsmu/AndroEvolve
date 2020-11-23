package example.codeclan.com.alfor3;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class Alarm extends AppCompatActivity {


    AlarmManager alarmManager;
    TextView alarmReason;
    PendingIntent pendingIntent;
    Calendar calendar;
    TimePicker  timePicker;
    Switch repeat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        //Initialize everything on the page.
        timePicker = (TimePicker) findViewById(R.id.timePicker);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmReason = (TextView) findViewById(R.id.alarmReason);
        Button setAlarm = (Button) findViewById(R.id.setAlarm);
        calendar = Calendar.getInstance();
        repeat = (Switch)findViewById(R.id.repeatSwitch);
        repeat.setChecked(false);
        final Intent sender  = new Intent(Alarm.this, AlarmReceiver.class);
        System.out.println(sender);


        repeat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Toast.makeText(Alarm.this, "This alarm will repeat daily", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(Alarm.this, "This one day only alarm", Toast.LENGTH_SHORT).show();
                }
            }
        });



        setAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                calendar.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
                calendar.set(Calendar.MINUTE,timePicker.getCurrentMinute());
                System.out.println(timePicker.getCurrentHour());
                System.out.println(timePicker.getCurrentMinute());



                int hour = timePicker.getCurrentHour();
                int min= timePicker.getCurrentMinute();

                String hour_string = String.valueOf(hour);
                String minute_string = String.valueOf(min);

                if(min < 10){
                     minute_string = "0" + String.valueOf(min);
                 }

                Toast.makeText(Alarm.this, "You have set alarm reminder " + alarmReason.getText().toString() + " " +
                        hour_string + ":" + minute_string, Toast.LENGTH_LONG).show();

                // its probable that the 0 below will need to be a variable that keeps changing if you want to add multiple alarms.
                pendingIntent = PendingIntent.getBroadcast(Alarm.this, 0, sender, PendingIntent.FLAG_UPDATE_CURRENT);

                if(repeat.isChecked()){
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                            AlarmManager.INTERVAL_DAY, pendingIntent);
                }else{
                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),pendingIntent);
                }


            }



        });

    }


}
