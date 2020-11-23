package com.pinnet.chargerapp.ui.common.dialogfragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

import com.pinnet.chargerapp.R;
import com.pinnet.chargerapp.base.BaseDialogFragment;
import com.pinnet.chargerapp.utils.ToastUtils;

/**
 * @author P00558
 * @date 2018/6/22
 */

public class TimePickerDialogFragment extends BaseDialogFragment implements TimePicker.OnTimeChangedListener {
    private Context mContext;
    private TimePicker.OnTimeChangedListener onTimeChangedListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View view = LayoutInflater.from(mContext).inflate(R.layout.charger_method_option_item_timepicker, null);
        final TimePicker timePicker = (TimePicker) view.findViewById(R.id.time_picker);
        Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        Button btnConfirm = (Button) view.findViewById(R.id.btn_confirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onTimeChangedListener != null) {
                    if (timePicker.getCurrentHour() == 0 && timePicker.getCurrentMinute() == 0) {
                        ToastUtils.getInstance().showMessage(getString(R.string.charger_method_option_useless_value));
                        return;
                    }
                    if (timePicker.getCurrentHour()>16||(timePicker.getCurrentHour() == 16 && timePicker.getCurrentMinute() > 0)){
                        ToastUtils.getInstance().showMessage(getString(R.string.charger_method_option_useless_max_value));
                        return;
                    }
                    onTimeChangedListener.onTimeChanged(timePicker, timePicker.getCurrentHour(), timePicker.getCurrentMinute());
                    dismiss();
                }
            }
        });
        timePicker.setIs24HourView(true);
        timePicker.setCurrentHour(0);
        timePicker.setCurrentMinute(10);
        builder.setView(view);
        return builder.create();
    }

    public void show(FragmentManager fragmentManager, String tag, TimePicker.OnTimeChangedListener onTimeChangedListener) {
        this.onTimeChangedListener = onTimeChangedListener;
        show(fragmentManager, tag);
    }
}
