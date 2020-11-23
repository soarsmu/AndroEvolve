package com.sds.android.ttpod.framework.modules.skin.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Xfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.util.AttributeSet;

public class MaskImageView extends RecyclingImageView {
    private Drawable a;
    private final Xfermode b = new PorterDuffXfermode(Mode.SRC_IN);

    public MaskImageView(Context context) {
        super(context);
    }

    public MaskImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public MaskImageView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public void setMaskImageDrawable(Drawable drawable) {
        if ((drawable instanceof ShapeDrawable) || (drawable instanceof BitmapDrawable)) {
            this.a = drawable;
        } else {
            this.a = null;
        }
    }

    protected void onDraw(Canvas canvas) {
        Drawable drawable = this.a;
        if (drawable != null) {
            Paint a = a(drawable);
            Paint a2 = a(getDrawable());
            if (!(a == null || a2 == null)) {
                Rect bounds = drawable.getBounds();
                int saveLayer = canvas.saveLayer((float) bounds.left, (float) bounds.top, (float) bounds.right, (float) bounds.bottom, a, 31);
                Xfermode xfermode = a2.getXfermode();
                drawable.draw(canvas);
                a2.setXfermode(this.b);
                super.onDraw(canvas);
                canvas.restoreToCount(saveLayer);
                a2.setXfermode(xfermode);
                return;
            }
        }
        super.onDraw(canvas);
    }

    protected boolean setFrame(int i, int i2, int i3, int i4) {
        boolean frame = super.setFrame(i, i2, i3, i4);
        if (this.a != null) {
            int paddingTop = getPaddingTop() + i2;
            int paddingRight = i3 - getPaddingRight();
            int paddingLeft = paddingRight - (getPaddingLeft() + i);
            paddingRight = (i4 - getPaddingBottom()) - paddingTop;
            int intrinsicWidth = this.a.getIntrinsicWidth();
            int intrinsicHeight = this.a.getIntrinsicHeight();
            if (intrinsicWidth > 0 && intrinsicHeight > 0) {
                float min = Math.min(((float) paddingLeft) / ((float) intrinsicWidth), ((float) paddingRight) / ((float) intrinsicHeight));
                intrinsicWidth = (int) (((float) intrinsicWidth) * min);
                paddingLeft = (paddingLeft - intrinsicWidth) >> 1;
                this.a.setBounds(paddingLeft, paddingTop, intrinsicWidth + paddingLeft, ((int) (min * ((float) intrinsicHeight))) + paddingTop);
            }
        }
        return frame;
    }

    private Paint a(Drawable drawable) {
        if (drawable instanceof ShapeDrawable) {
            return ((ShapeDrawable) drawable).getPaint();
        }
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getPaint();
        }
        return null;
    }
}
