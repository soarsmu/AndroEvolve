package com.example.gaeunlee.babysitter.add_child_categories;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.gaeunlee.babysitter.R;

import java.util.Calendar;


public class TimePickerMeal extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {






    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);




        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));


    }




    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {



        // Do something with the time chosen by the user
        TextView tv1=(TextView) getActivity().findViewById(R.id.textView1);
        tv1.setText("아침 식사: "+view.getCurrentHour()+"시 " +view.getCurrentMinute()  +   "분 ");

        TextView tv2=(TextView) getActivity().findViewById(R.id.textView2);
        tv2.setText("점심 식사: "+view.getCurrentHour()+"시 " +view.getCurrentMinute()  +   "분 ");


        TextView tv3=(TextView) getActivity().findViewById(R.id.textView3);
        tv3.setText("저녁 식사: "+view.getCurrentHour()+"시 " +view.getCurrentMinute()  +   "분 ");

    }









}