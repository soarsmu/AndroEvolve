package cn.time24.kezhu.service;

import android.content.Context;
import android.media.AudioManager;

import cn.time24.kezhu.MainActivity;

import static android.content.Context.AUDIO_SERVICE;

/**
 * Created by hzwangchenyan on 2017/8/8.
 */
public class AudioFocusManager implements AudioManager.OnAudioFocusChangeListener {
    private AudioManager audioManager;
    private boolean isPausedByFocusLossTransient;
    /**
     * protected MusicService musicService = SubscribePlayerActivity.instance.musicService; 为null，可能是还没赋值
     */

    public AudioFocusManager(Context context) {
        audioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
    }

    public boolean requestAudioFocus() {
        return audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
                == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    public void abandonAudioFocus() {
        audioManager.abandonAudioFocus(this);
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            // 重新获得焦点
            case AudioManager.AUDIOFOCUS_GAIN:
                if (isPausedByFocusLossTransient) {
                    // 通话结束，恢复播放
                    MainActivity.musicService.startPlayer();
                }

                // 恢复音量
                MainActivity.musicService.mp.setVolume(1f, 1f);

                isPausedByFocusLossTransient = false;
                break;
            // 永久丢失焦点，如被其他播放器抢占
            case AudioManager.AUDIOFOCUS_LOSS:
                MainActivity.musicService.pausePlayer();
                break;
            // 短暂丢失焦点，如来电
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                MainActivity.musicService.pausePlayer(false);
                isPausedByFocusLossTransient = true;
                break;
            // 瞬间丢失焦点，如通知
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // 音量减小为一半
                MainActivity.musicService.mp.setVolume(0.5f, 0.5f);
                break;
        }
    }
}
