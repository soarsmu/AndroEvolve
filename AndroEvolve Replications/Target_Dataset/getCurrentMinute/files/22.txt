package alberto.hugo.ezzit;

import java.sql.Date;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.sunil.timepickerdemo.R;

public class EzzitActivity extends Activity {
	protected static final String TAG = null;
	/** Called when the activity is first created. */

	TimePicker timepickerIn;
	TimePicker timepickerOut;
	TimePicker timepickerIn2;
	TimePicker timepickerOut2;
	DatePicker datePicker;
	CheckBox exitNotification;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ezzitmain);

		final EditText editTxtWorkload = (EditText) findViewById(R.id.editTxtWorkload);
		editTxtWorkload.setText("8");

		timepickerIn = (TimePicker) findViewById(R.id.timePickerIn);
		timepickerIn
				.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);
		timepickerIn.setIs24HourView(true);
		timepickerIn.setCurrentHour(8);
		timepickerIn.setCurrentMinute(0);

		timepickerOut = (TimePicker) findViewById(R.id.timePickerOut);
		timepickerOut
				.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);
		timepickerOut.setIs24HourView(true);
		timepickerOut.setCurrentHour(12);
		timepickerOut.setCurrentMinute(0);

		timepickerIn2 = (TimePicker) findViewById(R.id.timePickerIn2);
		timepickerIn2
				.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);
		timepickerIn2.setIs24HourView(true);
		timepickerIn2.setCurrentHour(13);
		timepickerIn2.setCurrentMinute(0);

		timepickerOut2 = (TimePicker) findViewById(R.id.timePickerOut2);
		timepickerOut2
				.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);
		timepickerOut2.setIs24HourView(true);
		timepickerOut2.setCurrentMinute(0);
		timepickerOut2.setCurrentHour(0);
		timepickerOut2.setEnabled(false);

		
		exitNotification = (CheckBox) findViewById(R.id.ckBoxExit);

		// Button View
		Button button = (Button) findViewById(R.id.btn);
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				String workload = editTxtWorkload.getText().toString();

				if (workload.trim().length() == 0) {
					Toast.makeText(getBaseContext(),"Error: Without workload!", Toast.LENGTH_SHORT).show();
					return;
				} else {
					int workloadInt = Integer.parseInt(workload);

					int inHour = timepickerIn.getCurrentHour()*60;
					int inMin = timepickerIn.getCurrentMinute();
					int outHour = timepickerOut.getCurrentHour()*60;
					int outMin = timepickerOut.getCurrentMinute();
					int in2Hour = timepickerIn2.getCurrentHour()*60;
					int in2Min = timepickerIn2.getCurrentMinute();
					int out2Hour;
					int out2Min;
					
					workloadInt = workloadInt*60;
					int workOut2 = - ((outHour + outMin)- (inHour + inMin) - workloadInt - (in2Hour + in2Min) );
					
					out2Hour= workOut2/60;
					out2Min = workOut2%60;

					if (inHour > outHour || outHour > in2Hour)
						Toast.makeText(getBaseContext(),
								"Error: Problem with hours!",
								Toast.LENGTH_SHORT).show();
					else {
						timepickerOut2.setCurrentHour(out2Hour);
						timepickerOut2.setCurrentMinute(out2Min);
					}

					if (exitNotification.isChecked()) {

						// ---use the AlarmManager to trigger an alarm---
						AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

						// ---get current date and time---
						Calendar calendar = Calendar.getInstance();
						Date dat = new Date(1);
						Calendar now_calendar = Calendar.getInstance();
						now_calendar.setTime(dat);

						// ---sets the time for the alarm to trigger---
						calendar.set(Calendar.HOUR_OF_DAY, timepickerOut2.getCurrentHour());
						calendar.set(Calendar.MINUTE, timepickerOut2.getCurrentMinute());
						calendar.set(Calendar.SECOND, 0);

						if (calendar.before(now_calendar)) {
							calendar.add(Calendar.DATE, 1);
						}

						Intent i = new Intent(EzzitActivity.this, DisplayNotifications.class);
						i.putExtra("NotifID", 1);

						PendingIntent displayIntent = PendingIntent.getActivity(getBaseContext(), 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

								alarmManager.set(AlarmManager.RTC_WAKEUP,
								calendar.getTimeInMillis(), displayIntent);
					}
				}

			}
		});
	}
}