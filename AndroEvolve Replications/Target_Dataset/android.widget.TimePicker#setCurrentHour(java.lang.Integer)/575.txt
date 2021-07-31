package com.fudd.calendarstudy.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TimePicker;

import com.fudd.main.R;

/**
 * Created by fudd-office on 2017-3-29 10:27.
 * Email: 5036175@qq.com
 * QQ: 5036175
 * Description:
 */

public class DateDialog extends DialogFragment {
    public DateDialog() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_datepicker,null);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);


        TimePicker am_timePicker = (TimePicker) view.findViewById(R.id.am_picker);
        TimePicker pm_timePicker = (TimePicker) view.findViewById(R.id.pm_picker);

        am_timePicker.setIs24HourView(true);
        am_timePicker.setCurrentHour(Integer.valueOf(9));
        am_timePicker.setCurrentMinute(Integer.valueOf(0));
        pm_timePicker.setIs24HourView(true);
        pm_timePicker.setCurrentHour(Integer.valueOf(18));
        pm_timePicker.setCurrentMinute(Integer.valueOf(0));
        return view;
    }

//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        return  new AlertDialog.Builder(getActivity()).setTitle("选择时间")
//                .setView(R.)
//    }
}
