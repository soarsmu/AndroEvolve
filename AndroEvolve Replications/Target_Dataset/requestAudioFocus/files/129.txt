package net.tatans.coeus.network.speaker;

import java.io.File;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.os.Environment;
import android.util.Log;

/**
 * 提供暂停继续功能  试用与小说和新闻
 * 不会被talkback和talkback操作打断
 * 使用mediaplay播放
 * @author 周焕
 *
 */
@SuppressLint("NewApi")
public class SpeakerControlUtils extends MediaPlayer implements OnAudioFocusChangeListener {
//	文本
	private String text="";
	private String filename;
	private static String name="textToFileCache.wav";
	private Speaker speaker;
	private static SpeakerControlUtils singleton;
	private String packagename;
	private AudioManager audioManager;

//	单例初始化
	public static SpeakerControlUtils getInstance(Context ctx) {
        if (singleton == null)
            singleton = new SpeakerControlUtils(ctx.getApplicationContext());
        return singleton;
    }
	private SpeakerControlUtils(Context ctx){
		packagename=ctx.getPackageName();
		speaker=Speaker.getInstance(ctx);
		audioManager = (AudioManager)ctx.getSystemService(Context.AUDIO_SERVICE); 
		this.filename=Environment.getExternalStorageDirectory().toString()+File.separator+packagename+"/"+name;
	}
//	装载数据准备
	/**
	 * @param text 需要播报的文本   
	 * @return
	 */
	public SpeakerControlUtils init(String text){
		reset();
		if(text==null||"".equals(text.trim())){
			return null;
		}
		this.text=text;
		File file=new File(filename);
		if(!file.exists()){
			try {
				new File(filename.substring(0, filename.lastIndexOf("/")+1)).mkdirs();
				file.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		Log.i("path", filename);
//		设置play环境
		speaker.toFile(this.text, this.filename,new Callback() {
			@Override
			public void onDone() {
				super.onDone();
//					文本转化为语音成功后准备播放
				try {
					setAudioStreamType(AudioManager.STREAM_MUSIC);
					setDataSource(filename);
					prepare();
					requestAudioFocus();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		return singleton;
	}
	
	@SuppressLint("NewApi")
	private void requestAudioFocus(){
/*
	  	AUDIOFOCUS_REQUEST_GRANTED   永久获取媒体焦点（播放音乐）
	   	AUDIOFOCUS_GAIN_TRANSIENT  暂时获取焦点 适用于短暂的音频
	   	AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK Duck我们应用跟其他应用共用焦点 我们播放的时候其他音频会降低音量
*/		int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_REQUEST_GRANTED); 
//		获取焦点时候
		if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
			stop();
		}else{
			start();
		}
	}
	/**
	 * 停止后需要重新调用init()初始化
	 */
	public String getText() {
		return text;
	}
	public String getFilename() {
		return filename;
	}
	public void onAudioFocusChange(int arg0) {
//		得到焦点
		if(arg0==AudioManager.AUDIOFOCUS_GAIN){
			if(!isPlaying()){
				start();
			}
//		失去焦点很长时间
		}else if(arg0==AudioManager.AUDIOFOCUS_LOSS){
			stop();
//		取消焦点监听
			audioManager.abandonAudioFocus(this);
//		失去焦点比较短时间	
		}else if(arg0==AudioManager.AUDIOFOCUS_LOSS_TRANSIENT){
			if(isPlaying()){
				pause();
			}
//		失去焦点 不过可以调低音量播放	
		}else if(arg0==AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK){
			if(isPlaying()){
				pause();
			}
		}
	}
	
}
