package com.hongliang.demo.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

import com.hongliang.demo.util.UIHelper;


public class CanvasView extends View {


    private Paint mPaint;
    private int mViewWidth, mViewHeight;// 控件宽高

    private float curValue = 0f;

    private int OFFSET = 100;//偏移量


    private Context mContext;

    public CanvasView(Context context) {
        this(context, null);
    }

    public CanvasView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CanvasView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
        initAnimator();

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        /*
         * 获取控件宽高
         */
        mViewWidth = w;
        mViewHeight = h;
    }

    private void init() {

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(10);
        mPaint.setStyle(Paint.Style.FILL);
        float size = UIHelper.dp2px(mContext, 15);
        mPaint.setTextSize(size);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //设置一个BLUE的背景颜色
        canvas.drawColor(Color.BLUE);

        //创建一个画布，
        int saveCount = canvas.saveLayer(OFFSET, OFFSET, mViewWidth - OFFSET, mViewHeight - OFFSET, mPaint, Canvas.ALL_SAVE_FLAG);
        //设置画布的颜色为GREEN
        canvas.drawColor(Color.GREEN);

        //再创建一个画布
        int saveCount1 = canvas.saveLayer(OFFSET * 2, OFFSET * 2, mViewWidth - OFFSET * 2, mViewHeight - OFFSET * 2, mPaint, Canvas.ALL_SAVE_FLAG);
        //设置画布的颜色为BLACK
        canvas.drawColor(Color.BLACK);
        //在BLACK画布上画一个圆， 注意这个圆不能显示完全，因为画布的大小有限制，左边点的位置还是相对于屏幕坐上角
        canvas.drawCircle(300, 300, 300, mPaint);

        //创建一个新画笔
        Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(10);
        mPaint.setStyle(Paint.Style.FILL);
        //还是在黑色画布上画一个矩形
        canvas.drawRect(new Rect(0, 0, 350, 350), mPaint);
        //切换到黑色画布之前，就是绿色画布
        canvas.restoreToCount(saveCount1);

        mPaint.setColor(Color.CYAN);
        //在绿色画布上再画一个矩形
        canvas.drawRect(new Rect(0, 0, 300, 300), mPaint);

        Toast.makeText(mContext, " canvas.getSaveCount()" + canvas.getSaveCount(), Toast.LENGTH_LONG).show();

    }


    private void initAnimator() {

        ValueAnimator animator = ValueAnimator.ofFloat(0, 90);
        animator.setDuration(1000);
        animator.setStartDelay(2000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                curValue = (float) animation.getAnimatedValue();
                invalidate();

            }
        });
        animator.start();
    }


}
