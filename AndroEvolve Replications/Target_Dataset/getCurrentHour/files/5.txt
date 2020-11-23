package com.rymarczykdawidgmail.classschedule;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

/**
 * Created by JAN on 2015-11-07.
 */
public class AddClass extends Activity {
    Button Confirm;
    EditText ClassName;
    TimePicker StartTime;
    TimePicker EndTime;
    RadioGroup Day;
    RadioButton WhichDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_class);

        Confirm = (Button)findViewById(R.id.Confirm);
        ClassName = (EditText)findViewById(R.id.classNameIn);
        StartTime = (TimePicker)findViewById(R.id.startTimeIN);
        EndTime = (TimePicker)findViewById(R.id.endTimeIn);
        Day = (RadioGroup)findViewById(R.id.Day);
        Confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedID = Day.getCheckedRadioButtonId();
                WhichDay = (RadioButton) findViewById(selectedID);
                if (isFulfillCorrectly()) {
                    Intent intent = new Intent();
                    intent.putExtra("ClassName", ClassName.getText().toString());
                    intent.putExtra("StartTimeH", StartTime.getCurrentHour());
                    intent.putExtra("EndTimeH", EndTime.getCurrentHour());
                    intent.putExtra("StartTimeM", StartTime.getCurrentMinute());
                    intent.putExtra("EndTimeM", EndTime.getCurrentMinute());
                    intent.putExtra("Day", WhichDay.getText().toString());
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Wrong input parameters. Correct it!",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private boolean isFulfillCorrectly() {
        return WhichDay.getText().toString() != null && ClassName.getText().toString() != null
                && StartTime.getCurrentHour() <= EndTime.getCurrentHour()
                && StartTime.getCurrentHour() >= 8 && EndTime.getCurrentHour() <= 20
                && ((StartTime.getCurrentHour() == EndTime.getCurrentHour()
                && StartTime.getCurrentMinute() >= EndTime.getCurrentMinute())? 0==1 : 1==1);
    }


}
