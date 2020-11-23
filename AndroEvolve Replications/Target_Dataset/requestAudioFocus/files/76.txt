package com.tencent.wechat.manager;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.util.Log;

public class AudioWrapperManager {

	private static final String TAG = AudioWrapperManager.class.getSimpleName();

	private static int curAudioFocus = AudioManager.AUDIOFOCUS_LOSS;

	private Context mContext;

	private AudioManager audioManager = null;

	private static AudioWrapperManager instance = null;

	private List<OnAudioFocusChangeListener> listenerList = new ArrayList<OnAudioFocusChangeListener>();

	private static boolean isPeempt = false;

	private AudioWrapperManager(Context context) {
		this.mContext = context;
		audioManager = (AudioManager) mContext
				.getSystemService(context.AUDIO_SERVICE);
	}

	// mAudioWrapperManager = AudioWrapperManager.getInstance(this);

	public static AudioWrapperManager getInstance(Context mContext) {
		if (instance == null) {
			instance = new AudioWrapperManager(mContext);
		}
		return instance;
	}

	public static AudioWrapperManager getInstance() {
		return instance;
	}

	private OnAudioFocusChangeListener mListener = new OnAudioFocusChangeListener() {
		@Override
		public void onAudioFocusChange(int focusChange) {
			Log.d(TAG, "onAudioFocusChange: thread id = "
					+ Thread.currentThread().getId());
			Log.d(TAG,
					"onAudioFocusChange: listenerList size="
							+ listenerList.size());
			for (OnAudioFocusChangeListener l : listenerList) {
				l.onAudioFocusChange(focusChange);
			}
		}
	};

	public boolean isAudioFocusGained() {
		if (curAudioFocus == AudioManager.AUDIOFOCUS_GAIN) {
			return true;
		} else {
			return false;
		}
	}

	public boolean requestAudioFocus() {
		Log.d(TAG, "requestAudioChannel");
		// if (curAudioFocus != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {

		int ret = audioManager.requestAudioFocus(mListener,
				AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
		if (ret == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
			isPeempt = false;
			Log.d(TAG, "wechat requestAudioFocus stream music gain granted");
		} else if (ret == AudioManager.AUDIOFOCUS_REQUEST_FAILED) {
			Log.d(TAG, "wechat requestAudioFocus stream music gain failed");
		}
		return false;
	}

	public boolean abandonAudioFocus() {
		int ret = audioManager.abandonAudioFocus(mListener);
		if (ret == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
			Log.d(TAG, "wechat abandonAudioFocus stream music gain granted");
		} else if (ret == AudioManager.AUDIOFOCUS_REQUEST_FAILED) {
			Log.d(TAG, "wechat abandonAudioFocus stream music gain failed");
		}
		curAudioFocus = AudioManager.AUDIOFOCUS_LOSS;
		return true;
	}

	public void registerListener(OnAudioFocusChangeListener listener) {
		Log.d(TAG, "registerListener: ");
		if (!listenerList.contains(listener)) {
			Log.d(TAG, "registerListener: add");
			listenerList.add(listener);
		}
	}

	public void unRegisterListener(OnAudioFocusChangeListener listener) {
		Log.d(TAG, "unRegisterListener: ");
		if (listenerList.contains(listener)) {
			Log.d(TAG, "unRegisterListener: remove");
			listenerList.remove(listener);
		}
	}

	// private static OnAudioFocusChangeListener listener = new
	// OnAudioFocusChangeListener() {
	//
	// @Override
	// public void onAudioFocusChange(int focusChange) {
	// 
	// Log.d(TAG, "onAudioFocusChange " + focusChange);
	// switch (focusChange) {
	// case AudioManager.AUDIOFOCUS_GAIN:
	// Log.d(TAG, "music onAudioFocus gain");
	// isPeempt = false;
	// curAudioFocus = AudioManager.AUDIOFOCUS_GAIN;
	// break;
	// case AudioManager.AUDIOFOCUS_LOSS:
	// Log.d(TAG, "music onAudioFocus loss");
	// isPeempt = true;
	// curAudioFocus = AudioManager.AUDIOFOCUS_LOSS;
	// break;
	// case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
	// Log.d(TAG, "music onAudioFocus loss transient");
	// isPeempt = true;
	// break;
	// case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
	// Log.d(TAG, "music onAudioFocus loss transient can duck");
	// isPeempt = true;
	// break;
	// default:
	// break;
	// }
	//
	// }
	// };
}
