package com.example.carlos.exercisebuddy;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;


public class SleepActivity extends ActionBarActivity {
    public final static String ACTIVITY_TIMES = "com.example.carlos.exercisebuddy.SleepActivity.times";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep);
    }

    public void sendWeekDayTimes(View view){
        TimePicker bedTimeWeekday = (TimePicker) findViewById(R.id.timePicker);
        TimePicker awakeWeekday = (TimePicker) findViewById(R.id.timePicker2);


        int bedtimeH = bedTimeWeekday.getCurrentHour();
        int bedtimeMin = bedTimeWeekday.getCurrentMinute();
        int awakeH = awakeWeekday.getCurrentHour();
        int awakeMin = awakeWeekday.getCurrentMinute();
        //final String weekday = bedtimeH + " " + bedtimeMin + " " + awakeH + " " + awakeMin;
        final String weekday = String.format("%02d", bedtimeH) + " " + String.format("%02d", bedtimeMin) + " " + String.format("%02d", awakeH) + " " + String.format("%02d", awakeMin);

       setContentView(R.layout.activity_sleep_weekend);
        Button moveToMain = (Button) findViewById(R.id.moveToWeek);

        moveToMain.setOnClickListener(new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getBaseContext(),MainActivity.class);
            TimePicker bedTimeWeekend = (TimePicker) findViewById(R.id.timePicker);
            TimePicker awakeWeekend = (TimePicker) findViewById(R.id.timePicker2);
            int weekendbedtimeH = bedTimeWeekend.getCurrentHour();
            int weekendbedtimeMin = bedTimeWeekend.getCurrentMinute();
            int weekendawakeH = awakeWeekend.getCurrentHour();
            int weekendawakeMin = awakeWeekend.getCurrentMinute();
            String.format("%02d", weekendbedtimeH);
            //String weekend = weekendbedtimeH + " " + weekendbedtimeMin + " " + weekendawakeH + " " + weekendawakeMin;
            String weekend = String.format("%02d", weekendbedtimeH) + " " + String.format("%02d", weekendbedtimeMin) + " " + String.format("%02d", weekendawakeH) + " " + String.format("%02d", weekendawakeMin);




            String times = weekday + " " + weekend;

            Log.i("playing",times);
            intent.putExtra(ACTIVITY_TIMES, times);
            startActivity(intent);
        }

    });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sleep, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
