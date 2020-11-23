package com.example.macx.music_app.activities;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.macx.music_app.R;

import java.io.IOException;

/**
 * Created by MacX on 2018-04-04.
 */

public class PlayerActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private ImageView playPause;
    private AudioManager audioManager;

    //it is a parameter for methods {@link requestAudioFocus} and {@link abandonAudioFocus}
    AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT || focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                try {
                    mediaPlayer.pause();
                } catch (Exception e) {
                    // do nothing
                }
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {

                mediaPlayer.start();
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {

                releaseMediaPlayer();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.play_screen);

        // Create and setup the {@link AudioManager} to request audio focus
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        Log.i(PlayerActivity.class.getName(), "RUN TEST");

        String title = getIntent().getStringExtra("TITLE");
        String author = getIntent().getStringExtra("AUTHOR");
        int rawId = getIntent().getIntExtra("RAWID", 0);
        int imgId = getIntent().getIntExtra("IMGID", 0);

        Log.i(PlayerActivity.class.getName(), "TEST:" + title);
        TextView titleTextView = findViewById(R.id.title_text);
        titleTextView.setText(title);

        TextView authorTextView = findViewById(R.id.author_text);
        authorTextView.setText(author);

        ImageView imageView = findViewById(R.id.image_view);
        imageView.setImageResource(imgId);

        mediaPlayer = MediaPlayer.create(PlayerActivity.this, rawId);

        ImageView rewindBack = findViewById(R.id.rewind_backward);
        ImageView rewindForward = findViewById(R.id.rewind_forward);
        playPause = findViewById(R.id.play_pause);

        rewindBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                try {
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                playPause.setImageResource(R.drawable.play);
            }
        });

        rewindForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                releaseMediaPlayer();
                Intent backToPlaylist = new Intent(PlayerActivity.this, ListActivity.class);
                startActivity(backToPlaylist);
            }
        });

        playPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    playPause.setImageResource(R.drawable.play);
                } else {

                    int result = audioManager.requestAudioFocus(onAudioFocusChangeListener,
                            AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

                    if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                        mediaPlayer.start();
                    }
                    playPause.setImageResource(R.drawable.pause);
                }
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.release();
                Intent backToPlaylist = new Intent(PlayerActivity.this, ListActivity.class);
                startActivity(backToPlaylist);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        releaseMediaPlayer();
    }

    /**
     * We need to check if media player is not null before we call release() method on it
     * otherwise this will cause NullPointerException
     */
    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
}
