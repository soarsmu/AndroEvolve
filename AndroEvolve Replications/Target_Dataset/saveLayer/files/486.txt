package com.anglll.beelayout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * 讲边框线半径和图片的剪切半径设置为相同，是边框能够完全覆盖住图片半径
 */
public class BeeItemView extends FrameLayout {

    private Path path = new Path();
    private Paint strokePaint;
    private Paint paint;
    private Path strokePath = new Path();
    private float strokeR;
    private int borderColor = Color.RED;
    private float borderWidth = 10f;
    private boolean hasBorder = false;
    private float roundCorner = 0.1f;
    private float spaceWidth = 10f;

    public BeeItemView(Context context) {
        super(context);
        init(context, null);
    }

    public BeeItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public BeeItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor;
        strokePaint.setColor(borderColor);
    }

    public void setBorderWidth(float borderWidth) {
        this.borderWidth = borderWidth;
        strokePaint.setStrokeWidth(borderWidth);
        //边框宽度
        strokeR = (float) (borderWidth / 2 / Math.sin(60 * Math.PI / 180));
    }

    public void hasBorder(boolean hasBorder) {
        this.hasBorder = hasBorder;
    }

    public void setSpaceWidth(float spaceWidth) {
        this.spaceWidth = spaceWidth;
    }

    public void setRoundCorner(float roundCorner) {
        this.roundCorner = roundCorner;
    }


    private void init(Context context, AttributeSet attrs) {
        strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        strokePaint.setStrokeWidth(borderWidth);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setColor(borderColor);
        //边框宽度
        strokeR = (float) (borderWidth / 2 / Math.sin(60 * Math.PI / 180));

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }


    @Override
    protected void dispatchDraw(Canvas canvas) {
        /**
         * @path 要显示的形状
         * 未设置path 设置了园角度
         */
        path.reset();
        strokePath.reset();
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int R = Math.min(width, width) / 2;//最大半径
        float r = R - strokeR - spaceWidth / 2;//边框中心半径和图片剪切半径

        //裁剪背景
        int save = canvas.saveLayer((float) (width / 2 - r * Math.sin(60 * Math.PI / 180)),
                height / 2 - r,
                (float) (width / 2 + r * Math.sin(60 * Math.PI / 180)),
                height / 2 + r, null, Canvas.ALL_SAVE_FLAG);
        super.dispatchDraw(canvas);
        canvas.translate(width / 2, height / 2);

        for (int i = 0; i < 6; i++) {
            if (i == 0) {
                path.moveTo((float) (r * Math.cos((30 + i * 60) * Math.PI / 180)),
                        (float) (r * Math.sin((30 + i * 60) * Math.PI / 180)));
                strokePath.moveTo((float) (r * Math.cos((30 + i * 60) * Math.PI / 180)),
                        (float) (r * Math.sin((30 + i * 60) * Math.PI / 180)));
            } else {
                path.lineTo((float) (r * Math.cos((30 + i * 60) * Math.PI / 180)),
                        (float) (r * Math.sin((30 + i * 60) * Math.PI / 180)));
                strokePath.lineTo((float) (r * Math.cos((30 + i * 60) * Math.PI / 180)),
                        (float) (r * Math.sin((30 + i * 60) * Math.PI / 180)));
            }
        }
        path.close();
        strokePath.close();

        //圆角处理
        paint.setPathEffect(new CornerPathEffect(r * roundCorner));
        strokePaint.setPathEffect(new CornerPathEffect(r * roundCorner));

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.drawPath(path, paint);
        canvas.restoreToCount(save);
        paint.setXfermode(null);
        save = canvas.saveLayer(0,
                0, width,
                height, null, Canvas.ALL_SAVE_FLAG);
        canvas.translate(width / 2, height / 2);
        if (hasBorder)
            canvas.drawPath(strokePath, strokePaint);
        canvas.restoreToCount(save);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
