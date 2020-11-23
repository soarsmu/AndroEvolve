package com.lvj.bookoneday.activity.controllers;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.lvj.bookoneday.R;

//音频播放 sd卡本地，资源，或者web
public class MediaDemo extends Activity{
	//private final static String AUDIO_PATH = "http://191.8.1.101:8080/wei/12.wma";
	//private static String testWeburl = "http://www.androidbook.com/akc/filestorage/android/documentfiles/3389/play.mp3";
	//"http://streaming.radionomy.com/Radio-Mozart";
	private final static String AUDIO_PATH =
			Environment.getExternalStoragePublicDirectory("tencent") + "/QQfile_recv/roar.mp3"; //Environment.DIRECTORY_MUSIC
	private TextView tv = null;
	private AudioManager audioMrg = null;
	private OnAudioFocusChangeListener audioListener = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.media_demo_activity);
		tv = (TextView) findViewById(R.id.textMediaMedo);


		audioMrg = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

		//来电等进行干预
		audioListener = new OnAudioFocusChangeListener() {

			@Override
			public void onAudioFocusChange(int focusChange) {
				// TODO Auto-generated method stub
				debug("onAudioFocusChange code = " + focusChange);
				if(mediaPlayer == null)
					return;
				switch(focusChange){
					case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT: //临时事情audio的focuse，例如有来电，进行音乐播放暂停操作
						debug("AUDIOFOCUS_LOSS_TRANSIENT");
						pausePlayingAudio(null);
						break;

					case AudioManager.AUDIOFOCUS_GAIN:  //获得audio的focuse，对暂停的音乐继续播放
						debug("AUDIOFOCUS_GAIN");
						restartPlayingAudio(null);
						break;

					case AudioManager.AUDIOFOCUS_LOSS:
						debug("AUDIOFOCUS_LOSS");
						//resume?
						stopPlayingAudio(null);
						break;

					case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK://例如有短信的通知音，可以调低声音，无需silent
						debug("AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK");
						//可以通过AudioManager.get调低声音，或简单地不做处理。
						debug(""+ audioMrg.getStreamVolume(AudioManager.AUDIOFOCUS_GAIN));
						break;

					default:
						break;
				}
			}
		};
		int result = audioMrg.requestAudioFocus(audioListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
		//返回granted或者failed，根据android的reference，对于市场未知的，例如播放音乐或者视频，采用AUDIOFOCUS_GAIN。
		// 对于短时间的，例如通知音，看采用AUDIOFOCUS_GAIN_TRANSIENT；允许叠加，共同放音，用AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK；
		// 对于不希望系统声音干扰的，例如进行memo录音、语音识别，采用AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE。
		if(result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
			debug("requestAudioFocus : granted");
		}else if(result == AudioManager.AUDIOFOCUS_REQUEST_FAILED){
			debug("requestAudioFocus : failed");
		}else{
			debug("requestAudioFocus : " + result);
		}	
	}
	
	public void startPlayingAudio(View v){
		v.setEnabled(false);
		findViewById(R.id.stopPlayButton).setEnabled(true);
		Log.d("WEI","URL:" + AUDIO_PATH);
		try{
			//startPlayWebAudio(AUDIO_PATH);
			startPlayRawAudio(R.raw.roar);
			//startPlayRawAndioUsingFileDescriptor(R.raw.test);
		}catch(Exception e){
			debug("error : " + e.toString());
			e.printStackTrace();
		}
	}

	public void pausePlayingAudio(View v){ 
		if(mediaPlayer != null && mediaPlayer.isPlaying()){
			playbackPosition = mediaPlayer.getCurrentPosition(); //记录当前位置，以便restart时在该位置开始播放
			mediaPlayer.pause();
			debug("stop at " + playbackPosition/1000.0f + " secs.");
		}
	}
	
	public void restartPlayingAudio(View v){
		if(mediaPlayer != null && !mediaPlayer.isPlaying()){
			debug("restart playing at " + playbackPosition/1000.0f + " secs.");
			mediaPlayer.seekTo(playbackPosition); //从指定位置开始播放
			mediaPlayer.start();
		}
	}
	
	public void stopPlayingAudio(View v){
		findViewById(R.id.startPlayButton).setEnabled(true);
		if(mediaPlayer != null){
			//停止。对于stop()，如果需要start()，要先进行prepare()。但对于pause()，可以直接进行start()。
			mediaPlayer.stop();
			playbackPosition = 0;
		}
	}
	
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		killMediaPlayer();
		audioMrg.abandonAudioFocus(audioListener);
		super.onDestroy();
	}

	private MediaPlayer mediaPlayer = null;
	private int playbackPosition = 0;

 	private void startPlayWebAudio(String url) throws Exception{
		killMediaPlayer();
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mediaPlayer.setDataSource(url);
		mediaPlayer.setLooping(false);
		mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
			
			@Override
			public void onPrepared(MediaPlayer mp) {
				debug("MediaPlayer is prepeared..");
				mp.start();  //开始播放
			}
		});
		/* 属于MediaPlayer在播放时出现的错误，出现在start()之后，可以用setOnErrorListener进行捕抓
		mediaPlayer.setOnErrorListener(new OnErrorListener() {
			
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				debug("ERROR: " + what + "  " + extra);
				return false;
			}
		});*/


		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "播放结束", Toast.LENGTH_SHORT).show();
				debug("onCompletion");
				mp.stop();

				//可能还需要加上release()，看需求，是结束播放，还是下一首。
				findViewById(R.id.startPlayButton).setEnabled(true);
				findViewById(R.id.stopPlayButton).setEnabled(false);
				killMediaPlayer();
			}
		});

		//音乐播放有时需要持续性播放，我们需要为MediaPlayer申请wake lock，相关代码如下，当然，
		// 不要忘记获取相关的权限"android.permission.WAKE_LOCK"。
		//mediaPlayer.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK);

		//由于audio内容，来自网络，下载需要时间，如果在UI线程，使用mediaPlayer.prepare()
		// 进行准备播放，很容易造成ANR异常， 所以采用异步的方式，并通过 onPreparedListenner
		// 来获得转变成功的回调函数
		mediaPlayer.prepareAsync();   //准备过程，包括从网络下载
		debug("------After prepareAsync()-----------");
	}

	//使用resource ID创建MediaPlayer对象的方式
	private void startPlayRawAudio(int resourceId){
		killMediaPlayer();
		mediaPlayer = MediaPlayer.create(this, resourceId);
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mediaPlayer.setLooping(false);
		mediaPlayer.start();
	}

	//对于本地音频，media player可以通过文件描述符file descriptor来获取，代码片段如下：
	private void startPlayRawAndioUsingFileDescriptor(int resourceId) throws Exception{
		killMediaPlayer();
		AssetFileDescriptor fd = getResources().openRawResourceFd(resourceId);
		if(fd != null){
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setDataSource(fd.getFileDescriptor());
			//mediaPlayer.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
			fd.close();
			mediaPlayer.prepare();
			mediaPlayer.start();
		}
	}
	
	private void killMediaPlayer(){
		if(mediaPlayer != null){
			try{
				//应确保APP在不是用mediaPlayer的时候进行release()。对于Activity，可以在onDestory()中进行释放。
				mediaPlayer.release();
				mediaPlayer = null;
			}catch(Exception e){	
				debug("error:"+e.toString());
				e.printStackTrace();
			}
		}
	}

	private void debug(String info){
		Log.i("WEI",info);
		tv.setText(info+ "\n" + tv.getText());
	}
}
