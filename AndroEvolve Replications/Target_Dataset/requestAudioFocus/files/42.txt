package com.novelot.audiofocus;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.novelot.lib.player.framework.PlayerUtils;
import com.novelot.mediasession.BaseMediaSessionCallback;
import com.novelot.mediasession.MediaSessionImpl;
import com.novelot.mediasession.MediaSessionManager;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private AudioManager.OnAudioFocusChangeListener mAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int i) {
            showFocusInfo(i);
        }
    };
    private TextView tvFocusInfo;
    private RadioGroup rgStreamType;
    private RadioGroup rgDurationHint;
    private AudioManager mAudioManager;
    private int mStreamType = AudioManager.STREAM_MUSIC;
    private int mDurationHint = AudioManager.AUDIOFOCUS_GAIN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PlayerUtils.getInstance().init(getApplicationContext());
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        //
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        //
        tvFocusInfo = (TextView) findViewById(R.id.tvFocusInfo);
        //
        rgStreamType = (RadioGroup) findViewById(R.id.rgStreamType);
        rgStreamType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rbSTREAM_VOICE_CALL:
                        mStreamType = AudioManager.STREAM_VOICE_CALL;
                        break;
                    case R.id.rbSTREAM_SYSTEM:
                        mStreamType = AudioManager.STREAM_SYSTEM;
                        break;
                    case R.id.rbSTREAM_RING:
                        mStreamType = AudioManager.STREAM_RING;
                        break;
                    case R.id.rbSTREAM_MUSIC:
                        mStreamType = AudioManager.STREAM_MUSIC;
                        break;
                    case R.id.rbSTREAM_ALARM:
                        mStreamType = AudioManager.STREAM_ALARM;
                        break;
                    case R.id.rbSTREAM_NOTIFICATION:
                        mStreamType = AudioManager.STREAM_NOTIFICATION;
                        break;
                    case R.id.rbSTREAM_DTMF:
                        mStreamType = AudioManager.STREAM_DTMF;
                        break;
//                    case R.id.rbSTREAM_BLUETOOTH_SCO:
//                        mStreamType = AudioManager.STREAM_BLUETOOTH_SCO;
//                        break;
//                    case R.id.rbSTREAM_SYSTEM_ENFORCED:
//                        mStreamType = AudioManager.STREAM_SYSTEM_ENFORCED;
//                        break;
//                    case R.id.rbSTREAM_TTS:
//                        mStreamType = AudioManager.STREAM_TTS;
//                        break;
                    default:
                        mStreamType = AudioManager.STREAM_MUSIC;
                        break;
                }
            }
        });
        //
        rgDurationHint = (RadioGroup) findViewById(R.id.rgDurationHint);
        rgDurationHint.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rbMayDuck:
                        mDurationHint = AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK;
                        break;
                    case R.id.rbGainTransient:
                        mDurationHint = AudioManager.AUDIOFOCUS_GAIN_TRANSIENT;
                        break;
                    case R.id.rbGainTransientExclusive:
                        mDurationHint = AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE;
                        break;
                    case R.id.rbGain:
                        mDurationHint = AudioManager.AUDIOFOCUS_GAIN;
                        break;
                }
            }
        });
        findViewById(R.id.btnRequestAudioFocus).setOnClickListener(this);
        //
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnRequestAudioFocus:
                requestAudioFocus();
                break;
        }
    }

    private void requestAudioFocus() {
        int result = mAudioManager.requestAudioFocus(mAudioFocusChangeListener, mStreamType, mDurationHint);
        //
        if (AudioManager.AUDIOFOCUS_REQUEST_GRANTED == result) {
            MediaSessionManager.getInstance().registerMediaSession(new MediaSessionImpl(getApplicationContext(), new BaseMediaSessionCallback()));
            PlayerUtils.getInstance().play();
        }
        //
        showFocusResult(result);
    }

    private void showFocusResult(int result) {
        String info = "";
        switch (result) {
            case AudioManager.AUDIOFOCUS_REQUEST_GRANTED:
                info = "AudioManager.requestAudioFocus:请求焦点结果:获取成功";
                break;
            case AudioManager.AUDIOFOCUS_REQUEST_FAILED:
                info = "AudioManager.requestAudioFocus:请求焦点结果:获取失败";
                break;
            default:

                break;
        }

        tvFocusInfo.setText(info);
    }

    private void showFocusInfo(int result) {
        String info = "";
        switch (result) {
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                info = "OnAudioFocusChangeListener:暂时失去AudioFocus，但是可以继续播放，不过要在降低音量";
                PlayerUtils.getInstance().setVolume(0.1f, 0.1f);
                break;
            case AudioManager.AUDIOFOCUS_GAIN:
                info = "OnAudioFocusChangeListener:获得了Audio Focus";
                PlayerUtils.getInstance().play();
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                info = "OnAudioFocusChangeListener:失去了Audio Focus，并将会持续很长的时间";
                PlayerUtils.getInstance().stop();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                info = "OnAudioFocusChangeListener:暂时失去Audio Focus，并会很快再次获得";
                PlayerUtils.getInstance().pause();
                break;
            default:

                break;
        }

        tvFocusInfo.setText(info);
    }
}
