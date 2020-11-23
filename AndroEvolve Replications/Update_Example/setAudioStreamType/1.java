package net.weberlisper.android.lispervideoplayer.player;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.Surface;
import android.view.TextureView;
import android.widget.FrameLayout;

import net.weberlisper.android.lispervideoplayer.controller.AbstractLisperVideoPlayerController;

import java.io.IOException;

/**
 * 播放器具体类
 * Created by 李松平 on 2017/10/12.
 */

public class LisperVideoPlayer extends FrameLayout
        implements ILisperVideoPlayer, TextureView.SurfaceTextureListener {
    private static final String TAG = LisperVideoPlayer.class.getSimpleName();

    // 内置的系统播放器
    private MediaPlayer mMediaPlayer;

    // 显示影像的控件
    private TextureView mTextureView;

    private Surface mSurface;

    private SurfaceTexture mSurfaceTexture;

    // 控制器
    private AbstractLisperVideoPlayerController mVideoPlayerController;

    // 上下文
    private Context mContext;

    private int mCurrentPlayerState = PlayerStateConstants.STATE_END;

    // 资源是否是以URL的形式呈现
    private boolean isSourcePath = false;

    // 资源路径
    private String mPath;

    // 资源Uri
    private Uri mUri;

    // 缓冲进度
    private int mBufferingPercent;

    public LisperVideoPlayer(@NonNull Context context) {
        this(context, null);
    }

    public LisperVideoPlayer(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LisperVideoPlayer(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
//        this.setBackgroundColor(Color.BLACK);
    }

    @Override
    public void setVideoUrl(String path) {
        isSourcePath = true;
        mPath = path;
    }


    @Override
    public void setVideoContentUri(Uri uri) {
        isSourcePath = false;
        mUri = uri;
    }

    @Override
    public void setVideoPlayerController(AbstractLisperVideoPlayerController videoPlayerController) {
        this.removeView(mVideoPlayerController);
        this.mVideoPlayerController = videoPlayerController;
        this.mVideoPlayerController.setLisperVideoPlayer(this);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        this.addView(mVideoPlayerController, params);
    }

    @Override
    public void start() {
        if (mMediaPlayer != null && (isPaused() || isBufferingPaused()
                || isCompleted() || isPrepared())) {
            mMediaPlayer.start();
            if (isBufferingPaused())
                changePlayerState(PlayerStateConstants.STATE_BUFFERING_STARTED);
            else {
                changePlayerState(PlayerStateConstants.STATE_STARTED);
            }
            return;
        }

        initMediaPlayer();
        initTextureView();
    }

    // 初始化播放器
    private void initMediaPlayer() {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            changePlayerState(PlayerStateConstants.STATE_IDLE);

            // 设置监听器
            mMediaPlayer.setOnPreparedListener(mPreparedListener);
            mMediaPlayer.setOnCompletionListener(mCompletionListener);
            mMediaPlayer.setOnInfoListener(mInfoListener);
            mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
        }
    }

    /**
     * 初始化TextureView
     */
    private void initTextureView() {
        if (mTextureView == null) {
            mTextureView = new TextureView(mContext);
            LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT, Gravity.CENTER);
            this.addView(mTextureView, 0, params);

            mTextureView.setSurfaceTextureListener(this);
        }
    }

    @Override
    public void pause() {
        if (mMediaPlayer != null || isStarted() || isBufferingStarted()) {
            mMediaPlayer.pause();
            if (isStarted())
                changePlayerState(PlayerStateConstants.STATE_PAUSED);
            else {
                changePlayerState(PlayerStateConstants.STATE_BUFFERING_PAUSED);
            }
        }
    }

    @Override
    public void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
            changePlayerState(PlayerStateConstants.STATE_END);
        }
    }

    @Override
    public long getBufferingPercent() {
        return mBufferingPercent;
    }

    @Override
    public boolean isIdle() {
        return mCurrentPlayerState == PlayerStateConstants.STATE_IDLE;
    }

    @Override
    public boolean isInitialized() {
        return mCurrentPlayerState == PlayerStateConstants.STATE_INITIALIZED;
    }

    @Override
    public boolean isPreparing() {
        return mCurrentPlayerState == PlayerStateConstants.STATE_PREPARING;
    }

    @Override
    public boolean isPrepared() {
        return mCurrentPlayerState == PlayerStateConstants.STATE_PREPARED;
    }

    @Override
    public boolean isStarted() {
        return mCurrentPlayerState == PlayerStateConstants.STATE_STARTED;
    }

    @Override
    public boolean isPaused() {
        return mCurrentPlayerState == PlayerStateConstants.STATE_PAUSED;
    }

    @Override
    public boolean isCompleted() {
        return mCurrentPlayerState == PlayerStateConstants.STATE_COMPLETED;
    }

    @Override
    public boolean isEnd() {
        return mCurrentPlayerState == PlayerStateConstants.STATE_END;
    }

    @Override
    public boolean isBufferingStarted() {
        return mCurrentPlayerState == PlayerStateConstants.STATE_BUFFERING_STARTED;
    }

    @Override
    public boolean isBufferingPaused() {
        return mCurrentPlayerState == PlayerStateConstants.STATE_BUFFERING_PAUSED;
    }

    @Override
    public boolean isError() {
        return mCurrentPlayerState == PlayerStateConstants.STATE_ERROR;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        if (mSurfaceTexture == null) {
            this.mSurfaceTexture = surface;
            openMediaPlayer();
        } else {
            this.mTextureView.setSurfaceTexture(mSurfaceTexture);
        }
    }

    /**
     * 打开音乐播放器
     */
    private void openMediaPlayer() {
        if (mMediaPlayer == null) return;
        try {
            if (isSourcePath) {
                mMediaPlayer.setDataSource(mPath);
            } else {
                mMediaPlayer.setDataSource(mContext, mUri);
            }
            changePlayerState(PlayerStateConstants.STATE_INITIALIZED);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            	AudioAttributes.Builder builder = new AudioAttributes.Builder();
            	builder.setUsage(AudioAttributes.USAGE_MEDIA);
            	AudioAttributes attributes = builder.build();
                mMediaPlayer.setAudioAttributes(attributes);
            } else {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            }
            if (mSurface == null) {
                mSurface = new Surface(mSurfaceTexture);
            }
            mMediaPlayer.setSurface(mSurface);
            mMediaPlayer.prepareAsync();
            // 正在准备中
            changePlayerState(PlayerStateConstants.STATE_PREPARING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        release();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    /**
     * 播放准备完毕监听器
     */
    private MediaPlayer.OnPreparedListener mPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            // 已经准备好
            changePlayerState(PlayerStateConstants.STATE_PREPARED);
            mp.start();
            // 已开始播放
            changePlayerState(PlayerStateConstants.STATE_STARTED);
        }
    };

    /**
     * 播放完毕监听器
     */
    private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {

        }
    };

    /**
     * 播放信息或者警告监听器
     */
    private MediaPlayer.OnInfoListener mInfoListener = new MediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            switch (what) {
                case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                    // 缓冲开始
                    Log.d(TAG, "开始缓冲");
                    if (isBufferingStarted() || isStarted()) {
                        changePlayerState(PlayerStateConstants.STATE_BUFFERING_STARTED);
                    } else {
                        changePlayerState(PlayerStateConstants.STATE_BUFFERING_PAUSED);
                    }
                    return true;
                case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                    Log.d(TAG, "暂停缓冲");
                    // 缓冲结束
                    if (isBufferingStarted() || isStarted()) {
                        changePlayerState(PlayerStateConstants.STATE_STARTED);
                    } else {
                        changePlayerState(PlayerStateConstants.STATE_PAUSED);
                    }
                    return true;
            }
            return false;
        }
    };

    /**
     * 缓冲监听器
     */
    private MediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            mBufferingPercent = percent;
            Log.d(TAG, "缓冲进度：" + mBufferingPercent);
        }
    };

    /**
     * 改变播放器状态后调用的方法
     */
    private void changePlayerState(int newPlayerState) {
        mCurrentPlayerState = newPlayerState;
        if (mVideoPlayerController != null)
            mVideoPlayerController.onPlayerStateChanged(mCurrentPlayerState);
    }
}
