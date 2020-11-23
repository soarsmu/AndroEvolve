package com.myandroidapplication;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TimePicker;
import android.app.Activity;

public class Time extends Activity implements OnClickListener {

	private Chronometer chronometer;
	private TimePicker timePicker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_time);
		chronometer = (Chronometer) findViewById(R.id.chronometer);
		((Button) findViewById(R.id.start_button)).setOnClickListener(this);
		// ((Button) findViewById(R.id.stop_button)).setOnClickListener(this);
		timePicker = (TimePicker) findViewById(R.id.timePicker1);
	}

	@Override
	public void onClick(View v) {

		// switch (v.getId()) {
		// case R.id.start_button:
		long difference = 0;
		int pickedTime = timePicker.getCurrentHour();
		int currentTime = new java.sql.Time(System.currentTimeMillis())
				.getHours();
		if (pickedTime < currentTime) {
			difference = (long) (currentTime - pickedTime) * 3600000;
			pickedTime = timePicker.getCurrentMinute();
			currentTime = new java.sql.Time(System.currentTimeMillis())
					.getMinutes();
			difference += (long) (currentTime - pickedTime) * 60000;

		} else if(pickedTime == currentTime) {
			pickedTime = timePicker.getCurrentMinute();
			currentTime = new java.sql.Time(System.currentTimeMillis())
					.getMinutes();
			if(pickedTime<=currentTime){
				difference=(long)(currentTime-pickedTime)*60000;
			}else{
				difference=(long)((24*3600000)-((pickedTime-currentTime)*60000));
			}
		}else{
			difference = (long) (((24 - pickedTime) * 3600000) + (currentTime * 3600000));
			pickedTime = timePicker.getCurrentMinute();
			currentTime = new java.sql.Time(System.currentTimeMillis())
					.getMinutes();
			difference += (long) (currentTime - pickedTime) * 60000;
		}

		difference += new java.sql.Time(System.currentTimeMillis())
				.getSeconds();


		chronometer.setBase(SystemClock.elapsedRealtime() - difference);
		chronometer.start();
		// break;
		// case R.id.stop_button:
		// chronometer.stop();
		// break;

	}
	

}