package com.example.autoreply;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TimePicker;

public class Scheduler extends Activity {

	private Button settimes;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scheduler);
		getTimes();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.scheduler, menu);
		return true;
	}
	
	public void getTimes(){
		settimes=(Button)findViewById(R.id.set);
		final   TimePicker start = (TimePicker) findViewById(R.id.timestart);
		final   TimePicker stop = (TimePicker) findViewById(R.id.timestop);
		
		settimes.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
        		GlobalVariables g = GlobalVariables.getInstance();
        		g.setStartHour(start.getCurrentHour());
        		g.setStartMin(start.getCurrentMinute());
        		g.setStopHour(start.getCurrentHour());
        		g.setStopMin(stop.getCurrentMinute());
        		//System.out.println(start.getCurrentHour());
				//System.out.println(stop.getCurrentHour());
				
			}
			
		});


	}

}
