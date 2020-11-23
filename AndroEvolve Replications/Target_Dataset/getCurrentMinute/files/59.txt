package com.example.retentionscheduler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CalendarView.OnDateChangeListener;
import android.widget.TimePicker;

public class Create1 extends ActionBarActivity implements OnClickListener {
	CalendarView calendarView;
	TimePicker timePicker;
	String name = "",description="",files="";
	String time="";
	String calendar="";
	Button button;
	DataAccessObject dao = new DataAccessObject(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create1);
		
		final Intent intent = getIntent();
		name = intent.getStringExtra("eventTitle");
		description = intent.getStringExtra("description");
		files=intent.getStringExtra("files");
		button = (Button)findViewById(R.id.button1);
		button.setOnClickListener(this);
		
		calendarView=(CalendarView) findViewById(R.id.calendarView1);
		calendarView.setDate(Calendar.getInstance().getTimeInMillis(),false,true);
		calendarView.setOnDateChangeListener(new OnDateChangeListener() {
			long selected = calendarView.getDate();
			@Override
			public void onSelectedDayChange(CalendarView view, int year, int month,
					int dayOfMonth) {
				//Toast.makeText(getApplicationContext(), ""+selected, 0).show();// TODO Auto-generated method stub
				
				//System.out.println(String.valueOf(year));
				//System.out.println(String.valueOf(month));
				//System.out.println(String.valueOf(dayOfMonth));
				
				if(selected != view.getDate()){
						selected=view.getDate();
						month=month+1;
						calendar=month+"A"+dayOfMonth+"A"+year;
				}
			}
		});
		
		DateFormat dateFormat1 = new SimpleDateFormat("MM");
		DateFormat dateFormat2 = new SimpleDateFormat("dd");
		DateFormat dateFormat3 = new SimpleDateFormat("yyyy");
		Date date = new Date();
		String month = (dateFormat1.format(date)); 
		String day = (dateFormat2.format(date));
		String year = (dateFormat3.format(date));
		if(month.charAt(0)=='0'){
			month=""+month.charAt(1);
		}
		if(day.charAt(0)=='0'){
			day=""+day.charAt(1);
		}
		calendar=month+"A"+day+"A"+year;
		System.out.println(calendar);
		timePicker = (TimePicker) findViewById(R.id.timePicker1);
		 int hour = timePicker.getCurrentHour();
		 int min = timePicker.getCurrentMinute();
		 timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
			int hour = timePicker.getCurrentHour();
			 int min = timePicker.getCurrentMinute();
	            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
	                if(hour!= timePicker.getCurrentHour() || min !=timePicker.getCurrentMinute()){
	               	 hour = timePicker.getCurrentHour();
	               	 min = timePicker.getCurrentMinute();
	               	 time=hour+":"+min;
	                }
	            }
	        });
		 time=hour+":"+min;
		 
		 //dao.writeFile(name, description, calendar, time, files);
	}
	@Override
	public void onClick(View v){
          //respond to click
		if (v.getId() == R.id.button1) {
			try {
				dao.writeFile(name, description, calendar, time, files);
				
			}catch(Exception e){}
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is
		// present.
		getMenuInflater().inflate(R.menu.create1, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
