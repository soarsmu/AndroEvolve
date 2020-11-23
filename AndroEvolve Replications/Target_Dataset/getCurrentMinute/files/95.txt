package com.iglin.lab2rest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.iglin.lab2rest.model.DateTimeFormatter;
import com.iglin.lab2rest.model.Meeting;
import com.iglin.lab2rest.model.Priority;

import java.text.ParseException;
import java.util.Objects;

public class NewMeetingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_meeting);
        setTitle("New Meeting");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<Priority> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Priority.values());
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        Button button = (Button) findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createMeeting();
            }
        });
    }

    private void createMeeting() {
        Editable name = ((EditText) findViewById(R.id.editTextName)).getText();
        if (name == null || name.length() == 0) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Name must not be empty!", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        TimePicker timePicker1 = ((TimePicker) findViewById(R.id.timePicker1));
        TimePicker timePicker2 = ((TimePicker) findViewById(R.id.timePicker2));
        timePicker1.clearFocus();
        timePicker2.clearFocus();
        if ((timePicker1.getCurrentHour() > timePicker2.getCurrentHour())
                || (Objects.equals(timePicker1.getCurrentHour(), timePicker2.getCurrentHour())
                    && timePicker1.getCurrentMinute() >= timePicker2.getCurrentMinute())) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Starting time must be earlier than ending time!", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        Meeting meeting = new Meeting();
        meeting.setName(name.toString());
        EditText editTextDescr = (EditText) findViewById(R.id.editTextDescr);
        if (editTextDescr.getText() != null && editTextDescr.getText().length() > 0) {
            meeting.setDescription(editTextDescr.getText().toString());
        }

        meeting.setPriority(String.valueOf(((Spinner) findViewById(R.id.spinner)).getSelectedItem()));

        DatePicker datePicker = (DatePicker) findViewById(R.id.datePicker);
        String startTime = datePicker.getYear() + "-" + (datePicker.getMonth() + 1) + "-" + datePicker.getDayOfMonth();
        String endTime = startTime;
        startTime += "T" + timePicker1.getCurrentHour() + ":" + timePicker1.getCurrentMinute() + ":00";
        endTime += "T" + timePicker2.getCurrentHour() + ":" + timePicker2.getCurrentMinute() + ":00";
        try {
            meeting.setStartTime(DateTimeFormatter.getDate(startTime));
            meeting.setEndTime(DateTimeFormatter.getDate(endTime));
        } catch (ParseException e) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Couldn't parse date!", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        try {
            DatabaseReference database = FirebaseDatabase.getInstance().getReference();
            database.child("meetings").push().setValue(meeting);
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Meeting successfully created.", Toast.LENGTH_SHORT);
            toast.show();
        } catch (Throwable e) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Error during saving data to the server! Check your network connection.", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
