/*
MainActivity.java is out practice interface

Enter from Home Page

This interface operates similarly to the Login testing with a few exceptions.
Data for testing is not recorded here.
User password is displayed so user can learn it.
Can be used unlimited times.

Exits to Home Page

 */





package com.opticnerve.nccc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import java.io.FileOutputStream;
import java.util.Random;


public class MainActivity extends Activity {
    private TimePicker timePicker1;
    private TimePicker timePicker2;
    private TimePicker timePicker3;
    private TimePicker timePicker4;

    private Button clock1;
    private Button clock2;
    private Button clock3;
    private Button clock4;

    private Button check_button;
    private Button proceed_button;

    private TextView password;
    private TextView current_password;
    private TextView password_check;
    private TextView password_type;

    private Random rand;

    private static int proceedCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rand = new Random();

        // init clock faces
        timePicker1 = (TimePicker) findViewById(R.id.timePicker1);
        timePicker1.setIs24HourView(true);
        timePicker1.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener(){
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                updateCurrentPasswordText();
            }
        });

        timePicker2 = (TimePicker) findViewById(R.id.timePicker2);
        timePicker2.setIs24HourView(true);
        timePicker2.setVisibility(View.INVISIBLE);
        timePicker2.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener(){
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                updateCurrentPasswordText();
            }
        });

        timePicker3 = (TimePicker) findViewById(R.id.timePicker3);
        timePicker3.setIs24HourView(true);
        timePicker3.setVisibility(View.INVISIBLE);
        timePicker3.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener(){
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                updateCurrentPasswordText();
            }
        });

        timePicker4 = (TimePicker) findViewById(R.id.timePicker4);
        timePicker4.setIs24HourView(true);
        timePicker4.setVisibility(View.INVISIBLE);
        timePicker4.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener(){
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                updateCurrentPasswordText();
            }
        });


        //init buttons for selecting clock faces
        clock1 = (Button)findViewById(R.id.button);
        clock1.setBackgroundColor(Color.RED);
        clock1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(timePicker1.getVisibility() == View.INVISIBLE) {
                    clock1.setBackgroundColor(Color.RED);
                    clock2.setBackgroundColor(Color.WHITE);
                    clock3.setBackgroundColor(Color.WHITE);
                    clock4.setBackgroundColor(Color.WHITE);
                    timePicker1.setVisibility(View.VISIBLE);
                    timePicker2.setVisibility(View.INVISIBLE);
                    timePicker3.setVisibility(View.INVISIBLE);
                    timePicker4.setVisibility(View.INVISIBLE);
                }
            }
        });

        clock2 = (Button)findViewById(R.id.button2);
        clock2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(timePicker2.getVisibility() == View.INVISIBLE) {
                    clock1.setBackgroundColor(Color.WHITE);
                    clock2.setBackgroundColor(Color.RED);
                    clock3.setBackgroundColor(Color.WHITE);
                    clock4.setBackgroundColor(Color.WHITE);
                    timePicker1.setVisibility(View.INVISIBLE);
                    timePicker2.setVisibility(View.VISIBLE);
                    timePicker3.setVisibility(View.INVISIBLE);
                    timePicker4.setVisibility(View.INVISIBLE);
                }
            }
        });

        clock3 = (Button)findViewById(R.id.button3);
        clock3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(timePicker3.getVisibility() == View.INVISIBLE) {
                    clock1.setBackgroundColor(Color.WHITE);
                    clock2.setBackgroundColor(Color.WHITE);
                    clock3.setBackgroundColor(Color.RED);
                    clock4.setBackgroundColor(Color.WHITE);
                    timePicker1.setVisibility(View.INVISIBLE);
                    timePicker2.setVisibility(View.INVISIBLE);
                    timePicker3.setVisibility(View.VISIBLE);
                    timePicker4.setVisibility(View.INVISIBLE);
                }
            }
        });

        clock4 = (Button)findViewById(R.id.button4);
        clock4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(timePicker4.getVisibility() == View.INVISIBLE) {
                    clock1.setBackgroundColor(Color.WHITE);
                    clock2.setBackgroundColor(Color.WHITE);
                    clock3.setBackgroundColor(Color.WHITE);
                    clock4.setBackgroundColor(Color.RED);
                    timePicker1.setVisibility(View.INVISIBLE);
                    timePicker2.setVisibility(View.INVISIBLE);
                    timePicker3.setVisibility(View.INVISIBLE);
                    timePicker4.setVisibility(View.VISIBLE);
                }
            }
        });
        setTime();

        // init current password display
        current_password = (TextView) findViewById(R.id.CurrentPassword);
        String new_output = "Current: " + timePicker1.getCurrentHour() + ":" + timePicker1.getCurrentMinute()
                + ",    " + timePicker2.getCurrentHour() + ":" + timePicker2.getCurrentMinute()
                + ",    " + timePicker3.getCurrentHour() + ":" + timePicker3.getCurrentMinute()
                + ",    " + timePicker4.getCurrentHour() + ":" + timePicker4.getCurrentMinute();
        current_password.setText(new_output, TextView.BufferType.SPANNABLE);

        // T or F password checker
        password_check= (TextView) findViewById(R.id.PasswordCheck);
        password_check.setText("", TextView.BufferType.SPANNABLE);

        // password to learn
        password = (TextView) findViewById(R.id.passwordTextView);
        User data = new User();
        String type = "";
        int[] goal_password = new int [8];
        if(proceedCounter==0) {
            type = "Gmail";
            goal_password = data.getGmail_pass();
        }
        if(proceedCounter==1) {
            type = "Facebook";
            goal_password = data.getFacebook_pass();
        }
        if(proceedCounter==2) {
            type = "Bank";
            goal_password = data.getBank_pass();
        }
        String goal_output = "Goal: " + goal_password[0] + ":" + goal_password[1] + ",    " + goal_password[2] + ":" + goal_password[3]
                           + ",    " + goal_password[4] + ":" + goal_password[5] + ",    " + goal_password[6] + ":" + goal_password[7];
        password.setText(goal_output, TextView.BufferType.SPANNABLE);
        //init password type
        password_type = (TextView)findViewById(R.id.PasswordType);
        password_type.setText(type, TextView.BufferType.SPANNABLE);


        //init action buttons
        check_button = (Button)findViewById(R.id.CheckButton);
        check_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temp;
                if (comparePassword()) {
                    temp = "true";
                    password_check.setTextColor(Color.GREEN);
                }
                else {
                    temp = "false";
                    password_check.setTextColor(Color.RED);
                }
                password_check.setText(temp, TextView.BufferType.SPANNABLE);
            }
        });

        proceed_button = (Button)findViewById(R.id.ProceedButton);
        proceed_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(proceedCounter<2) {
                    proceedCounter+=1;
                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                }else{
                    proceedCounter=0;
                    startActivity(new Intent(MainActivity.this, HomePage.class));
                }
            }
        });

    }



    public void setTime() {
        timePicker1.setCurrentHour(hourCreator());
        timePicker1.setCurrentMinute( minCreator());

        timePicker2.setCurrentHour(hourCreator());
        timePicker2.setCurrentMinute( minCreator());

        timePicker3.setCurrentHour(hourCreator());
        timePicker3.setCurrentMinute( minCreator());

        timePicker4.setCurrentHour(hourCreator());
        timePicker4.setCurrentMinute( minCreator());
    }

    public int hourCreator(){

        int  hour = rand.nextInt(24) + 1;
        return hour;
    }

    public int minCreator(){

        int  min = rand.nextInt(59);
        return min;
    }


    public boolean comparePassword(){
        int[] goal_password = new int [8];
        User data = new User();
        if(proceedCounter==0) {
            goal_password = data.getGmail_pass();
        }
        if(proceedCounter==1) {
            goal_password = data.getFacebook_pass();
        }
        if(proceedCounter==2) {
            goal_password = data.getBank_pass();
        }
        int [] curr_pass = new int[8];
        curr_pass[0] = timePicker1.getCurrentHour(); curr_pass[1] = timePicker1.getCurrentMinute();

        curr_pass[2] = timePicker2.getCurrentHour(); curr_pass[3] = timePicker2.getCurrentMinute();

        curr_pass[4] = timePicker3.getCurrentHour(); curr_pass[5] = timePicker3.getCurrentMinute();

        curr_pass[6] = timePicker4.getCurrentHour(); curr_pass[7] = timePicker4.getCurrentMinute();

        for(int i = 0; i<8; i++){
            if(curr_pass[i] != goal_password[i]){
                return false;
            }
        }
        return true;
    }


    public void updateCurrentPasswordText(){
        current_password = (TextView) findViewById(R.id.CurrentPassword);
        String new_output = "Current: " + timePicker1.getCurrentHour() + ":" + timePicker1.getCurrentMinute()
                + ",    " + timePicker2.getCurrentHour() + ":" + timePicker2.getCurrentMinute()
                + ",    " + timePicker3.getCurrentHour() + ":" + timePicker3.getCurrentMinute()
                + ",    " + timePicker4.getCurrentHour() + ":" + timePicker4.getCurrentMinute();
        current_password.setText(new_output);


    }

}