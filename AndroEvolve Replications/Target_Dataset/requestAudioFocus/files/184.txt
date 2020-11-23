package kr.co.schoolholic.util;

import android.content.ComponentName;
import android.content.Context;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.telephony.TelephonyManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class SoundManager {
	public static void registerBTKeyEventReceiver(Context context, final ComponentName component) {
		final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		audioManager.registerMediaButtonEventReceiver(component);
	}
	
	public static void unregisterBTKeyEventReceiver(Context context, ComponentName component) {
		AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		audioManager.unregisterMediaButtonEventReceiver(component);
	}
	
	public static void requestAudioFocus(Context context, OnAudioFocusChangeListener listener) {
		AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		audioManager.requestAudioFocus(listener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
	}

	public static void abandonAudioFocus(Context context, OnAudioFocusChangeListener listener) {
		AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		audioManager.abandonAudioFocus(listener);
	}

	
	
	public static int getCurrentvolumeLevel(Context context) {
		AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
	}
	
	public static int getMaxvloumeLevel(Context context){
		AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		return audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
	}
	
	/* 2011.08.30. Added by Kevin.Park
	 * For check the current mode of sound.
	 */
	public static boolean isMuteMode(Context context) {
		TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

		
		//It's for SCMS.
		if( am.isBluetoothA2dpOn() == true ) {
			//return true;
		}

		//2011.10.06. Kevin.Park.
		return tm.getCallState() == TelephonyManager.CALL_STATE_OFFHOOK || tm.getCallState() == TelephonyManager.CALL_STATE_RINGING;


	}
	
	public static boolean isCalling(Context context) {
		TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);

		return tm.getCallState() == TelephonyManager.CALL_STATE_OFFHOOK || tm.getCallState() == TelephonyManager.CALL_STATE_RINGING;
	}
	
	public static int getRingerMode(Context context) {
		AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		if(audioManager != null) return audioManager.getRingerMode();
		return AudioManager.RINGER_MODE_SILENT;
	}
	
	public static boolean isWiredHeadsetOn(Context context) {
		AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		if(audioManager != null) return audioManager.isWiredHeadsetOn();
		return false;
	}
	


	public static boolean setForceUse(int state) {
		
		// First 1 == FOR_MEDIA, second 1 == FORCE_SPEAKER. To go back to the default
		// behavior, use FORCE_NONE (0).

		Class<?> audioSystemClass = null;
		try {
			audioSystemClass = Class.forName("android.media.AudioSystem");
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		Method setForceUse = null;
		try {
			setForceUse = audioSystemClass.getMethod("setForceUse", int.class, int.class);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}

		try {
			setForceUse.invoke(null, 1, state);
			return true;

		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		return false;

	}
}
