package com.telerik.demoapplication;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TimePicker;

public class SecondaryActivity extends Activity {
    private static final String sSavedHourKey = "com.telerik.demoapplication.SecondaryActivity.savedHour";
    private static final String sSavedMinuteKey = "com.telerik.demoapplication.SecondaryActivity.savedMinute";

    private Bundle mSavedInstanceState;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.secondarytwo);

        mSavedInstanceState = savedInstanceState;
	}

    @Override
    protected void onResume() {
        super.onResume();

        if (mSavedInstanceState != null) {
            fixTimePickerState();
            mSavedInstanceState = null;
        }
    }

    private void fixTimePickerState() {
        TimePicker timePicker = (TimePicker)findViewById(R.id.timePicker1);
        timePicker.setCurrentHour(0);
        timePicker.setCurrentHour(1);
        timePicker.setCurrentMinute(0);
        timePicker.setCurrentMinute(1);
        timePicker.setCurrentHour(mSavedInstanceState.getInt(sSavedHourKey));
        timePicker.setCurrentMinute(mSavedInstanceState.getInt(sSavedMinuteKey));
        timePicker.invalidate();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        TimePicker timePicker = (TimePicker)findViewById(R.id.timePicker1);
        outState.putInt(sSavedHourKey, timePicker.getCurrentHour());
        outState.putInt(sSavedMinuteKey, timePicker.getCurrentMinute());

        super.onSaveInstanceState(outState);
    }
}
