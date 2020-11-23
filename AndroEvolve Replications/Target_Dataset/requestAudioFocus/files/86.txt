package com.sygic.aura.feature.sound;

import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import java.lang.reflect.Method;

public class SoundManager {
    private static Method mAM_abondonAudioFocus;
    private static Method mAM_requestAudioFocus;
    private OnAudioFocusChangeListener m_AFChangeListener;
    private AudioManager m_AudioManager;

    /* renamed from: com.sygic.aura.feature.sound.SoundManager.1 */
    class C12331 implements OnAudioFocusChangeListener {
        C12331() {
        }

        public void onAudioFocusChange(int i) {
        }
    }

    static {
        initCompatibility();
    }

    private static void initCompatibility() {
        try {
            mAM_requestAudioFocus = AudioManager.class.getMethod("requestAudioFocus", new Class[]{OnAudioFocusChangeListener.class, Integer.TYPE, Integer.TYPE});
            mAM_abondonAudioFocus = AudioManager.class.getMethod("abandonAudioFocus", new Class[]{OnAudioFocusChangeListener.class});
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e2) {
            e2.printStackTrace();
        }
    }

    public SoundManager(AudioManager audioManager) {
        this.m_AFChangeListener = new C12331();
        this.m_AudioManager = audioManager;
    }

    public int requestAudioFocus(int streamType) {
        if (this.m_AudioManager == null || mAM_requestAudioFocus == null) {
            return 0;
        }
        return this.m_AudioManager.requestAudioFocus(this.m_AFChangeListener, streamType, 3);
    }

    public int abandonAudioFocus() {
        if (this.m_AudioManager == null || mAM_abondonAudioFocus == null) {
            return 0;
        }
        return this.m_AudioManager.abandonAudioFocus(this.m_AFChangeListener);
    }
}
