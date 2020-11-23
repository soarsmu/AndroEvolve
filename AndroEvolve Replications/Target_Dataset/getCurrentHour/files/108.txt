package com.shirco.calendar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Date;

public class AddEventActivity extends AppCompatActivity {

    Event event;
    EditText detailsEditText;
    DatePicker datePicker;
    TimePicker timePicker;
    Button timeButton;
    Button dateButton;
    Button openTimeButton;
    Button openDateButton;
    Button addButton;
    LinearLayout dateView;
    LinearLayout timeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get Views
        setContentView(R.layout.activity_add_event);
        detailsEditText = (EditText) findViewById(R.id.detailsEditView);
        datePicker = (DatePicker) findViewById(R.id.datePicker);
        timePicker = (TimePicker) findViewById(R.id.timePicker);

        dateView = (LinearLayout) findViewById(R.id.dateView);
        timeView = (LinearLayout) findViewById(R.id.timeView);

        addButton = (Button) findViewById(R.id.registerButton);
        openDateButton = (Button) findViewById(R.id.openDateButton);
        openTimeButton = (Button) findViewById(R.id.openTimeButton);
        dateButton = (Button) findViewById(R.id.dateButton);
        timeButton = (Button) findViewById(R.id.timeButton);

        // Set Min Date possible to today
        datePicker.setMinDate(new Date().getTime());

        int id = -1;
        // Check if event must be created or edited
        if (getIntent() != null && getIntent().getExtras() != null) {
            id = getIntent().getExtras().getInt("id", -1);
        }

        if (id == -1) {
            // Create new Event
            String details = detailsEditText.getText().toString();
            Date tmp = new Date();
            tmp.setMonth(datePicker.getMonth());
            tmp.setYear(datePicker.getYear());
            tmp.setDate(datePicker.getDayOfMonth());
            tmp.setMinutes(timePicker.getCurrentMinute());
            tmp.setHours(timePicker.getCurrentHour());
            String date = datePicker.getYear() + "/" + (datePicker.getMonth() + 1) + "/" + datePicker.getDayOfMonth() + " " + timePicker.getCurrentHour() + ":" + timePicker.getCurrentMinute();

            event = new Event(date, details, tmp);
        } else {
            // Get Existing Event
            event = Singleton.getInstance().getEvents().get(id);
            timePicker.setCurrentMinute(event.getDate().getMinutes());
            timePicker.setCurrentHour(event.getDate().getHours());
            datePicker.updateDate(event.getDate().getYear(), event.getDate().getMonth(), event.getDate().getDate());
        }

        // Set base text of details, time and date based on Event info
        detailsEditText.setText(event.getDescription());
        openTimeButton.setText(timePicker.getCurrentHour() + ":" + timePicker.getCurrentMinute());
        openDateButton.setText(datePicker.getYear() + "/" + (datePicker.getMonth() + 1) + "/" + datePicker.getDayOfMonth());

        //Click Listeners for buttons
        openDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateView.setVisibility(View.VISIBLE);
            }
        });

        openTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeView.setVisibility(View.VISIBLE);
            }
        });

        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeView.setVisibility(View.GONE);
                openTimeButton.setText(timePicker.getCurrentHour() + ":" + timePicker.getCurrentMinute());
            }
        });

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateView.setVisibility(View.GONE);
                openDateButton.setText(datePicker.getYear() + "/" + (datePicker.getMonth() + 1) + "/" + datePicker.getDayOfMonth());
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save event infos into tmp variables
                String details = detailsEditText.getText().toString();
                Date tmp = new Date();
                tmp.setMonth(datePicker.getMonth());
                tmp.setYear(datePicker.getYear());
                tmp.setDate(datePicker.getDayOfMonth());
                tmp.setMinutes(timePicker.getCurrentMinute());
                tmp.setHours(timePicker.getCurrentHour());
                String date = datePicker.getYear() + "/" + (datePicker.getMonth() + 1) + "/" + datePicker.getDayOfMonth() + " " + timePicker.getCurrentHour() + ":" + timePicker.getCurrentMinute();

                // Set event infos
                event.setDate(tmp);
                event.setDescription(details);
                event.setStrDate(date);

                // If event doesn't exist, add to Events list
                if (!Singleton.getInstance().getEvents().contains(event)) {
                    Singleton.getInstance().getEvents().add(event);
                }
                Singleton.getInstance().sortEvents();
                onBackPressed();
            }
        });
    }
}
