package com.example.android.sanskrittutoring;


import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ColorsFragment extends Fragment {
    MediaPlayer mediaPlayer=null;
    ArrayList<Word> words = new ArrayList<Word>();
    AudioManager audioManager;
    int check=1;
    AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener()
    {
        public void onAudioFocusChange(int focusChange)
        {
            if(focusChange == AudioManager.AUDIOFOCUS_GAIN)
            {
                if(mediaPlayer!=null)
                {
                    mediaPlayer.setVolume(1.0f,1.0f);
                    mediaPlayer.start();
                }
            }

            else if(focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT)
            {
                if(mediaPlayer!=null)
                {
                    mediaPlayer.pause();
                }
            }

            else if(focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK)
            {
                mediaPlayer.setVolume(0.09f,0.09f);
            }

            else if(focusChange == AudioManager.AUDIOFOCUS_LOSS)
            {
                if(mediaPlayer!=null)
                {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer=null;
                    audioManager.abandonAudioFocus(onAudioFocusChangeListener);
                }
            }

        }
    };

    MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            if(mediaPlayer!=null)
            {
                mediaPlayer.release();
                audioManager.abandonAudioFocus(onAudioFocusChangeListener);
            }
        }
    };
    public ColorsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_colors, container, false);
        if(check==1) {
            words.add(new Word("Red", "लोहितः (Lohitaḥ)", R.drawable.red, R.raw.audio));
            words.add(new Word("Green", "Haritaḥ (हरितः)", R.drawable.green, R.raw.audio));
            words.add(new Word("Blue", "Nīlaḥ (नीलः)", R.drawable.blue, R.raw.audio));
            words.add(new Word("Black", "Śyāmaḥ (श्यामः)", R.drawable.blacks, R.raw.audio));
            words.add(new Word("White", "Śuklaḥ (शुक्लः)", R.drawable.white, R.raw.audio));
            words.add(new Word("Grey", "Dhūsaraḥ (धूसरः)", R.drawable.gray, R.raw.audio));
            words.add(new Word("Brown", "Śyāvaḥ (श्यावः)", R.drawable.brown, R.raw.audio));
            words.add(new Word("Pink", "Pāṭalaḥ (पाटलः)", R.drawable.pink, R.raw.audio));
            words.add(new Word("Yellow", "Pītaḥ (पीतः)", R.drawable.yellow, R.raw.audio));
            words.add(new Word("Orange", "Kausumbhaḥ (कौसुम्भः)", R.drawable.orange, R.raw.audio));
            check = 0;
        }
        //words.add(new Word("Crimson", "Śoṇaḥ (शोणः)", R.drawable.colors));
        audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        WordAdapter wordAdapter = new WordAdapter(getActivity(), words);
        ListView listView = rootView.findViewById(R.id.listcolors);
        listView.setAdapter(wordAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(mediaPlayer != null && mediaPlayer.isPlaying())
                {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer=null;
                }

                int result = 0;
                try {
                    result = audioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
                } catch (Exception e) {
                    Log.d("status", toString());
                }
                if(result==AudioManager.AUDIOFOCUS_REQUEST_GRANTED)
                {
                    mediaPlayer = MediaPlayer.create(getActivity(), words.get(i).getMsound());
                    try {
                        mediaPlayer.start();
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                    mediaPlayer.setOnCompletionListener(onCompletionListener);
                }
            }
        });
        return rootView;
    }
    @Override
    public void onStop() {
        super.onStop();
        if(mediaPlayer!=null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=null;
            audioManager.abandonAudioFocus(onAudioFocusChangeListener);
        }
    }
}
