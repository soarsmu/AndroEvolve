package com.example.android.simplealarmapp;

import android.app.DialogFragment;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;

public class PopTime extends DialogFragment implements View.OnClickListener {

    // Widgets
    private TimePicker timePicker;
    private Button button;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pop_time, container, false);
        timePicker = view.findViewById(R.id.time_picker);
        button = view.findViewById(R.id.bu_done);
        button.setOnClickListener(this);

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        this.dismiss();
        MainActivity mainActivity = (MainActivity) getActivity();
        if (Build.VERSION.SDK_INT > 23) {
            mainActivity.setTime(timePicker.getHour(), timePicker.getMinute());
            mainActivity.alarmTextView.setText(timePicker.getHour() + " : " + timePicker.getMinute());
        } else {
            mainActivity.setTime(timePicker.getCurrentHour(), timePicker.getCurrentMinute());
            mainActivity.alarmTextView.setText(timePicker.getCurrentHour() + " : " + timePicker.getCurrentMinute());
        }

    }
}
