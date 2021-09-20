package jp.classmethod.android.sample.androidscrapcode;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

public class MainActivity extends Activity {
	
	private Button callTimeFunc;
	
	private TimePicker timePicker;
	
	private MainActivity self = MainActivity.this;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		callTimeFunc = (Button) findViewById(R.id.call_time_func);
		callTimeFunc.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				Intent intent = new Intent();
//				intent.setClassName("com.omms", "com.omms.activity.SplashActivity");
//				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				startActivity(intent);
			}
		});
		
		timePicker = (TimePicker) findViewById(R.id.time_picker);
		timePicker.setIs24HourView(true);
		timePicker.setOnTimeChangedListener(new OnTimeChangedListener() {
			
			@Override
			public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				// 15分ごとの下一桁15分で切り上げ
				final int mod = minute % 15;
				if(mod == 0) {
					// No operation
				} else if(mod <= 7) {
					final int value = minute + 15 - mod;
					view.setCurrentMinute(value);
					
					if(value == 60) {
						// 60分なら1時間追加
						view.setCurrentHour(hourOfDay + 1);
					}
				} else {
					view.setCurrentMinute(minute - mod);
				}
			}
		});
		
		// フォーカス無効
		timePicker.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);
	}
}
