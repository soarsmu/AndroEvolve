package com.sds.android.ttpod.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.sds.android.sdk.lib.util.b;
import com.sds.android.ttpod.R;
import com.sds.android.ttpod.b.v;
import com.sds.android.ttpod.framework.modules.theme.ThemeElement;

public class GlobalMenuThumbImageView extends ImageView {
    private Bitmap a;
    private float b;
    private PorterDuffXfermode c = new PorterDuffXfermode(Mode.DST_OUT);
    private Paint d = new Paint(1);
    private Drawable e;

    public GlobalMenuThumbImageView(Context context) {
        super(context);
    }

    public GlobalMenuThumbImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public GlobalMenuThumbImageView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        this.a = null;
    }

    public void setThumbDrawable(Drawable drawable) {
        this.e = drawable;
        this.a = null;
        invalidate();
    }

    private void b() {
        int intrinsicHeight = this.e.getIntrinsicHeight();
        int height = getHeight();
        if (intrinsicHeight == height && (this.e instanceof BitmapDrawable)) {
            this.a = ((BitmapDrawable) this.e).getBitmap();
        } else {
            float f = (((float) height) * 1.0f) / ((float) intrinsicHeight);
            this.a = b.a(this.e, (int) (((float) this.e.getIntrinsicWidth()) * f), (int) (((float) intrinsicHeight) * f));
        }
        this.d.setShader(new BitmapShader(this.a, TileMode.CLAMP, TileMode.CLAMP));
    }

    protected void onDraw(Canvas canvas) {
        if (this.e == null) {
            super.onDraw(canvas);
            return;
        }
        if (this.a == null) {
            b();
        }
        int height = getHeight();
        Canvas canvas2 = canvas;
        int saveLayer = canvas2.saveLayer((float) getPaddingLeft(), (float) getPaddingTop(), (float) (getWidth() - getPaddingRight()), (float) (height - getPaddingBottom()), null, 31);
        super.onDraw(canvas);
        int width = this.a.getWidth();
        int width2 = getWidth() >> 1;
        width2 = (((int) (((float) width2) * (1.0f - this.b))) + (width2 >> 1)) - (width >> 1);
        this.d.setXfermode(this.c);
        canvas.translate((float) width2, 0.0f);
        canvas.drawRect(0.0f, 0.0f, (float) width, (float) height, this.d);
        this.d.setXfermode(null);
        canvas.restoreToCount(saveLayer);
    }

    public void setThumbOffset(float f) {
        this.b = f;
        invalidate();
    }

    public void a() {
        setImageDrawable(v.a(ThemeElement.SETTING_MENU_INDICATOR_BACKGROUND_IMAGE, (int) R.drawable.img_menu_indicator_background));
        setThumbDrawable(getResources().getDrawable(R.drawable.img_menu_indicator_thumb));
    }
}
