package com.shuame.rootgenius.p115ui.view;

import android.content.Context;
import android.content.p013pm.PermissionInfo;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.SweepGradient;
import android.support.p015v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import com.shuame.rootgenius.common.p087ui.view.C1378a;
import com.shuame.rootgenius.common.p087ui.view.C1378a.C1177a;

/* renamed from: com.shuame.rootgenius.ui.view.MobileOptimizeProgressBar */
public class MobileOptimizeProgressBar extends View implements C1177a {
    /* renamed from: A */
    private int f4408A = 0;
    /* renamed from: B */
    private int f4409B = 0;
    /* renamed from: C */
    private int f4410C;
    /* renamed from: D */
    private int f4411D;
    /* renamed from: E */
    private Rect f4412E;
    /* renamed from: F */
    private int f4413F;
    /* renamed from: G */
    private int f4414G;
    /* renamed from: H */
    private int f4415H;
    /* renamed from: I */
    private int f4416I;
    /* renamed from: J */
    private String f4417J = "分";
    /* renamed from: K */
    private float f4418K;
    /* renamed from: L */
    private float f4419L;
    /* renamed from: a */
    private Paint f4420a;
    /* renamed from: b */
    private Shader f4421b;
    /* renamed from: c */
    private int f4422c;
    /* renamed from: d */
    private int f4423d;
    /* renamed from: e */
    private float f4424e;
    /* renamed from: f */
    private Bitmap f4425f;
    /* renamed from: g */
    private Bitmap f4426g;
    /* renamed from: h */
    private float f4427h;
    /* renamed from: i */
    private float f4428i;
    /* renamed from: j */
    private float f4429j;
    /* renamed from: k */
    private int f4430k = 0;
    /* renamed from: l */
    private int f4431l = 0;
    /* renamed from: m */
    private int f4432m = 0;
    /* renamed from: n */
    private int f4433n = PermissionInfo.PROTECTION_MASK_FLAGS;
    /* renamed from: o */
    private float f4434o = (((float) this.f4433n) / 100.0f);
    /* renamed from: p */
    private float f4435p = 8.5f;
    /* renamed from: q */
    private float f4436q;
    /* renamed from: r */
    private int f4437r;
    /* renamed from: s */
    private int f4438s;
    /* renamed from: t */
    private float f4439t;
    /* renamed from: u */
    private float f4440u;
    /* renamed from: v */
    private float f4441v;
    /* renamed from: w */
    private float f4442w;
    /* renamed from: x */
    private float f4443x;
    /* renamed from: y */
    private float f4444y;
    /* renamed from: z */
    private LinearGradient f4445z;

    public MobileOptimizeProgressBar(Context context) {
        super(context);
    }

    public MobileOptimizeProgressBar(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public MobileOptimizeProgressBar(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    private RectF getArcRect() {
        float f = this.f4429j * 2.0f;
        return new RectF(this.f4427h, this.f4428i, this.f4427h + f, f + this.f4428i);
    }

    /* renamed from: a */
    public final void mo6801a(float f) {
        this.f4424e = f;
        this.f4430k = Math.round(((float) this.f4431l) + (((float) (this.f4432m - this.f4431l)) * this.f4424e));
        this.f4408A = Math.round(((float) this.f4410C) + (((float) (this.f4411D - this.f4410C)) * this.f4424e));
        invalidate();
    }

    /* renamed from: a */
    public final void mo7531a(int i) {
        if ((i == 0 ? null : 1) != null) {
            clearAnimation();
            this.f4410C = this.f4408A;
            this.f4411D = i;
            int round = Math.round((((float) i) * 100.0f) / ((float) this.f4409B));
            this.f4431l = this.f4430k;
            this.f4432m = round;
            long abs = (long) ((Math.abs((float) (round - this.f4430k)) * this.f4434o) * this.f4435p);
            Animation c1378a = new C1378a(this);
            c1378a.setDuration(abs);
            c1378a.setInterpolator(new LinearInterpolator());
            startAnimation(c1378a);
        }
    }

    protected void onDraw(Canvas canvas) {
        if (!isInEditMode()) {
            Canvas canvas2;
            Paint paint;
            int saveLayer = canvas.saveLayer(0.0f, 0.0f, (float) this.f4422c, (float) this.f4423d, null, 31);
            this.f4420a.setColor(-1);
            if (this.f4425f == null) {
                this.f4425f = Bitmap.createBitmap(this.f4422c, this.f4423d, Config.ARGB_8888);
                canvas2 = new Canvas(this.f4425f);
                paint = new Paint();
                paint.setAntiAlias(true);
                paint.setColor(-1);
                paint.setStyle(Style.STROKE);
                paint.setStrokeCap(Cap.ROUND);
                paint.setStrokeWidth(this.f4436q);
                canvas2.rotate(150.0f, (float) this.f4437r, (float) this.f4438s);
                canvas2.drawArc(getArcRect(), 6.0f, (float) (this.f4433n - 12), false, paint);
            }
            canvas.drawBitmap(this.f4425f, 0.0f, 0.0f, this.f4420a);
            if (this.f4421b == null) {
                this.f4421b = new SweepGradient((float) this.f4437r, (float) this.f4438s, new int[]{Color.argb(255, 251, 109, 0), Color.argb(255, 255, 216, 0), Color.argb(255, 31, 191, 105), Color.argb(255, 32, 193, 106)}, new float[]{0.07f, 0.23f, 0.47f, 0.7f});
            }
            float f = ((float) this.f4430k) * this.f4434o;
            this.f4420a.setColor(-1);
            this.f4426g = Bitmap.createBitmap(this.f4422c, this.f4423d, Config.ARGB_8888);
            canvas2 = new Canvas(this.f4426g);
            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStyle(Style.STROKE);
            paint.setStrokeWidth(this.f4436q + 10.0f);
            paint.setShader(this.f4421b);
            canvas2.rotate(150.0f, (float) this.f4437r, (float) this.f4438s);
            canvas2.drawArc(getArcRect(), 0.0f, f, false, paint);
            this.f4420a.setXfermode(new PorterDuffXfermode(Mode.SRC_ATOP));
            canvas.drawBitmap(this.f4426g, 0.0f, 0.0f, this.f4420a);
            this.f4420a.setXfermode(null);
            canvas.restoreToCount(saveLayer);
            this.f4420a.setStyle(Style.FILL);
            this.f4420a.setStrokeWidth(1.0f);
            canvas.drawCircle((float) this.f4437r, (float) this.f4438s, this.f4439t, this.f4420a);
            this.f4420a.setStyle(Style.STROKE);
            this.f4420a.setStrokeWidth(this.f4442w);
            this.f4420a.setColor(Color.argb(78, 31, 191, 105));
            canvas.drawCircle((float) this.f4437r, (float) this.f4438s, this.f4440u - (this.f4442w / 2.0f), this.f4420a);
            this.f4420a.setStrokeWidth(this.f4443x);
            this.f4420a.setColor(Color.argb(255, 247, 107, 28));
            canvas.drawCircle((float) this.f4437r, (float) this.f4438s, this.f4441v, this.f4420a);
            canvas.save();
            float f2 = ((float) this.f4430k) * this.f4434o;
            if (f2 <= 2.0f) {
                f2 = 2.0f;
            } else if (f2 >= ((float) this.f4433n) - 2.0f) {
                f2 = ((float) this.f4433n) - 2.0f;
            }
            canvas.rotate((f2 - 150.0f) + 30.0f, (float) this.f4437r, (float) this.f4438s);
            this.f4420a.setStyle(Style.STROKE);
            this.f4420a.setStrokeWidth(this.f4444y);
            if (this.f4445z == null) {
                float f3 = 0.0f;
                this.f4445z = new LinearGradient((float) this.f4437r, (float) this.f4438s, (float) this.f4437r, f3, new int[]{Color.argb(255, 247, 107, 28), Color.argb(255, 252, 200, 0)}, new float[]{0.3f, 0.7f}, TileMode.CLAMP);
            }
            this.f4420a.setColor(-1);
            this.f4420a.setShader(this.f4445z);
            canvas.drawLine((float) this.f4437r, ((float) this.f4438s) - this.f4441v, (float) this.f4437r, 0.0f, this.f4420a);
            this.f4420a.setShader(null);
            canvas.restore();
            this.f4420a.reset();
            this.f4420a.setAntiAlias(true);
            this.f4420a.setColor(ViewCompat.MEASURED_STATE_MASK);
            this.f4420a.setTextSize(this.f4418K);
            this.f4420a.getTextBounds(String.valueOf(this.f4408A), 0, String.valueOf(this.f4408A).length(), this.f4412E);
            if (this.f4412E.right > this.f4413F) {
                this.f4413F = this.f4412E.right;
            }
            f2 = (float) (this.f4437r - (this.f4413F / 2));
            float f4 = ((float) this.f4423d) * 0.86f;
            canvas.drawText(this.f4408A, f2, f4, this.f4420a);
            this.f4420a.setTextSize(this.f4419L);
            canvas.drawText(this.f4417J, (f2 + ((float) this.f4413F)) + (((float) this.f4423d) * 0.02f), (f4 + ((float) this.f4414G)) - ((float) this.f4416I), this.f4420a);
        }
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        FontMetricsInt fontMetricsInt;
        super.onLayout(z, i, i2, i3, i4);
        if (this.f4420a == null) {
            this.f4420a = new Paint();
            this.f4420a.setAntiAlias(true);
            this.f4412E = new Rect();
        }
        this.f4422c = getWidth();
        this.f4423d = getHeight();
        this.f4437r = this.f4422c / 2;
        this.f4438s = this.f4423d / 2;
        this.f4436q = (float) Math.floor((double) (((float) this.f4423d) * 0.08f));
        this.f4429j = ((float) this.f4438s) * 0.85f;
        this.f4427h = (((float) this.f4422c) - (this.f4429j * 2.0f)) / 2.0f;
        this.f4428i = (((float) this.f4423d) - (this.f4429j * 2.0f)) / 2.0f;
        this.f4439t = this.f4429j * 0.35f;
        this.f4440u = this.f4439t * 0.918f;
        this.f4441v = this.f4439t * 0.14f;
        this.f4442w = this.f4440u * 0.15f;
        this.f4443x = this.f4442w * 0.916f;
        this.f4444y = this.f4443x / 2.0f;
        if (this.f4414G == 0) {
            this.f4418K = ((float) this.f4423d) * 0.188f;
            this.f4420a.setTextSize(this.f4418K);
            fontMetricsInt = this.f4420a.getFontMetricsInt();
            this.f4414G = fontMetricsInt.descent + fontMetricsInt.ascent;
        }
        if (this.f4415H == 0) {
            this.f4419L = this.f4418K * 0.4f;
            this.f4420a.setTextSize(this.f4419L);
            Rect rect = new Rect();
            this.f4420a.getTextBounds(this.f4417J, 0, 1, rect);
            this.f4415H = rect.right;
            fontMetricsInt = this.f4420a.getFontMetricsInt();
            this.f4416I = fontMetricsInt.descent + fontMetricsInt.ascent;
        }
    }

    public void setTotalScore(int i) {
        this.f4409B = i;
    }
}
