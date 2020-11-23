package com.cbs.app.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import com.cbs.app.view.ThumbNailView;

public class CustomAdapterViewGroup
  extends FrameLayout
{
  public ImageView a;
  public ThumbNailView b;
  public TextGroup c;
  private boolean d = false;
  private int e;
  private int f;
  private int g;
  private int h;
  private int i;
  private int j;
  
  public CustomAdapterViewGroup(Context paramContext)
  {
    this(paramContext, 0, 0, false);
  }
  
  public CustomAdapterViewGroup(Context paramContext, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    super(paramContext);
    d = paramBoolean;
    a(paramContext, paramInt1, paramInt2);
  }
  
  /* Error */
  public CustomAdapterViewGroup(Context paramContext, android.util.AttributeSet paramAttributeSet)
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore_3
    //   2: aload_0
    //   3: aload_1
    //   4: aload_2
    //   5: invokespecial 38	android/widget/FrameLayout:<init>	(Landroid/content/Context;Landroid/util/AttributeSet;)V
    //   8: aload_0
    //   9: iconst_0
    //   10: putfield 32	com/cbs/app/adapter/CustomAdapterViewGroup:d	Z
    //   13: aload_1
    //   14: aload_2
    //   15: getstatic 44	com/cbs/app/R$styleable:CustomAdapterViewGroup	[I
    //   18: invokevirtual 50	android/content/Context:obtainStyledAttributes	(Landroid/util/AttributeSet;[I)Landroid/content/res/TypedArray;
    //   21: astore 6
    //   23: aload_1
    //   24: aload_2
    //   25: iconst_2
    //   26: newarray <illegal type>
    //   28: dup
    //   29: iconst_0
    //   30: ldc 51
    //   32: iastore
    //   33: dup
    //   34: iconst_1
    //   35: ldc 52
    //   37: iastore
    //   38: invokevirtual 50	android/content/Context:obtainStyledAttributes	(Landroid/util/AttributeSet;[I)Landroid/content/res/TypedArray;
    //   41: astore_2
    //   42: aload_0
    //   43: aload 6
    //   45: iconst_0
    //   46: iconst_0
    //   47: invokevirtual 58	android/content/res/TypedArray:getBoolean	(IZ)Z
    //   50: putfield 32	com/cbs/app/adapter/CustomAdapterViewGroup:d	Z
    //   53: aload 6
    //   55: iconst_1
    //   56: iconst_1
    //   57: invokevirtual 58	android/content/res/TypedArray:getBoolean	(IZ)Z
    //   60: istore 5
    //   62: aload_2
    //   63: iconst_0
    //   64: sipush 300
    //   67: invokevirtual 62	android/content/res/TypedArray:getDimensionPixelSize	(II)I
    //   70: istore 4
    //   72: aload_0
    //   73: aload_1
    //   74: iload 4
    //   76: aload_2
    //   77: iconst_1
    //   78: iload 4
    //   80: i2f
    //   81: ldc 63
    //   83: fmul
    //   84: f2i
    //   85: invokevirtual 62	android/content/res/TypedArray:getDimensionPixelSize	(II)I
    //   88: invokespecial 35	com/cbs/app/adapter/CustomAdapterViewGroup:a	(Landroid/content/Context;II)V
    //   91: aload_0
    //   92: getfield 65	com/cbs/app/adapter/CustomAdapterViewGroup:a	Landroid/widget/ImageView;
    //   95: astore_1
    //   96: iload 5
    //   98: ifeq +18 -> 116
    //   101: aload_1
    //   102: iload_3
    //   103: invokevirtual 71	android/widget/ImageView:setVisibility	(I)V
    //   106: aload 6
    //   108: invokevirtual 75	android/content/res/TypedArray:recycle	()V
    //   111: aload_2
    //   112: invokevirtual 75	android/content/res/TypedArray:recycle	()V
    //   115: return
    //   116: bipush 8
    //   118: istore_3
    //   119: goto -18 -> 101
    //   122: astore_1
    //   123: aload 6
    //   125: invokevirtual 75	android/content/res/TypedArray:recycle	()V
    //   128: aload_2
    //   129: invokevirtual 75	android/content/res/TypedArray:recycle	()V
    //   132: aload_1
    //   133: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	134	0	this	CustomAdapterViewGroup
    //   0	134	1	paramContext	Context
    //   0	134	2	paramAttributeSet	android.util.AttributeSet
    //   1	118	3	k	int
    //   70	9	4	m	int
    //   60	37	5	bool	boolean
    //   21	103	6	localTypedArray	TypedArray
    // Exception table:
    //   from	to	target	type
    //   42	96	122	finally
    //   101	106	122	finally
  }
  
  private void a(Context paramContext, int paramInt1, int paramInt2)
  {
    e = paramInt1;
    f = paramInt2;
    j = paramContext.getResources().getColor(2131558436);
    b = new ThumbNailView(paramContext);
    c = new TextGroup(paramContext);
    g = ((int)(paramInt1 * 0.02F));
    h = ((int)(paramInt1 * 0.04F));
    i = ((int)(e * 0.01F));
    addView(b);
    addView(c);
    if (d)
    {
      a = new ImageView(paramContext);
      a.setScaleType(ImageView.ScaleType.FIT_CENTER);
      a.setScaleX(0.8F);
      a.setScaleY(0.8F);
      a.setImageResource(2130837928);
      addView(a);
    }
    paramContext = paramContext.obtainStyledAttributes(new int[] { 16843534 });
    setForeground(paramContext.getDrawable(0));
    paramContext.recycle();
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramInt1 = 0;
    if (d)
    {
      if (a.getVisibility() == 0) {
        paramInt1 = a.getMeasuredWidth();
      }
      paramInt2 = (f - b.getMeasuredHeight()) / 2;
      b.layout(g, paramInt2, e, f - paramInt2);
      c.layout(b.getMeasuredWidth() + h, paramInt2 + b.getFlagTopMargin(), e - paramInt1, f);
      paramInt2 = (f - a.getMeasuredHeight()) / 2;
      a.layout(e - paramInt1 - g, paramInt2, e - g, f - paramInt2);
      return;
    }
    b.layout(h, 0, e, f);
    c.layout(h, b.getMeasuredHeight(), e - h * 2, f);
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    if (d)
    {
      b.measure((int)(e * 0.45D), f);
      a.measure(i, i);
      paramInt1 = View.MeasureSpec.makeMeasureSpec(e - b.getMeasuredWidth() - a.getMeasuredWidth() - h, 1073741824);
      paramInt2 = View.MeasureSpec.makeMeasureSpec(f, Integer.MIN_VALUE);
      c.measure(paramInt1, paramInt2);
    }
    for (;;)
    {
      setMeasuredDimension(e, f);
      return;
      b.measure((int)(e * 0.9F), f);
      int k = e;
      int m = h;
      int n = h;
      paramInt1 = f;
      paramInt2 = b.getMeasuredHeight();
      k = View.MeasureSpec.makeMeasureSpec(k - m - n, 1073741824);
      paramInt1 = View.MeasureSpec.makeMeasureSpec(paramInt1 - paramInt2, Integer.MIN_VALUE);
      c.measure(k, paramInt1);
    }
  }
  
  public void setWidthAndHeight(int paramInt1, int paramInt2)
  {
    e = paramInt1;
    f = paramInt2;
    g = ((int)(e * 0.02F));
    h = ((int)(e * 0.04F));
    i = ((int)(e * 0.01F));
    invalidate();
    requestLayout();
  }
  
  public class TextGroup
    extends ViewGroup
  {
    public TextView a;
    public TextView b;
    public TextView c;
    public TextView d;
    
    public TextGroup(Context paramContext)
    {
      super();
      a = new TextView(paramContext);
      b = new TextView(paramContext);
      c = new TextView(paramContext);
      d = new TextView(paramContext);
      if (CustomAdapterViewGroup.a(CustomAdapterViewGroup.this))
      {
        a.setTextAppearance(paramContext, 2131427544);
        b.setTextAppearance(paramContext, 2131427545);
        c.setTextAppearance(paramContext, 2131427545);
        d.setTextAppearance(paramContext, 2131427545);
      }
      for (;;)
      {
        a.setMaxLines(2);
        a.setEllipsize(TextUtils.TruncateAt.END);
        b.setMaxLines(2);
        b.setEllipsize(TextUtils.TruncateAt.END);
        addView(a);
        addView(b);
        addView(c);
        addView(d);
        return;
        a.setTextAppearance(paramContext, 2131427542);
        b.setTextAppearance(paramContext, 2131427546);
        c.setTextAppearance(paramContext, 2131427546);
        d.setTextAppearance(paramContext, 2131427546);
        d.setTextColor(CustomAdapterViewGroup.b(CustomAdapterViewGroup.this));
      }
    }
    
    protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      paramInt2 = a.getMeasuredHeight();
      int i = b.getMeasuredHeight();
      int j = c.getMeasuredHeight();
      a.layout(0, 0, paramInt3, paramInt2);
      b.layout(0, paramInt2, CustomAdapterViewGroup.c(CustomAdapterViewGroup.this) - CustomAdapterViewGroup.d(CustomAdapterViewGroup.this), paramInt2 + i);
      if (CustomAdapterViewGroup.a(CustomAdapterViewGroup.this))
      {
        c.layout(0, paramInt2 + i, paramInt3, paramInt2 + i + j);
        d.layout(0, paramInt2 + i + j, CustomAdapterViewGroup.c(CustomAdapterViewGroup.this), CustomAdapterViewGroup.e(CustomAdapterViewGroup.this));
        return;
      }
      c.layout(0, paramInt2 + i, c.getMeasuredWidth(), CustomAdapterViewGroup.e(CustomAdapterViewGroup.this));
      paramInt1 = (int)(3.5F * paramInt1);
      d.layout(CustomAdapterViewGroup.c(CustomAdapterViewGroup.this) - paramInt1 - d.getMeasuredWidth(), paramInt2 + i, CustomAdapterViewGroup.c(CustomAdapterViewGroup.this) - paramInt1, paramInt4);
    }
    
    protected void onMeasure(int paramInt1, int paramInt2)
    {
      int i = 0;
      while (i < 3)
      {
        if (getChildAt(i).getVisibility() == 0) {
          getChildAt(i).measure(paramInt1, paramInt2);
        }
        i += 1;
      }
      i = View.MeasureSpec.makeMeasureSpec(paramInt1, Integer.MIN_VALUE);
      d.measure(i, paramInt2);
      setMeasuredDimension(paramInt1, paramInt2);
    }
  }
}

/* Location:
 * Qualified Name:     com.cbs.app.adapter.CustomAdapterViewGroup
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.7.1
 */