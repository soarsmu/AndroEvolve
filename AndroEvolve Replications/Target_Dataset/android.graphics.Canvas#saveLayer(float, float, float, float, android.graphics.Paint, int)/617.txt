package com.example.chapter09.part3;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author wangzhichao
 * @date 2019/10/09
 */
public class ALPHA_LAYER_SAVE_FLAG_View2 extends View {
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    public ALPHA_LAYER_SAVE_FLAG_View2(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        paint.setColor(Color.GREEN);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 绘制红色背景
        canvas.drawColor(Color.RED);

        // 新建图层
//        canvas.saveLayer(0, 0, 200, 200, paint, Canvas.MATRIX_SAVE_FLAG);
        canvas.saveLayer(0, 0, 200, 200, paint, Canvas.CLIP_SAVE_FLAG);
        canvas.rotate(40);
        canvas.drawRect(50,50,150,150, paint);
        // 恢复图层
        canvas.restore();
    }
}

/**
 * 总结：
 * 1，对于 saveLayer 方法，当单独使用 MATRIX_SAVE_FLAG 或者 CLIP_SAVE_FLAG 时，默认是 FULL_COLOR_LAYER_SAVE_FLAG 的效果。
 */
