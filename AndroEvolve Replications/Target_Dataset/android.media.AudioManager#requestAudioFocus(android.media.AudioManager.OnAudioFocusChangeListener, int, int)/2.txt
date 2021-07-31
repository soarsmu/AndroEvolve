package com.pp.neteasemusic.netease.utils;

import android.content.Context;
import android.media.AudioFocusRequest;
import android.os.Build;
import android.os.RemoteException;

import androidx.lifecycle.MutableLiveData;

import com.pp.neteasemusic.netease.room.RoomManager;
import com.pp.neteasemusic.ui.music.songlist.SongListViewModel;

public class AudioManager {
    private static MutableLiveData<Boolean> hasFocus = new MutableLiveData<>(false);

    public static void initAudioFocus(Context context) {
        final android.media.AudioManager audioManager = (android.media.AudioManager) context.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        if (audioManager == null) return;
        android.media.AudioManager.OnAudioFocusChangeListener focusChangeListener = new android.media.AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {
                if (RoomManager.getMusicController() == null || SongListViewModel.getCurrent() == -1)
                    return;
                switch (focusChange) {
                    case android.media.AudioManager.AUDIOFOCUS_LOSS:
                    case android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    case android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                        System.out.println("getHasFocus().getValue() = " + getHasFocus().getValue());
                        if (hasFocus.getValue() != null && hasFocus.getValue()) {
                            hasFocus.setValue(false);
                            System.out.println("AudioManager.onAudioFocusChange1-1");
                        } else {
                            System.out.println("AudioManager.onAudioFocusChange1-2");
                        }
                        break;
                    default:
                        break;
                }
            }
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AudioFocusRequest audioFocusRequest = new AudioFocusRequest.Builder(android.media.AudioManager.STREAM_MUSIC)
                    .setOnAudioFocusChangeListener(focusChangeListener)
                    .setFocusGain(android.media.AudioManager.AUDIOFOCUS_GAIN)
                    .build();
            audioManager.requestAudioFocus(audioFocusRequest);
        } else {
            audioManager.requestAudioFocus(focusChangeListener, android.media.AudioManager.STREAM_MUSIC, android.media.AudioManager.AUDIOFOCUS_GAIN);
        }
    }

    public static MutableLiveData<Boolean> getHasFocus() {
        return hasFocus;
    }

    public static void pause() {
        if (RoomManager.getMusicController() == null || SongListViewModel.getCurrent() == -1)
            return;
        try {
            if (RoomManager.getMusicController().isPlaying())
                SongListViewModel.updateNotification(true);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void resume() {
        if (RoomManager.getMusicController() == null || SongListViewModel.getCurrent() == -1)
            return;
        try {
            if (!RoomManager.getMusicController().isPlaying())
                SongListViewModel.updateNotification(true);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
