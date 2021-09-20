package com.example.android.miwok;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import static android.media.AudioManager.AUDIOFOCUS_GAIN;
import static android.media.AudioManager.AUDIOFOCUS_LOSS;
import static android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT;
import static android.media.AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK;

public class familyActivity extends AppCompatActivity {
    private MediaPlayer mMediaPlayer;

    private AudioManager mAudioManager;

    private MediaPlayer.OnCompletionListener mOnCompleteListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            releaseMediaPlayer();
        }
    } ;



    AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int i) {

            if( i== AUDIOFOCUS_LOSS_TRANSIENT|| i == AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK )
            {
                mMediaPlayer.pause();
                mMediaPlayer.seekTo(0);
            }
            else if( i == AUDIOFOCUS_GAIN )
            {
                mMediaPlayer.start();
            }
            else if( i == AUDIOFOCUS_LOSS )
            {
                releaseMediaPlayer();

            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family);

        final ArrayList<words> word = new ArrayList<words>();

        word.add(new words("father", "әpә", R.drawable.family_father,R.raw.family_father));
        word.add(new words("mother", "әṭa", R.drawable.family_mother,R.raw.family_mother));
        word.add(new words("son", "angsi", R.drawable.family_son,R.raw.family_son));
        word.add(new words("daughter", "tune", R.drawable.family_daughter,R.raw.family_daughter));
        word.add(new words("older brother", "taachi", R.drawable.family_older_brother,R.raw.family_older_brother));
        word.add(new words("younger brother", "chalitti", R.drawable.family_younger_brother,R.raw.family_younger_brother));
        word.add(new words("older sister", "teṭe", R.drawable.family_older_sister,R.raw.family_older_sister));
        word.add(new words("younger sister", "kolliti", R.drawable.family_younger_sister,R.raw.family_younger_sister));
        word.add(new words("grandmother ", "ama", R.drawable.family_grandmother,R.raw.family_grandmother));
        word.add(new words("grandfather", "paapa", R.drawable.family_grandfather,R.raw.family_grandfather));

        wordAdapter adapter = new wordAdapter(this,word,R.color.category_family);

        ListView listView = (ListView) findViewById(R.id.familyList);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                words currWord = word.get(i);

                releaseMediaPlayer();

                mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

                int res = mAudioManager.requestAudioFocus(mOnAudioFocusChangeListener,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

                if(res == AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
                {
                    mMediaPlayer = MediaPlayer.create(familyActivity.this,currWord.getAudioResId());
                    mMediaPlayer.start();

                    mMediaPlayer.setOnCompletionListener(mOnCompleteListener);
                }
            }
        });


    }

    @Override
    protected void onStop() {
        super.onStop();

        releaseMediaPlayer();
    }

    private void releaseMediaPlayer()
    {
        if(mMediaPlayer != null )
        {
            mMediaPlayer.release();
            mMediaPlayer = null;
            mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener );
        }
    }
}