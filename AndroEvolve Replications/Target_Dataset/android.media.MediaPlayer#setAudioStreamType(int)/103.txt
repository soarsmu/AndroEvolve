package com.bytedance.sdk.openadsdk.core.video.p177c;

import android.content.Context;
import android.media.MediaDataSource;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Surface;
import android.view.SurfaceHolder;
import com.bytedance.sdk.openadsdk.core.InternalContainer;
import com.bytedance.sdk.openadsdk.utils.C2564t;
import java.io.FileDescriptor;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

/* renamed from: com.bytedance.sdk.openadsdk.core.video.c.b */
public class AndroidMediaPlayer extends AbstractMediaPlayer {
    /* renamed from: a */
    private final MediaPlayer f7985a;
    /* renamed from: b */
    private final C2303a f7986b;
    /* renamed from: c */
    private MediaDataSource f7987c;
    /* renamed from: d */
    private Surface f7988d;
    /* renamed from: e */
    private final Object f7989e = new Object();
    /* renamed from: f */
    private boolean f7990f;

    /* compiled from: AndroidMediaPlayer */
    /* renamed from: com.bytedance.sdk.openadsdk.core.video.c.b$a */
    private class C2303a implements OnBufferingUpdateListener, OnCompletionListener, OnErrorListener, OnInfoListener, OnPreparedListener, OnSeekCompleteListener, OnVideoSizeChangedListener {
        /* renamed from: b */
        private final WeakReference<AndroidMediaPlayer> f7992b;

        public C2303a(AndroidMediaPlayer bVar) {
            this.f7992b = new WeakReference<>(bVar);
        }

        public boolean onInfo(MediaPlayer mediaPlayer, int i, int i2) {
            boolean z = false;
            try {
                if (((AndroidMediaPlayer) this.f7992b.get()) != null && AndroidMediaPlayer.this.mo16020b(i, i2)) {
                    z = true;
                }
                return z;
            } catch (Throwable th) {
                C2564t.m12223c("AndroidMediaPlayer", "AndroidMediaPlayerListenerHolder.onInfo error: ", th);
                return false;
            }
        }

        public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
            boolean z = false;
            try {
                if (((AndroidMediaPlayer) this.f7992b.get()) != null && AndroidMediaPlayer.this.mo16018a(i, i2)) {
                    z = true;
                }
                return z;
            } catch (Throwable th) {
                C2564t.m12223c("AndroidMediaPlayer", "AndroidMediaPlayerListenerHolder.onError error: ", th);
                return false;
            }
        }

        public void onVideoSizeChanged(MediaPlayer mediaPlayer, int i, int i2) {
            try {
                if (((AndroidMediaPlayer) this.f7992b.get()) != null) {
                    AndroidMediaPlayer.this.mo16010a(i, i2, 1, 1);
                }
            } catch (Throwable th) {
                C2564t.m12223c("AndroidMediaPlayer", "AndroidMediaPlayerListenerHolder.onVideoSizeChanged error: ", th);
            }
        }

        public void onSeekComplete(MediaPlayer mediaPlayer) {
            try {
                if (((AndroidMediaPlayer) this.f7992b.get()) != null) {
                    AndroidMediaPlayer.this.mo16022d();
                }
            } catch (Throwable th) {
                C2564t.m12223c("AndroidMediaPlayer", "AndroidMediaPlayerListenerHolder.onSeekComplete error: ", th);
            }
        }

        public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
            try {
                if (((AndroidMediaPlayer) this.f7992b.get()) != null) {
                    AndroidMediaPlayer.this.mo16009a(i);
                }
            } catch (Throwable th) {
                C2564t.m12223c("AndroidMediaPlayer", "AndroidMediaPlayerListenerHolder.onBufferingUpdate error: ", th);
            }
        }

        public void onCompletion(MediaPlayer mediaPlayer) {
            try {
                if (((AndroidMediaPlayer) this.f7992b.get()) != null) {
                    AndroidMediaPlayer.this.mo16021c();
                }
            } catch (Throwable th) {
                C2564t.m12223c("AndroidMediaPlayer", "AndroidMediaPlayerListenerHolder.onCompletion error: ", th);
            }
        }

        public void onPrepared(MediaPlayer mediaPlayer) {
            try {
                if (((AndroidMediaPlayer) this.f7992b.get()) != null) {
                    AndroidMediaPlayer.this.mo16019b();
                }
            } catch (Throwable th) {
                C2564t.m12223c("AndroidMediaPlayer", "AndroidMediaPlayerListenerHolder.onPrepared error: ", th);
            }
        }
    }

    public AndroidMediaPlayer() {
        synchronized (this.f7989e) {
            this.f7985a = new MediaPlayer();
        }
        m10294a(this.f7985a);
        try {
            this.f7985a.setAudioStreamType(3);
        } catch (Throwable th) {
            C2564t.m12223c("AndroidMediaPlayer", "setAudioStreamType error: ", th);
        }
        this.f7986b = new C2303a(this);
        m10296n();
    }

    /* renamed from: a */
    private void m10294a(MediaPlayer mediaPlayer) {
        Field declaredField;
        String str = "AndroidMediaPlayer";
        if (VERSION.SDK_INT >= 19 && VERSION.SDK_INT < 28) {
            try {
                Class cls = Class.forName("android.media.MediaTimeProvider");
                Class cls2 = Class.forName("android.media.SubtitleController");
                Class cls3 = Class.forName("android.media.SubtitleController$Anchor");
                Object newInstance = cls2.getConstructor(new Class[]{Context.class, cls, Class.forName("android.media.SubtitleController$Listener")}).newInstance(new Object[]{InternalContainer.m10059a(), null, null});
                declaredField = cls2.getDeclaredField("mHandler");
                declaredField.setAccessible(true);
                declaredField.set(newInstance, new Handler());
                declaredField.setAccessible(false);
                mediaPlayer.getClass().getMethod("setSubtitleAnchor", new Class[]{cls2, cls3}).invoke(mediaPlayer, new Object[]{newInstance, null});
            } catch (Throwable th) {
                C2564t.m12223c(str, "setSubtitleController error: ", th);
            }
        }
    }

    /* renamed from: e */
    public MediaPlayer mo16032e() {
        return this.f7985a;
    }

    /* renamed from: a */
    public void mo16027a(SurfaceHolder surfaceHolder) throws Throwable {
        synchronized (this.f7989e) {
            if (!this.f7990f) {
                this.f7985a.setDisplay(surfaceHolder);
            }
        }
    }

    /* renamed from: a */
    public void mo16026a(Surface surface) {
        m10297o();
        this.f7988d = surface;
        this.f7985a.setSurface(surface);
    }

    /* renamed from: a */
    public void mo16029a(String str) throws Throwable {
        Uri parse = Uri.parse(str);
        String scheme = parse.getScheme();
        if (TextUtils.isEmpty(scheme) || !scheme.equalsIgnoreCase("file")) {
            this.f7985a.setDataSource(str);
        } else {
            this.f7985a.setDataSource(parse.getPath());
        }
    }

    /* renamed from: a */
    public void mo16028a(FileDescriptor fileDescriptor) throws Throwable {
        this.f7985a.setDataSource(fileDescriptor);
    }

    /* renamed from: m */
    private void m10295m() {
        MediaDataSource mediaDataSource = this.f7987c;
        if (mediaDataSource != null) {
            try {
                mediaDataSource.close();
            } catch (Throwable th) {
                C2564t.m12223c("AndroidMediaPlayer", "releaseMediaDataSource error: ", th);
            }
            this.f7987c = null;
        }
    }

    /* renamed from: f */
    public void mo16033f() throws Throwable {
        this.f7985a.start();
    }

    /* renamed from: g */
    public void mo16035g() throws Throwable {
        this.f7985a.stop();
    }

    /* renamed from: h */
    public void mo16036h() throws Throwable {
        this.f7985a.pause();
    }

    /* renamed from: a */
    public void mo16030a(boolean z) throws Throwable {
        this.f7985a.setScreenOnWhilePlaying(z);
    }

    /* renamed from: a */
    public void mo16024a(long j) throws Throwable {
        this.f7985a.seekTo((int) j);
    }

    /* renamed from: i */
    public long mo16037i() {
        try {
            return (long) this.f7985a.getCurrentPosition();
        } catch (Throwable th) {
            C2564t.m12223c("AndroidMediaPlayer", "getCurrentPosition error: ", th);
            return 0;
        }
    }

    /* renamed from: j */
    public long mo16038j() {
        try {
            return (long) this.f7985a.getDuration();
        } catch (Throwable th) {
            C2564t.m12223c("AndroidMediaPlayer", "getDuration error: ", th);
            return 0;
        }
    }

    /* renamed from: k */
    public void mo16039k() throws Throwable {
        this.f7990f = true;
        this.f7985a.release();
        m10297o();
        m10295m();
        mo16008a();
        m10296n();
    }

    /* renamed from: l */
    public void mo16040l() throws Throwable {
        try {
            this.f7985a.reset();
        } catch (Throwable th) {
            C2564t.m12223c("AndroidMediaPlayer", "reset error: ", th);
        }
        m10295m();
        mo16008a();
        m10296n();
    }

    /* renamed from: b */
    public void mo16031b(boolean z) throws Throwable {
        this.f7985a.setLooping(z);
    }

    /* renamed from: a */
    public void mo16023a(float f, float f2) throws Throwable {
        this.f7985a.setVolume(f, f2);
    }

    /* renamed from: a */
    public void mo16025a(Context context, int i) throws Throwable {
        this.f7985a.setWakeMode(context, i);
    }

    /* renamed from: n */
    private void m10296n() {
        this.f7985a.setOnPreparedListener(this.f7986b);
        this.f7985a.setOnBufferingUpdateListener(this.f7986b);
        this.f7985a.setOnCompletionListener(this.f7986b);
        this.f7985a.setOnSeekCompleteListener(this.f7986b);
        this.f7985a.setOnVideoSizeChangedListener(this.f7986b);
        this.f7985a.setOnErrorListener(this.f7986b);
        this.f7985a.setOnInfoListener(this.f7986b);
    }

    /* access modifiers changed from: protected */
    public void finalize() throws Throwable {
        super.finalize();
        m10297o();
    }

    /* renamed from: o */
    private void m10297o() {
        try {
            if (this.f7988d != null) {
                this.f7988d.release();
                this.f7988d = null;
            }
        } catch (Throwable unused) {
        }
    }
}
