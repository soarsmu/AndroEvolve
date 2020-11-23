package com.qubaopen.dialog;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import com.qubaopen.R;

public class TimePickerDialog extends Dialog implements View.OnClickListener {

	private Context context;

	private TimePicker timePickerStart;
	private TimePicker timePickerEnd;

	private Button confirmBtn;
	private Button cancelBtn;

	private int startHour;
	private int startMinute;

	private int endHour;
	private int endMinute;

	private boolean leaveWithConfirm;

	public TimePickerDialog(Context context, int theme, String startTime,
			String endTime) {
		super(context, theme);
		setContentView(R.layout.dialog_receive_time);
		this.context = context;

		timePickerStart = (TimePicker) this.findViewById(R.id.timePickerStart);
		timePickerEnd = (TimePicker) this.findViewById(R.id.timePickerEnd);

		timePickerStart
				.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);
		timePickerEnd
				.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);

		timePickerStart.setIs24HourView(true);
		timePickerEnd.setIs24HourView(true);

		String[] startTimeArray = startTime.split(":");
		String[] endTimeArray = endTime.split(":");

		startHour = Integer.parseInt(startTimeArray[0]);
		startMinute = Integer.parseInt(startTimeArray[1]);

		endHour = Integer.parseInt(endTimeArray[0]);
		endMinute = Integer.parseInt(endTimeArray[1]);

		timePickerStart.setCurrentHour(startHour);
		timePickerStart.setCurrentMinute(startMinute);

		timePickerEnd.setCurrentHour(endHour);
		timePickerEnd.setCurrentMinute(endMinute);

		confirmBtn = (Button) this.findViewById(R.id.confirmBtn);
		cancelBtn = (Button) this.findViewById(R.id.cancelBtn);

		confirmBtn.setOnClickListener(this);
		cancelBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.confirmBtn:
			if (checkTimeValid()) {
				startHour = timePickerStart.getCurrentHour();
				startMinute = timePickerStart.getCurrentMinute();

				endHour = timePickerEnd.getCurrentHour();
				endMinute = timePickerEnd.getCurrentMinute();
				leaveWithConfirm = true;
				this.dismiss();
			} else {
				Toast.makeText(
						context,
						context.getString(R.string.toast_starttime_grater_than_endtime),
						Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.cancelBtn:

			leaveWithConfirm = false;
			this.dismiss();
			break;
		}
	}

	public int getStartHour() {
		return startHour;
	}

	public int getStartMinute() {
		return startMinute;
	}

	public int getEndHour() {
		return endHour;
	}

	public int getEndMinute() {
		return endMinute;
	}

	public boolean isLeaveWithConfirm() {
		return leaveWithConfirm;
	}

	private boolean checkTimeValid() {
		Log.i("timepicker", "endTime" + timePickerEnd.getCurrentHour() + ""
				+ timePickerEnd.getCurrentMinute());
		if (timePickerEnd.getCurrentHour() != 0
				&& timePickerEnd.getCurrentMinute() != 0) {
			if (timePickerStart.getCurrentHour() > timePickerEnd
					.getCurrentHour()) {
				return false;
			} else if (timePickerStart.getCurrentHour() == timePickerEnd
					.getCurrentHour()) {

				if (timePickerStart.getCurrentMinute() >= timePickerEnd
						.getCurrentMinute()) {
					return false;

				}
			}
		}

		return true;
	}

}
