package com.example.android.musicplayerapp;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class NowPlayingActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer;
    private MediaPlayer.OnCompletionListener onCompletionListener=new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            releasemediaplayer();
        }
    };
    private AudioManager audioManager;
    private AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener=new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            if(focusChange==AudioManager.AUDIOFOCUS_LOSS_TRANSIENT){
                mediaPlayer.pause();
            }
            if(focusChange==AudioManager.AUDIOFOCUS_LOSS){
                releasemediaplayer();
            }
            if(focusChange==AudioManager.AUDIOFOCUS_GAIN){
                mediaPlayer.start();
                mediaPlayer.seekTo(0);
            }
        }
    };
    private SeekBar seekBar;
    private Button playpausebutton;
    private TextView currentsongtext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(!getIntent().hasExtra("songobj")){
            super.onCreate(savedInstanceState);
            return;
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_now_playing);
        audioManager=(AudioManager)this.getSystemService(Context.AUDIO_SERVICE);
        final Song song=(Song) getIntent().getSerializableExtra("songobj");
        final int songid=song.getSongid();


        int result=audioManager.requestAudioFocus(onAudioFocusChangeListener,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN);
        if(result==AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
            mediaPlayer=MediaPlayer.create(getApplicationContext(),songid);
        }
        seekBar=(SeekBar)findViewById(R.id.seekbar);
        playpausebutton=(Button)findViewById(R.id.playpausebutton);
        currentsongtext=(TextView)findViewById(R.id.currentsong);
        new PlaymediaTask().execute();
        playpausebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer!=null){
                    if(mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                    }else {
                        mediaPlayer.start();
                    }
                }else {
                    int result=audioManager.requestAudioFocus(onAudioFocusChangeListener,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN);
                    if(result==AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
                        mediaPlayer=MediaPlayer.create(getApplicationContext(),songid);
                    }
                    new PlaymediaTask().execute();
                }
            }
        });

        Button getmoreinfobutton=(Button)findViewById(R.id.getmoreinfo);
        getmoreinfobutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),GetMoreInfoActivity.class);
                intent.putExtra("songobject",(Song)getIntent().getSerializableExtra("songobj"));
                startActivity(intent);
            }
        });
    }
    private void releasemediaplayer(){
        if(mediaPlayer!=null) {
            mediaPlayer.release();
            mediaPlayer=null;
            audioManager.abandonAudioFocus(onAudioFocusChangeListener);
        }
    }

    private class PlaymediaTask extends AsyncTask<Void, Integer, Void> {
        @Override
        protected void onPreExecute() {
            seekBar.setMax(mediaPlayer.getDuration());
            Song song=(Song)getIntent().getSerializableExtra("songobj");
            currentsongtext.setText(song.getSongtitle());
        }

        @Override
        protected Void doInBackground(Void... params) {
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(onCompletionListener);
            int currentposition;

            while (mediaPlayer!=null){
                try{
                    currentposition=mediaPlayer.getCurrentPosition();
                    publishProgress(currentposition);
                    //Thread.sleep(10);
                }catch (Exception e){
                }
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            seekBar.setProgress(values[0]);
            //Toast.makeText(getApplicationContext(),""+values[0],Toast.LENGTH_SHORT).show();
        }

    }

    /*public void getmoreinfo(View view){
        Intent intent=new Intent(this,GetMoreInfoActivity.class);
        intent.putExtra("songobject",(Song)getIntent().getSerializableExtra("songobj"));
        startActivity(intent);
    }*/
}
