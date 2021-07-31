package com.example.timepicker;

import java.util.Calendar;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

public class MainActivity extends Activity {

	EditText et;
	TimePicker picker;
	Button b;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		et=(EditText) findViewById(R.id.editText1);
		picker=(TimePicker) findViewById(R.id.timePicker1);
		b=(Button) findViewById(R.id.button1);
		b.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
			/*	Calendar cal=Calendar.getInstance();
				cal.set(Calendar.HOUR_OF_DAY,6);
				cal.set(Calendar.MINUTE,00);
				//if(picker==cal.Set(Calendar.HOUR,23)){
					
					
					
				}*/
				
			et.setText("CurrentTime:"+picker.getCurrentHour()+":"+picker.getCurrentMinute());	
			Toast.makeText(getBaseContext(), "Current Time Selected :  "+picker.getCurrentHour()+":"+picker.getCurrentMinute(), Toast.LENGTH_LONG).show();
			
			}});
		
		
		
	}

	

}
