package com.kebab;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.DialogPreference;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import com.kebab.Llama.EventConditions.HourMinute;
import com.kebab.Llama.LlamaSettings;
import com.kebab.Llama.R;
import com.kebab.PreferenceEx.Helper;

public class DualTimePickerPreference<TValue> extends DialogPreference implements OnTimeChangedListener, PreferenceEx<TValue> {
    private static final String VALIDATION_EXPRESSION = "[0-2]*[0-9]:[0-5]*[0-9]";
    CharSequence _ExistingSummary;
    OnGetValueEx<TValue> _OnGetValueEx;
    OnPreferenceClick _OnPreferenceClick;
    int hour1;
    int hour2;
    boolean initted = false;
    int minute1;
    int minute2;
    TimePicker tp1;
    TimePicker tp2;

    public DualTimePickerPreference(Context context) {
        super(context, null);
        initialize();
    }

    public DualTimePickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public DualTimePickerPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    private void initialize() {
    }

    public View GimmeView() {
        return onCreateDialogView();
    }

    /* Access modifiers changed, original: protected */
    public View onCreateDialogView() {
        ViewGroup scrollingView;
        boolean scrollRequired;
        this.initted = false;
        this.tp1 = new TimePicker(getContext());
        TimePicker timePicker = this.tp1;
        boolean z = DateFormat.is24HourFormat(getContext()) && !((Boolean) LlamaSettings.Use12HourTimePickers.GetValue(getContext())).booleanValue();
        timePicker.setIs24HourView(Boolean.valueOf(z));
        this.tp1.setOnTimeChangedListener(this);
        this.tp1.setOnFocusChangeListener(new OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                DualTimePickerPreference.this.hour1 = DualTimePickerPreference.this.tp1.getCurrentHour().intValue();
                DualTimePickerPreference.this.minute1 = DualTimePickerPreference.this.tp1.getCurrentMinute().intValue();
            }
        });
        this.tp2 = new TimePicker(getContext());
        this.tp2.setIs24HourView(Boolean.valueOf(this.tp1.is24HourView()));
        this.tp2.setOnTimeChangedListener(this);
        this.tp2.setOnFocusChangeListener(new OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                DualTimePickerPreference.this.hour2 = DualTimePickerPreference.this.tp2.getCurrentHour().intValue();
                DualTimePickerPreference.this.hour2 = DualTimePickerPreference.this.tp2.getCurrentMinute().intValue();
            }
        });
        this.hour1 = getHour1();
        this.minute1 = getMinute1();
        if (this.hour1 >= 0 && this.minute1 >= 0) {
            this.tp1.setCurrentHour(Integer.valueOf(this.hour1));
            this.tp1.setCurrentMinute(Integer.valueOf(this.minute1));
        }
        this.hour2 = getHour2();
        this.minute2 = getMinute2();
        if (this.hour2 >= 0 && this.minute2 >= 0) {
            this.tp2.setCurrentHour(Integer.valueOf(this.hour2));
            this.tp2.setCurrentMinute(Integer.valueOf(this.minute2));
        }
        Display display = ((WindowManager) getContext().getSystemService("window")).getDefaultDisplay();
        int displayGetHeight = display.getHeight();
        TextView tv1 = new TextView(getContext());
        TextView tv2 = new TextView(getContext());
        tv1.setText(R.string.hrFrom);
        tv2.setText(R.string.hrTo);
        boolean isVertical = ((double) (((float) display.getWidth()) / ((float) display.getHeight()))) < 0.675d;
        if (isVertical) {
            ViewGroup scrollingViewL = new LinearLayout(getContext());
            ((LinearLayout) scrollingViewL).setOrientation(1);
            scrollingView = scrollingViewL;
            scrollRequired = false;
        } else {
            scrollingView = new HorizontalScrollView(getContext());
            scrollRequired = true;
        }
        scrollingView.setFocusable(false);
        LinearLayout timersHolder = new LinearLayout(getContext());
        scrollingView.addView(timersHolder);
        if (isVertical) {
            timersHolder.setOrientation(1);
        } else {
            timersHolder.setOrientation(0);
        }
        timersHolder.setScrollContainer(scrollRequired);
        timersHolder.addView(tv1);
        timersHolder.addView(this.tp1);
        timersHolder.addView(tv2);
        timersHolder.addView(this.tp2);
        this.initted = true;
        return scrollingView;
    }

    public void onClick(DialogInterface dialog, int which) {
        Dialog sdialog = super.getDialog();
        if (sdialog != null) {
            View v = sdialog.getCurrentFocus();
            if (v != null) {
                v.clearFocus();
            }
        }
        super.onClick(dialog, which);
    }

    public void onTimeChanged(TimePicker view, int hour, int minute) {
        if (!this.initted) {
        }
    }

    /* Access modifiers changed, original: protected */
    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            this.hour1 = this.tp1.getCurrentHour().intValue();
            this.minute1 = this.tp1.getCurrentMinute().intValue();
            this.hour2 = this.tp2.getCurrentHour().intValue();
            this.minute2 = this.tp2.getCurrentMinute().intValue();
            persistString(getHour1() + ":" + getMinute1() + "-" + getHour2() + ":" + getMinute2());
        }
        super.onDialogClosed(positiveResult);
        onChanged();
    }

    public void setSummary(CharSequence value) {
        this._ExistingSummary = value;
        Helper.UpdateValueAndSummary(this);
    }

    public void setActualSummary(CharSequence value) {
        super.setSummary(value);
    }

    public void onAttachedToActivity() {
        super.onAttachedToActivity();
        this._ExistingSummary = super.getSummary();
        Helper.UpdateValueAndSummary(this);
    }

    public void onChanged() {
        Helper.UpdateValueAndSummary(this);
    }

    public CharSequence getOriginalSummary() {
        return this._ExistingSummary;
    }

    public String getHumanReadableValue() {
        return new HourMinute(getHour1(), getMinute1()) + " - " + new HourMinute(getHour2(), getMinute2());
    }

    public void setDefaultValue(HourMinute hm1, HourMinute hm2) {
        this.hour1 = hm1.Hours;
        this.minute1 = hm1.Minutes;
        this.hour2 = hm2.Hours;
        this.minute2 = hm2.Minutes;
    }

    public void setDefaultValue(Object defaultValue) {
        super.setDefaultValue(defaultValue);
        if (defaultValue instanceof String) {
            String[] parts = ((String) defaultValue).split("-");
            if (parts.length >= 2 && parts[0].matches(VALIDATION_EXPRESSION) && parts[1].matches(VALIDATION_EXPRESSION)) {
                String[] time1 = parts[0].split(":");
                String[] time2 = parts[1].split(":");
                this.hour1 = Integer.parseInt(time1[0]);
                this.minute1 = Integer.parseInt(time1[1]);
                this.hour2 = Integer.parseInt(time2[0]);
                this.minute2 = Integer.parseInt(time2[1]);
            }
        }
    }

    public int getHour1() {
        return this.hour1;
    }

    public int getMinute1() {
        return this.minute1;
    }

    public int getHour2() {
        return this.hour2;
    }

    public int getMinute2() {
        return this.minute2;
    }

    public void onClick() {
        if (this._OnPreferenceClick == null || this._OnPreferenceClick.CanShowDialog(this)) {
            super.onClick();
        }
    }

    public void setOnPreferenceClick(OnPreferenceClick onPreferenceClick) {
        this._OnPreferenceClick = onPreferenceClick;
    }

    public TValue GetValueEx() {
        return this._OnGetValueEx.GetValue(this);
    }

    public void SetOnGetValueExCallback(OnGetValueEx<TValue> onGetValueEx) {
        this._OnGetValueEx = onGetValueEx;
    }
}
