package com.example.viltsu.birdbonging;


import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class BirdInfoFragment extends Fragment {

    private MediaPlayer mp;
    private MediaPlayer.OnCompletionListener completionListener= new MediaPlayer.OnCompletionListener(){
        @Override
        public void onCompletion(MediaPlayer mp){
            releaseMediaPlayer();
        }
    };
    private AudioManager mAudioManager;
    //uusi metodi statejen käsittelyyn. Takaa estottoman audio toiston.
    private AudioManager.OnAudioFocusChangeListener audioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT || focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK){
                //audio focus lähti hetkeksi, tulee takas. Mitä teet? Pause ja alkuun, soitto jatkuu focuksen tultua takasin.
                mp.pause();
                mp.seekTo(0);
            }else if(focusChange == AudioManager.AUDIOFOCUS_GAIN){
                //focus tulee takas niin soitto alkaa.
                mp.start();
            }else if(focusChange == AudioManager.AUDIOFOCUS_LOSS){
                //vapauttaa resurssit taas.
                releaseMediaPlayer();
            }
        }
    };

    public BirdInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_bird_info, container, false);

        mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);

        ImageView playpenguin = rootView.findViewById(R.id.penguinsound);

        playpenguin.setOnClickListener(new View.OnClickListener(){
            public void onClick(View arg0){
                releaseMediaPlayer();
                //haetaan toisto-oikeus järjestelmältä:
                int audiostate = mAudioManager.requestAudioFocus(audioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
                //jos saadaan toisto-oikeus:
                if(audiostate == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    //mediaplayer luodaan ja mp3 ladataan
                    mp = MediaPlayer.create(getActivity(), R.raw.shortpenguin);
                    //toisto mp3
                    mp.start();
                    //kun soitto loppu, aja oma releaseMediaPlayer metodi
                    mp.setOnCompletionListener(completionListener);
                }
            }
        });

        ImageView playduck = rootView.findViewById(R.id.ducksound);

        playduck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int audiostate = mAudioManager.requestAudioFocus(audioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
                //jos saadaan toisto-oikeus:
                if(audiostate == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    //mediaplayer luodaan ja mp3 ladataan
                    mp = MediaPlayer.create(getActivity(), R.raw.shortduck);
                    //toisto mp3
                    mp.start();
                    //kun soitto loppu, aja oma releaseMediaPlayer metodi
                    mp.setOnCompletionListener(completionListener);
                }
            }
        });

        ImageView playmallord = rootView.findViewById(R.id.mallordsound);

        playmallord.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                int audiostate = mAudioManager.requestAudioFocus(audioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

                if(audiostate == AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
                    mp = MediaPlayer.create(getActivity(), R.raw.shortmallord);
                    mp.start();
                    mp.setOnCompletionListener(completionListener);
                }
            }
        });

        return rootView;
    }

    //applikaatiosta poistuttaessa tehdään tämä jotta musiikki loppuu:
    @Override
    public void onStop() {
        super.onStop();
        releaseMediaPlayer();
    }

    //vapautetaan mediaplayer
    public void releaseMediaPlayer(){
        if(mp != null){
            mp.release();
            mp = null;
            mAudioManager.abandonAudioFocus(audioFocusChangeListener);
        }
    }

}
