package com.example.fangwei.customviewgroup;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ClipData;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.text.style.UpdateLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IllegalFormatCodePointException;
import java.util.LinkedList;
import java.util.List;

import javax.security.auth.login.LoginException;

/**
 * 拖拽控件
 * Created by fangwei on 15/11/2.
 */
public class DragGridView extends ViewGroup {

    boolean isFirst = true;

    boolean isAnim = false;

    Context mcontext;

    HashMap<String, Drawable> cache = new HashMap<String, Drawable>();

    boolean isEnter = false;

    AnimatorSet resultSet;

    ImageView dragView;

    ImageView changView;

    int dragposition = -1;

    int mdownx = 100;

    private Scroller mScroller;

    int mdowny = 100;

    int xcount = 5;

    int ycount = 8;

    int marginx = 20;

    int marginy = 10;

    int xinter = 10;

    int yinter = 20;


    public DragGridView(Context context) {
        super(context);

        mcontext = context;

        mScroller = new Scroller(context);

        init();
    }

    public DragGridView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mcontext = context;

        mScroller = new Scroller(context);

        init();
    }

    public DragGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mcontext = context;

        mScroller = new Scroller(context);

        init();

    }


    private void init() {

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (ev.getAction() == MotionEvent.ACTION_OUTSIDE) {
            finishUI();
        } else if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            mdownx = (int) ev.getX();
            mdowny = (int) ev.getY();
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();

        if (mScroller.computeScrollOffset()) {

            ((View) getParent()).scrollTo(mScroller.getCurrX(), mScroller.getCurrY());

            invalidate();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int width = getWidth();

        int itemwidth = (width - marginx) / xcount;

        itemwidth = itemwidth - xinter;

        int itemHeight = itemwidth;

        int lineWith = marginx / 2;

        int lineheight = marginy;

        for (int j = 0; j < ycount; j++) {

            for (int i = 0; i < xcount; i++) {

                ImageView iv;

                if (isFirst == true) {
                    iv = new ImageView(mcontext);

                    iv.setTag(j * xcount + i);

                    addView(iv);

                } else {
                    iv = (ImageView) getChildAt(j * xcount + i);
                    iv.setVisibility(VISIBLE);
                }

                if ((j * xcount + i) == dragposition) {
                    iv.setVisibility(INVISIBLE);
                    changView = iv;
                }


                if (i == 1) {
                    if (cache.containsKey(String.valueOf(i))) {
                        iv.setBackground(cache.get(String.valueOf(i)));

                    } else {
                        Drawable drawable = mcontext.getResources().getDrawable(R.drawable.test);
                        iv.setBackground(drawable);
                        cache.put(String.valueOf(i), drawable);
                    }
                } else if (i == 2) {
                    if (cache.containsKey(String.valueOf(i))) {
                        iv.setBackground(cache.get(String.valueOf(i)));

                    } else {
                        Drawable drawable = mcontext.getResources().getDrawable(R.drawable.test1);
                        iv.setBackground(drawable);
                        cache.put(String.valueOf(i), drawable);
                    }
                }
                if (i == 3) {

                    if (cache.containsKey(String.valueOf(i)))
                    {
                        iv.setBackground(cache.get(String.valueOf(i)));

                    }else
                    {
                        Drawable drawable = mcontext.getResources().getDrawable(R.drawable.test2);
                        iv.setBackground(drawable);
                        cache.put(String.valueOf(i),drawable);
                    }

                }
                if (i == 4) {
                    if (cache.containsKey(String.valueOf(i)))
                    {
                        iv.setBackground(cache.get(String.valueOf(i)));

                    }else
                    {
                        Drawable drawable = mcontext.getResources().getDrawable(R.drawable.test3);
                        iv.setBackground(drawable);
                        cache.put(String.valueOf(i),drawable);
                    }
                }
                if (i == 5) {
                    if (cache.containsKey(String.valueOf(i)))
                    {
                        iv.setBackground(cache.get(String.valueOf(i)));

                    }else
                    {
                        Drawable drawable = mcontext.getResources().getDrawable(R.drawable.test4);
                        iv.setBackground(drawable);
                        cache.put(String.valueOf(i),drawable);
                    }
                }
                if (i == 6) {
                    if (cache.containsKey(String.valueOf(i)))
                    {
                        iv.setBackground(cache.get(String.valueOf(i)));

                    }else
                    {
                        Drawable drawable = mcontext.getResources().getDrawable(R.drawable.test5);
                        iv.setBackground(drawable);
                        cache.put(String.valueOf(i),drawable);
                    }
                } else {
                    iv.setBackground(mcontext.getResources().getDrawable(R.drawable.test6));
                }
                iv.layout(l + lineWith, t + lineheight, l + lineWith + itemwidth, t + lineheight + itemHeight);

                iv.setLeft(l + lineWith);

                iv.setRight(l + lineWith + itemwidth);

                iv.setMaxHeight(itemHeight);

                iv.setMaxWidth(itemwidth);

                iv.setScaleType(ImageView.ScaleType.FIT_CENTER);

                iv.setOnLongClickListener(mlongListener);

                lineWith = lineWith + itemwidth + xinter;
            }

            lineheight = lineheight + yinter + itemHeight;

            lineWith = marginx / 2;
        }

        isFirst = false;
    }

    @Override
    public boolean onInterceptHoverEvent(MotionEvent event) {
        return super.onInterceptHoverEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    OnLongClickListener mlongListener = new OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {

            if (isAnim == true) {
                return false;
            }

            dragposition = pointToPosition(v.getLeft() + v.getWidth() / 2, v.getTop() + v.getHeight() / 2);

            dragView = (ImageView) v;

            isEnter = false;

            mdownx = v.getLeft();

            mdowny = v.getTop();

            final DragShadowBuilder builder = new DragShadowBuilder(v);

            ClipData data = ClipData.newPlainText("dot", "Dot : " + v.toString());

            v.startDrag(data, builder, v, 0);

            v.setOnDragListener(new OnDragListener() {
                @Override
                public boolean onDrag(View v, DragEvent event) {
                    switch (event.getAction()) {
                        case DragEvent.ACTION_DRAG_STARTED:

                            dragView.setVisibility(INVISIBLE);

                            isEnter = true;

                            break;
                        case DragEvent.ACTION_DRAG_EXITED:

                            dragView.setVisibility(VISIBLE);

                            break;
                    }
                    return true;
                }
            });
            return true;
        }
    };

    public int pointToPosition(int x, int y) {

        Rect frame = new Rect();

        final int count = getChildCount();

        for (int i = count - 1; i >= 0; i--) {

            final View child = getChildAt(i);

            if (child.getVisibility() == View.VISIBLE) {

                child.getHitRect(frame);

                if (frame.contains(x, y)) {
                    return i;
                }
            }
        }
        return AdapterView.INVALID_POSITION;
    }


    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        return super.drawChild(canvas, child, drawingTime);
    }

    @Override
    public boolean dispatchDragEvent(DragEvent event) {

        //截获拖拽事件的坐标，通过发消息通知，控件更新动画
        // Log.e("dragcc",mdownx+"|"+mdowny);
        if (isEnter) {
            updateUI();
        }

        mdowny = (int) event.getY();
        mdownx = (int) event.getX();

        isEnter = true;

        if (event.getAction() == DragEvent.ACTION_DRAG_ENDED || event.getAction() == DragEvent.ACTION_DRAG_EXITED) {

            finishUI();

            return true;
        }

        return super.dispatchDragEvent(event);
    }

    public void updateUI() {
        if (isAnim == true) {
            return;
        }

        final int tempPosit = pointToPosition(mdownx, mdowny);


        if (tempPosit == -1) {
            return;
        }

        isAnim = true;

        boolean isForward = tempPosit > dragposition;

        List<Animator> resultList = new LinkedList<Animator>();

        Log.e("ccc", dragposition + "|" + tempPosit);

        if (isForward) {
            for (int i = dragposition; i < tempPosit; i++) {
                ObjectAnimator objx = null;

                ObjectAnimator objy = null;

                AnimatorSet animatorSet = new AnimatorSet();

                int temp1 = i;

                int temp2 = i + 1;

                changView = (ImageView) getChildAt(temp2);

                int y1 = (int) Math.ceil((float) (temp1 + 1f) / (float) xcount);

                int y2 = (int) Math.ceil((float) (temp2 + 1f) / (float) xcount);

                int x1 = temp1 % xcount;

                int x2 = temp2 % xcount;

                if (y1 != y2) {

                    if (y2 > y1) {
                        objy = ObjectAnimator.ofFloat(changView, "translationY", -(y2 - y1) * (changView.getHeight() + yinter));
                    } else {
                        objy = ObjectAnimator.ofFloat(changView, "translationY", (y1 - y2) * (changView.getHeight() + yinter));
                    }
                }

                if (x2 > x1) {
                    objx = ObjectAnimator.ofFloat(changView, "translationX", -(changView.getWidth() + xinter) * (Math.abs(x1 - x2)));

                } else {
                    objx = ObjectAnimator.ofFloat(changView, "translationX", (changView.getWidth() + xinter) * (Math.abs(x2 - x1)));
                }

                isAnim = true;

                if (objy != null) {
                    animatorSet.playTogether(objx, objy);
                } else {
                    animatorSet.playTogether(objx);
                }

                resultList.add(animatorSet);
            }
        } else {
            for (int i = dragposition; i > tempPosit; i--) {

                ObjectAnimator objx = null;

                ObjectAnimator objy = null;

                ObjectAnimator objx1 = null;

                ObjectAnimator objy1 = null;

                AnimatorSet animatorSet = new AnimatorSet();

                int temp1 = i;

                int temp2 = i - 1;

                changView = (ImageView) getChildAt(temp2);

                View dragView = getChildAt(temp1);

                int y1 = (int) Math.ceil((float) (temp1 + 1f) / (float) xcount);

                int y2 = (int) Math.ceil((float) (temp2 + 1f) / (float) xcount);

                int x1 = temp1 % xcount;

                int x2 = temp2 % xcount;

                if (y1 != y2) {

                    if (y2 > y1) {
                        objy = ObjectAnimator.ofFloat(changView, "translationY", -(y2 - y1) * (changView.getHeight() + yinter));
                        objy1 = ObjectAnimator.ofFloat(changView, "translationY", (y2 - y1) * (changView.getHeight() + yinter));

                    } else {
                        objy = ObjectAnimator.ofFloat(changView, "translationY", (y1 - y2) * (changView.getHeight() + yinter));
                        objy1 = ObjectAnimator.ofFloat(changView, "translationY", -(y2 - y1) * (changView.getHeight() + yinter));

                    }
                }

                if (x2 > x1) {
                    objx = ObjectAnimator.ofFloat(changView, "translationX", -(changView.getWidth() + xinter) * (Math.abs(x1 - x2)));

                } else {
                    objx = ObjectAnimator.ofFloat(changView, "translationX", (changView.getWidth() + xinter) * (Math.abs(x2 - x1)));
                }

                if (objy != null) {
                    animatorSet.playTogether(objy, objx);
                } else {
                    animatorSet.playTogether(objx);
                }

                resultList.add(animatorSet);
            }
        }

        resultSet = new AnimatorSet();

        resultSet.playTogether(resultList);

        resultSet.setDuration(200);

        resultSet.setInterpolator(new AccelerateDecelerateInterpolator());

        resultSet.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

                invalidate();

                dragposition = tempPosit;

                isFirst = true;

                removeAllViews();

                requestLayout();

                //invalidate();

                isAnim = false;
            }
        });
        resultSet.start();
    }

    public void finishUI() {

        if (dragposition >= 0) {

            ObjectAnimator anim = ObjectAnimator.ofFloat(getChildAt(dragposition), "alpha", 0f, 1f);

            anim.setDuration(100);

            anim.setInterpolator(new AccelerateInterpolator());

            if (resultSet != null && resultSet.isRunning()) {
                anim.setStartDelay(200);
            }
            anim.start();
            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {

                    View view = getChildAt(dragposition);
                    if (view != null) {
                        view.setVisibility(VISIBLE);
                    }

                    if (dragView != null) {
                        dragView.setVisibility(VISIBLE);
                    }

                    int count = getChildCount();

                    for (int i = 0; i < count; i++) {
                        getChildAt(i).setVisibility(VISIBLE);
                    }

                    if (changView != null) {
                        changView.setVisibility(VISIBLE);
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }

        isEnter = false;

    }
}
