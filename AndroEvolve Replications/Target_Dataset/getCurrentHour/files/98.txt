package hacktober.snoozify;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

/**
 * Created by jcd4912 on 10/21/2017.
 */

public class NewAlarmActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_new_alarm);
        setTitle("Reminder");

        Button btnNext = (Button) findViewById(R.id.btnNext1);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myintent = new Intent(NewAlarmActivity.this, MainActivity.class);
                startActivity(myintent);
                finish();
            }
        });

        Button btnSet = (Button) findViewById(R.id.btnSetDateTime);
//        final DatePicker dp = (DatePicker) findViewById(R.id.datePicker);
        final TimePicker tp = (TimePicker) findViewById(R.id.timePicker);

        btnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String strDateTime = dp.getYear() + "-" + (dp.getMonth() + 1) + "-" + dp.getDayOfMonth() + " " + tp.getCurrentHour() + ":" + tp.getCurrentMinute();
                Alarm A = new Alarm(tp.getCurrentHour(), tp.getCurrentMinute());
                String strDateTime = tp.getCurrentHour() + ":" + tp.getCurrentMinute();
                Toast.makeText(NewAlarmActivity.this, "User has selected " + strDateTime, Toast.LENGTH_LONG).show();

                finish();// move to previous activity
            }
        });

    }
}
