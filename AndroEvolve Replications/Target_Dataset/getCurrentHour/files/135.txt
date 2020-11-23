
package co.uk.justcheckingin;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

public class TimersActivity extends Activity {
    TimePicker picker1, picker2, picker3;
    Spinner spinner1, spinner2, spinner3;

    int time1, time2, time3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timers);

        spinner1 = (Spinner) findViewById(R.id.spinner1);
        spinner2 = (Spinner) findViewById(R.id.spinner2);
        spinner3 = (Spinner) findViewById(R.id.spinner3);
        ArrayAdapter<ContactList> adapter = new ArrayAdapter<ContactList>(this,
                android.R.layout.simple_spinner_item, ContactListsActivity.contactsList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter);
        spinner2.setAdapter(adapter);
        spinner3.setAdapter(adapter);

        Button start = (Button) findViewById(R.id.button4);
        picker1 = (TimePicker) findViewById(R.id.timePicker1);
        picker1.setIs24HourView(true);
        picker2 = (TimePicker) findViewById(R.id.timePicker2);
        picker2.setIs24HourView(true);
        picker3 = (TimePicker) findViewById(R.id.timePicker3);
        picker3.setIs24HourView(true);
        picker1.setCurrentHour(0);
        picker1.setCurrentMinute(30);
        picker2.setCurrentHour(0);
        picker2.setCurrentMinute(45);
        picker3.setCurrentHour(1);
        picker3.setCurrentMinute(0);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int hour1 = picker1.getCurrentHour();
                int minute1 = picker1.getCurrentMinute();
                int hour2 = picker2.getCurrentHour();
                int minute2 = picker2.getCurrentMinute();
                int hour3 = picker3.getCurrentHour();
                int minute3 = picker3.getCurrentMinute();

                time1 = hour1 * 60 + minute1;
                time2 = hour2 * 60 + minute2;
                time3 = hour3 * 60 + minute3;
                Toast.makeText(getApplicationContext(),
                        "1:(" + time1 + " mins), 2:(" + time2 + " mins), 3:(" + time3 + "mins)",
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}
