package com.oasisgranger.media;

import android.content.Context;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;

import com.google.inject.Inject;

public class AudioManagement {
	
	private final Context context;

	@Inject
	public AudioManagement(final Context context) {
		this.context = context;
	}

	public int requestAudioFocus(OnAudioFocusChangeListener listener, int streamType, int durationHint) {
		return getAudioManager().requestAudioFocus(listener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
	}

	private AudioManager getAudioManager() {
		return (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
	}
}
