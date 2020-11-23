package aparcandgo;

import android.annotation.TargetApi;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
/**
 * Hora de entrada: 12:00 � 13:00 - 14:00
 * Hora de salida: 9:15 � 10:15 - 11:00
 * @author Fran
 *
 */
public class PortTimePickDialog extends TimePickerDialog {
	final OnTimeSetListener mCallback;
	TimePicker mTimePicker;
	
	private boolean isEnter;
	private List<String> displayedMinuteValues;
	private List<String> displayedHourValues;

	public PortTimePickDialog(Context context, OnTimeSetListener callBack, boolean isEnter) {
		super(context, callBack, 2, 0,	true);
		
		this.mCallback = callBack;
		this.isEnter = isEnter;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (mCallback != null && mTimePicker != null) {
			mTimePicker.clearFocus();
			int hourOfDay = Integer.valueOf(displayedHourValues.get(mTimePicker.getCurrentHour()));
			int minute = Integer.valueOf(displayedMinuteValues.get(mTimePicker.getCurrentMinute()));
			mCallback.onTimeSet(mTimePicker, hourOfDay,	minute);
		}
	}

	@Override
	protected void onStop() {
		// override and do nothing
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			Class<?> rClass = Class.forName("com.android.internal.R$id");
			Field timePicker = rClass.getField("timePicker");
			this.mTimePicker = (TimePicker) findViewById(timePicker
					.getInt(null));
			
			//Minutos
			Field m = rClass.getField("minute");

			NumberPicker mMinuteSpinner = (NumberPicker) mTimePicker
					.findViewById(m.getInt(null));
			mMinuteSpinner.setMinValue(0);
			mMinuteSpinner.setMaxValue(isEnter ? 0:1);
			
			displayedMinuteValues = new ArrayList<String>();
			displayedMinuteValues.add("00");
			if (!isEnter)
				displayedMinuteValues.add("15");
			
			mMinuteSpinner.setDisplayedValues(displayedMinuteValues
					.toArray(new String[0]));
			
			mMinuteSpinner.setEnabled(isEnter);
			
			//Horas
			Field h = rClass.getField("hour");

			NumberPicker mHourSpinner = (NumberPicker) mTimePicker
					.findViewById(h.getInt(null));
			mHourSpinner.setMinValue(0);
			mHourSpinner.setMaxValue(2);
			
			displayedHourValues = new ArrayList<String>();
			if (isEnter) {
				displayedHourValues.add("12");
				displayedHourValues.add("13");
				displayedHourValues.add("14");
			}
			else {
				displayedHourValues.add("09");
				displayedHourValues.add("10");
				displayedHourValues.add("11");
			}
			mHourSpinner.setDisplayedValues(displayedHourValues
					.toArray(new String[0]));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
		Log.d("onTimeChanged", hourOfDay + " " + minute);
		if (!isEnter)
			switch (hourOfDay) {
			case 0:
			case 1:
				view.setCurrentMinute(1);
				break;
			case 2:
				view.setCurrentMinute(0);
				break;
			default:
				break;
			}
	}
}
