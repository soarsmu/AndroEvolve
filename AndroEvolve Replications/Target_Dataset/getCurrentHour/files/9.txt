package com.takecare.backgate;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

public class From_Time extends Activity {

    TimePicker timePicker;
    TextView time_selected;
    ImageView imageButton_from;
    String[] details_array = new String[7];
    String[] incoming_text=new String[1];
    String min_str;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        details_array = getIntent().getStringArrayExtra("DETAILS");
        incoming_text = getIntent().getStringArrayExtra("FLOW_LEVEL_DETAILS");

        if(incoming_text[0].equals("HOUR")){
            setContentView(R.layout.from_time_hour);
            NumberPicker num_pick=(NumberPicker)findViewById(R.id.to_hour_value);
            num_pick.setMaxValue(24);
        }
        else{
            setContentView(R.layout.from_time);
        }

        ActionBar mActionBar = getActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);

        View mCustomView = mInflater.inflate(R.layout.custom_actionbar, null);
        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);

        timePicker=(TimePicker)findViewById(R.id.timePicker1);

        time_selected=(TextView)findViewById(R.id.time_selected);
        String hour = Integer.toString(((timePicker.getCurrentHour().intValue())>12)?((timePicker.getCurrentHour().intValue()) - 12) : (timePicker.getCurrentHour().intValue()));
        String am_pm = get_am_pm(timePicker.getCurrentHour());
        int minute = timePicker.getCurrentMinute();
        if(hour.length()==1){
            hour="0"+hour;
        }
        if(minute<10){
            min_str="0"+String.valueOf(minute);
        }else{
            min_str=String.valueOf(minute);
        }
        time_selected.setText(" "+String.valueOf(hour)+":"+min_str+" "+am_pm);
        details_array[1] = String.valueOf(time_selected.getText());

        imageButton_from=(ImageView)findViewById(R.id.imageButton_from);

        addListenerOnButton();
    }

    public void addListenerOnButton() {
        //Select a specific button to bundle it with the action you want

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {

            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                String hour = Integer.toString(((view.getCurrentHour().intValue()) > 12) ? ((view.getCurrentHour().intValue()) - 12) : (view.getCurrentHour().intValue()));
                minute = timePicker.getCurrentMinute();
                String am_pm = get_am_pm(timePicker.getCurrentHour());
                if(hour.length()==1){
                    hour="0"+hour;
                }
                if(minute<10){
                    min_str="0"+String.valueOf(minute);
                }else{
                    min_str=String.valueOf(minute);
                }
                time_selected.setText(" "+String.valueOf(hour)+":"+min_str+" "+am_pm);
                details_array[1] = String.valueOf(time_selected.getText());
                //Log.d("Note :",details_array[0]);
                //Log.d("Note :",details_array[1]);
            }
        });

        imageButton_from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent myIntent;
                if(incoming_text[0].equals("HOUR")){
                    NumberPicker num_pick=(NumberPicker)findViewById(R.id.to_hour_value);
                    num_pick.setMaxValue(24);
                    details_array[2]="NA";
                    details_array[3]=String.valueOf(num_pick.getValue());
                    myIntent = new Intent(From_Time.this, Details.class);
                }
                else{
                    myIntent = new Intent(From_Time.this, To_Date.class);
                }
                myIntent.putExtra("DETAILS", details_array);
                myIntent.putExtra("FLOW_LEVEL_DETAILS", incoming_text);
                From_Time.this.startActivity(myIntent);
            }
        });
    }

    public String get_am_pm(int hour){
        if(hour>11)
            return "PM";
        else
            return "AM";
    }
}
