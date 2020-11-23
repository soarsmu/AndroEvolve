package com.yelp.android.ui.util;

import android.annotation.TargetApi;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.NumberPicker;
import android.widget.TimePicker;
import com.yelp.android.appdata.n;
import com.yelp.android.util.YelpLog;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class IntervalTimePickerDialog
  extends TimePickerDialog
{
  private final TimePickerDialog.OnTimeSetListener mCallback;
  private final int mInterval;
  private int mPreviousMinute;
  private TimePicker mTimePicker;
  private boolean mUseRoundingMethod;
  
  public IntervalTimePickerDialog(Context paramContext, TimePickerDialog.OnTimeSetListener paramOnTimeSetListener, int paramInt1, int paramInt2, boolean paramBoolean, int paramInt3)
  {
    super(paramContext, paramOnTimeSetListener, paramInt1, paramInt2, paramBoolean);
    mCallback = paramOnTimeSetListener;
    mPreviousMinute = paramInt2;
    mInterval = paramInt3;
    mUseRoundingMethod = n.b(11);
  }
  
  @TargetApi(11)
  public void onAttachedToWindow()
  {
    int i = 0;
    super.onAttachedToWindow();
    if (mUseRoundingMethod) {
      return;
    }
    try
    {
      Object localObject = Class.forName("com.android.internal.R$id");
      mTimePicker = ((TimePicker)findViewById(((Class)localObject).getField("timePicker").getInt(null)));
      localObject = ((Class)localObject).getField("minute");
      localObject = (NumberPicker)mTimePicker.findViewById(((Field)localObject).getInt(null));
      ((NumberPicker)localObject).setMinValue(0);
      ((NumberPicker)localObject).setMaxValue(60 / mInterval - 1);
      ArrayList localArrayList = new ArrayList();
      while (i < 60)
      {
        localArrayList.add(String.format("%02d", new Object[] { Integer.valueOf(i) }));
        i += mInterval;
      }
      ((NumberPicker)localObject).setDisplayedValues((String[])localArrayList.toArray(new String[0]));
      ((NumberPicker)localObject).setOnValueChangedListener(null);
      return;
    }
    catch (Exception localException)
    {
      mUseRoundingMethod = true;
      YelpLog.error(getContext(), "Using reflection inside IntervalTimePickerDialog isn't working.", localException);
    }
  }
  
  @TargetApi(11)
  public void onClick(DialogInterface paramDialogInterface, int paramInt)
  {
    super.onClick(paramDialogInterface, paramInt);
    if ((!mUseRoundingMethod) && (mCallback != null) && (mTimePicker != null))
    {
      mTimePicker.clearFocus();
      mCallback.onTimeSet(mTimePicker, mTimePicker.getCurrentHour().intValue(), mTimePicker.getCurrentMinute().intValue() * mInterval);
    }
  }
  
  protected void onStop()
  {
    if (mUseRoundingMethod) {
      super.onStop();
    }
  }
  
  public void onTimeChanged(TimePicker paramTimePicker, int paramInt1, int paramInt2)
  {
    int k = 0;
    super.onTimeChanged(paramTimePicker, paramInt1, paramInt2);
    if (!mUseRoundingMethod) {}
    while (paramInt2 == mPreviousMinute) {
      return;
    }
    int i;
    int j;
    if ((paramInt2 == 59) && (mPreviousMinute == 0))
    {
      paramInt2 = 60 - mInterval;
      paramInt1 -= 1;
      i = paramInt1;
      j = paramInt2;
      if (paramInt1 < 0)
      {
        i = 23;
        j = paramInt2;
      }
      paramInt2 = Math.round(j / mInterval) * mInterval;
      if (paramInt2 < 60) {
        break label230;
      }
      paramInt1 = i + 1;
      paramInt2 = 0;
    }
    for (;;)
    {
      if (paramInt1 > 23) {
        paramInt1 = k;
      }
      for (;;)
      {
        mPreviousMinute = paramInt2;
        paramTimePicker.setCurrentMinute(Integer.valueOf(paramInt2));
        paramTimePicker.setCurrentHour(Integer.valueOf(paramInt1));
        return;
        if (paramInt2 >= 60 - mInterval / 2)
        {
          i = paramInt1 + 1;
          j = 0;
          break;
        }
        if (paramInt2 - mPreviousMinute == 1)
        {
          j = mPreviousMinute + mInterval;
          i = paramInt1;
          break;
        }
        if (paramInt2 - mPreviousMinute != -1)
        {
          i = paramInt1;
          j = paramInt2;
          if (paramInt2 - mPreviousMinute != -59) {
            break;
          }
        }
        j = Math.abs(mPreviousMinute - mInterval);
        i = paramInt1;
        break;
      }
      label230:
      paramInt1 = i;
    }
  }
}

/* Location:
 * Qualified Name:     com.yelp.android.ui.util.IntervalTimePickerDialog
 * Java Class Version: 6 (50.0)
 * JD-Core Version:    0.7.1
 */