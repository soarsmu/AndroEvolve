package com.example.home.punctuality;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TimePicker;


public class EditSchedule2 extends Activity {

    private TimePicker timePicker1;
    private TimePicker timePicker2;
    private TimePicker timePicker3;
    private TimePicker timePicker4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_schedule2);

        timePicker1 = (TimePicker) findViewById(R.id.tp1);
        int hourtp1 = timePicker1.getCurrentHour();
        int mintp1 = timePicker1.getCurrentMinute();

        timePicker2 = (TimePicker) findViewById(R.id.tp2);
        int hourtp2 = timePicker2.getCurrentHour();
        int mintp2 = timePicker2.getCurrentMinute();

        timePicker3 = (TimePicker) findViewById(R.id.tp3);
        int hourtp3 = timePicker3.getCurrentHour();
        int mintp3 = timePicker3.getCurrentMinute();

        timePicker4 = (TimePicker) findViewById(R.id.tp4);
        int hourtp4 = timePicker4.getCurrentHour();
        int mintp4 = timePicker4.getCurrentMinute();
    }
}
