package application.com.car.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

import application.com.car.R;
import application.com.car.entity.Route;

/**
 * Created by Zahit Talipov on 18.01.2016.
 */
public class TimeChoiceFragment extends DialogFragment implements DialogInterface.OnClickListener {
    TimePicker timePicker;
    Button button;

    public TimeChoiceFragment(Button v) {
        button = v;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        builder.setPositiveButton("ОК", this);
        View view = layoutInflater.inflate(R.layout.choice_time, null);
        timePicker = (TimePicker) view.findViewById(R.id.timePickerFinish);
        timePicker.setIs24HourView(true);
        builder.setView(view);
        return builder.create();
    }


    @Override
    public void onClick(DialogInterface dialog, int which) {
        Route.setTime(timePicker.getCurrentHour(), timePicker.getCurrentMinute());
        if (timePicker.getCurrentMinute() < 10)
            button.setText(timePicker.getCurrentHour() + ":" + "0" + timePicker.getCurrentMinute());
        else
            button.setText(timePicker.getCurrentHour() + ":" + timePicker.getCurrentMinute());
    }
}
