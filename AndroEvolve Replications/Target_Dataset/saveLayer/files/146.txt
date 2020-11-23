package com.android.camera.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import com.android.camera.R;
import com.android.camera.Util;
import com.android.camera.protocol.ModeCoordinatorImpl;
import com.android.camera.protocol.ModeProtocol;

public class EdgeHorizonScrollView extends HorizontalScrollView {
    private float mDownX = -1.0f;
    private Paint mEdgePaint;
    private int mEdgeWidth;
    private boolean mIsRTL;
    private boolean mScrolled = false;

    public EdgeHorizonScrollView(Context context) {
        super(context);
        initView();
    }

    public EdgeHorizonScrollView(Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
        initView();
    }

    public EdgeHorizonScrollView(Context context, @Nullable AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initView();
    }

    /* access modifiers changed from: protected */
    public boolean drawChild(Canvas canvas, View view, long j) {
        Canvas canvas2 = canvas;
        int width = getWidth();
        int height = getHeight();
        float f2 = (float) height;
        int saveLayer = canvas.saveLayer(0.0f, 0.0f, (float) Math.max(width, view.getWidth()), f2, (Paint) null, 31);
        boolean drawChild = super.drawChild(canvas, view, j);
        canvas2.translate(this.mIsRTL ? (float) Math.max(0, view.getWidth() - width) : 0.0f, 0.0f);
        float f3 = (float) width;
        float f4 = f3 / 2.0f;
        float f5 = f2 / 2.0f;
        float f6 = ((float) (height - width)) / 2.0f;
        int save = canvas.save();
        canvas2.rotate(90.0f, f4, f5);
        canvas2.translate(0.0f, f6);
        float f7 = 0.0f - f6;
        float f8 = f3 + f6;
        canvas.drawRect(f7, 0.0f, f8, (float) this.mEdgeWidth, this.mEdgePaint);
        canvas2.restoreToCount(save);
        int save2 = canvas.save();
        canvas2.rotate(-90.0f, f4, f5);
        canvas2.translate(0.0f, f6);
        canvas.drawRect(f7, 0.0f, f8, (float) this.mEdgeWidth, this.mEdgePaint);
        canvas2.restoreToCount(save2);
        canvas2.restoreToCount(saveLayer);
        return drawChild;
    }

    public void initView() {
        this.mIsRTL = Util.isLayoutRTL(getContext());
        this.mEdgeWidth = getResources().getDimensionPixelSize(R.dimen.mode_select_layout_edge);
        this.mEdgePaint = new Paint();
        this.mEdgePaint.setAntiAlias(true);
        this.mEdgePaint.setStyle(Paint.Style.FILL);
        this.mEdgePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        Paint paint = this.mEdgePaint;
        LinearGradient linearGradient = new LinearGradient(0.0f, 0.0f, 0.0f, (float) this.mEdgeWidth, new int[]{ViewCompat.MEASURED_STATE_MASK, -1728053248, 0}, new float[]{0.0f, 0.3f, 2.0f}, Shader.TileMode.CLAMP);
        paint.setShader(linearGradient);
        setFocusable(false);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:6:0x0010, code lost:
        if (r0 != 3) goto L_0x006a;
     */
    public boolean onTouchEvent(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (action != 0) {
            if (action != 1) {
                if (action == 2) {
                    if (this.mDownX == -1.0f) {
                        this.mDownX = motionEvent.getX();
                    }
                    float x = motionEvent.getX() - this.mDownX;
                    ModeProtocol.ModeChangeController modeChangeController = (ModeProtocol.ModeChangeController) ModeCoordinatorImpl.getInstance().getAttachProtocol(179);
                    if (!this.mScrolled && modeChangeController != null) {
                        if (x > ((float) Util.dpToPixel(17.0f)) && modeChangeController.canSwipeChangeMode()) {
                            modeChangeController.changeModeByGravity(3, 0);
                            this.mScrolled = true;
                        } else if (x < ((float) (-Util.dpToPixel(17.0f))) && modeChangeController.canSwipeChangeMode()) {
                            modeChangeController.changeModeByGravity(5, 0);
                            this.mScrolled = true;
                        }
                    }
                    return true;
                }
            }
            this.mDownX = -1.0f;
            this.mScrolled = false;
            return false;
        }
        this.mDownX = motionEvent.getX();
        this.mScrolled = false;
        return true;
    }
}
