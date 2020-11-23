package org.androidtown.healthcareguide.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import org.androidtown.healthcareguide.Fragment.BloodPressureFragment1;
import org.androidtown.healthcareguide.R;

import java.util.Calendar;

/**
 * Created by yjhyj on 2017-11-24.
 */

public class DateTimeDialog2 extends Dialog{


    TimePicker timePicker;
    DatePicker datePicker;
    TextView tv_dates;

    public DateTimeDialog2(@NonNull Context context, TextView tv_dates) {
        super(context);
        this.tv_dates= tv_dates;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.datatime_dialog2);

        timePicker=findViewById(R.id.timePicker2);
        datePicker=findViewById(R.id.datePicker2);



        Calendar calendar=Calendar.getInstance();
        calendar.set(Calendar.YEAR, datePicker.getYear());
        calendar.set(Calendar.MONTH, datePicker.getMonth());
        calendar.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
        calendar.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
        calendar.set(Calendar.MINUTE, timePicker.getCurrentMinute());
        calendar.set(Calendar.SECOND, 0);


        Button bt_datesave2=findViewById(R.id.bt_datesave2);
        bt_datesave2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String str=datePicker.getYear()+"-"+(datePicker.getMonth()+1)+"-"+datePicker.getDayOfMonth()+" "+timePicker.getCurrentHour()+":"+timePicker.getCurrentMinute();

                String date = datePicker.getYear()+"-"+(datePicker.getMonth()+1)+"-"+datePicker.getDayOfMonth();
                String time = timePicker.getCurrentHour()+":"+timePicker.getCurrentMinute();

                BloodPressureFragment1.setD(date);
                BloodPressureFragment1.setT(time);
                tv_dates.setText(str);
                dismiss();
            }
        });


    }

}
