package ru.android.cyfral.servisnik.ui.ordercard;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.NumberPicker;
import android.widget.TimePicker;

public class MyTimePicker extends TimePicker {

    private static final int TIME_PICKER_MINUTE_INTERVAL = 5;
    private TimePicker.OnTimeChangedListener timeChangedListener;

    public MyTimePicker(Context context) {
        super(context);

        try {
            Class<?> classForId = Class.forName("com.android.internal.R$id");
            Field field = classForId.getField("minute");

            NumberPicker minuteSpinner = (NumberPicker) this.findViewById(field.getInt(null));
            minuteSpinner.setMaxValue((60 / TIME_PICKER_MINUTE_INTERVAL) - 1);
            List<String> displayedValues = new ArrayList<>();
            for (int i = 0; i < 60; i += TIME_PICKER_MINUTE_INTERVAL)
                displayedValues.add(String.format("%02d", i));
            minuteSpinner.setDisplayedValues(displayedValues.toArray(new String[displayedValues.size()]));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int maxMinuteIndex() {
        return (60 / TIME_PICKER_MINUTE_INTERVAL) - 1;
    }

    @Override
    public void setOnTimeChangedListener(TimePicker.OnTimeChangedListener onTimeChangedListener) {
        super.setOnTimeChangedListener(internalTimeChangedListener);
        this.timeChangedListener = onTimeChangedListener;
    }

    @Override
    public Integer getCurrentMinute() {
        return super.getCurrentMinute() * TIME_PICKER_MINUTE_INTERVAL;
    }

    @Override
    public void setCurrentMinute(Integer currentMinute) {
        int cleanMinute = currentMinute / TIME_PICKER_MINUTE_INTERVAL;
        if(currentMinute % TIME_PICKER_MINUTE_INTERVAL > 0) {
            if(cleanMinute == maxMinuteIndex()) {
                cleanMinute = 0;
                setCurrentHour(getCurrentHour()+1);
            } else {
                cleanMinute++;
            }
        }
        super.setCurrentMinute(cleanMinute);
    }

    // We want to proxy all the calls to our member variable OnTimeChangedListener with our own
    // internal listener in order to make sure our overridden getCurrentMinute is called. Without
    // this some versions of android return the underlying minute index.
    private TimePicker.OnTimeChangedListener internalTimeChangedListener = new TimePicker.OnTimeChangedListener() {
        @Override
        public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
            timeChangedListener.onTimeChanged(view, getCurrentHour(), getCurrentMinute());
        }
    };
}