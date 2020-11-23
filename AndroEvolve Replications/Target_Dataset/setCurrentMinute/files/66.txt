package com.cecivg.pruebita;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import com.cecivg.pruebita.lists.ShiftRosterList;
import com.cecivg.pruebita.model.ShiftRoster;

import java.lang.reflect.Field;
import java.util.Calendar;

import static java.lang.Integer.getInteger;

public class loadShiftRosterActivity extends AppCompatActivity {

    private ShiftRosterList shiftRosterList;

    private String rosterFilename;

    private String work;

    private String mode;
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_shift_roster);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        loadShiftsListAndWork();
        setViewsValues();

    }

    private void loadShiftsListAndWork(){
        Intent intent = getIntent();
        work= intent.getExtras().getString("work");
        rosterFilename =intent.getExtras().getString("rosterFilename");
        mode= intent.getExtras().getString("mode");
        if(mode.equals("update")){date=intent.getExtras().getString("date");}

        shiftRosterList = new ShiftRosterList();

        shiftRosterList.openFile(getApplicationContext(), rosterFilename);
        shiftRosterList.setShifts(getApplicationContext());


    }


    private void setViewsValues(){

        setMinuteInterval();
        setCurrentDate();


        //si se edita roster ya cargado
        Intent intent = getIntent();
        //cargar datos si existen

        String mode= intent.getStringExtra("mode");
        if(mode!=null && mode.equals("update")) {
            // load data in views
            setDateInView(intent.getStringExtra("date"));
            setTimeInView(intent.getStringExtra("startTime"),
                    intent.getStringExtra("finishTime"));
            setNotesInView(intent.getStringExtra("notes"));
            // show delete button
            Button deleteButton= (Button) findViewById(R.id.button_delete_roter_shift);
            deleteButton.setVisibility(View.VISIBLE);
        }

    }

    private void setDateInView(String date){

        DatePicker dateView;

        if(date!= null && !date.equals("")) {
            String[] splitDate = date.split("/");
            dateView = (DatePicker) findViewById(R.id.date_picker);
            int year, monthOfYear, day;
            year = Integer.parseInt(splitDate[0]);
            monthOfYear = Integer.parseInt(splitDate[1]);
            day = Integer.parseInt(splitDate[2]);

            dateView.updateDate(year, monthOfYear, day);
        }
    }

    private void setTimeInView(String startTime, String finishTime){
        TimePicker startTimeView, finishTimeView;
        String[] splitTime;
        int hour, min;

        if(startTime!=null && !startTime.equals("")){
            splitTime= startTime.split(":");
            startTimeView= (TimePicker) findViewById(R.id.start_time_picker);
            hour= Integer.parseInt(splitTime[0]);
            min= Integer.parseInt(splitTime[1]);
            startTimeView.setCurrentHour(hour);
            startTimeView.setCurrentMinute(min);
        }

        if(finishTime!=null && !finishTime.equals("")){
            splitTime= finishTime.split(":");
            finishTimeView= (TimePicker) findViewById(R.id.finish_time_picker);
            hour= Integer.parseInt(splitTime[0]);
            min= Integer.parseInt(splitTime[1]);
            finishTimeView.setCurrentHour(hour);
            finishTimeView.setCurrentMinute(min);
        }
    }

    private void setNotesInView(String notes){
        EditText notesText;
        if(notes!=null && !notes.equals("")){
            notesText = (EditText) findViewById(R.id.notes_text);
            notesText.setText(notes);
        }
    }

    private void setMinuteInterval(){

        TimePicker startTimePicker = (TimePicker) findViewById(R.id.start_time_picker);
        TimePicker finishTimePicker = (TimePicker) findViewById(R.id.finish_time_picker);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            try {
                Class<?> classForid = Class.forName("com.android.internal.R$id");
                // Field timePickerField = classForid.getField("timePicker");

                Field field = classForid.getField("minute");
                NumberPicker startMinutePicker = (NumberPicker) startTimePicker
                        .findViewById(field.getInt(null));
                NumberPicker finishMinutePicker = (NumberPicker) finishTimePicker
                        .findViewById(field.getInt(null));

                startMinutePicker.setMinValue(0);
                startMinutePicker.setMaxValue(3);
                startMinutePicker.setDisplayedValues(new String[]{"0", "15", "30", "45"});
                startMinutePicker.setOnLongPressUpdateInterval(100);

                finishMinutePicker.setMinValue(0);
                finishMinutePicker.setMaxValue(3);
                finishMinutePicker.setDisplayedValues(new String[]{"0", "15", "30", "45"});
                finishMinutePicker.setOnLongPressUpdateInterval(100);

            } catch (Exception e) {
                Log.v(e.getMessage(), "Error setting time picker minute interval");
            }
        }
    }


    private void setCurrentDate() {
        DatePicker datePicker = (DatePicker) findViewById(R.id.date_picker);

        final Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        datePicker.init(year, month, day, null);

    }

    private void showConfirmationMessage(String msg){
        CoordinatorLayout coordinatorLayout=
                (CoordinatorLayout) findViewById(R.id.coordinator_layout_load_shift_roster);

        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, msg, Snackbar.LENGTH_LONG);

        snackbar.show();
    }

    public void saveShift(View view){

        try {
            Context context = view.getContext();

            // Abrir archivo
            // File file = new File(Environment.getExternalStorageDirectory(), filename); -- para cuando lo instale

            DatePicker selectedDate = (DatePicker) findViewById(R.id.date_picker);

            int selYear = selectedDate.getYear();
            int selMonth = selectedDate.getMonth();
            int selDay = selectedDate.getDayOfMonth();
            String date = String.valueOf(selYear) + "/" +
                    String.valueOf(selMonth) + "/" +
                    String.valueOf(selDay);

            ShiftRoster shift = shiftRosterList.findShift(work, date);

            TimePicker start_time = (TimePicker) findViewById(R.id.start_time_picker);
            TimePicker finish_time = (TimePicker) findViewById(R.id.finish_time_picker);
            String startHour = String.valueOf(start_time.getCurrentHour());
            String startMin = String.valueOf(start_time.getCurrentMinute()*15);
            String finishHour = String.valueOf(finish_time.getCurrentHour());
            String finishMin = String.valueOf(finish_time.getCurrentMinute()*15);
            EditText notes_text = (EditText) findViewById(R.id.notes_text);
            String notes= notes_text.getText().toString();


            if (shift == null) {
                shift = new ShiftRoster();
                shift.setWork(work);
                shift.setDate(selYear, selMonth, selDay);
                shift.setStartTime(Integer.valueOf(startHour), Integer.valueOf(startMin));
                shift.setFinishTime(Integer.valueOf(finishHour), Integer.valueOf(finishMin));
                shift.setNotes(notes);
                shiftRosterList.addShift(shift);
            } else {
                shift.setStartTime(Integer.valueOf(startHour), Integer.valueOf(startMin));
                shift.setFinishTime(Integer.valueOf(finishHour), Integer.valueOf(finishMin));
            }
            shiftRosterList.saveShifts(context);

            showConfirmationMessage("Roster shift has been loaded");


        } catch (Exception e) {
            Log.v("Otra exception", e.getMessage());
        }


    }

    public void deleteShift(View view) {
        try {
            shiftRosterList.deleteShift(this.work, this.date);
            Context context = view.getContext();
            shiftRosterList.saveShifts(context);

            showConfirmationMessage("Roster shift has been deleted");
        } catch (Exception e) {
            Log.v("Exc delete roster shift", e.getMessage());
        }
    }

}
