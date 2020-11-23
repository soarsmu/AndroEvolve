package deepin.ctk.media;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import deepin.ctk.R;

public class MainService extends Service {
    private static final String TAG = "leeTest----->";

    private MediaPlayer player;

    private AudioManager audioManager;

    private MyOnAudioFocusChangeListener mListener;


    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate");

        player = MediaPlayer.create(this, R.raw.call_ring);  // 在res目录下新建raw目录，复制一个test.mp3文件到此目录下。
        player.setLooping(true);

        audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        mListener = new MyOnAudioFocusChangeListener();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onStart(Intent intent, int startid) {
        Toast.makeText(this, "My Service Start", Toast.LENGTH_LONG).show();
        Log.i(TAG, "onStart");

        // Request audio focus for playback
        int result = audioManager.requestAudioFocus(mListener,
                AudioManager.STREAM_VOICE_CALL,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            Log.i(TAG, "requestAudioFocus successfully.");
            audioManager.setMode(AudioManager.STREAM_VOICE_CALL);
            // Start playback.
            player.start();
        } else {
            Log.e(TAG, "requestAudioFocus failed.");
        }
    }


    @Override
    public void onDestroy() {
        Toast.makeText(this, "My Service Stoped", Toast.LENGTH_LONG).show();
        Log.i(TAG, "onDestroy");
        player.stop();

        audioManager.abandonAudioFocus(mListener);
    }


    private class MyOnAudioFocusChangeListener implements
            OnAudioFocusChangeListener {
        @Override
        public void onAudioFocusChange(int focusChange) {
            Log.i(TAG, "focusChange=" + focusChange);
        }
    }
}