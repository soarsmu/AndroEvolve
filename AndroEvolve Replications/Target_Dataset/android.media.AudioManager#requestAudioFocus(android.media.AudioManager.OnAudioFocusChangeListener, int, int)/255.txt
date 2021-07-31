package com.me.ui.sample.thirdparty.audio;

import android.content.Context;
import android.media.AudioManager;
import android.view.View;

import com.me.ui.library.sample.SampleFragment;
import com.me.ui.util.LogUtils;

import java.util.List;

/**
 * @author kylingo
 * @since 2019/10/17 17:12
 */
public class AudioFragment extends SampleFragment<String> {

    private static final String TAG = AudioFragment.class.getSimpleName();
    private AudioManager mAudioManager;

    @Override
    protected void initView(View view) {
        super.initView(view);

        mAudioManager = (AudioManager) view.getContext().getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    protected void addItems(List<String> items) {
        items.add("打开扬声器");
        items.add("关闭扬声器");
        items.add("AUDIOFOCUS_GAIN");
        items.add("AUDIOFOCUS_GAIN_TRANSIENT");
        items.add("AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK");
        items.add("ABANDON_AUDIOFOCUS");
    }

    @Override
    protected void onClickItem(String item) {
        switch (item) {
            case "打开扬声器":
                mAudioManager.setMode(AudioManager.MODE_NORMAL);
                mAudioManager.setSpeakerphoneOn(true);
                LogUtils.d(TAG, "mAudioManager.getMode:" + mAudioManager.getMode());
                LogUtils.d(TAG, "mAudioManager.isSpeakerphoneOn:" + mAudioManager.isSpeakerphoneOn());
                break;

            case "关闭扬声器":
                mAudioManager.setMode(AudioManager.MODE_NORMAL);
                mAudioManager.setSpeakerphoneOn(false);
                LogUtils.d(TAG, "mAudioManager.getMode:" + mAudioManager.getMode());
                LogUtils.d(TAG, "mAudioManager.isSpeakerphoneOn:" + mAudioManager.isSpeakerphoneOn());
                break;

            case "AUDIOFOCUS_GAIN":
                requestAudioFocus(AudioManager.AUDIOFOCUS_GAIN);
                break;

            case "AUDIOFOCUS_GAIN_TRANSIENT":
                requestAudioFocus(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
                break;

            case "AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK":
                requestAudioFocus(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);
                break;

            case "ABANDON_AUDIOFOCUS":
                abandonAudioFocus();
                break;
        }
    }

    private void requestAudioFocus(int durationHint) {
        try {
            AudioManager am = (AudioManager) getActivity().getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
            am.requestAudioFocus(null, AudioManager.STREAM_MUSIC, durationHint);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void abandonAudioFocus() {
        try {
            AudioManager am = (AudioManager) getActivity().getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
            am.abandonAudioFocus(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
