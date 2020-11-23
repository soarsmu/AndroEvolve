package Tools;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

/**
 * Created by cunfe on 2015-11-29.
 */
public class SoundHelper {
    //音频焦点监听
    AudioManager.OnAudioFocusChangeListener afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            if(focusChange==AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {//暂时失去Audio Focus，并会很快再次获得
                audiomanager.setStreamVolume(AudioManager.STREAM_MUSIC,currentVolume/2,0);
            }else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {//获得了焦点
                audiomanager.setStreamVolume(AudioManager.STREAM_MUSIC,currentVolume,0);
                Play(-1);
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {//失去了Audio Focus，并将会持续很长的时间
                //am.unregisterMediaButtonEventReceiver(RemoteControlReceiver);
                abandonAudioFocus();
                Stop();
            }

        }
    };
    AudioManager audiomanager;
    //最大音量
    int maxVolume = 0;
    //当前音量
    int currentVolume =0;
    Intent _playmusic;
    Context _context;
    //构造函数
    public  SoundHelper(Context context, Intent playmusic)
    {
        audiomanager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        maxVolume=audiomanager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        currentVolume=audiomanager.getStreamVolume(AudioManager.STREAM_MUSIC);
        _playmusic=playmusic;
        _context = context;
    }
    //获取音频焦点
    public boolean requestAudioFocus()
    {
        int result = audiomanager.requestAudioFocus(afChangeListener,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN);
        return result==AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }
    //放弃音频焦点
    public void abandonAudioFocus()
    {
        audiomanager.abandonAudioFocus(afChangeListener);
    }

    public void Play(int songid) {
        this.requestAudioFocus();
        _playmusic.putExtra("songid", songid);
        _playmusic.putExtra("play","play");
        _context.startService(_playmusic);
    }

    public void Pause() {
        _playmusic.putExtra("songid", -1);
        _playmusic.putExtra("play","pause");
        _context.startService(_playmusic);
        this.abandonAudioFocus();
    }

    public void Stop(){
        _playmusic.putExtra("songid",-1);
        _playmusic.putExtra("play","stop");
        _context.startService(_playmusic);
        this.abandonAudioFocus();
    }
}
