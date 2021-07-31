package com.google.android.gms.ads.internal.overlay;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import com.google.android.gms.cast.TextTrackStyle;
import com.google.android.gms.internal.zzmb;

@TargetApi(14)
@zzmb
public class zzab implements OnAudioFocusChangeListener {
    private final AudioManager mAudioManager;
    private boolean zzNq;
    private final zza zzOB;
    private boolean zzOC;
    private boolean zzOD;
    private float zzOE;

    interface zza {
        void zzhh();
    }

    public zzab(Context context, zza com_google_android_gms_ads_internal_overlay_zzab_zza) {
        this.zzOE = TextTrackStyle.DEFAULT_FONT_SCALE;
        this.mAudioManager = (AudioManager) context.getSystemService("audio");
        this.zzOB = com_google_android_gms_ads_internal_overlay_zzab_zza;
    }

    private void zzif() {
        Object obj = (!this.zzNq || this.zzOD || this.zzOE <= 0.0f) ? null : 1;
        if (obj != null && !this.zzOC) {
            zzig();
            this.zzOB.zzhh();
        } else if (obj == null && this.zzOC) {
            zzih();
            this.zzOB.zzhh();
        }
    }

    private void zzig() {
        boolean z = true;
        if (this.mAudioManager != null && !this.zzOC) {
            if (this.mAudioManager.requestAudioFocus(this, 3, 2) != 1) {
                z = false;
            }
            this.zzOC = z;
        }
    }

    private void zzih() {
        if (this.mAudioManager != null && this.zzOC) {
            this.zzOC = this.mAudioManager.abandonAudioFocus(this) == 0;
        }
    }

    public void onAudioFocusChange(int i) {
        this.zzOC = i > 0;
        this.zzOB.zzhh();
    }

    public void setMuted(boolean z) {
        this.zzOD = z;
        zzif();
    }

    public void zzb(float f) {
        this.zzOE = f;
        zzif();
    }

    public void zzib() {
        this.zzNq = true;
        zzif();
    }

    public void zzic() {
        this.zzNq = false;
        zzif();
    }

    public float zzie() {
        return this.zzOC ? this.zzOD ? 0.0f : this.zzOE : 0.0f;
    }
}
