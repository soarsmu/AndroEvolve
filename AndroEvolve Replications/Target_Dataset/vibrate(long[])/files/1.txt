package com.android.runintest;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.UserHandle;
import android.os.Vibrator;
import android.os.PowerManager.WakeLock;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.TextView;

public class VibrateTestActivity extends BaseActivity{
	
	private static final String TAG = "VibrateTestActivity";
	private VibrateTestActivity vibrateTestActivity;
	private Vibrator vibrator;
	private TextView tv_vibrate;
	private int longSize;
	private int delayTime;
	private int isVibrate = 0;
	private SharedPreferences mSharedPreferences = null;
	private static final int VIBRATE_REPEAT = 3;
	private static final int DELAY_TIME = 5 * 1000;
	private static final int  VIBRATE_STOP_TIME = 1000;
	private static final int VIBRATE_RUN_TIME = 2000;
	
	private static  boolean mTestSuccess  = true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		vibrateTestActivity = new VibrateTestActivity();
		vibrateTestActivity.isMonkeyRunning(TAG, "onCreate", VibrateTestActivity.this);
		LogRuningTest.printInfo(TAG, "start VibrateTestActivity", this);
		setContentView(R.layout.activity_vibrate_test);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		mSharedPreferences = this.getSharedPreferences("runintest", Activity.MODE_PRIVATE);
		tv_vibrate = (TextView) findViewById(R.id.vibrate_test_count);
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		if(vibrator != null && vibrator.hasVibrator() != false){
			longSize = VIBRATE_REPEAT * 2 ;
			delayTime = DELAY_TIME + VIBRATE_REPEAT *(VIBRATE_STOP_TIME + VIBRATE_RUN_TIME);
			long[] pattern=new long[longSize];
			for(int i = 0;i < longSize;i++){
				if(i/2 == 0){
				  pattern[i] = VIBRATE_STOP_TIME;
				}else{
				  pattern[i] = VIBRATE_RUN_TIME;
				}
			}
			
			tv_vibrate.setText(getResources().getString(R.string.vibrate_count)+VIBRATE_REPEAT);
			
			vibrator.vibrate(pattern,-1);
			LogRuningTest.printDebug(TAG, "result:Vibrator test Success", this);
			
		}else{
			LogRuningTest.printDebug(TAG, "result:Vibrator test failed", this);
            LogRuningTest.printError(TAG, "reason:" + "Vibrator is not existed", this);
            mTestSuccess = false ;
		}
		mLCDHandler.sendEmptyMessageDelayed(1, delayTime);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		vibrateTestActivity.isMonkeyRunning(TAG, "onResume", VibrateTestActivity.this);
	}

	private void goToLCDtest(){
		vibrator.cancel();
		Editor editor=mSharedPreferences.edit();
		if(!mTestSuccess){
			editor.putBoolean("vibrator_test", mTestSuccess);
		}
		editor.commit();
		Intent intent = new Intent(TestService.ACTION_LCD_TEST);
		sendBroadcast(intent);
	}
	
	
	Handler mLCDHandler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			goToLCDtest();
			finish();
			super.handleMessage(msg);
		}
	};

	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
            	
                LogRuningTest.printInfo(TAG, "User click KEYCODE_BACK. Ignore it.", this);
                
                return false;
                
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

}
