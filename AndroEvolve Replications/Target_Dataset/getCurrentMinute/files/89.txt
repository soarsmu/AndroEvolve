package com.greenhouse.widget;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import com.greenhouse.R;
import com.greenhouse.ui.Timer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

public class CustomDatePicker implements OnDateChangedListener, OnTimeChangedListener{


	private DatePicker datePicker;
	private TimePicker timePicker;
	private AlertDialog ad;
	private String start_time="0";
	public static String Init_Start_time;
	public static String ZZ="0";
	private Activity activity;
//	public static String Year="0",Month="0",Day="0",Hour="0",Minute="0";
	public static int Year,Month,Day,Hour,Minute;

	public CustomDatePicker(Activity activity) {
		this.activity = activity;
	}

	
	public void init(DatePicker datePicker, TimePicker timePicker) {
		Calendar calendar = Calendar.getInstance();	
		datePicker.init(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONDAY),calendar.get(Calendar.DAY_OF_MONTH),this);
		datePicker.setCalendarViewShown(false);
		
		timePicker.setIs24HourView(true);
		timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
		timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
		calendar.set(datePicker.getYear(),datePicker.getMonth(),datePicker.getDayOfMonth(),timePicker.getCurrentHour(),timePicker.getCurrentMinute());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm");
		Init_Start_time = sdf.format(calendar.getTime());		
		Log.i("init start_time","init start time"+Init_Start_time);
	}


	public AlertDialog DatePickDialog(final TextView inputData){
		LinearLayout TimeLayout = (LinearLayout)activity.getLayoutInflater().inflate(R.layout.date_picker, null);
		datePicker = (DatePicker) TimeLayout.findViewById(R.id.datepicker);
		timePicker = (TimePicker) TimeLayout.findViewById(R.id.timepicker);
		init(datePicker, timePicker);
		
		timePicker.setOnTimeChangedListener(this);
		
		ad = new AlertDialog.Builder(activity)
		.setTitle(" :")			
		.setView(TimeLayout)
		.setCancelable(false)
		.setPositiveButton("确定", new DialogInterface.OnClickListener() {					
			public void onClick(DialogInterface dialog, int which) {
				inputData.setText(start_time);
				Timer.startFlag = true;					
			}
		})
			
		.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				inputData.setText("请设置时间");
			}
		}).show();			
		onDateChanged(null,0,0,0);		
		return ad;
	}
	
	
	public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
		onDateChanged(null, 0, 0, 0);
	}
		@Override
	public void onDateChanged(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		// TODO Auto-generated method stub
		Calendar calendar = Calendar.getInstance();
		calendar.set(datePicker.getYear(), datePicker.getMonth(),
				datePicker.getDayOfMonth(), timePicker.getCurrentHour(),
				timePicker.getCurrentMinute());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm");
		start_time = sdf.format(calendar.getTime());

		int a = datePicker.getYear();
		int b = datePicker.getMonth();
		int c = datePicker.getDayOfMonth();
		int d = timePicker.getCurrentHour();
		int e = timePicker.getCurrentMinute();
		
		Year = a;
		Month = b + 1;
		Day = c;
		Hour = d;
		Minute = e;
		
		ZZ = start_time;
		ad.setTitle(start_time);

	}



}
