package com.thrivepregnancy.ui;


import com.thrivepregnancy.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

/**
 * A Fragment encapsulating a AlertDialog containing a TimePicker. 
 */
public class TimeDialogFragment extends DialogFragment{

	private TimePicker 			timePicker;
	private OnTimeSetListener 	timeListener;
	private OnTimeChangedListener m_timeChangedListener;
	private OnTimeChangedListener m_timeChangedListenerNull = null;
	
	private static int m_hour;
	private static int m_min;
	
	public static TimeDialogFragment newInstance(String fragmentNumber, int h, int m, OnTimeSetListener timeListener) {
		TimeDialogFragment newInstance = new TimeDialogFragment();
		newInstance.timeListener =timeListener;
		Bundle args = new Bundle();
		args.putString("fragnum", fragmentNumber);
		args.putInt("hour", h);
		args.putInt("min", m);
		
		m_hour = h;
		m_min = m;
		
		newInstance.setArguments(args);
			
		return newInstance;
	}
		
	private void updateDisplay(TimePicker timePicker, int hourOfDay, int minute) {
	    int nextMinute = 0;

	    timePicker.setOnTimeChangedListener(m_timeChangedListenerNull);

	    if (minute >= 55 && minute <= 59) 
	        nextMinute = 55;  
	    else if (minute  >= 50) 
	        nextMinute = 50;
	    else if (minute >= 45) 
	        nextMinute = 45;
	    else if (minute >= 40) 
	        nextMinute = 40; 
	    else if (minute  >= 35) 
	        nextMinute = 35;
	    else if (minute >= 30) 
	        nextMinute = 30;
	    else if (minute >= 25) 
	        nextMinute = 25;
	    else if (minute  >= 20) 
	        nextMinute = 20;
	    else if (minute >= 15) 
	        nextMinute = 15;
	    else if (minute >= 10) 
	        nextMinute = 10;
	    else if (minute >= 5) 
	        nextMinute = 5;
	    else if (minute > 0) 
	        nextMinute = 0;
	    else { 
	        nextMinute = 55; 
	    }

	    if (minute - nextMinute == 1) {
	        if (minute >= 55 && minute <= 59) 
	            nextMinute = 00; 
	        else if(minute  >= 50) 
	            nextMinute = 55;
	        else if(minute >= 45) 
	            nextMinute = 50;
	        else if(minute >= 40) 
	            nextMinute = 45;
	        else if(minute >= 35) 
	            nextMinute = 40;
	        else if(minute >= 30) 
	            nextMinute = 35;
	        else if(minute >= 25) 
	            nextMinute = 30;
	        else if(minute >= 20) 
	            nextMinute = 25;
	        else if(minute >= 15) 
	            nextMinute = 20;
	        else if(minute >= 10) 
	            nextMinute = 15;
	        else if(minute >= 5) 
	            nextMinute = 10;
	        else if(minute > 0) 
	            nextMinute = 5; 
	        else { 
	            nextMinute = 5; 
	        }
	    }

	    timePicker.setCurrentMinute(nextMinute);

	    timePicker.setOnTimeChangedListener(m_timeChangedListener);

	}
	
	@Override
	public AlertDialog onCreateDialog(Bundle savedInstanceState) {
		
		Activity parentActivity = getActivity();	
		timePicker = (TimePicker)parentActivity.getLayoutInflater().inflate(R.layout.time_picker, null);
		
		timePicker.setCurrentHour(m_hour);
		timePicker.setCurrentMinute(m_min);
		
		m_timeChangedListener = new TimePicker.OnTimeChangedListener() {

			    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
			        updateDisplay(view, hourOfDay, minute);          
			    }
			};
			
		//timePicker.setOnTimeChangedListener(m_timeChangedListener);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(parentActivity);
		
		builder.setTitle("Appointment Time");		
		builder.setView(timePicker);
		
		// Pass the selected date back to the activity only when Save is clicked
		builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				timeListener.onTimeSet(timePicker, timePicker.getCurrentHour(), timePicker.getCurrentMinute());				
			}
		});
		
		return builder.create();
	}
}
