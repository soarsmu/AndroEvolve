package com.example.usmansh.proofofconcept;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PersistableBundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;

public class setReminderAct extends AppCompatActivity  {

    Button reminderBt1,reminderBt2,reminderBt3,reminderBt4,doneBt;

    TimePicker timePicker;
    int AlarmCode=0;
    int remOneSel,remTwoSel,remThreeSel,remFourSel;
    int isReminderSet=0;
    int rem1H=00,rem1M=00,rem2H=00,rem2M=00,rem3H=00,rem3M=00,rem4H=00,rem4M=00;
    int CurrentH,CurrentM,CurrentDay;
    String ProName,dayChoice,userIdd,coming;
    int year,month,day,minut,hour;
    int yearFinal,monthFinal,dayFinal,minutFinal,hourFinal;
    Calendar calendar,c;
    ArrayList<Long> alarmTimeArr = new ArrayList<>();
    long Rem1Time = 0,Rem2Time = 0,Rem3Time = 0,Rem4Time = 0;
    int startProDate;
    int startProhour,endProhour,startProminute,endProminute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_reminder);

        timePicker = (TimePicker) findViewById(R.id.timepicker);

        reminderBt1 = (Button)findViewById(R.id.reminderbt1);
        reminderBt2 = (Button)findViewById(R.id.reminderbt2);
        reminderBt3 = (Button)findViewById(R.id.reminderbt3);
        reminderBt4 = (Button)findViewById(R.id.reminderbt4);
        doneBt      = (Button)findViewById(R.id.doneBt);

        //backBt.setVisibility(View.INVISIBLE);
         CurrentDay     = Calendar.DAY_OF_MONTH;
         CurrentH       = timePicker.getCurrentHour();
         CurrentM       = timePicker.getCurrentMinute();
         dayChoice      = getIntent().getStringExtra("dayChoice");
         ProName        = getIntent().getStringExtra("ProName");
         userIdd        = getIntent().getStringExtra("userId");
         startProhour   = getIntent().getIntExtra("sHour",0);
         startProminute = getIntent().getIntExtra("sMinute",0);
         endProhour     = getIntent().getIntExtra("eHour",0);
         endProminute   = getIntent().getIntExtra("eMinute",0);
         startProDate = getIntent().getIntExtra("startProDate",0);

        //Toast.makeText(this, "SProDate: "+startProDate, Toast.LENGTH_SHORT).show();
        //Toast.makeText(this, "Sh : "+startProhour, Toast.LENGTH_SHORT).show();
        //Toast.makeText(this, "Sm : "+startProminute, Toast.LENGTH_SHORT).show();
        //Toast.makeText(this, "Eh: "+endProhour, Toast.LENGTH_SHORT).show();
        //Toast.makeText(this, "Em: "+endProminute, Toast.LENGTH_SHORT).show();


         coming = getIntent().getStringExtra("coming");
         //Toast.makeText(this, "ProName: "+ProName, Toast.LENGTH_SHORT).show();
        //Toast.makeText(this, "Day ch: "+dayChoice, Toast.LENGTH_SHORT).show();


        reminderBt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlarmCode = 1;


                getTimeFromCalendar(AlarmCode);

                }
        });


        reminderBt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(remOneSel ==1) {

                    AlarmCode = 2;
                    getTimeFromCalendar(AlarmCode);


                }else{
                    Toast.makeText(setReminderAct.this, "Set Previous Reminder First..!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        reminderBt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(remOneSel == 1 && remTwoSel == 1) {

                    AlarmCode = 3;
                    getTimeFromCalendar(AlarmCode);

                }else{
                    Toast.makeText(setReminderAct.this, "Set Previous Reminder First..!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        reminderBt4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(remOneSel == 1 && remTwoSel == 1 && remThreeSel ==1) {

                    AlarmCode = 4;
                    getTimeFromCalendar(AlarmCode);

                }else{
                    Toast.makeText(setReminderAct.this, "Set Previous Reminder First..!", Toast.LENGTH_SHORT).show();
                }
            }
        });



        doneBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                setArrTimeAndAlarm();

                isReminderSet = 1;
                Intent intent = new Intent();
                intent.putExtra("isReminderSet", isReminderSet);
                intent.putExtra("TimeList",alarmTimeArr);
                intent.putExtra("rem1H",rem1H);
                intent.putExtra("rem1M",rem1M);
                intent.putExtra("rem2H",rem2H);
                intent.putExtra("rem2M",rem2M);
                intent.putExtra("rem3H",rem3H);
                intent.putExtra("rem3M",rem3M);
                intent.putExtra("rem4H",rem4H);
                intent.putExtra("rem4M",rem4M);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

    }



    private void setArrTimeAndAlarm() {

        if (Rem1Time != 0) {
                alarmTimeArr.add(Rem1Time);
            }
            if (Rem2Time != 0) {
                alarmTimeArr.add(Rem2Time);
            }
            if (Rem3Time != 0) {
                alarmTimeArr.add(Rem3Time);
            }
            if (Rem4Time != 0) {
                alarmTimeArr.add(Rem4Time);
            }

        if(alarmTimeArr.size() > 0 ) {

            Intent intent = null;
            PendingIntent pendingIntent = null;

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            intent = new Intent(getApplicationContext(), MyAlarm1.class);
            intent.putExtra("key", 1);
            // Toast.makeText(this, "Reminder 1 is set", Toast.LENGTH_SHORT).show();
            intent.putExtra("userId", userIdd);
            intent.putExtra("ProName", ProName);
            intent.putExtra("TimeList", alarmTimeArr);
            intent.putExtra("arrIndex", 0);

            pendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);


            assert alarmManager != null;

            if (Build.VERSION.SDK_INT >= 23) {
                Toast.makeText(this, "arrSize: " + alarmTimeArr.size(), Toast.LENGTH_SHORT).show();
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTimeArr.get(0), pendingIntent);
               // Toast.makeText(this, "setExactAndAllowWhileIdle", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, "arrSize: " + alarmTimeArr.size(), Toast.LENGTH_SHORT).show();
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTimeArr.get(0), pendingIntent);
                //Toast.makeText(this, "setExact", Toast.LENGTH_SHORT).show();

            }
        }else {
            Toast.makeText(this, "Your All Alarms are Empty..!", Toast.LENGTH_SHORT).show();
        }





    }


    private void getTimeFromCalendar(int alarmCodee) {


        Calendar calendar = Calendar.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if(dayChoice !=null && dayChoice.equals("MD")){

                //Toast.makeText(this, "Multi-Day-1", Toast.LENGTH_SHORT).show();
                calendar.set(
                        calendar.get(Calendar.YEAR)
                        ,calendar.get(Calendar.MONTH)
                        ,startProDate
                        ,timePicker.getHour()
                        ,timePicker.getMinute()
                        ,0
                );


                setAlarm(calendar.getTimeInMillis(), alarmCodee);

                //Toast.makeText(this, "MD-1-Day: "+startProDate, Toast.LENGTH_SHORT).show();

            }else{


                //Toast.makeText(this, "Single-Day-1", Toast.LENGTH_SHORT).show();

                calendar.set(
                        calendar.get(Calendar.YEAR)
                        ,calendar.get(Calendar.MONTH)
                        ,calendar.get(Calendar.DAY_OF_MONTH)
                        ,timePicker.getHour()
                        ,timePicker.getMinute()
                        ,0
                );


                if(timePicker.getHour() >= CurrentH) {
                    setAlarm(calendar.getTimeInMillis(), alarmCodee);
                }else{
                    Toast.makeText(this, "Error: You are selecting wrong hour..!\nDon't Select  from the previous hours..!", Toast.LENGTH_LONG).show();
                }


            }




        }else{



            if(dayChoice !=null && dayChoice.equals("MD")) {


                calendar.set(
                        calendar.get(Calendar.YEAR)
                        ,calendar.get(Calendar.MONTH)
                        ,startProDate
                        ,timePicker.getCurrentHour()
                        ,timePicker.getCurrentMinute()
                        ,0
                );


                setAlarm(calendar.getTimeInMillis(), alarmCodee);

                //Toast.makeText(this, "MD-2-Day: "+startProDate, Toast.LENGTH_SHORT).show();

            }else{


                calendar.set(
                        calendar.get(Calendar.YEAR)
                        ,calendar.get(Calendar.MONTH)
                        ,calendar.get(Calendar.DAY_OF_MONTH)
                        ,timePicker.getCurrentHour()
                        ,timePicker.getCurrentMinute()
                        ,0
                );

                if(timePicker.getCurrentHour() >= CurrentH) {
                    setAlarm(calendar.getTimeInMillis(), alarmCodee);
                }else{
                    Toast.makeText(this, "Error: You are selecting wrong hour..!\nDon't Select  from the previous hours..!", Toast.LENGTH_LONG).show();
                }


            }


        }



    }



    @SuppressLint("SetTextI18n")
    private void setAlarm(long timeInMillis, int alarmCodee) {



    if(alarmCodee == 1){



            if (dayChoice != null && dayChoice.equals("MD")) {


                if(timePicker.getCurrentHour() == startProhour && timePicker.getCurrentMinute() == startProminute) {
                    Toast.makeText(this, "Error: Rem1 Selectig time is equal to Start Project Time..!", Toast.LENGTH_SHORT).show();

                }else {

                    reminderBt1.setText("Time Set: " + timePicker.getCurrentHour() + ":" + timePicker.getCurrentMinute() + "");
                    rem1H = timePicker.getCurrentHour();
                    rem1M = timePicker.getCurrentMinute();
                    Rem1Time = timeInMillis;
                    remOneSel = 1;
                    Toast.makeText(this, "Reminder 1 is set", Toast.LENGTH_SHORT).show();

                }

            } else {

                if(timePicker.getCurrentHour() == startProhour && timePicker.getCurrentMinute() == startProminute) {
                    Toast.makeText(this, "Error: Rem1 Selectig time is equal to Start Project Time..!", Toast.LENGTH_SHORT).show();
                }else {

                    reminderBt1.setText("Time Set: " + timePicker.getCurrentHour() + ":" + timePicker.getCurrentMinute() + "");
                    rem1H = timePicker.getCurrentHour();
                    rem1M = timePicker.getCurrentMinute();
                    Rem1Time = timeInMillis;
                    remOneSel = 1;
                    Toast.makeText(this, "Reminder 1 is set", Toast.LENGTH_SHORT).show();

                }
            }



    }

    else if(alarmCodee == 2){


            if (dayChoice != null && dayChoice.equals("MD")) {

                    if(timeInMillis > Rem1Time) {

                        reminderBt2.setText("Time Set: " + timePicker.getCurrentHour() + ":" + timePicker.getCurrentMinute() + "");
                        rem2H = timePicker.getCurrentHour();
                        rem2M = timePicker.getCurrentMinute();
                        Rem2Time = timeInMillis;
                        Toast.makeText(this, "Reminder 2 is set", Toast.LENGTH_SHORT).show();
                        remTwoSel = 1;

                    }else {
                        Toast.makeText(this, "Rem2 Time must be greater than Rem1 Time..!", Toast.LENGTH_SHORT).show();
                    }


            } else {

                if(timeInMillis > Rem1Time) {

                    reminderBt2.setText("Time Set: " + timePicker.getCurrentHour() + ":" + timePicker.getCurrentMinute() + "");
                    rem2H = timePicker.getCurrentHour();
                    rem2M = timePicker.getCurrentMinute();
                    Rem2Time = timeInMillis;
                    Toast.makeText(this, "Reminder 2 is set", Toast.LENGTH_SHORT).show();
                    remTwoSel = 1;

                }else {
                    Toast.makeText(this, "Rem2 Time must be greater than Rem1 Time..!", Toast.LENGTH_SHORT).show();
                }

            }


        }


        else if(alarmCodee == 3){



        if(dayChoice !=null && dayChoice.equals("MD")) {

            if(timeInMillis > Rem2Time){

                reminderBt3.setText("Time Set: " + timePicker.getCurrentHour() + ":" +timePicker.getCurrentMinute() + "");
                rem3H = timePicker.getCurrentHour();
                rem3M = timePicker.getCurrentMinute();
                Rem3Time = timeInMillis;
            }else {
                Toast.makeText(this, "Rem3 Time must be greater than Rem2 Time..!", Toast.LENGTH_SHORT).show();
            }

        }else
        {
            if(timeInMillis>Rem2Time){

                reminderBt3.setText("Time Set: " + timePicker.getCurrentHour() + ":" + timePicker.getCurrentMinute() + "");
            rem3H = timePicker.getCurrentHour();
            rem3M = timePicker.getCurrentMinute();
            Rem3Time = timeInMillis;
            }else {
                Toast.makeText(this, "Rem3 Time must be greater than Rem2 Time..!", Toast.LENGTH_SHORT).show();
            }
        }


        remThreeSel = 1;

    }

    else if(alarmCodee == 4){


    if(dayChoice !=null && dayChoice.equals("MD")) {

        if(timeInMillis > Rem3Time){

            reminderBt4.setText("Time Set: " + timePicker.getCurrentHour() + ":" + timePicker.getCurrentMinute() + "");
            rem4H = timePicker.getCurrentHour();
            rem4M = timePicker.getCurrentMinute();
            Rem4Time = timeInMillis;
        }else {
            Toast.makeText(this, "Rem4 Time must be greater than Rem3 Time..!", Toast.LENGTH_SHORT).show();
        }

        }else {

        if(timeInMillis > Rem3Time){

            reminderBt4.setText("Time Set: " + timePicker.getCurrentHour() + ":" + timePicker.getCurrentMinute() + "");
            rem4H = timePicker.getCurrentHour();
            rem4M = timePicker.getCurrentMinute();
            Rem4Time = timeInMillis;
        }else {
            Toast.makeText(this, "Rem4 Time must be greater than Rem3 Time..!", Toast.LENGTH_SHORT).show();
        }
        }

        remFourSel = 1;
    }



    }



    @Override
    public void onBackPressed() {

        // Do Here what ever you want do on back press;
        if(coming != null && coming.equalsIgnoreCase("ProSet")){
            finish();
        }else{
            Toast.makeText(this, "Press the Done button..!", Toast.LENGTH_SHORT).show();
        }

    }




}



