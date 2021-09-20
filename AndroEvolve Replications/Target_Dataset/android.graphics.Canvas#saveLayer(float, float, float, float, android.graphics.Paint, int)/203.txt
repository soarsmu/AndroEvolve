package com.example.chivas.customdraw.utils.bitmap;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import static com.example.chivas.customdraw.constants.XferModeConstants.MODE_MAP;
import static com.example.chivas.customdraw.constants.XferModeConstants.MODE_TYPE;

public class BitmapAdapter15 extends AbstractAdapter {

    @Override
    public void parse(Canvas canvas, Bitmap bitmap) {
        int totalW = bitmap.getWidth();
        int totalH = bitmap.getHeight();

        int startX = 200;
        int startY = 200;
        int endX = 400;
        int endY = 400;
        canvas.drawColor(Color.WHITE);
        Paint paint = new Paint();
        paint.setTextSize(30);

        /**
         * 设置View的离屏缓冲。在绘图的时候新建一个“层”，所有的操作都在该层而不会影响该层以外的图像
         * 必须设置，否则设置的PorterDuffXfermode会无效，
         * 具体原因是canvas画布调用了drawXXX后可视范围即为XXX的大小
         */
        int sc = canvas.saveLayer(0, 0, totalW, totalH, paint, Canvas.ALL_SAVE_FLAG);
        canvas.drawText("Mode： PorterDuff.Mode.CLEAR", 100, 50, paint);
        mPaint.setColor(Color.YELLOW);//DST是黄色圆，SRC是蓝色矩形
        canvas.drawCircle(startX, startY, 100, mPaint);//先画目标

        mPaint.setColor(Color.BLUE);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawRect(startX, startY, endX, endY, mPaint);//再画源
        mPaint.setXfermode(null);
        canvas.restoreToCount(sc);

//--------------------------------------------------------------------------------------------------
        for (int i = 1; i < 16; i++) {
            int sc1 = canvas.saveLayer(0, 0, totalW, totalH, paint, Canvas.ALL_SAVE_FLAG);
            Paint paint1 = new Paint();
            paint.setTextSize(30);
            canvas.drawText("Mode： PorterDuff.Mode." + MODE_TYPE[i], 100, endY + 100, paint);
            startY = endY + 230;
            endY = endY + 430;
            paint1.setColor(Color.YELLOW);
            canvas.drawCircle(startX, startY, 100, paint1);//目标像素dst

            paint1.setColor(Color.BLUE);
            paint1.setXfermode(MODE_MAP.get(MODE_TYPE[i]));
            canvas.drawRect(startX, startY, endX, endY, paint1);//源像素src
            paint1.setXfermode(null);
            canvas.restoreToCount(sc1);
        }
    }

}
