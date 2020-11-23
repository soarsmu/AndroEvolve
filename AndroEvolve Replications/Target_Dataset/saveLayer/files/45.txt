package com.beastbikes.framework.ui.android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.beastbikes.framework.ui.android.C2824R;

public class CircleImageView extends ImageView {
    private final int borderColor;
    private int borderType;
    private int borderWidth;
    private final Paint edgePaint;
    private Bitmap mask;
    private final Paint maskPaint;
    private final Paint nonePaint;

    public CircleImageView(Context context) {
        this(context, null);
        this.borderWidth = 0;
    }

    public CircleImageView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public CircleImageView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, C2824R.styleable.CircularImageView);
        this.borderColor = obtainStyledAttributes.getColor(C2824R.styleable.CircularImageView_borderColor, 0);
        this.borderWidth = (int) obtainStyledAttributes.getDimension(C2824R.styleable.CircularImageView_imageBorderWidth, 6.0f);
        this.borderType = obtainStyledAttributes.getInt(C2824R.styleable.CircularImageView_borderType, 1);
        obtainStyledAttributes.recycle();
        this.nonePaint = new Paint();
        this.nonePaint.setAntiAlias(true);
        this.edgePaint = new Paint();
        this.edgePaint.setStyle(Style.STROKE);
        this.edgePaint.setAntiAlias(true);
        this.edgePaint.setColor(this.borderColor);
        this.edgePaint.setStrokeWidth((float) this.borderWidth);
        this.maskPaint = new Paint();
        this.maskPaint.setAntiAlias(true);
        this.maskPaint.setFilterBitmap(false);
        this.maskPaint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
    }

    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        if (drawable != null && !(drawable instanceof NinePatchDrawable)) {
            int width = getWidth();
            int height = getHeight();
            int saveLayer = canvas.saveLayer(0.0f, 0.0f, (float) width, (float) height, this.nonePaint, 31);
            drawable.setBounds(0, 0, width, height);
            drawable.draw(canvas);
            if (this.mask == null || this.mask.isRecycled()) {
                this.mask = createOvalBitmap(width, height);
            }
            canvas.drawBitmap(this.mask, 0.0f, 0.0f, this.maskPaint);
            canvas.restoreToCount(saveLayer);
            drawBorder(canvas, width, height);
        }
    }

    private void drawBorder(Canvas canvas, int i, int i2) {
        if (this.borderWidth > 0) {
            canvas.drawCircle((float) (i >> 1), (float) (i2 >> 1), (float) ((i - this.borderWidth) >> 1), this.edgePaint);
        }
    }

    public Bitmap createOvalBitmap(int i, int i2) {
        RectF rectF;
        Bitmap createBitmap = Bitmap.createBitmap(i, i2, Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        int i3 = this.borderWidth > 1 ? this.borderWidth >> 1 : 1;
        if (this.borderType == 1) {
            rectF = new RectF((float) (i3 * 2), (float) (i3 * 2), (float) (i - (i3 * 2)), (float) (i2 - (i3 * 2)));
        } else {
            rectF = new RectF(0.0f, 0.0f, (float) i, (float) i2);
        }
        canvas.drawOval(rectF, this.nonePaint);
        return createBitmap;
    }
}
