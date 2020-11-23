package tw.com.tp6gl4cj86.java_tool.Tool;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;

/**
 * Created by tp6gl4cj86 on 2016/4/8.
 */
public class OlisAudioFocusTool
{

    private static OlisAudioFocusTool.OnAudioFocusChangeListener onAudioFocusChangeListener;

    public interface OnAudioFocusChangeListener
    {
        void onAudioFocusGain();

        void onAudioFocusLoss();
    }

    public static void requestAudioFocus(Activity activity, OlisAudioFocusTool.OnAudioFocusChangeListener onAudioFocusChangeListener)
    {
        OlisAudioFocusTool.onAudioFocusChangeListener = onAudioFocusChangeListener;

        final AudioManager audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null)
        {
            audioManager.requestAudioFocus(listener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        }
    }

    public static void abandonAudioFocus(Activity activity)
    {
        final AudioManager audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null)
        {
            audioManager.abandonAudioFocus(listener);
        }
    }

    private final static AudioManager.OnAudioFocusChangeListener listener = new AudioManager.OnAudioFocusChangeListener()
    {
        @Override
        public void onAudioFocusChange(int focusChange)
        {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT || focusChange == AudioManager.AUDIOFOCUS_LOSS)
            {
                if (onAudioFocusChangeListener != null)
                {
                    onAudioFocusChangeListener.onAudioFocusLoss();
                }
            }
            else if (focusChange == AudioManager.AUDIOFOCUS_GAIN)
            {
                if (onAudioFocusChangeListener != null)
                {
                    onAudioFocusChangeListener.onAudioFocusGain();
                }
            }
        }
    };
    ;

}
