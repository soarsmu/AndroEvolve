package js.lib.android.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;

/**
 * Audio Manager Common Methods
 * 
 * @author Jun.Wang
 */
public class AudioManagerUtil {

	private static AudioManager mAudioManager;

	public static AudioManager getAudioMananger(Context cxt) {
		if (mAudioManager == null) {
			mAudioManager = (AudioManager) cxt.getSystemService(Context.AUDIO_SERVICE);
		}
		return mAudioManager;
	}

	public static int abandon(Context cxt, OnAudioFocusChangeListener l) {
		return getAudioMananger(cxt).abandonAudioFocus(l);
	}

	public static int requestMusicGain(Context cxt, OnAudioFocusChangeListener l) {
		return request(cxt, l, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
	}

	private static int request(Context cxt, OnAudioFocusChangeListener l, int streamType, int durationHint) {
		return getAudioMananger(cxt).requestAudioFocus(l, streamType, durationHint);
	}

	public static boolean isAudioFocusRegistered(int flag) {
		return (flag == AudioManager.AUDIOFOCUS_REQUEST_GRANTED);
	}
}
