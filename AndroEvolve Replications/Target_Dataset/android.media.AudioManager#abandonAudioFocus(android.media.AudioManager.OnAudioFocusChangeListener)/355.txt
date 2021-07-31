package defpackage;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Handler;
import com.google.android.apps.hangouts.service.AudioPlayerService;

/* renamed from: fqm */
public final class fqm implements OnAudioFocusChangeListener, OnCompletionListener, OnErrorListener, OnPreparedListener {
    final /* synthetic */ AudioPlayerService a;
    private final MediaPlayer b;
    private final AudioManager c;
    private final String d;
    private boolean e;
    private boolean f;
    private boolean g;
    private int h;
    private final Object i;
    private final Handler j;
    private final Runnable k;

    public fqm(AudioPlayerService audioPlayerService, Context context, String str, String str2) {
        this.a = audioPlayerService;
        this.i = new Object();
        this.j = new Handler();
        this.k = new fqn(this);
        this.d = str2;
        this.c = (AudioManager) context.getSystemService("audio");
        this.b = new MediaPlayer();
        this.h = 0;
        try {
            this.b.setOnPreparedListener(this);
            this.b.setOnCompletionListener(this);
            this.b.setOnErrorListener(this);
            this.b.setAudioStreamType(0);
            this.b.setDataSource(context, Uri.parse(str));
            this.g = false;
            this.e = true;
            this.b.prepareAsync();
        } catch (Throwable e) {
            glk.c("Babel", "AudioPlayerService got an IOException in PlayOperation constructor.", e);
            this.b.release();
            throw e;
        }
    }

    public String a() {
        return this.d;
    }

    public void b() {
        if (this.g || this.c.requestAudioFocus(this, 0, 1) == 1) {
            this.g = true;
            this.b.start();
            this.a.sendBroadcast(a("play_started"));
            return;
        }
        glk.d("Babel", "Unable to get audio focus.", new Object[0]);
        g();
    }

    public void c() {
        if (this.g) {
            this.c.abandonAudioFocus(this);
            this.g = false;
        }
        this.b.pause();
        this.a.sendBroadcast(a("play_paused"));
    }

    public void a(int i) {
        this.b.seekTo(i);
    }

    public void d() {
        Object obj = 1;
        synchronized (this.i) {
            this.h++;
            if (this.h != 1) {
                obj = null;
            }
        }
        if (obj != null) {
            f();
        } else {
            h();
        }
    }

    public void e() {
        synchronized (this.i) {
            if (this.h > 0) {
                this.h--;
            } else {
                glk.e("Babel", "unregisterForCurrentPositionNotification: currentPositionListenerCount <= 0!", new Object[0]);
            }
        }
    }

    private void h() {
        synchronized (this.i) {
            if (this.h == 0) {
                return;
            }
            Intent a = a("current_position");
            a.putExtra("position_in_milliseconds", this.b.getCurrentPosition());
            a.putExtra("duration_in_milliseconds", this.b.getDuration());
            this.a.sendBroadcast(a);
        }
    }

    void f() {
        h();
        synchronized (this.i) {
            if (this.h != 0) {
                this.j.postDelayed(this.k, 250);
            }
        }
    }

    public void g() {
        synchronized (this.i) {
            this.h = 0;
        }
        if (this.e) {
            this.f = true;
            return;
        }
        if (this.g) {
            this.c.abandonAudioFocus(this);
            this.g = false;
        }
        this.b.stop();
        this.b.reset();
        this.b.release();
        this.a.sendBroadcast(a("play_stopped"));
        if (this == this.a.b) {
            this.a.b = null;
        }
    }

    public void onPrepared(MediaPlayer mediaPlayer) {
        this.e = false;
        if (this.f) {
            g();
            return;
        }
        Intent a = a("ready_to_play");
        a.putExtra("duration_in_milliseconds", mediaPlayer.getDuration());
        this.a.sendBroadcast(a);
    }

    public void onCompletion(MediaPlayer mediaPlayer) {
        g();
    }

    public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
        glk.d("Babel", "AudioPlayerService: MediaPlayer error. what: " + i + " extra: " + i2, new Object[0]);
        if (this.e) {
            mediaPlayer.reset();
            mediaPlayer.release();
            this.a.sendBroadcast(a("play_stopped"));
        } else {
            g();
        }
        return true;
    }

    public void onAudioFocusChange(int i) {
        boolean z;
        switch (i) {
            case -3:
                z = AudioPlayerService.a;
                if (this.b.isPlaying()) {
                    this.b.setVolume(0.1f, 0.1f);
                }
            case -2:
                z = AudioPlayerService.a;
                c();
            case -1:
                z = AudioPlayerService.a;
                g();
            case wi.j /*1*/:
                z = AudioPlayerService.a;
                this.b.setVolume(1.0f, 1.0f);
            default:
        }
    }

    private Intent a(String str) {
        if (AudioPlayerService.a) {
            String str2 = this.d;
            new StringBuilder((String.valueOf(str).length() + 30) + String.valueOf(str2).length()).append("Sending Action: ").append(str).append(" for play_id: ").append(str2);
        }
        return AudioPlayerService.a(str, this.d);
    }
}
