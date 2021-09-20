package com.example.myapplication.weight.xformode;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.myapplication.R;

import static android.graphics.Canvas.*;

public class HeatbeatView extends View {
    public HeatbeatView(Context context) {
        super(context);
        init();
    }

    public HeatbeatView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HeatbeatView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    Bitmap mBitmapSrc, mBitmapDst;
    Paint paint;
    int bitmapWidth;
    int dx = 0;

    public void init() {
        mBitmapDst = BitmapFactory.decodeResource(getResources(), R.mipmap.heartmap);
        mBitmapSrc = BitmapFactory.decodeResource(getResources(), R.mipmap.circle_shape);
        paint = new Paint();
        bitmapWidth = mBitmapDst.getWidth();
        post(new Runnable() {
            @Override
            public void run() {
                dx += 5;
                if (dx >= bitmapWidth)
                    dx = 0;
                invalidate();
                postDelayed(this, 50);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int layerId = canvas.saveLayer(0, 0, mBitmapSrc.getWidth(), mBitmapSrc.getHeight(),null, ALL_SAVE_FLAG);

        canvas.drawBitmap(mBitmapDst, new Rect(dx, 0, dx + mBitmapSrc.getWidth(), mBitmapSrc.getHeight()),
                new Rect(0, 0, mBitmapSrc.getWidth(), mBitmapSrc.getHeight()), paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.drawBitmap(mBitmapSrc,0,0,paint);
        paint.setXfermode(null);
        canvas.restoreToCount(layerId);
    }
}
