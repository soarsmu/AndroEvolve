package com.solutions.medadhere.medadheresolutionsapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;


/**
 * Created by Yeshy on 7/13/2016.
 */

public class MedicationLogActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    Spinner spinner;
    String medicine;
    String date;
    ArrayList<Medication> medicationList;
    String [] mArray;
    String [] passedArray;
    //String [] records = new String [ ]{ " ", " "," "," ", " "};

    protected void onCreate(Bundle savedInstanceState) {
        final Intent i = getIntent();
        String action = i.getAction();

        date = i.getStringExtra("date");
        //Log.e("date",date);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication_log);



        mDatabase = FirebaseDatabase.getInstance().getReference();
        String UID = ((MyApplication) this.getApplication()).getUID();

        //String c = ((MyApplication) this.getApplication()).getMedicationList().getValue
        //Log.d("medicine", c);
        //medicationList = ((MyApplication) this.getApplication()).getMedicationList();


        final String [] medNames = new String[((MyApplication) this.getApplication()).getMeds().size()];
        for(int ind=0;ind<((MyApplication) this.getApplication()).getMeds().size();ind++){
            medNames[ind] = ((MyApplication) this.getApplication()).getMeds().get(ind);
        }
        setArray(medNames);

    }

    public void setArray(String [] x){

        final Spinner spinner = (Spinner) findViewById(R.id.medicationSpinner);
// Application of the Array to the Spinner
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(MedicationLogActivity.this, android.R.layout.simple_spinner_item, x);
        //ArrayAdapter<CharSequence> spinnerArrayAdapter= ArrayAdapter.createFromResource(this,R.array.medicationArray,android.R.layout.simple_spinner_item);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spinner.setAdapter(spinnerArrayAdapter);


        //spinner.setPrompt(records[0]);

        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub
                //Log.v("item", (String) parent.getItemAtPosition(position));
                medicine = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                //spinner.setPrompt(records[0]);
            }
        });
    }

    public void returnToCal(View v){
        Intent i = new Intent(this, MedicationActivity.class);
        startActivity(i);
        finish();
    }

    public void submit(View v) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        String UID = ((MyApplication) this.getApplication()).getUID();
        TimePicker timePicker = (TimePicker) findViewById(R.id.timePicker);
        Calendar rightNow = Calendar.getInstance();
        int isecond = rightNow.get(Calendar.SECOND);
        String time;
        if (Build.VERSION.SDK_INT >= 23 ) {
            time = timePicker.getHour() + ":";
            int minute = timePicker.getMinute();
            if (minute <10){
                time+="0"+minute;
            }
            else{
                time+=minute;
            }

        } else {
            time = timePicker.getCurrentHour() + ":";
            int minute = timePicker.getCurrentMinute();
            if (minute <10){
                time+="0"+minute;
            }
            else{
                time+=minute;
            }
        }
        time+=":";
        if (isecond <10){
            time+="0"+isecond;
        }
        else{
            time+=isecond;
        }

        if (medicine !=" ") {
            //medicine = records.get(0);
            com.solutions.medadhere.medadheresolutionsapp.TimeLog timer = new com.solutions.medadhere.medadheresolutionsapp.TimeLog();
            mDatabase.child("app").child("users").child(UID).child("medicineLog").child(date).child(time).setValue(medicine);
//TODO: add cancel of repeating alarm within a time frame
            com.solutions.medadhere.medadheresolutionsapp.ReminderService reminder = new com.solutions.medadhere.medadheresolutionsapp.ReminderService();

            //10AM  limit to 5AM to 12PM
            if((timePicker.getCurrentHour()<12)&(timePicker.getCurrentHour()>5)){
                final Intent intent = getIntent();
                Log.e("Should Cancel", "sending 4");
                Intent i = new Intent(this, com.solutions.medadhere.medadheresolutionsapp.ReminderService.class);
                i.putExtra("type", 4);
                startService(i);
            }
            //2PM  limit to 12PM to 4PM
            if((timePicker.getCurrentHour()<16)&(timePicker.getCurrentHour()>=12)){
                Intent alarmIntent = new Intent(getApplicationContext(), com.solutions.medadhere.medadheresolutionsapp.MyAlarmReceiver.class);
                alarmIntent.setData(Uri.parse("custom://" + 1000002));
                alarmIntent.setAction(String.valueOf(1000002));
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                PendingIntent displayIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.cancel(displayIntent);
            }
            //8PM  limit to 4PM to 12AM
            if((timePicker.getCurrentHour()<24)&(timePicker.getCurrentHour()>=14)){
                Intent alarmIntent = new Intent(getApplicationContext(), com.solutions.medadhere.medadheresolutionsapp.MyAlarmReceiver.class);
                alarmIntent.setData(Uri.parse("custom://" + 1000003));
                alarmIntent.setAction(String.valueOf(1000003));
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                PendingIntent displayIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.cancel(displayIntent);
            }

            Snackbar.make(v, "Your medication has been logged at " + time, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
        else{
            Snackbar.make(v,
                    "Please select a medication.",
                    Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }

    }

    public void checkLogs(View v) {
        Intent i = new Intent(this, com.solutions.medadhere.medadheresolutionsapp.MedicationLogReadActivity.class);
        i.putExtra("date", date);
        startActivity(i);
    }
}