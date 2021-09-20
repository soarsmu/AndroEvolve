package kinstalk.com.qloveaicore;
/*
 * Created by majorxia on 2018/5/7.
 * Copyright (c) 2016 Beijing Shuzijiayuan, All Rights Reserved.
 * Beijing Shuzijiayuan Confidential and Proprietary
 */

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

import kinstalk.com.common.utils.QAILog;

public class QAIAudioFocusMgr {
    private static final String TAG = "QAIAudioFocusMgr";
    private Context mContext;
    private static QAIAudioFocusMgr sInst = null;
    private AudioManager mAudioManager;
    private int mFocusChange;
    private final MyAudioFocusChangeListener mFocusListener = new MyAudioFocusChangeListener(null);

    public QAIAudioFocusMgr() {
    }

    public synchronized static QAIAudioFocusMgr getInst() {
        if (sInst == null) {
            sInst = new QAIAudioFocusMgr();
        }
        return sInst;
    }

    // must be init on first use.
    public void init(Context applicationContext) {
        mContext = applicationContext;
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
    }

    /**
     * same prototype with AudioManager.requestAudioFocus
     *
     * @param l
     * @param streamType
     * @param durationHint
     * @return
     */
    public int requestAudioFocus(final AudioManager.OnAudioFocusChangeListener l, int streamType, int durationHint) {
        Log.d(TAG, "requestAudioFocus: current:" + mFocusChange);
        int ret = mAudioManager.requestAudioFocus(l, streamType, durationHint);
        if (ret == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mFocusChange = durationHint;
            Log.d(TAG, "requestAudioFocus: focus:" + mFocusChange);
        } else {
            Log.d(TAG, "requestAudioFocus: failed:" + ret);
        }

        return ret;
    }

    public int abandonAudioFocus(AudioManager.OnAudioFocusChangeListener l) {
        Log.d(TAG, "abandonAudioFocus: current:" + mFocusChange);

        return mAudioManager.abandonAudioFocus(l);
    }

    public int getCurrentFocus() {
        return mFocusChange;
    }

    public AudioManager.OnAudioFocusChangeListener getFocusListener() {
        return mFocusListener;
    }

    private class MyAudioFocusChangeListener implements AudioManager.OnAudioFocusChangeListener {

        AudioManager.OnAudioFocusChangeListener outListener;

        public MyAudioFocusChangeListener(AudioManager.OnAudioFocusChangeListener outListener) {
            this.outListener = outListener;
        }

        @Override
        public void onAudioFocusChange(int focusChange) {
            Log.d(TAG, "onAudioFocusChange: old:" + mFocusChange + " new:" + focusChange);
            if (outListener != null) {
                outListener.onAudioFocusChange(focusChange);
            }

            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ||
                    focusChange == AudioManager.AUDIOFOCUS_LOSS ||
                    focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                QAILog.d(TAG, "ChangeListener: AUDIOFOCUS_LOSS:" + focusChange);
                mFocusChange = focusChange;
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN ||
                    focusChange == AudioManager.AUDIOFOCUS_GAIN_TRANSIENT ||
                    focusChange == AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE ||
                    focusChange == AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK) {
                QAILog.d(TAG, "ChangeListener: AUDIOFOCUS_GAIN");
                mFocusChange = focusChange;
            }
        }
    }
}
