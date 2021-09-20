package com.foreign.Fuse.Controls.Native.Android;

// fuse defined imports
import com.uno.UnoObject;
import com.uno.BoolArray;
import com.uno.ByteArray;
import com.uno.CharArray;
import com.uno.DoubleArray;
import com.uno.FloatArray;
import com.uno.IntArray;
import com.uno.LongArray;
import com.uno.ObjectArray;
import com.uno.ShortArray;
import com.uno.StringArray;
import com.Bindings.UnoHelper;
import com.Bindings.UnoWrapped;
import com.Bindings.ExternedBlockHost;

public class TimePickerView
{
    static void debug_log(Object message)
    {
        android.util.Log.d("v2", (message==null ? "null" : message.toString()));
    }

    public static Object Create218()
    {
        return new android.widget.TimePicker(com.apps.v2.v2.GetRootActivity());
    }
    
    public static long GetTimeInMsSince1970InUtc219(final UnoObject _this, final Object timePickerHandle)
    {
        android.widget.TimePicker timePicker = (android.widget.TimePicker)timePickerHandle;
        
        int hour, minute;
        
        if (android.os.Build.VERSION.SDK_INT >= 23) {
        	hour = timePicker.getHour();
        	minute = timePicker.getMinute();
        } else {
        	hour = timePicker.getCurrentHour();
        	minute = timePicker.getCurrentMinute();
        }
        
        // Remove date offsets so we only express the time in UTC
        java.util.Calendar cal = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC"), java.util.Locale.getDefault());
        
        cal.set(java.util.Calendar.YEAR, 1970);
        cal.set(java.util.Calendar.MONTH, 0); // Android month starts at 0
        cal.set(java.util.Calendar.DAY_OF_MONTH, 1);
        
        cal.set(java.util.Calendar.AM_PM, java.util.Calendar.AM);
        cal.set(java.util.Calendar.HOUR, hour);
        cal.set(java.util.Calendar.MINUTE, minute);
        cal.set(java.util.Calendar.SECOND, 0);
        cal.set(java.util.Calendar.MILLISECOND, 0);
        
        return cal.getTimeInMillis();
    }
    
    public static void SetIs24HourView220(final UnoObject _this, final Object timePickerHandle,final boolean value)
    {
        android.widget.TimePicker timePicker = (android.widget.TimePicker)timePickerHandle;
        
        timePicker.setIs24HourView(value);
    }
    
    public static void SetTime221(final UnoObject _this, final Object timePickerHandle,final long msSince1970InUtc)
    {
        java.util.Calendar cal = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC"), java.util.Locale.getDefault());
        cal.setTimeInMillis(msSince1970InUtc);
        
        int hour = cal.get(java.util.Calendar.HOUR);
        if (cal.get(java.util.Calendar.AM_PM) == java.util.Calendar.PM)
        	hour += 12;
        int minute = cal.get(java.util.Calendar.MINUTE);
        
        android.widget.TimePicker timePicker = (android.widget.TimePicker)timePickerHandle;
        
        if (android.os.Build.VERSION.SDK_INT >= 23) {
        	timePicker.setHour(hour);
        	timePicker.setMinute(minute);
        } else {
        	timePicker.setCurrentHour(hour);
        	timePicker.setCurrentMinute(minute);
        }
    }
    
}
