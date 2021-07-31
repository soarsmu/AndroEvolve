package com.foreign.Fuse.Controls.VideoImpl.Android;

// fuse defined imports
import com.uno.UnoObject;
import com.uno.BoolArray;
import com.uno.ByteArray;
import com.uno.CharArray;
import com.uno.DoubleArray;
import com.uno.FloatArray;
import com.uno.IntArray;
import com.uno.LongArray;
import com.uno.ObjectArray;
import com.uno.ShortArray;
import com.uno.StringArray;
import com.Bindings.UnoHelper;
import com.Bindings.UnoWrapped;
import com.Bindings.ExternedBlockHost;

public class MediaPlayer
{
    static void debug_log(Object message)
    {
        android.util.Log.d("maryjane", (message==null ? "null" : message.toString()));
    }

    public static Object CreateMediaPlayer256(final UnoObject _this, final Object surfaceHandle)
    {
        android.media.MediaPlayer player = new android.media.MediaPlayer();
        player.setAudioStreamType(android.media.AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(new android.media.MediaPlayer.OnPreparedListener() {
        	public void onPrepared(android.media.MediaPlayer mp) {
        		ExternedBlockHost.callUno_Fuse_Controls_VideoImpl_Android_MediaPlayer_OnPrepared257((UnoObject)_this);
        	}
        });
        player.setOnCompletionListener(new android.media.MediaPlayer.OnCompletionListener() {
        	public void onCompletion(android.media.MediaPlayer mp) {
        		ExternedBlockHost.callUno_Fuse_Controls_VideoImpl_Android_MediaPlayer_OnCompletion258((UnoObject)_this);
        	}
        });
        player.setOnErrorListener(new android.media.MediaPlayer.OnErrorListener() {
        	public boolean onError(android.media.MediaPlayer mp, int what, int extra) {
        		ExternedBlockHost.callUno_Fuse_Controls_VideoImpl_Android_MediaPlayer_OnError259((UnoObject)_this,(int)what,(int)extra);
        		return false;
        	}
        });
        player.setOnBufferingUpdateListener(new android.media.MediaPlayer.OnBufferingUpdateListener() {
        	public void onBufferingUpdate(android.media.MediaPlayer mp, int percent) {
        		ExternedBlockHost.callUno_Fuse_Controls_VideoImpl_Android_MediaPlayer_OnBuffer260((UnoObject)_this,(int)percent);
        	}
        });
        player.setSurface(((android.view.Surface)surfaceHandle));
        return player;
    }
    
    public static Object CreateSurface261(final UnoObject _this, final Object surfaceTexture)
    {
        return new android.view.Surface(((android.graphics.SurfaceTexture)surfaceTexture));
    }
    
    public static Object CreateSurfaceTexture262(final UnoObject _this, final int glHandle)
    {
        android.graphics.SurfaceTexture surfaceTexture = new android.graphics.SurfaceTexture(glHandle);
        surfaceTexture.setOnFrameAvailableListener(new android.graphics.SurfaceTexture.OnFrameAvailableListener() {
        	public void onFrameAvailable(android.graphics.SurfaceTexture surfaceTexture) {
        		ExternedBlockHost.callUno_Fuse_Controls_VideoImpl_Android_MediaPlayer_OnFrameAvailable263((UnoObject)_this);
        	}
        });
        return surfaceTexture;
    }
    
    public static void Dispose1264(final Object mediaplayerHandle,final Object surfaceHandle,final Object surfaceTextureHandle)
    {
        android.media.MediaPlayer player = (android.media.MediaPlayer)mediaplayerHandle;
        player.reset();
        player.release();
        
        android.view.Surface surface = (android.view.Surface)surfaceHandle;
        surface.release();
        
        android.graphics.SurfaceTexture surfaceTexture = (android.graphics.SurfaceTexture)surfaceTextureHandle;
        surfaceTexture.release();
    }
    
    public static int GetCurrentPosition265(final Object handle)
    {
        return ((android.media.MediaPlayer)handle).getCurrentPosition();
    }
    
    public static int GetDuration266(final Object handle)
    {
        return ((android.media.MediaPlayer)handle).getDuration();
    }
    
    public static int GetHeight267(final Object handle)
    {
        return ((android.media.MediaPlayer)handle).getVideoHeight();
    }
    
    public static int GetOrientation268(final Object handle)
    {
        if (android.os.Build.VERSION.SDK_INT < 19) // we need API level 19 to call MediaPlayer.TrackInfo.getFormat()
        	return 0;
        
        android.media.MediaPlayer player = (android.media.MediaPlayer)handle;
        int degrees = 0;
        android.media.MediaPlayer.TrackInfo[] tracks = player.getTrackInfo();
        for (int i = 0; i < tracks.length; i++)
        {
        	android.media.MediaPlayer.TrackInfo track = tracks[i];
        	if (track.getTrackType() == android.media.MediaPlayer.TrackInfo.MEDIA_TRACK_TYPE_VIDEO)
        	{
        		android.media.MediaFormat format = track.getFormat();
        		if (format != null)
        		{
        			if (format.getFeatureEnabled(android.media.MediaFormat.KEY_ROTATION))
        			{
        				degrees = format.getInteger(android.media.MediaFormat.KEY_ROTATION);
        				break;
        			}
        		}
        	}
        }
        return degrees;
    }
    
    public static int GetWidth269(final Object handle)
    {
        return ((android.media.MediaPlayer)handle).getVideoWidth();
    }
    
    public static boolean IsHardwareAccelerated270()
    {
        android.view.Window window = com.fuse.Activity.getRootActivity().getWindow();
        
        if (window != null) {
        	if ((window.getAttributes().flags & android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED) != 0) {
        		return true;
        	}
        }
        
        try
        {
        	android.content.pm.ActivityInfo info = com.fuse.Activity.getRootActivity().getPackageManager().getActivityInfo(com.fuse.Activity.getRootActivity().getComponentName(), 0);
        	if ((info.flags & android.content.pm.ActivityInfo.FLAG_HARDWARE_ACCELERATED) != 0) {
        		return true;
        	}
        }
        catch (android.content.pm.PackageManager.NameNotFoundException e)
        {
        
        }
        
        return false;
    }
    
    public static void LoadAsyncAsset271(final UnoObject _this, final Object handle,final String assetName)
    {
        android.media.MediaPlayer player = (android.media.MediaPlayer)handle;
        android.content.res.AssetFileDescriptor afd = null;
        try
        {
        	afd = com.fuse.Activity.getRootActivity()
        		.getAssets()
        		.openFd(assetName);
        }
        catch (Exception e)
        {
        	// checked exceptions suck (ﾉಥДಥ)ﾉ︵┻━┻･/
        	android.util.Log.e("Fuse.Video", e.getMessage());
        	ExternedBlockHost.callUno_Fuse_Controls_VideoImpl_Android_MediaPlayer_OnErrorOccurred272((UnoObject)_this,(String)e.getMessage());
        }
        
        /// AAAAAAAAAA JAVA
        if (afd == null)
        {
        	// (ﾉಥДಥ)ﾉ︵┻━┻･/
        	return;
        }
        
        player.reset();
        try
        {
        	// (ﾉಥДಥ)ﾉ︵┻━┻･/
        	player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
        }
        // (ﾉಥДಥ)ﾉ︵┻━┻･/
        catch (Exception e)
        {
        	// (ﾉಥДಥ)ﾉ︵┻━┻･/
        	android.util.Log.e("Fuse.Video", e.getMessage());
        	ExternedBlockHost.callUno_Fuse_Controls_VideoImpl_Android_MediaPlayer_OnErrorOccurred272((UnoObject)_this,(String)e.getMessage());
        }
        
        player.prepareAsync();
    }
    
    public static void LoadAsyncUrl273(final UnoObject _this, final Object handle,final String url)
    {
        android.media.MediaPlayer player = (android.media.MediaPlayer)handle;
        player.reset();
        try
        {
        	player.setDataSource(url);
        }
        catch(Exception e)
        {
        	android.util.Log.e("Fuse.Video", e.getMessage());
        	ExternedBlockHost.callUno_Fuse_Controls_VideoImpl_Android_MediaPlayer_OnErrorOccurred272((UnoObject)_this,(String)e.getMessage());
        }
        player.prepareAsync();
    }
    
    public static void Pause1274(final Object handle)
    {
        android.media.MediaPlayer player = (android.media.MediaPlayer)handle;
        if (player.isPlaying())
        {
        	android.media.AudioManager am = (android.media.AudioManager)com.fuse.Activity.getRootActivity().getSystemService(android.content.Context.AUDIO_SERVICE);
        	am.abandonAudioFocus(null);
        	player.pause();
        }
    }
    
    public static void Play1275(final Object handle)
    {
        android.media.MediaPlayer player = (android.media.MediaPlayer)handle;
        if (!player.isPlaying())
        {
        	android.media.AudioManager am = (android.media.AudioManager)com.fuse.Activity.getRootActivity().getSystemService(android.content.Context.AUDIO_SERVICE);
        	am.requestAudioFocus(null, android.media.AudioManager.STREAM_MUSIC, android.media.AudioManager.AUDIOFOCUS_GAIN);
        	player.start();
        }
    }
    
    public static void SeekTo276(final Object handle,final int position)
    {
        ((android.media.MediaPlayer)handle).seekTo(position);
    }
    
    public static void SetVolume277(final Object handle,final float left,final float right)
    {
        ((android.media.MediaPlayer)handle).setVolume(left, right);
    }
    
    public static void UpdateTexture1278(final Object surfaceTextureHandle)
    {
        ((android.graphics.SurfaceTexture)surfaceTextureHandle).updateTexImage();
    }
    
}
