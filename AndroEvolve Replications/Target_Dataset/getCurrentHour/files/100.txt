package com.personal.poryto.poryto.ViewHolder;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.personal.poryto.poryto.R;
import com.personal.poryto.poryto.Ulti.Ulti;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;

/**
 * Created by user on 9/10/2017.
 */

public class ChildClockViewholder extends ChildViewHolder implements View.OnClickListener {

    private TextView txt_workTime;
    private TextView txt_breakTime;
    private CheckBox cn_viberation;
    private Spinner sp_rington;
    private Context context;
    private SharedPreferences sharedPreferences;

    public ChildClockViewholder(View itemView,Context context) {
        super(itemView);
        this.context = context;
        txt_workTime = itemView.findViewById(R.id.txt_work_time);
        txt_breakTime = itemView.findViewById(R.id.txt_break_time);
        cn_viberation = itemView.findViewById(R.id.cb_viberation);
        txt_workTime.setOnClickListener(this);
        txt_breakTime.setOnClickListener(this);
        //sp_rington = itemView.findViewById(R.id.ringtone);

    }



    @Override
    public void onClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final TimePicker timePicker = new TimePicker(context);

        switch (view.getId()){
                case R.id.txt_work_time:

                    builder.setTitle("Set Work Time");
                    timePicker.setIs24HourView(true);
                    builder.setView(timePicker);

                    builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Ulti.Loger("Time is from View "+timePicker.getCurrentHour()+":"+timePicker.getCurrentMinute());
                            txt_workTime.setText(timePicker.getCurrentHour()+":"+timePicker.getCurrentMinute());

                        }
                    });
                    builder.show();

                    break;
            case R.id.txt_break_time:

                builder.setTitle("Set Break Time");


                timePicker.setIs24HourView(true);
                builder.setView(timePicker);

                builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Ulti.Loger("Time is from View "+timePicker.getCurrentHour()+":"+timePicker.getCurrentMinute());
                        txt_breakTime.setText(timePicker.getCurrentHour()+":"+timePicker.getCurrentMinute());

                    }
                });
                builder.show();

                break;
        }
    }

}
