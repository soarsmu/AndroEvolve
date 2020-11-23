package com.hy.happyword.widget;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TimePicker;

import java.util.HashMap;

/**
 * Created by 980559 on 2017/4/24.
 */
public class timePreference extends DialogPreference {

    public LinearLayout ll;
    public Context context;
    TimePicker timePicker;

    public timePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    @Override
    protected void onPrepareDialogBuilder(Builder builder) {
        super.onPrepareDialogBuilder(builder);
        ll = new LinearLayout(context);
        ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
        ll.setGravity(Gravity.CENTER);
        timePicker = new TimePicker(context);
        timePicker.setIs24HourView(true);
        ll.addView(timePicker);
        builder.setView(ll);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult){
            HashMap<String, Integer> map = new HashMap<String, Integer>();
            map.put("hour", timePicker.getCurrentHour());
            map.put("minute", timePicker.getCurrentMinute());
            String ifAm =" 上午";
            if(timePicker.getCurrentHour()>11)ifAm=" 下午";
            int minute=map.get("minute");
            String mi=String.valueOf(minute);
            if (minute<10)mi="0"+minute;
            this.persistString(""+timePicker.getCurrentHour()+":"+mi+ifAm);
            this.getOnPreferenceChangeListener().onPreferenceChange(this, map);

        }
        super.onDialogClosed(positiveResult);
    }
}
