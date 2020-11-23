package cn.com.yqhome.instrumentapp;

import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.IOException;

/**
 * Created by depengli on 2017/10/6.
 */

public class MusicService extends Service implements MediaPlayer.OnPreparedListener,MediaPlayer.OnCompletionListener
    ,MediaPlayer.OnErrorListener
{
    private static String TAG = "MusicService";
    private final IBinder iBinder = new MusicBinder();
    private MediaPlayer mediaPlayer;
    private boolean prepared = false;
//    private final IBinder iBinder = new Mu();
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate() {
        super.onCreate();
        AudioAttributes.Builder builder = new AudioAttributes.Builder();
        builder.setContentType(AudioAttributes.CONTENT_TYPE_MUSIC);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//        mediaPlayer.setAudioAttributes(builder.build());
//        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.FULL_WAKE_LOCK);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnPreparedListener(this);
    }

    class MusicBinder extends Binder{
        MusicService getService(){
            return MusicService.this;
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.i(TAG,"had prepared");
        prepared = true;
        mediaPlayer.start();
    }

    @Override
    public void onDestroy() {
        mediaPlayer.release();
        super.onDestroy();
    }


    public void start(String path) {
        mediaPlayer.reset();
        try{
//            mediaPlayer = MediaPlayer.create(this, Uri.parse("http://vprbbc.streamguys.net:80/vprbbc24.mp3"))
//            mediaPlayer.setDataSource(path);
            mediaPlayer.setDataSource(getApplicationContext(),Uri.parse(path));
//            mediaPlayer = MediaPlayer.create(getApplicationContext(),Uri.parse(path));
        }catch (Exception e){
            e.printStackTrace();
        }

        try{
            mediaPlayer.prepare();

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void play(String path){
        if (prepared){
            mediaPlayer.start();
        }else{
            start(path);
        }
    }

    public void pause(){
        if (mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }
    }

    public void seekTo(int i){
        if (mediaPlayer.isPlaying()){
            float precentage = (float) i/ 100;
            float seek = precentage * mediaPlayer.getDuration();
            mediaPlayer.seekTo((int) seek);
        }
    }

    public int getProgress(){
        if (mediaPlayer.isPlaying()){
            return (int) (mediaPlayer.getCurrentPosition()/mediaPlayer.getDuration() * 100);
        }
        return 0;
    }

    public String getTimeShow(){

        return mediaPlayer.getCurrentPosition() +"/" + mediaPlayer.getDuration();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mediaPlayer.stop();
        mediaPlayer.release();
        return super.onUnbind(intent);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mediaPlayer.reset();
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }
}
