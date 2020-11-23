package com.example.android.miwok;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import java.util.ArrayList;

public class ColorsActivity extends AppCompatActivity {

    MediaPlayer mediaPlayer;
    ArrayList<Word> words = new ArrayList<Word>();
    MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion( MediaPlayer mediaPlayer) {
            releaseMediaPlayer();
        }
    };
    AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int i) {
            if(i==AudioManager.AUDIOFOCUS_GAIN)
            {
                mediaPlayer.seekTo(0);
                mediaPlayer.start();
            }
            else if(i==AudioManager.AUDIOFOCUS_LOSS)
            {
                mediaPlayer.stop();
                releaseMediaPlayer();
            }
            else if(i==AudioManager.AUDIOFOCUS_LOSS_TRANSIENT)
            {
                mediaPlayer.pause();
            }
            else if(i==AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK)
            {
                mediaPlayer.pause();
            }

        }
    };
    AudioManager audioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {




        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_list);
        words.add(new Word("red","laal",R.drawable.color_red,R.raw.red));
        words.add(new Word("green", "shobuch", R.drawable.color_green, R.raw.green));
        words.add(new Word("brown", "badami", R.drawable.color_brown, R.raw.brown));
        words.add(new Word("gray", "dhushara", R.drawable.color_gray, R.raw.gray));
        words.add(new Word("black", "kalo", R.drawable.color_black, R.raw.black));
        words.add(new Word("white", "shadha", R.drawable.color_white, R.raw.white));
        words.add(new Word("yellow", "halud", R.drawable.color_mustard_yellow, R.raw.yellow));

        //int audioFocus = audioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC,  AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        //onAudioFocusChangeListener.onAudioFocusChange(audioFocus);

        WordAdapter adapter = new WordAdapter(this, words, R.color.category_colors);

        ListView listView = (ListView) findViewById(R.id.list);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                releaseMediaPlayer();
                audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);


                int audioFocus = audioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC,  AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);


                if (audioFocus == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    // Start playback
                    mediaPlayer = MediaPlayer.create(ColorsActivity.this, words.get(position).getAudioResourceId());
                    mediaPlayer.start();
                    mediaPlayer.setOnCompletionListener(onCompletionListener);
                }

            }
        });




        listView.setAdapter(adapter);

    }

    @Override
    protected void onStop() {
        super.onStop();
        releaseMediaPlayer();
    }

    /**
     * Clean up the media player by releasing its resources.
     */
    private void releaseMediaPlayer() {
        // If the media player is not null, then it may be currently playing a sound.
        if (mediaPlayer != null) {
            // Regardless of the current state of the media player, release its resources
            // because we no longer need it.
            mediaPlayer.release();

            // Set the media player back to null. For our code, we've decided that
            // setting the media player to null is an easy way to tell that the media player
            // is not configured to play an audio file at the moment.
            mediaPlayer = null;
            audioManager.abandonAudioFocus(onAudioFocusChangeListener);
        }
    }


}
