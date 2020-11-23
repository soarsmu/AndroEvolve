package com.polozaur.testing.alarms;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

public class NewTaskActivity extends Activity {

	DatePicker datePicker;
	TimePicker timePicker;
	Button setTaskBtn;
	PendingIntent pendingIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.newtask_layout);

		this.datePicker = (DatePicker) findViewById(R.id.datePicker);
		this.timePicker = (TimePicker) findViewById(R.id.timePicker);
		this.setTaskBtn = (Button) findViewById(R.id.setTaskBtn);

		this.setTaskBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent wakeUpIntent = new Intent(NewTaskActivity.this, TaskService.class);
				pendingIntent = PendingIntent.getService(NewTaskActivity.this,
						0, wakeUpIntent, 0);
				AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
				Calendar calendar = Calendar.getInstance();
				calendar.setTimeInMillis(System.currentTimeMillis());
				calendar.clear();
				calendar.set(datePicker.getYear(), datePicker.getMonth(),
						datePicker.getDayOfMonth(), timePicker.getCurrentHour(),
						timePicker.getCurrentMinute());
				alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
						pendingIntent);
				Toast.makeText(NewTaskActivity.this,
						"Year: " + datePicker.getYear() + 
						"\nMonth: " + datePicker.getMonth() +
						"\nDay: " + datePicker.getDayOfMonth() + 
						"\nHour: " + timePicker.getCurrentHour() + 
						"\nMinute: " + timePicker.getCurrentMinute(),Toast.LENGTH_LONG).show();
			}
		});

	}
}
