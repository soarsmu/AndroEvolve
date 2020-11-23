package com.example.android.to_do_list_2_0.myActivities;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Vibrator;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.android.to_do_list_2_0.R;
import com.example.android.to_do_list_2_0.room.Task;
import com.example.android.to_do_list_2_0.myViewModel.myViewModel;

import java.util.ArrayList;
import java.util.Calendar;

public class addToDo extends AppCompatActivity {


    private myViewModel viewModel;
    private ArrayList<Task> todoTask = new ArrayList<Task>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_do);

        //Force the keyboard to pop up
        InputMethodManager imm = (InputMethodManager)   getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        viewModel = ViewModelProviders.of(this).get(myViewModel.class);
    }
    //User hit the "Add ToDo" button. Add in the new Task to the database and display task on screen
    public void addToDo(View view){

        //Get the text the user entered
        EditText editText = findViewById(R.id.edit_text_add_todo);
        editText.requestFocus();

        Intent replyIntent = new Intent();
        //replyIntent.putExtra("todoTask", editText.getText().toString());

        //Get time fro m the button
        Button button = findViewById(R.id.button_add_time);
        String[] array = new String[2];
        array[0] = editText.getText().toString();
        array[1] =  button.getText().toString();

        //Check to see if user entered a time or not
        if(array[1].equals("Add a Time")){
            array[1] = "11:59PM";
        }
        replyIntent.putExtra("todoTask", array);

        //Get rid of the onscreen keyboard
        hideKeyboard(this);
        //Create toast saying todoTask added
        Toast.makeText(this, "ToDo Created!", Toast.LENGTH_SHORT).show();

        setResult(RESULT_OK, replyIntent);
        finish();
    }

    //Hide keyboard
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void addTime(View view){

        //Hide Keyboard
        hideKeyboard(this);

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = layoutInflater.inflate(R.layout.time_layout, null);

        final TimePicker timePicker = v.findViewById(R.id.time_picker);

        Button button = v.findViewById(R.id.button_test_time);

        final PopupWindow popupWindow = new PopupWindow(v, RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT, true);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {checkTime(popupWindow, timePicker); }});

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {

                //Have this minute variable because getCurrentMinute doesnt have "01", its only "1"
                String minute = null;
                boolean apoveAPI23 = false;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    minute = String.valueOf(timePicker.getMinute());
                    apoveAPI23 = true;
                }else{
                    minute = String.valueOf(timePicker.getCurrentMinute());
                }

                if(apoveAPI23){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if(timePicker.getMinute() < 10){
                            minute = "0" + String.valueOf(timePicker.getCurrentMinute());
                        }
                    }
                }
                else{
                    minute = "0" + String.valueOf(timePicker.getCurrentMinute());
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Log.d("timePicker", "hour = " + String.valueOf(timePicker.getHour()) + " minute = " + String.valueOf(i1));
                }

                else {
                    String amOrPm = "PM";
                    if (apoveAPI23) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (timePicker.getHour() < 12) {
                                amOrPm = "AM";
                            }
                        }
                    } else {
                        if (timePicker.getCurrentHour() < 12) {
                            amOrPm = "AM";
                        }
                        Log.d("timePicker", "hour = " + String.valueOf(timePicker.getCurrentHour())
                                + "minute = " + minute
                                + " " + amOrPm);
                    }
                }
            }
        });


        popupWindow.setTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

    }

    public void checkTime(PopupWindow popupWindow, TimePicker timePicker){

        Calendar calendar = Calendar.getInstance();

        //Have to check if the user entered hour is less than the current hour &&
        //If the hour is equal to the current hour check the minute time
        //getCurrentHour is in 24 hour time. ie 1pm = 13
        if(timePicker.getCurrentHour() < calendar.get(Calendar.HOUR_OF_DAY) ||
                (timePicker.getCurrentHour() == calendar.get(Calendar.HOUR_OF_DAY) &&
                 timePicker.getCurrentMinute() < calendar.get(Calendar.MINUTE))){
            Log.d("timePicker","hour = " + String.valueOf(timePicker.getCurrentHour()) + " actual hour = " + String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)));
            Log.d("timePicker", "minute = " + String.valueOf(timePicker.getCurrentMinute() + "actual minute = " + String.valueOf(calendar.get(Calendar.MINUTE))));
            Toast.makeText(this,"Time has to be after the current time. We can't time travel yet", Toast.LENGTH_LONG).show();
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            }
        }
        //Time is valid
        else{
            Button button = findViewById(R.id.button_add_time);

            //Have this minute variable because getCurrentMinute doesnt have "01", its only "1"
            String minute = String.valueOf(timePicker.getCurrentMinute());

            if(timePicker.getCurrentMinute() < 10){
                minute = "0" + String.valueOf(timePicker.getCurrentMinute());
            }

            if(timePicker.getCurrentHour() > 12) {
                button.setText(String.valueOf(timePicker.getCurrentHour() - 12) + ":"
                        + minute + "PM");
            }
            else{
                button.setText(String.valueOf(timePicker.getCurrentHour() + ":"
                        + minute + "AM"));
            }

            popupWindow.dismiss();
        }
    }
}
