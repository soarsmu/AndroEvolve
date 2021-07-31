package com.skinod.tzzo.skinod.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.util.Log;


/**
 * Created by Administrator on 2018/3/13.
 */

public class MediaPlayer implements android.media.MediaPlayer.OnBufferingUpdateListener,
        android.media.MediaPlayer.OnCompletionListener, android.media.MediaPlayer.OnPreparedListener{
    private  Uri uri;
    private Context mContext;
    private String filepath;
    private static  MediaPlayer mp;
    private android.media.MediaPlayer mediaPlayer;

    private MediaPlayer(String path,Context con) {
        filepath=path;
         mContext=con;
        initMediaPlay();
    }

    private MediaPlayer(Uri mediaVolumeUri,Context con) {
        uri=mediaVolumeUri;
        mContext=con;
        initMediaPlay();
    }


    public static MediaPlayer getConstant(Context con, Uri mediaVolumeUri) {
        if(mp==null) mp=new MediaPlayer(mediaVolumeUri,con);
        return  mp;
    }

    public static MediaPlayer getConstant(String path,Context con){
        if(mp==null) mp=new MediaPlayer(path,con);
        return  mp;
    }

    private void initMediaPlay() {
        if(ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            //requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},100);
            Log.d("sxf","PlaymusicActivity_no permission.WRITE_EXTERNAL_STORAGE");
        }
        if(ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS)!= PackageManager.PERMISSION_GRANTED){
            // requestPermissions(new String[]{android.Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS},200);
            Log.d("sxf","PlaymusicActivity_no permission.MOUNT_UNMOUNT_FILESYSTEMS");
        }

        try {
            mediaPlayer = new android.media.MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnBufferingUpdateListener(this);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setOnCompletionListener(new android.media.MediaPlayer.OnCompletionListener(){
                @Override
                public void onCompletion(android.media.MediaPlayer mediaPlayer) {
                   // handleProgress.sendEmptyMessage(FINISH_PLAYER);
                }
            });
        } catch (Exception e) {
            Log.e("mediaPlayer", "error", e);
        }

    }


    @Override
    public void onBufferingUpdate(android.media.MediaPlayer mp, int percent) {

    }

    @Override
    public void onCompletion(android.media.MediaPlayer mp) {

    }

    @Override
    public void onPrepared(android.media.MediaPlayer mp) {

    }

    public void startByUri(){
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(mContext,uri);
            mediaPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startPlay(){
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(filepath);
            mediaPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopPlay() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

}
