package hagai.edu.implicitintents;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.AlarmClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void dial(View view) {
        //data
        Uri tel = Uri.parse("tel:0544513183");

        Intent dialIntent = new Intent(Intent.ACTION_DIAL, tel);

        //intent resolution?
        startActivity(dialIntent);


    }

    public void website(View view) {
        Uri web = Uri.parse("http://www.fourtec.com");
        Intent webIntent = new Intent(Intent.ACTION_VIEW, web);

        //Intent resolution
        if (canOpen(webIntent)) {
            startActivity(webIntent);
        } else {
            Toast.makeText(this, "No Browser", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean canOpen(Intent webIntent) {
        return webIntent.resolveActivity(getPackageManager()) != null;
    }


    public void createAlarm(String message, int hour, int minutes) {
        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM)
                .putExtra(AlarmClock.EXTRA_MESSAGE, message)
                .putExtra(AlarmClock.EXTRA_HOUR, hour)
                .putExtra(AlarmClock.EXTRA_MINUTES, minutes);
//                .putExtra(AlarmClock.EXTRA_SKIP_UI, true);


        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }


    public void setAlarm(View view) {
        TimePicker picker = (TimePicker) findViewById(R.id.timePicker);
        int hour = 0;
        int minutes = 0;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            hour = picker.getHour();
            minutes = picker.getMinute();

        } else {
            hour = picker.getCurrentHour();
            minutes = picker.getCurrentMinute();
        }
        

//        int hour = picker.getCurrentHour();
//        int minutes = picker.getCurrentMinute();

        createAlarm("Good Morning ", hour, minutes);
    }
}
