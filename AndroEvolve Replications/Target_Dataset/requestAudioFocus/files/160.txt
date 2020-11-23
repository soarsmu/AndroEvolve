package com.kimballleavitt.swipe_soundboard;

import android.content.Context;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.IOException;

public class SoundPlayer {
    private static SoundPlayer soundPlayer = new SoundPlayer();
    private SoundPlayer(){}

    public static SoundPlayer getSoundPlayer() {
        return soundPlayer;
    }
    public void playSound(Context c, int i) {
        Uri uri = Uri.parse("android.resource://" + c.getPackageName() + '/' + i );
        SoundPlayer.getSoundPlayer().playSound(c, uri);
    }
    public void playSound(Context c, Uri pathToFile) {
        try {
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(c, pathToFile);
            mediaPlayer.setLooping(false);
            mediaPlayer.prepare();
            AudioManager mAudioManager = (AudioManager) c.getSystemService(Context.AUDIO_SERVICE);
            AudioManager.OnAudioFocusChangeListener listener = new MyFocusListener(mediaPlayer, mAudioManager);
            mediaPlayer.setOnCompletionListener(new MyCompletionListener(mAudioManager, listener));
            if (mAudioManager.requestAudioFocus(listener, 1, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                    == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                mediaPlayer.start();
            }
        } catch (IOException exception){
            System.out.println("File not found!\n" + exception.getMessage());
            exception.printStackTrace();
        }
    }
    private static void debug(String message) {
        System.out.println("***********************\n" + message + "\n*******************");
    }

    private class MyFocusListener implements AudioManager.OnAudioFocusChangeListener {
        private MediaPlayer mp;
        private AudioManager am;
        MyFocusListener(MediaPlayer mp, AudioManager am) {
            this.mp  = mp;
            this.am = am;
        }
        @Override
        public void onAudioFocusChange(int i) {
            if (i < 0) {
                mp.stop();
                mp.reset();
                mp.release();
                am.abandonAudioFocus(this);
            }
        }
    }
    private class MyCompletionListener implements  MediaPlayer.OnCompletionListener {
        private AudioManager am;
        private AudioManager.OnAudioFocusChangeListener listener;
        MyCompletionListener(AudioManager am, AudioManager.OnAudioFocusChangeListener listener) {
            this.am = am;
            this.listener = listener;
        }
        @Override
        public void onCompletion(MediaPlayer mp) {
            mp.stop();
            mp.reset();
            mp.release();
            am.abandonAudioFocus(listener);
        }
    }
}
