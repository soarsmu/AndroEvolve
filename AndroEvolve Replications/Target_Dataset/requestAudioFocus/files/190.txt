package com.readboy.mathproblem.util;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

/**
 * Created by oubin on 2017/6/19.
 */

public class AudioUtils {
    private static final String TAG = "AudioUtils";

    private AudioUtils() {
        Log.e(TAG, "AudioUtils: u can not create me...");
        throw new UnsupportedOperationException();
    }

    /**
     * 申请临时焦点，暂停其他应用播放, 调用{@link #abandonAudioFocus(Context, AudioManager.OnAudioFocusChangeListener)}可恢复播放。
     *
     * @return 是否抢焦点成功，如果为{@link AudioManager#AUDIOFOCUS_GAIN}则代表抢焦点成功，
     * 其他则不成功。
     */
    public static int requestAudioFocusTransient(Context context, AudioManager.OnAudioFocusChangeListener listener) {
        Log.d(TAG, "requestAudioFocusTransient: ");
        AudioManager sAudioManager =
                (AudioManager) context.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        if (sAudioManager != null) {
            int ret = sAudioManager.requestAudioFocus(listener,
                    AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
            Log.d(TAG, "abandonAudioFocus: ret = " + ret);
            Log.e(TAG, "requestAudioFocusTransient: ret = " + ret);
            if (ret == AudioManager.AUDIOFOCUS_REQUEST_FAILED) {
                Log.e(TAG, "requestAudioFocus fail: ret = " + ret);
            }
            return ret;
        }
        Log.e(TAG, "requestAudioFocus: valueAt audio service fail");
        return AudioManager.AUDIOFOCUS_REQUEST_FAILED;
    }

    /**
     * 获取播放焦点，会停止其他应用播放
     *
     * @return 是否抢焦点成功，如果为{@link AudioManager#AUDIOFOCUS_GAIN}代表抢焦点成功，反之。
     */
    public static int requestAudioFocus(Context context) {
        Log.d(TAG, "requestAudioFocus: request audio focus");
        AudioManager sAudioManager =
                (AudioManager) context.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        if (sAudioManager != null) {
            int ret = sAudioManager.requestAudioFocus(null,
                    AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            Log.d(TAG, "abandonAudioFocus: ret = " + ret);
            if (ret == AudioManager.AUDIOFOCUS_REQUEST_FAILED) {
                Log.e(TAG, "requestAudioFocus fail: ret = " + ret);
            }
            return ret;
        }
        Log.e(TAG, "requestAudioFocus: valueAt audio service fail");
        return AudioManager.AUDIOFOCUS_REQUEST_FAILED;
    }

    public static int requestAudioFocusExclusive(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            int ret = audioManager.requestAudioFocus(mAudioFocusChangeListener,
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE);
            Log.e(TAG, "requestAudio: ret = " + ret);
        }
        return AudioManager.AUDIOFOCUS_REQUEST_FAILED;
    }

    public static int requestAudioFocusMayDuck(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            int ret = audioManager.requestAudioFocus(mAudioFocusChangeListener,
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);
            Log.e(TAG, "requestAudio: ret = " + ret);
            return ret;
        }

        return AudioManager.AUDIOFOCUS_REQUEST_FAILED;
    }

    /**
     * 恢复播放
     *
     * @return 是否抢焦点成功，如果为{@link AudioManager#AUDIOFOCUS_GAIN}代表抢焦点成功，反之。
     */
    public static int abandonAudioFocus(Context context, AudioManager.OnAudioFocusChangeListener listener) {
        Log.d(TAG, "abandonAudioFocus: abandon audio focus");
        AudioManager sAudioManager =
                (AudioManager) context.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        if (sAudioManager != null) {
            int ret = sAudioManager.abandonAudioFocus(listener);
            Log.d(TAG, "abandonAudioFocus: ret = " + ret);
            if (ret == AudioManager.AUDIOFOCUS_REQUEST_FAILED) {
                Log.e(TAG, "abandonAudioFocus fail: ret = " + ret);
            }
            return ret;
        }
        Log.e(TAG, "abandonAudioFocus: valueAt audio service fail");
        return AudioManager.AUDIOFOCUS_REQUEST_FAILED;
    }

    private static AudioManager.OnAudioFocusChangeListener mAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            Log.e(TAG, "onAudioFocusChange: focus = " + focusChange);
            if (AudioManager.AUDIOFOCUS_LOSS == focusChange) {
                //doSomething,
            }
        }
    };

}
