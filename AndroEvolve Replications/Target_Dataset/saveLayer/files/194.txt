package com.google.android.material.textfield;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.Drawable.Callback;
import android.graphics.drawable.GradientDrawable;
import android.os.Build.VERSION;
import android.view.View;

/* renamed from: com.google.android.material.textfield.a */
class C10825a extends GradientDrawable {

    /* renamed from: a */
    private final Paint f28329a = new Paint(1);

    /* renamed from: b */
    private final RectF f28330b;

    /* renamed from: c */
    private int f28331c;

    C10825a() {
        m27892c();
        this.f28330b = new RectF();
    }

    /* renamed from: c */
    private void m27892c() {
        this.f28329a.setStyle(Style.FILL_AND_STROKE);
        this.f28329a.setColor(-1);
        this.f28329a.setXfermode(new PorterDuffXfermode(Mode.DST_OUT));
    }

    /* access modifiers changed from: 0000 */
    /* renamed from: a */
    public boolean mo31409a() {
        return !this.f28330b.isEmpty();
    }

    /* access modifiers changed from: 0000 */
    /* renamed from: b */
    public void mo31410b() {
        mo31407a(0.0f, 0.0f, 0.0f, 0.0f);
    }

    public void draw(Canvas canvas) {
        m27891b(canvas);
        super.draw(canvas);
        canvas.drawRect(this.f28330b, this.f28329a);
        m27889a(canvas);
    }

    /* renamed from: b */
    private void m27891b(Canvas canvas) {
        Callback callback = getCallback();
        if (m27890a(callback)) {
            ((View) callback).setLayerType(2, null);
        } else {
            m27893c(canvas);
        }
    }

    /* access modifiers changed from: 0000 */
    /* renamed from: a */
    public void mo31407a(float f, float f2, float f3, float f4) {
        RectF rectF = this.f28330b;
        if (f != rectF.left || f2 != rectF.top || f3 != rectF.right || f4 != rectF.bottom) {
            this.f28330b.set(f, f2, f3, f4);
            invalidateSelf();
        }
    }

    /* renamed from: c */
    private void m27893c(Canvas canvas) {
        if (VERSION.SDK_INT >= 21) {
            this.f28331c = canvas.saveLayer(0.0f, 0.0f, (float) canvas.getWidth(), (float) canvas.getHeight(), null);
            return;
        }
        this.f28331c = canvas.saveLayer(0.0f, 0.0f, (float) canvas.getWidth(), (float) canvas.getHeight(), null, 31);
    }

    /* access modifiers changed from: 0000 */
    /* renamed from: a */
    public void mo31408a(RectF rectF) {
        mo31407a(rectF.left, rectF.top, rectF.right, rectF.bottom);
    }

    /* renamed from: a */
    private void m27889a(Canvas canvas) {
        if (!m27890a(getCallback())) {
            canvas.restoreToCount(this.f28331c);
        }
    }

    /* renamed from: a */
    private boolean m27890a(Callback callback) {
        return callback instanceof View;
    }
}
