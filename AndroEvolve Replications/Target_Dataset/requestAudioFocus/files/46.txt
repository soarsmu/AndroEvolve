package com.radiopirate.lib.utils;

import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;

public class AudioFocusWrapper {
    private AudioManager.OnAudioFocusChangeListener listener = new OnAudioFocusChangeListener() {

        @Override
        public void onAudioFocusChange(int focusChange) {
        }
    };

    public void requestAudioFocus(AudioManager audioManager) {
        audioManager.requestAudioFocus(listener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
    }

    public void abandonAudioFocus(AudioManager audioManager) {
        audioManager.abandonAudioFocus(listener);
    }
}
