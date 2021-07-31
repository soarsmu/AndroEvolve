package p021us.zoom.androidlib.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import androidx.annotation.NonNull;
import java.util.Calendar;
import p021us.zoom.androidlib.C4409R;

/* renamed from: us.zoom.androidlib.widget.ZMTimePickerDialog */
public class ZMTimePickerDialog extends ZMAlertDialog implements OnClickListener, OnTimeChangedListener {
    private static final String HOUR = "hour";
    private static final String IS_24_HOUR = "is24hour";
    private static final String MINUTE = "minute";
    Calendar calendar;
    private final OnTimeSetListener mCallback;
    int mInitialHourOfDay;
    int mInitialMinute;
    boolean mIs24HourView;
    private final TimePicker mTimePicker;

    /* renamed from: us.zoom.androidlib.widget.ZMTimePickerDialog$OnTimeSetListener */
    public interface OnTimeSetListener {
        void onTimeSet(TimePicker timePicker, int i, int i2);
    }

    public void onTimeChanged(TimePicker timePicker, int i, int i2) {
    }

    public ZMTimePickerDialog(Context context, OnTimeSetListener onTimeSetListener, int i, int i2, boolean z) {
        this(context, 0, onTimeSetListener, i, i2, z);
    }

    public ZMTimePickerDialog(Context context, int i, OnTimeSetListener onTimeSetListener, int i2, int i3, boolean z) {
        super(context, i);
        this.mCallback = onTimeSetListener;
        this.mInitialHourOfDay = i2;
        this.mInitialMinute = i3;
        this.mIs24HourView = z;
        setTitle(C4409R.string.zm_time_picker_dialog_title);
        Context context2 = getContext();
        setButton(-1, context2.getText(C4409R.string.zm_date_time_set), this);
        setButton(-3, context2.getText(C4409R.string.zm_date_time_cancel), this);
        View inflate = ((LayoutInflater) context2.getSystemService("layout_inflater")).inflate(C4409R.layout.zm_time_picker_dialog, null);
        setView(inflate);
        this.mTimePicker = (TimePicker) inflate.findViewById(C4409R.C4411id.timePicker);
        this.mTimePicker.setIs24HourView(Boolean.valueOf(this.mIs24HourView));
        this.mTimePicker.setCurrentHour(Integer.valueOf(this.mInitialHourOfDay));
        this.mTimePicker.setCurrentMinute(Integer.valueOf(this.mInitialMinute));
        this.mTimePicker.setOnTimeChangedListener(this);
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        if (i == -1) {
            tryNotifyTimeSet();
        } else {
            dismiss();
        }
    }

    public void updateTime(int i, int i2) {
        this.mTimePicker.setCurrentHour(Integer.valueOf(i));
        this.mTimePicker.setCurrentMinute(Integer.valueOf(i2));
    }

    private void tryNotifyTimeSet() {
        if (this.mCallback != null) {
            this.mTimePicker.clearFocus();
            OnTimeSetListener onTimeSetListener = this.mCallback;
            TimePicker timePicker = this.mTimePicker;
            onTimeSetListener.onTimeSet(timePicker, timePicker.getCurrentHour().intValue(), this.mTimePicker.getCurrentMinute().intValue());
        }
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        super.onStop();
    }

    @NonNull
    public Bundle onSaveInstanceState() {
        Bundle onSaveInstanceState = super.onSaveInstanceState();
        onSaveInstanceState.putInt(HOUR, this.mTimePicker.getCurrentHour().intValue());
        onSaveInstanceState.putInt(MINUTE, this.mTimePicker.getCurrentMinute().intValue());
        onSaveInstanceState.putBoolean(IS_24_HOUR, this.mTimePicker.is24HourView());
        return onSaveInstanceState;
    }

    public void onRestoreInstanceState(@NonNull Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        int i = bundle.getInt(HOUR);
        int i2 = bundle.getInt(MINUTE);
        this.mTimePicker.setIs24HourView(Boolean.valueOf(bundle.getBoolean(IS_24_HOUR)));
        this.mTimePicker.setCurrentHour(Integer.valueOf(i));
        this.mTimePicker.setCurrentMinute(Integer.valueOf(i2));
    }
}
