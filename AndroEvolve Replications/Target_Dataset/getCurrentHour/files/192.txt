package com.mustafa.silentplease.fragments;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Switch;
import android.widget.TimePicker;

import com.mustafa.silentplease.CustomSwitchCheckedListener;
import com.mustafa.silentplease.R;
import com.mustafa.silentplease.utils.Constants;
import com.mustafa.silentplease.utils.Utils;

/**
 * Created by Mustafa.Gamesterz on 25/05/16.
 */
public class TimeDIalogFragment extends DialogPreference {

    private int lastHour = Utils.getHour();
    private int lastMinute = Utils.getMinute();
    private boolean isAm = Utils.isAM();
    private TimePicker timePicker = null;
    private CustomSwitchCheckedListener switchCheckedListener;

    public TimeDIalogFragment(Context context, AttributeSet attrs){
        super(context,attrs);


        setNegativeButtonText(context.getString(R.string.negative_dialog_button));
        setPositiveButtonText(context.getString(R.string.positive_dialog_button));

    }

    private int get24Hour(String time){
        if(time == null)
            return 0;

        String[] pieces = time.split(":");
        return Integer.valueOf(pieces[0]);
    }

    public static int getMinute(String time){
        String[] pieces = time.split(":");
        String minPieces = pieces[1];
        minPieces = minPieces.substring(0,minPieces.indexOf(" "));
        return (Integer.parseInt(minPieces));
    }

    public CustomSwitchCheckedListener getSwitchLIStener(){
        return switchCheckedListener;
    }

    public boolean isSwitchON(){
        return switchCheckedListener.isSwitchON();
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        if(isAm){
            final int newHour = lastHour + 12;
            if(newHour <= 24)
                lastHour += 12;
        } else if (lastHour==12)
            lastHour =0 ;

        timePicker.setCurrentHour(lastHour);
        timePicker.setCurrentMinute(lastMinute);

    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);

        Switch swith = (Switch) view.findViewById(R.id.switchWidget);
        switchCheckedListener = new CustomSwitchCheckedListener(getContext(),swith,getKey() + Constants.PREF_KEY_SWITCH_SUFFIX);


    }

    @Override
    protected View onCreateDialogView() {
        timePicker = new TimePicker(getContext());
        timePicker.setIs24HourView(false);

        return timePicker;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if(positiveResult){
            int h24Format = timePicker.getCurrentHour();
            lastHour = timePicker.getCurrentHour();
            lastMinute = timePicker.getCurrentMinute();

            String suffix = " AM";
            isAm = true;
            if(timePicker.getCurrentHour() == 0)
                lastHour = 12;
            else if(timePicker.getCurrentHour() == 12){
                suffix = " PM";
                isAm = false;
                h24Format = 12;
            } else if(timePicker.getCurrentHour() > 12){
                suffix = " PM";
                lastHour -= 12;
                isAm = false;
            }

            String time = String.valueOf(h24Format) + ":" + String.format("%02d", lastMinute) + suffix;

            if(callChangeListener(time)){
                persistString(time);
            }

        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        String time;

        if(restorePersistedValue){
            if(defaultValue == null){
                time = getPersistedString(Utils.getNowTime());
            } else {
                time = getPersistedString(defaultValue.toString());
            }
        } else {
            time = defaultValue.toString();
        }

        lastHour = get24Hour(time);
        lastMinute = getMinute(time);

        persistString(time);
    }

    public void setNowValue(String time){
        if(time == null) return;

        lastHour = get24Hour(time);
        lastMinute = getMinute(time);

        persistString(time);
    }
}
