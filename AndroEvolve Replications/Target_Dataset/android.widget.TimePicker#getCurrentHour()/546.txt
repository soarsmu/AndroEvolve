package com.android.splitmyride.utils;

import java.util.Calendar;

import android.content.Context;
import android.widget.TimePicker;

public class TimeUtils {
	
	// Helper method to convert a TimePicker widget into an integer in epoch time format
    public static Long timePickerToEpochTime(TimePicker t){
    	Calendar deptTime = Calendar.getInstance();
    	Integer hour = t.getCurrentHour();
    	Integer min = t.getCurrentMinute();
    	deptTime.set(Calendar.HOUR_OF_DAY, hour);
    	deptTime.set(Calendar.MINUTE, min);
    	return deptTime.getTimeInMillis();
    	
    }
    
    // Returns the field of the given epoch time
    public static Integer epochTimeToCalendar(Long epochTime, Integer field){
    	Calendar cal = Calendar.getInstance();
    	cal.setTimeInMillis(epochTime);
    	return cal.get(field);
    }
}
