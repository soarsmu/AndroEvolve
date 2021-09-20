package jp.co.spookies.android.alarm;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

public class TimePickerPreference extends DialogPreference {
	private int hourOfDay;
	private int minute;

	public TimePickerPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected View onCreateDialogView() {
		TimePicker timePicker = new TimePicker(getContext());
		int[] time = parseTime(this.getPersistedInt(0));
		timePicker.setCurrentHour(time[0]);
		timePicker.setCurrentMinute(time[1]);
		timePicker.setOnTimeChangedListener(new OnTimeChangedListener() {
			@Override
			public void
					onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				TimePickerPreference.this.hourOfDay = hourOfDay;
				TimePickerPreference.this.minute = minute;
			}
		});
		timePicker.setIs24HourView(true);
		return timePicker;
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		if (positiveResult) {
			int time = hourOfDay * 60 + minute;
			persistInt(time);
			callChangeListener(time);
		}
		super.onDialogClosed(positiveResult);
	}

	public static int[] parseTime(int time) {
		int hour = time / 60;
		int minute = time % 60;
		return new int[] { hour, minute };
	}
}
