package com.foreign.Native.Android;

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
        android.util.Log.d("HSMRO", (message==null ? "null" : message.toString()));
    }

    public static Object Create428()
    {
        android.widget.TimePicker timePicker = new android.widget.TimePicker(com.apps.hsmro.HSMRO.GetRootActivity());
        timePicker.setIs24HourView(true);
        return timePicker;
    }
    
    public static void GetTime429(final Object handle,final com.uno.IntArray time)
    {
        android.widget.TimePicker tp = (android.widget.TimePicker)handle;
        if (android.os.Build.VERSION.SDK_INT >= 23) {
        	time.set(0, tp.getHour());
        	time.set(1, tp.getMinute());
        } else {
        	time.set(0, tp.getCurrentHour());
        	time.set(1, tp.getCurrentMinute());
        }
    }
    
    public static void Init430(final Object handle,final com.foreign.Uno.Action timeChangedCallback)
    {
        ((android.widget.TimePicker)handle).setOnTimeChangedListener(new android.widget.TimePicker.OnTimeChangedListener() {
        	public void onTimeChanged(android.widget.TimePicker view, int hourOfDay, int minute) {
        		timeChangedCallback.run();
        	}
        });
    }
    
    public static void SetTime431(final Object handle,final int hour,final int minute)
    {
        android.widget.TimePicker tp = (android.widget.TimePicker)handle;
        if (android.os.Build.VERSION.SDK_INT >= 23) {
        	tp.setHour(hour);
        	tp.setMinute(minute);
        } else {
        	tp.setCurrentHour(hour);
        	tp.setCurrentMinute(minute);
        }
    }
    
}
