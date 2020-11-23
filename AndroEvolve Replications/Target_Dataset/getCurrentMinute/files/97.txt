package com.igm.android.storyboard;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class Settings extends Activity implements OnClickListener {

    private EditText edit1 = null;
    int anneecour;
    String strDate;
    private TimePicker pk;
    Button notif, parcour, apropo;

    private ScheduleClient sc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);

        notif = (Button) findViewById(R.id.notification);
        notif.setOnClickListener(this);


        pk = (TimePicker) findViewById(R.id.picker);


        sc = new ScheduleClient(this);
        sc.doBindService();


    }


    public void notification(int day, int month, int year, int hour, int minute, int second) {
        // Create a new calendar set to the date chosen
        // we set the time to midnight (i.e. the first minute of that day)
        Calendar c = Calendar.getInstance();
        c.set(year, month, day);
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, second);
        c.get(Calendar.YEAR);

        // Ask our service to set an alarm for that date, this activity talks to the client that talks to the service

        sc.setAlarmForNotification(c);
        // Notify the user what they just did
        Toast.makeText(this, "Notification set for: " + day + "/" + (month + 1) + "/" + year, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onClick(View v) {
        Calendar cal = Calendar.getInstance();
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.notification:
                //Toast.makeText(getApplicationContext(), strDate, Toast.LENGTH_LONG).show();
                if (cal.get(Calendar.MONTH) < 7)
                    notification(28, 06, cal.get(Calendar.YEAR), pk.getCurrentHour(), pk.getCurrentMinute(), 0);
                if (cal.get(Calendar.MONTH) < 6)
                    notification(14, 05, cal.get(Calendar.YEAR), pk.getCurrentHour(), pk.getCurrentMinute(), 0);
                if (cal.get(Calendar.MONTH) < 5)
                    notification(8, 04, cal.get(Calendar.YEAR), pk.getCurrentHour(), pk.getCurrentMinute(), 0);

                break;


            default:
                break;
        }
    }


}
 
	
 
	
	