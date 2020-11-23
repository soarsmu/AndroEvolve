package com.pizzaisdavid.david.tellmetimer;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.speech.tts.UtteranceProgressListener;

public class RequestAudioFocus extends UtteranceProgressListener {
  private AudioManager audioManager;

  AudioManager.OnAudioFocusChangeListener audioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
    @Override
    public void onAudioFocusChange(int focusChange) {
      switch (focusChange) {
        case AudioManager.AUDIOFOCUS_GAIN:
          break;
        case AudioManager.AUDIOFOCUS_LOSS:
          audioManager.abandonAudioFocus(audioFocusChangeListener);
          break;
      }
    }
  };

  public RequestAudioFocus(Activity activity) {
    audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
  }

  @Override
  public void onStart(String utteranceId) {
    audioManager.requestAudioFocus(
      audioFocusChangeListener,
      AudioManager.STREAM_NOTIFICATION,
      AudioManager.AUDIOFOCUS_GAIN_TRANSIENT
    );
  }

  @Override
  public void onDone(String utteranceId) {
    audioManager.abandonAudioFocus(audioFocusChangeListener);
  }

  @Override
  public void onError(String utteranceId) {

  }
}
