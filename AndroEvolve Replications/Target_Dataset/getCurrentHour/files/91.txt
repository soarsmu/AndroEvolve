package org.androidtown.healthcareguide.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import org.androidtown.healthcareguide.Fragment.InputdiabetsFragment;
import org.androidtown.healthcareguide.R;

import java.util.Calendar;

/**
 * Created by yjhyj on 2017-11-19.
 */

public class DateTimeDialog extends Dialog{

    EditText editText;
    TimePicker timePicker;
    DatePicker datePicker;
    TextView tv_date;
    String date;
    String time;
    public DateTimeDialog(@NonNull Context context, TextView tv_date) {
        super(context);
        this.tv_date = tv_date;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.datetime_dialog);

        timePicker = findViewById(R.id.timePicker);
        datePicker = findViewById(R.id.datePicker);
      //  editText=(EditText)findViewById(R.id.et_date);


        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, datePicker.getYear());
        calendar.set(Calendar.MONTH, datePicker.getMonth());
        calendar.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
        calendar.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
        calendar.set(Calendar.MINUTE, timePicker.getCurrentMinute());
        calendar.set(Calendar.SECOND, 0);


        Button bt_datesave=(Button)findViewById(R.id.bt_datesave);
        bt_datesave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              
                String str = datePicker.getYear()+"-"+(datePicker.getMonth()+1)+"-"+datePicker.getDayOfMonth()+" "+timePicker.getCurrentHour()+":"+timePicker.getCurrentMinute();

                date = datePicker.getYear()+"-"+(datePicker.getMonth()+1)+"-"+datePicker.getDayOfMonth();
                time = timePicker.getCurrentHour()+":"+timePicker.getCurrentMinute();

                InputdiabetsFragment.setD(date);
                InputdiabetsFragment.setT(time);
                tv_date.setText(str);
                dismiss();
            }
        });


    }


}
