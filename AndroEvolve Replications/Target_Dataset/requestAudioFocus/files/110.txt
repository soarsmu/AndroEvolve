package p163g.p174d.p178b.p185g0.p186v5;

import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import p686n.p687a.Timber;

/* renamed from: g.d.b.g0.v5.e */
/* compiled from: AudioFocusFlow */
public class C5176e {

    /* renamed from: a */
    protected AudioManager f12501a;

    /* renamed from: b */
    protected OnAudioFocusChangeListener f12502b;

    C5176e(AudioManager audioManager, OnAudioFocusChangeListener onAudioFocusChangeListener) {
        this.f12501a = audioManager;
        this.f12502b = onAudioFocusChangeListener;
    }

    /* access modifiers changed from: 0000 */
    /* renamed from: a */
    public void mo16679a(Object obj) {
        boolean z = true;
        int requestAudioFocus = this.f12501a.requestAudioFocus(this.f12502b, 3, 1);
        StringBuilder sb = new StringBuilder();
        sb.append("Audio Focus Granted ");
        if (requestAudioFocus != 1) {
            z = false;
        }
        sb.append(z);
        Timber.m44529c(sb.toString(), new Object[0]);
    }

    /* access modifiers changed from: 0000 */
    /* renamed from: b */
    public void mo16680b(Object obj) {
        mo16678a();
    }

    /* access modifiers changed from: 0000 */
    /* renamed from: a */
    public void mo16678a() {
        this.f12501a.abandonAudioFocus(this.f12502b);
    }
}
