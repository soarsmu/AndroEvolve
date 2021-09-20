package com.hashicode.musika.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.util.Pair;

import com.hashicode.musika.AlbumCache;
import com.hashicode.musika.MediaMetadataWrapper;
import com.hashicode.musika.provider.MediaIdUtil;
import com.hashicode.musika.provider.MusicProvider;

import java.io.IOException;

import static android.media.AudioManager.AUDIOFOCUS_GAIN;
import static android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT;
import static android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK;
import static android.media.AudioManager.AUDIOFOCUS_REQUEST_GRANTED;

/**
 * Created by takahashi on 1/31/17.
 */
public class PlaybackService implements MediaPlayer.OnPreparedListener, AudioManager.OnAudioFocusChangeListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnSeekCompleteListener {

    private MediaSessionCallback mediaSessionCallback = new MediaSessionCallback();
    private PlaybackServieCallback playbackServieCallback;
    private MusicQueueService musicQueueService;
    private MediaPlayer mediaPlayer;
    private Context context;
    private AudioManager audioManager;
    private int audioFocusStatus;
    private String currentMediaId;
    private int mediaPlayerPosition = 0;
    private int playbackState = PlaybackStateCompat.STATE_STOPPED;

    private EqualizerService equalizerService;


    public static final float VOLUME_DUCK = 0.2f;

    public static final float VOLUME_NORMAL = 1.0f;

    private NoisyReceiver noisyReceiver =new NoisyReceiver();



    public PlaybackService(Context applicationContext, MusicMediaService musicMediaService, MusicProvider musicProvider) {
        this.playbackServieCallback = musicMediaService;
        this.musicQueueService = new MusicQueueService(musicProvider, musicMediaService);
        this.context = applicationContext;
        this.audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mediaPlayer = new MediaPlayer();
        equalizerService = new EqualizerService(mediaPlayer, context);
    }


    @Override
    public void onCompletion(MediaPlayer mp) {
        musicQueueService.skipNext();
        if(playbackState == PlaybackStateCompat.STATE_PLAYING) {
            doPlay();
        }
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        if(mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            playbackState = PlaybackState.STATE_PLAYING;
        }
        updatePlaybackState();

    }

    private class MediaSessionCallback extends MediaSessionCompat.Callback {

        @Override
        public void onPlay() {
            doPlay();
        }

        @Override
        public void onPause() {
            doPause();
        }

        @Override
        public void onStop() {
            doStop();
        }

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
            doPlayFromMediaId(mediaId);
        }

        @Override
        public void onSkipToNext() {
            doSkipToNext();
        }

        @Override
        public void onSkipToPrevious() {
            doSkiptToPrevious();
        }

        @Override
        public void onSeekTo(long pos) {
            doSeekTo(pos);
        }
    }

    private void doSeekTo(long pos) {
        mediaPlayerPosition =(int) pos;
        mediaPlayer.seekTo(mediaPlayerPosition);
    }

    private void doSkiptToPrevious() {
        musicQueueService.skipPrevious();
        if(mediaPlayer.isPlaying()) {
            doPlay();
        }else{
            playbackServieCallback.onMetadataChange(musicQueueService.getCurrentMediaMetadata().getMediaMetadataCompat());
        }
    }

    private void doSkipToNext() {
        musicQueueService.skipNext();
        if(mediaPlayer.isPlaying()) {
            doPlay();
        }
        else{
            playbackServieCallback.onMetadataChange(musicQueueService.getCurrentMediaMetadata().getMediaMetadataCompat());
        }
    }

    private void doPlayFromMediaId(String mediaId) {
        musicQueueService.createQueueFromMediaId(mediaId);
        doPlay();
    }

    private void doStop() {
        context.unregisterReceiver(noisyReceiver);
        audioManager.abandonAudioFocus(this);
        mediaPlayer.release();
        playbackServieCallback.onStop();
    }

    private void doPause() {
        mediaPlayerPosition =mediaPlayer.getCurrentPosition();
        mediaPlayer.pause();
        playbackState=PlaybackStateCompat.STATE_PAUSED;
        context.unregisterReceiver(noisyReceiver);
        updatePlaybackState();
        playbackServieCallback.onPause();
    }

    private void doPlay() {
        MediaMetadataWrapper currentItem = musicQueueService.getCurrentMediaMetadata();
        if(currentItem!=null){
            MediaDescriptionCompat description = currentItem.getMediaMetadataCompat().getDescription();
            MediaIdUtil.getLeaf(description.getMediaId());
            playbackServieCallback.onPlay();
            if(description.getIconUri()!=null && description.getIconBitmap()==null){
                updateAlbumArt(description.getIconUri().getPath(),currentItem);
            }
            else {
                playbackServieCallback.onMetadataChange(currentItem.getMediaMetadataCompat());
            }
            context.registerReceiver(noisyReceiver,new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY));
            playMedia(currentItem.getMediaMetadataCompat());

        }
    }

    private void updateAlbumArt(String path, final MediaMetadataWrapper mediaMetadataWrapper) {
        AlbumCache.getInstance().fetchAlbumArt(path, new AlbumCache.AlbumCacheCallback() {
            @Override
            public void onAlbumFetched(Pair<Bitmap, Bitmap> bitmaps) {
                musicQueueService.updateAlbumArt(bitmaps.second, bitmaps.first, mediaMetadataWrapper.getMediaMetadataCompat().getDescription().getMediaId());
                playbackServieCallback.onMetadataChange(musicQueueService.getCurrentMediaMetadata().getMediaMetadataCompat());
            }
        });
    }

    private void playMedia(MediaMetadataCompat currentItem) {
        try {
            if(requestAudioFocus()) {
                MediaDescriptionCompat mediaDescriptionCompat = currentItem.getDescription();
                //paused and playing current song again
                if(playbackState==PlaybackStateCompat.STATE_PAUSED && TextUtils.equals(mediaDescriptionCompat.getMediaId(), currentMediaId)){
                    mediaPlayer.seekTo(mediaPlayerPosition);
                    mediaPlayer.start();
                    playbackState = PlaybackStateCompat.STATE_PLAYING;
                    updatePlaybackState();

                }
                else {
                    prepareMediaPlayer();
                    Uri mediaUri = mediaDescriptionCompat.getMediaUri();
                    currentMediaId = mediaDescriptionCompat.getMediaId();
                    mediaPlayer.setDataSource(mediaUri.getPath());
                    playbackState = PlaybackStateCompat.STATE_BUFFERING;
                    mediaPlayer.prepareAsync();
                }
            }
            else{
                doPause();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void updatePlaybackState() {
        PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder()
                .setActions(getAvailableActions());
        long position = PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN;
        if (mediaPlayer != null) {
            position = mediaPlayer.getCurrentPosition();
        }
        stateBuilder.setState(playbackState,position,1.0f, SystemClock.elapsedRealtime());
        playbackServieCallback.onPlayBackStateChange(stateBuilder.build());
    }

    private boolean requestAudioFocus() {
        audioFocusStatus = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AUDIOFOCUS_GAIN);
        return audioFocusStatus == AUDIOFOCUS_REQUEST_GRANTED;
    }

    private void prepareMediaPlayer() {
        mediaPlayer.reset();
        mediaPlayer.setWakeMode(context.getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);


        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
//            mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
    }

    public MediaSessionCallback getMediaSessionCallback() {
        return mediaSessionCallback;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        playbackState = PlaybackStateCompat.STATE_PLAYING;
        updatePlaybackState();
    }

    public void destroy(){
        if(mediaPlayer!=null){
            mediaPlayer.release();
        }
    }

    private class NoisyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
                doPause();
            }
        }
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        audioFocusStatus = focusChange;
        if (audioFocusStatus == AudioManager.AUDIOFOCUS_LOSS) {
           doPause();
        }
        else if (audioFocusStatus == AUDIOFOCUS_LOSS_TRANSIENT) {
            // Pause playback
            doPause();
        } else if (audioFocusStatus == AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
            // Lower the volume, keep playing
            mediaPlayer.setVolume(VOLUME_DUCK,VOLUME_DUCK);
        } else if (audioFocusStatus == AudioManager.AUDIOFOCUS_GAIN) {
            mediaPlayer.setVolume(VOLUME_NORMAL,VOLUME_NORMAL);
            if(!mediaPlayer.isPlaying()){
                doPlay();
            }
        }
    }

    private long getAvailableActions() {
        long actions =
                PlaybackStateCompat.ACTION_PLAY_PAUSE |
                        PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID |
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT;
        if (mediaPlayer!=null && mediaPlayer.isPlaying()) {
            actions |= PlaybackStateCompat.ACTION_PAUSE;
        } else {
            actions |= PlaybackStateCompat.ACTION_PLAY;
        }
        return actions;
    }

}
