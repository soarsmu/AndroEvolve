package ru.vinyarsky.englishmedia.media;

import android.content.Context;
import android.media.AudioManager;

import java.util.concurrent.CopyOnWriteArraySet;

/* package */ class AudioFocusImpl implements AudioFocus {

    private boolean hasAudioFocus = false;
    private AudioManager audioManager;

    private final CopyOnWriteArraySet<AudioFocus.AudioFocusListener> listeners = new CopyOnWriteArraySet<>();

    private AudioManager.OnAudioFocusChangeListener audioFocusChangeListener = (focusChange) -> {
        // No duck support because we primarily play speech (not music),
        // i.e. we always stop playing
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                onResume();
                break;
            default:
                onPause();
                break;
        }
    };

    AudioFocusImpl(Context context) {
        this.audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    /**
     * @return true - request granted
     */
    @Override
    public boolean ensureAudioFocus() {
        if (this.hasAudioFocus)
            return true;
        this.hasAudioFocus = audioManager.requestAudioFocus(this.audioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
        return this.hasAudioFocus;
    }

    @Override
    public void abandonAudioFocus() {
        if (!this.hasAudioFocus)
            return;
        audioManager.abandonAudioFocus(this.audioFocusChangeListener);
        this.hasAudioFocus = false;
    }

    @Override
    public void addListener(AudioFocus.AudioFocusListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void removeListener(AudioFocus.AudioFocusListener listener) {
        this.listeners.remove(listener);
    }

    private void onResume() {
        for (AudioFocus.AudioFocusListener listener: this.listeners)
            listener.onResume();
    }

    private void onPause() {
        for (AudioFocus.AudioFocusListener listener: this.listeners)
            listener.onPause();
    }
}
