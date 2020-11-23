package com.zdd.myutil.system.util;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.util.Log;



import java.util.ArrayList;
import java.util.List;


/**
 * create by zhudedian on 2018/7/9.
 */

public class AudioUtil {

    private static AudioUtil audioUtil;
    public static void init(Activity activity){
        audioUtil = new AudioUtil(activity);
    }
    public static void closed(Activity activity){
        audioUtil.destroy(activity);
    }
    public static AudioUtil getAudioUtil(){
        if (audioUtil == null){
            throw new NullPointerException("Maybe you forget called AudioUtil.init(Context context)");
        }
        return audioUtil;
    }


    private AudioManager audioManager;
    private AudioManager.OnAudioFocusChangeListener audioFocusListener;

    public AudioUtil(Activity activity){
        audioManager = (AudioManager) activity.getSystemService(
                Context.AUDIO_SERVICE);
        audioFocusListener = new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.media.VOLUME_CHANGED_ACTION");
        activity.registerReceiver(volumeReceiver,intentFilter);
    }
    /**
     *  @Description 设置音量
     *  @author zhudedian
     *  @time 2018/7/9  13:57
     */
    public void setStreamVolume(int streamType,int volume){
        audioManager.setStreamVolume(streamType, volume, AudioManager.FLAG_SHOW_UI);
    }
    /**
     *  @Description 设置是否静音
     *  @author zhudedian
     *  @time 2018/7/9  14:15
     */
    public void setStreamMute(int streamType,final boolean isMute){
        audioManager.setStreamMute(streamType, isMute);
    }
    /**
     *  @Description 获取当前音量
     *  @author zhudedian
     *  @time 2018/7/9  13:59
     */
    public int getStreamVolume(int streamType){
        return audioManager.getStreamVolume(streamType);
    }
    /**
     *  @Description 获取音量最大值
     *  @author zhudedian
     *  @time 2018/7/11  16:48
     */
    public int getStreamMaxVolume(int streamType){
        return audioManager.getStreamMaxVolume(streamType);
    }
    /**
     *  @Description 获取audio焦点
     *  @author zhudedian
     *  @time 2018/7/9  14:03
     */
    public void requestAudioFocus(AudioManager.OnAudioFocusChangeListener listener){
        if (listener!=null) {
            audioManager.requestAudioFocus(listener,
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        }else {
            audioManager.requestAudioFocus(audioFocusListener,
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        }
    }
    /**
     *  @Description 释放audio焦点
     *  @author zhudedian
     *  @time 2018/7/9  14:05
     */
    public void abandonAudioFocus(AudioManager.OnAudioFocusChangeListener listener){
        if (listener!=null) {
            audioManager.abandonAudioFocus(listener);
        }else {
            audioManager.abandonAudioFocus(audioFocusListener);
        }
    }

    public void destroy(Activity activity){
        activity.unregisterReceiver(volumeReceiver);
    }
    private VolumeReceiver volumeReceiver = new VolumeReceiver();
    private class VolumeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int streamType = intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_TYPE",0);
            int volume = intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_VALUE",0);
            int oldVolume = intent.getIntExtra("android.media.EXTRA_PREV_VOLUME_STREAM_VALUE",0);
            if (volumeChangeListeners !=null){
                for (int i=0;i<volumeChangeListeners.size();i++){
                    volumeChangeListeners.get(i).onChange(streamType,volume,oldVolume);
                }
            }
//            if (streamType == AudioManager.STREAM_MUSIC){
//                SpeechUtil.sendEvent(Event.getSpeakerVolumeChangedEvent(Math.round(((float)volume)*100/getStreamMaxVolume(AudioManager.STREAM_MUSIC)),volume == 0));
//            }else if (streamType == AudioManager.STREAM_RING){
//                SpeechUtil.sendEvent(Event.getAlertsVolumeChangedEvent(Math.round(((float)volume)*100/getStreamMaxVolume(AudioManager.STREAM_RING))));
//            }
            Log.i("edong","streamType="+streamType+",volume="+volume+",oldVolume="+oldVolume);

        }
    }
    private List<VolumeChangeListener> volumeChangeListeners;
    public void addVolumeChangeListener(VolumeChangeListener listener){
        if (volumeChangeListeners == null){
            volumeChangeListeners = new ArrayList<>();
        }
        if (!volumeChangeListeners.contains(listener)) {
            volumeChangeListeners.add(listener);
        }
    }
    public void removeVolumeChangeListener(VolumeChangeListener listener){
        if (volumeChangeListeners!=null){
            volumeChangeListeners.remove(listener);
        }
    }
    public interface VolumeChangeListener{
        void onChange(int streamType, int volume, int oldVolume);
    }
}
