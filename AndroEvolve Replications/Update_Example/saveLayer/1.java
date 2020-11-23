package com.alexvasilkov.gestures.commons;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.alexvasilkov.gestures.Settings;
import com.alexvasilkov.gestures.internal.AnimationEngine;
import com.alexvasilkov.gestures.internal.UnitsUtils;
import com.alexvasilkov.gestures.utils.FloatScroller;
import com.alexvasilkov.gestures.utils.GravityUtils;
import com.alexvasilkov.gestures.utils.MathUtils;
import com.alexvasilkov.gestures.views.GestureImageView;

/**
 * View to draw movement area above {@link GestureImageView}, useful when implementing cropping.
 * <p/>
 * To use this view you should set corresponding {@link Settings} with
 * {@link #setSettings(Settings)} method. Then whenever movement area is changed
 * (see {@link Settings#setMovementArea(int, int)}) you will need to call {@link #update(boolean)}
 * method to apply changes.
 * <p/>
 * You may also use rounded corners with {@link #setRounded(boolean)} method, changes between
 * rounded and non-rounded mode can optionally be animated.
 */
public class FinderView extends View {

    public static final int DEFAULT_BACK_COLOR = Color.argb(128, 0, 0, 0);
    public static final int DEFAULT_BORDER_COLOR = Color.WHITE;
    public static final float DEFAULT_BORDER_WIDTH = 2f;

    // Temporary objects
    private static final Rect tmpRect = new Rect();

    private final RectF rect = new RectF();
    private float rounding = 0f;

    private final RectF strokeRect = new RectF();

    private final RectF startRect = new RectF();
    private final RectF endRect = new RectF();
    private float startRounding;
    private float endRounding;

    private final Paint paintStroke = new Paint();
    private final Paint paintClear = new Paint();

    private final FloatScroller stateScroller = new FloatScroller();
    private final AnimationEngine animationEngine = new LocalAnimationEngine();

    private int backColor;
    private Settings settings;

    public FinderView(Context context) {
        this(context, null);
    }

    public FinderView(Context context, AttributeSet attrs) {
        super(context, attrs);

        paintClear.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        paintClear.setAntiAlias(true);

        paintStroke.setStyle(Paint.Style.STROKE);
        paintStroke.setAntiAlias(true);

        // Default values
        setBackColor(DEFAULT_BACK_COLOR);
        setBorderColor(DEFAULT_BORDER_COLOR);
        setBorderWidth(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_BORDER_WIDTH);
    }

    /**
     * Sets background color. Default value is {@link #DEFAULT_BACK_COLOR}.
     */
    public void setBackColor(@ColorInt int color) {
        backColor = color;
    }

    /**
     * Sets borders color. Default value is {@link #DEFAULT_BORDER_COLOR}.
     */
    public void setBorderColor(@ColorInt int color) {
        paintStroke.setColor(color);
    }

    /**
     * Sets borders width in pixels. Default value is {@link #DEFAULT_BORDER_WIDTH} dp.
     */
    public void setBorderWidth(float width) {
        paintStroke.setStrokeWidth(width);
    }

    /**
     * Sets borders width in particular units (see {@link TypedValue}.COMPLEX_UNIT_* constants).
     * Default value is {@link #DEFAULT_BORDER_WIDTH} dp.
     */
    public void setBorderWidth(int unit, float width) {
        setBorderWidth(UnitsUtils.toPixels(getContext(), unit, width));
    }

    /**
     * Sets settings to get movement area from.
     */
    public void setSettings(Settings settings) {
        this.settings = settings;
        update(false);
    }

    /**
     * Whether to round bounds' corners or not. Method {@link #update(boolean)} should be called
     * to apply this setting with optional animation.
     */
    public void setRounded(boolean rounded) {
        startRounding = rounding;
        endRounding = rounded ? 1f : 0f;
    }

    /**
     * Applies area size, area position and corners rounding with optional animation.
     */
    public void update(boolean animate) {
        if (settings != null && getWidth() > 0 && getHeight() > 0) {
            startRect.set(rect);

            GravityUtils.getMovementAreaPosition(settings, tmpRect);
            endRect.set(tmpRect);
            endRect.offset(getPaddingLeft(), getPaddingTop());

            stateScroller.forceFinished();

            if (animate) {
                stateScroller.setDuration(settings.getAnimationsDuration());
                stateScroller.startScroll(0f, 1f);
                animationEngine.start();
            } else {
                setBounds(endRect, endRounding);
            }
        }
    }

    private void setBounds(RectF rect, float rounding) {
        this.rect.set(rect);
        this.rounding = rounding;

        // We want to stroke outside of finder rectangle, while by default stroke is centered
        strokeRect.set(rect);
        float halfStroke = 0.5f * paintStroke.getStrokeWidth();
        strokeRect.inset(-halfStroke, -halfStroke);

        invalidate();
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        update(false);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onDraw(Canvas canvas) {
        float rx = rounding * 0.5f * rect.width();
        float ry = rounding * 0.5f * rect.height();

    	float left = 0;
    	float top = 0;
    	float right = canvas.getWidth();
    	float bottom = canvas.getHeight();
    	Paint paint = null;
    	int saveFlags = 0;
        // Punching hole in background color requires offscreen drawing
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        	canvas.saveLayer(left, top, right, bottom, paint);
        } else {
        	canvas.saveLayer(left, top, right, bottom, paint, saveFlags);
        }
        canvas.drawColor(backColor);
        canvas.drawRoundRect(rect, rx, ry, paintClear);
        canvas.restore();

        canvas.drawRoundRect(strokeRect, rx, ry, paintStroke);
    }


    private class LocalAnimationEngine extends AnimationEngine {
        LocalAnimationEngine() {
            super(FinderView.this);
        }

        @Override
        public boolean onStep() {
            if (!stateScroller.isFinished()) {
                stateScroller.computeScroll();
                float state = stateScroller.getCurr();
                MathUtils.interpolate(rect, startRect, endRect, state);
                float rounding = MathUtils.interpolate(startRounding, endRounding, state);
                setBounds(rect, rounding);
                return true;
            }
            return false;
        }
    }

}
