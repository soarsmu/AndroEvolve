package com.kjw.twentyhour.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;


import com.kjw.twentyhour.R;
import com.kjw.twentyhour.StateActivity;
import com.kjw.twentyhour.data.Time;
import com.kjw.twentyhour.fragmenttofragmentinterface.FragmentDataSendInterface;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimeStartFragment extends Fragment {

    TimePicker timePicker;
    Button buttonTimeStart;

    int selectedHour;
    int selectedMin;

    FragmentDataSendInterface mListener;


    public TimeStartFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =inflater.inflate(R.layout.fragment_time_start, container, false);

        // Inflate the layout for this fragment


        timePicker = (TimePicker)view.findViewById(R.id.timepicker_start);
        timePicker.setIs24HourView(true);
        selectedHour = timePicker.getCurrentHour();
        selectedMin = timePicker.getCurrentHour();




        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {

                Integer hour = hourOfDay;
                Integer min = minute;

                selectedHour = hour;
                selectedMin = min;


            }
        });
        buttonTimeStart = view.findViewById(R.id.btn_time_s);



        buttonTimeStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectedHour = timePicker.getCurrentHour();
                Time time = new Time();
                time.setHour(selectedHour);
                time.setMin(selectedMin);

                mListener.myStartTimeStart(selectedHour , selectedMin);
                mListener.transferTab();


            }
        });

        return view;

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mListener = (StateActivity) context;


    }

}
