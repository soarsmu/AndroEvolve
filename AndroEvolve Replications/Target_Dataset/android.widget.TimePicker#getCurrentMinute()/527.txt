package com.constants;

import java.util.Calendar;
import java.util.Date;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

import com.stylist.R;


public class DateTimePickerDialog extends AlertDialog implements
		OnDateChangedListener, OnTimeChangedListener {

	private DatePicker dpReminder;
	private TimePicker tpReminder;
	private Calendar _c;
	TabHost tabHost;
	private float lastX;
	private LinearLayout lndatepicker;
	private LinearLayout lntimepicker;
	private long duration_time = 500;
	String date="",time="";
	/**
	 * @return the dpReminder
	 */
	public DatePicker getDpReminder() {
		return dpReminder;
	}

	/**
	 * @param dpReminder
	 *            the dpReminder to set
	 */
	public void setDpReminder(DatePicker dpReminder) {
		this.dpReminder = dpReminder;
	}

	/**
	 * @return the tpReminder
	 */
	public TimePicker getTpReminder() {
		return tpReminder;
	}

	/**
	 * @param tpReminder
	 *            the tpReminder to set
	 */
	public void setTpReminder(TimePicker tpReminder) {
		this.tpReminder = tpReminder;
	}

	/**
	 * @return the c
	 */
	public Calendar getCal() {
		return _c;
	}

	/**
	 * @param c
	 *            the calendar to set
	 */
	public void setCal(Calendar c) {
		this._c = c;
	}
	
	public Date getDate(){
		Calendar cal = Calendar.getInstance();
		cal.setTime(Helper.getDateFromDatePicket(dpReminder));
		cal.set(Calendar.HOUR_OF_DAY, tpReminder.getCurrentHour());
		cal.set(Calendar.MINUTE, tpReminder.getCurrentMinute());
		return cal.getTime();
	}

	public DateTimePickerDialog(Context context) {
		super(context);
		
		LayoutInflater inflater = (LayoutInflater) getLayoutInflater();
		View customView = inflater.inflate(R.layout.date_time_picker, null);

		tabHost = (TabHost) customView.findViewById(R.id.tabhost);
		tabHost.setup();

		TabHost.TabSpec spec = tabHost.newTabSpec("tag1");

		spec.setContent(R.id.datepicker);
		spec.setIndicator("Date");
		tabHost.addTab(spec);

		spec = tabHost.newTabSpec("tag2");
		spec.setContent(R.id.timepicker);
		spec.setIndicator("Time");
		tabHost.addTab(spec);

		this.setView(customView);
		dpReminder = (DatePicker) customView.findViewById(R.id.dpReminder);
		tpReminder = (TimePicker) customView.findViewById(R.id.tpReminder);

		lndatepicker = (LinearLayout) customView.findViewById(R.id.datepicker);
		lntimepicker = (LinearLayout) customView.findViewById(R.id.timepicker);
		_c = Calendar.getInstance();
		setTitle("Select Date and Time");
	}

	public void setDateTime(Calendar cal,String date11) {
		this._c.setTime(cal.getTime());
		
		String [] arr = date11.split("-");
		
		dpReminder.init(Integer.parseInt(arr[0]),(Integer.parseInt(arr[1])-1),
				Integer.parseInt(arr[2]), DateTimePickerDialog.this);
		
//		dpReminder.init(_c.get(Calendar.YEAR), _c.get(Calendar.MONTH),
//				_c.get(Calendar.DAY_OF_MONTH), DateTimePickerDialog.this);

		tpReminder.setCurrentHour(_c.get(Calendar.HOUR_OF_DAY));
		tpReminder.setCurrentMinute(_c.get(Calendar.MINUTE));
		tpReminder.setIs24HourView(false);

		tpReminder.setOnTimeChangedListener(DateTimePickerDialog.this);

		time = Helper.getStringTimeFromTime(tpReminder.getCurrentHour(),
				tpReminder.getCurrentMinute());
		/*String title = Helper.dateToString(_c.getTime(),
				Helper.NORMAL_DAY_OF_WEEK_FORMAT)
				+ ", "
				+ Helper.getStringTimeFromTime(tpReminder.getCurrentHour(),
						tpReminder.getCurrentMinute());*/
		date = _c.get(Calendar.YEAR)+"-"+ _c.get(Calendar.MONTH)+"-"+_c.get(Calendar.DAY_OF_MONTH);
		String title = date +","+time;
		setTitle(title);
	}

	@Override
	public void onDateChanged(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		_c = Calendar.getInstance();
		_c.set(year, monthOfYear, dayOfMonth);

		/*String title = Helper.dateToString(_c.getTime(),
				Helper.NORMAL_DAY_OF_WEEK_FORMAT)
				+ ", "
				+ Helper.getStringTimeFromTimePicker(tpReminder);*/
		date = year+"-"+monthOfYear+"-"+dayOfMonth;
		String title =date
				+","
				+ time;
		setTitle(title);

	}

	@Override
	public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
		// TODO Auto-generated method stub

	/*	String title = Helper.dateToString(
				Helper.getDateFromDatePicket(dpReminder),
				Helper.NORMAL_DAY_OF_WEEK_FORMAT)
				+ ", "
				+ Helper.getStringTimeFromTime(hourOfDay, minute);*/
		_c.set(Calendar.HOUR_OF_DAY, hourOfDay);
		_c.set(Calendar.MINUTE, minute);
		time = Helper.getStringTimeFromTime(hourOfDay, minute);
		String title = date +","+time;
		setTitle(title);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent touchevent) {
		
		switch (touchevent.getAction()) {
			case MotionEvent.ACTION_DOWN: {
				lastX = touchevent.getX();
				break;
			}
			case MotionEvent.ACTION_UP: {
				float currentX = touchevent.getX();
	
				// if left to right swipe on screen
				if (lastX < currentX) {
	
					switchTabs(false);
				}
	
				// if right to left swipe on screen
				if (lastX > currentX) {
					switchTabs(true);
				}
	
				break;
			}
		}
		
		return super.onTouchEvent(touchevent);
	}

	public Animation inFromRightAnimation() {

		Animation inFromRight = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, +1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		inFromRight.setDuration(duration_time);
		inFromRight.setInterpolator(new AccelerateInterpolator());
		return inFromRight;
	}
	public Animation inFromLeftAnimation() {

		Animation inFromRight = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, -1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		inFromRight.setDuration(duration_time);
		inFromRight.setInterpolator(new AccelerateInterpolator());
		return inFromRight;
	}

	public Animation outToRightAnimation() {
		Animation outtoLeft = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, +1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		outtoLeft.setDuration(duration_time);
		outtoLeft.setInterpolator(new AccelerateInterpolator());
		return outtoLeft;
	}
	
	public Animation outToLeftAnimation() {
		Animation outtoLeft = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, -1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);
		outtoLeft.setDuration(duration_time);
		outtoLeft.setInterpolator(new AccelerateInterpolator());
		return outtoLeft;
	}

	
	
	public void switchTabs(boolean direction) {
		if (!direction) // false = left to right
		{
			if (tabHost.getCurrentTab() != 0) {
				tabHost.setCurrentTab(tabHost.getCurrentTab() - 1);
				
				lndatepicker.setAnimation(inFromLeftAnimation());
				lntimepicker.setAnimation(outToRightAnimation());
			}
			// else
			// tabHost.setCurrentTab(tabHost.getTabWidget().getTabCount() - 1);

		} else
		// right to left
		{
			if (tabHost.getCurrentTab() != (tabHost.getTabWidget()
					.getTabCount() - 1)) {
				tabHost.setCurrentTab(tabHost.getCurrentTab() + 1);
				lndatepicker.setAnimation(outToLeftAnimation());
				lntimepicker.setAnimation(inFromRightAnimation());
			}
			// else
			// tabHost.setCurrentTab(0);
		}
	}

}
