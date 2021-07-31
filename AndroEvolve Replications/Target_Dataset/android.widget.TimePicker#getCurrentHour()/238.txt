package com.jsy.xuezhuli.utils;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

import com.jsy_jiaobao.main.R;

public class ShowPopup {
	private PopupWindow ppw;
	private Context mcontext;
	private String time;
	public ShowPopup(Context context){
		this.mcontext = context;
	}
	public void showPop(View layout,final TextView view) {
		LayoutInflater inflater = LayoutInflater.from(mcontext);
		final View popupLayout = inflater.inflate(R.layout.layout_timepicker, null);
		Button btn_take = (Button)popupLayout.findViewById(R.id.timepicker_btn_take);
		final TimePicker timepicker = (TimePicker)popupLayout.findViewById(R.id.timepicker);
		timepicker.setIs24HourView(true);
		ppw = new PopupWindow(mcontext);
		ppw.setBackgroundDrawable(new BitmapDrawable());
		ppw.setWidth(LayoutParams.WRAP_CONTENT);
		ppw.setHeight( LayoutParams.WRAP_CONTENT);
		ppw.setOutsideTouchable(false);
		ppw.setFocusable(true);
		ppw.setContentView(popupLayout);
		ppw.showAtLocation(layout, Gravity.CENTER, 0, 0);
		timepicker.setOnTimeChangedListener(new OnTimeChangedListener() {
			
			@Override
			public void onTimeChanged(TimePicker arg0, int hourOfDay, int minute) {
				time = hourOfDay+":"+minute+":00";
				view.setText(time);
			}
		});
		btn_take.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				ppw.dismiss();
				time = timepicker.getCurrentHour()+":"+timepicker.getCurrentMinute()+":00";
				view.setText(time);
			}
		});
	}
}