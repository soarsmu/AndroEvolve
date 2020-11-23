package org.jorge.lolin1.func.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;

import com.crashlytics.android.Crashlytics;

import org.jorge.lolin1.utils.LoLin1Utils;

/**
 * This file is part of LoLin1.
 * <p/>
 * LoLin1 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * LoLin1 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with LoLin1. If not, see <http://www.gnu.org/licenses/>.
 * <p/>
 * Created by Jorge Antonio Diaz-Benito Soriano on 07/01/14.
 */
public class TimePickerPreference extends DialogPreference
        implements TimePicker.OnTimeChangedListener {

    private static final String SANITY_EXPRESSION = "[0-5]*[0-9]:[0-5]*[0-9]";
    private static final String DEFAULT_VALUE = "00:00";
    private static final Integer ERROR_CODE = -1;
    private static final Integer COUNTER_AMOUNT = 3;
    private static Integer TYPE_COUNTER = 0;
    private String value;

    public TimePickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setPersistent(Boolean.TRUE);
        String instanceType;
        switch (TYPE_COUNTER % COUNTER_AMOUNT) {
            case 0:
                instanceType = "baron";
                break;
            case 1:
                instanceType = "dragon";
                break;
            case 2:
                instanceType = "buff";
                break;
            default:
                Crashlytics.log(Log.ERROR, "debug",
                        "Unsupported timer type - TYPE_COUNTER: " + TYPE_COUNTER);
                instanceType = "ERROR";
        }
        value = "pref_default_" + instanceType + "_respawn";
        TYPE_COUNTER++;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            persistString(value);
        }
    }

    @Override
    protected View onCreateDialogView() {
        TimePicker timePicker = new TimePicker(getContext());
        timePicker.setOnTimeChangedListener(this);

        int mm = getMinutes();
        int ss = getSeconds();

        timePicker.setIs24HourView(Boolean.TRUE);

        if (mm != ERROR_CODE && ss != ERROR_CODE) {
            timePicker.setCurrentHour(mm);
            timePicker.setCurrentMinute(ss);
        }

        return timePicker;
    }

    @Override
    public void onTimeChanged(TimePicker view, int minutes, int seconds) {
        String result = minutes + ":" + seconds;
        callChangeListener(result);
        value = result;
    }

    /**
     * @return {@link java.lang.Integer} The minutes, which will be 0 to 59 (inclusive)
     */
    private Integer getMinutes() {
        String time = getPersistedString(this.value);

        if (time == null || !time.matches(SANITY_EXPRESSION)) {
            return ERROR_CODE;
        }

        return Integer.valueOf(time.split(":")[0]);
    }

    /**
     * @return {@link java.lang.Integer} The seconds, which will be from 0 to 59 (inclusive)
     */
    private Integer getSeconds() {
        String time = getPersistedString(this.value);

        if (time == null || !time.matches(SANITY_EXPRESSION)) {
            return ERROR_CODE;
        }

        return Integer.valueOf(time.split(":")[1]);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            // Restore existing state
            value = this.getPersistedString(DEFAULT_VALUE);
        }
        else {
            // Set default state from the XML attribute
            value = (String) defaultValue;
            persistString(value);
        }
    }
}
