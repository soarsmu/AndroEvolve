package com.kludge.wakemeup;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

/**
 * Created by Zex on 15/6/2016.]
 * custom preference dialog
 */
public class TimePreference extends DialogPreference{

    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor editor;

    TimePicker timePicker;

    public TimePreference(Context context, AttributeSet attrSet){
        super(context,attrSet);

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
    }

    @Override
    protected View onCreateDialogView() {
        timePicker = new TimePicker(getContext());
        return timePicker;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        editor = sharedPrefs.edit();

        if(positiveResult){
            editor.putInt("preference_alarm_hour", timePicker.getCurrentHour());
            editor.putInt("preference_alarm_minute", timePicker.getCurrentMinute());
            editor.putInt("preference_alarm_time", timePicker.getCurrentHour()*3600000+timePicker.getCurrentMinute()*60000); //placeholder just so onSharedPrefChange is called? idk


            editor.putInt("preference_alarm_time", timePicker.getCurrentHour()*3600000 + timePicker.getCurrentMinute()*60000);

            editor.apply();
        }
    }

    //access timePicker here
    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
    }


}
