
package jp.ikisaki.www;

import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

public class TimeAndRouteTab3Activity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.time_and_route_tab3);

        final String forwardOrBackward = "";
        final String or = "";

        final Time time = new Time("Asia/Tokyo");
        time.setToNow();

        final TimePicker timePicker = (TimePicker)findViewById(R.id.time_and_route_tab3_timePicker);
        final DatePicker datePicker = (DatePicker)findViewById(R.id.time_and_route_tab3_datePicker);

        Date minDate = new Date(time.year - 1900, time.month, time.monthDay);
        datePicker.setMinDate(minDate.getTime());

        timePicker.setIs24HourView(true);
        timePicker.setDescendantFocusability(TimePicker.FOCUS_AFTER_DESCENDANTS);
        timePicker.setCurrentHour(time.hour);

        datePicker.setDescendantFocusability(DatePicker.FOCUS_AFTER_DESCENDANTS);

        if(BasicModel.getTabNumber() == 2){
        	timePicker.setCurrentHour(BasicModel.getHour());
        	timePicker.setCurrentMinute(BasicModel.getMinute());
        	datePicker.updateDate(BasicModel.getYear(), BasicModel.getMonth() - 1, BasicModel.getDay());
        }

        RadioButton forwardRadioButton = (RadioButton)findViewById(R.id.forward_radioButton1);
		RadioButton backwardRadioButton = (RadioButton)findViewById(R.id.backward_radioButton2);

		RadioGroup group = (RadioGroup)findViewById(R.id.radiogroup);
		group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				RadioButton radio = (RadioButton)findViewById(checkedId);
				if(radio.getText() == "出発"){
					BasicModel.setForwardOrBackward("&dir=forward");
				}
				else{
					BasicModel.setForwardOrBackward("&dir=backward");
				}

			}
		});

		if(BasicModel.getForwardOrBackward().equals("&dir=backward")){
			group.check(R.id.backward_radioButton2);
		}

        Button decisionButton = (Button)findViewById(R.id.decision_button);
        decisionButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				BasicModel.setDate("&date=" + (datePicker.getMonth() + 1) + "," + datePicker.getDayOfMonth());

				String setting =  (datePicker.getMonth() + 1) + "月" + datePicker.getDayOfMonth() + "日" +timePicker.getCurrentHour() + "時" + timePicker.getCurrentMinute() + "分に";

				if(timePicker.getCurrentMinute() == 0){
					setting = (datePicker.getMonth() + 1) + "月" + datePicker.getDayOfMonth() + "日" +timePicker.getCurrentHour() + "時" + "00" + "分に";
				}

				if(BasicModel.getForwardOrBackward() == "&dir=backward"){
					setting += "到着";
				}
				else{
					setting += "出発";
				}

				BasicModel.setSetting(setting);

				BasicModel.setTime("&hour=" + timePicker.getCurrentHour() + "&min=" + timePicker.getCurrentMinute());

				BasicModel.setMonth(datePicker.getMonth() + 1);
				BasicModel.setDay(datePicker.getDayOfMonth());
				BasicModel.setHour(timePicker.getCurrentHour());
				BasicModel.setMinute(timePicker.getCurrentMinute());
				BasicModel.setYear(datePicker.getYear());
				BasicModel.setTabNumber(2);

				Intent intent = new Intent(TimeAndRouteTab3Activity.this,
						RouteSearchActivity.class);
				intent.putExtra("keyword", "");
				startActivity(intent);
			}
		});
    }
}