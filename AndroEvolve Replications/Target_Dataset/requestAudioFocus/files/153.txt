package com.example.android.ugonabo;


import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
    MediaPlayer mp;
    private AudioManager audioManager;

    public ColorsFragment() {
        // Required empty public constructor
    }

    AudioManager.OnAudioFocusChangeListener MonAudioFocusChange = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            if(focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT || focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK){
                //When we lose focus for a little amount of time(Shortly)
                mp.pause();
                mp.seekTo(0);
            }
            else if(focusChange == AudioManager.AUDIOFOCUS_GAIN){
                // When we regain focus and can resume playback
                mp.start();
            }
            else if(focusChange == AudioManager.AUDIOFOCUS_LOSS){
                // When we permanently lose audio focus
                releaseMediaPlayer();
            }
        }
    };

    private MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            releaseMediaPlayer();
        }
    };

    public void releaseMediaPlayer(){
        if(mp != null){
            mp.release();
            mp = null;
            audioManager.abandonAudioFocus(MonAudioFocusChange);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        releaseMediaPlayer();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.word_list, container, false);


        //setContentView(R.layout.word_list);
        audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);



        final ArrayList<Word> words = new ArrayList<Word>();
        words.add(new Word("Black", "oji", R.drawable.color_black, R.raw.color_black));
        words.add(new Word("Blue", "amaloji", R.drawable.color_black, R.raw.color_black));
        words.add(new Word("Brown", "uri", R.drawable.color_brown, R.raw.color_brown));
        words.add(new Word("Green", "ndu-ndu", R.drawable.color_green, R.raw.color_green));
        words.add(new Word("Gray", "ntu-ntu", R.drawable.color_gray, R.raw.color_gray));
        words.add(new Word("Yellow", "edo", R.drawable.color_mustard_yellow, R.raw.color_mustard_yellow));
        words.add(new Word("Pink", "uhie ocha", R.drawable.color_red, R.raw.color_red));
        words.add(new Word("Red", "mme mme", R.drawable.color_red, R.raw.color_red));
        words.add(new Word("White", "ocha", R.drawable.color_white, R.raw.color_white));
        words.add(new Word("Purple", "ododo", R.drawable.color_dusty_yellow, R.raw.color_dusty_yellow));

        WordAdapter itemsAdapter = new WordAdapter(getActivity(), words, R.color.category_colors);
        ListView listView = (ListView) rootView.findViewById(R.id.root_view);
        listView.setAdapter(itemsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Word currentWord = words.get(position);
                int sd =currentWord.getSoundResourceId();
                releaseMediaPlayer();

                //int result = audioManager.requestAudioFocus(xxy, STREAM)
                int result = audioManager.requestAudioFocus(MonAudioFocusChange, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
                if(result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
                    //When we recieve audio focus
                    mp = MediaPlayer.create(getActivity(), sd);
                    mp.start();
                    mp.setOnCompletionListener(completionListener);
                }
            }

        });

        return rootView;}


}
