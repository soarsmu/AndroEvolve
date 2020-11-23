/* 
  * Copyright 2014 (C) The EMMES Corporation 
  *  
  * Created on : 02-06-2014
  * Last Update: Jul 7, 2014 3:28:25 PM
  * Author     : Mahbubur Rahman
  * Title      : Summer intern 2014 
  * Project    : Daily Diary Android Application
  * 
  */
package android.preference;


import com.emmes.aps.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

// TODO: Auto-generated Javadoc
/**
 * The Class TimePickerPreference.
 */
public class TimePickerPreference extends DialogPreference {
  
  /** The last hour. */
  private int mLastHour=0;
  
  /** The last minute. */
  private int mLastMinute=0;
  
  /** The picker. */
  private TimePicker mPicker=null;
  
  
  /**
   * Gets the hour.
   *
   * @param time the time
   * @return the hour
   */
  public static int getHour(String time) {
    String[] pieces=time.split(":");
    
    return(Integer.parseInt(pieces[0]));
  }
  
  /**
   * Gets the minute.
   *
   * @param time the time
   * @return the minute
   */
  public static int getMinute(String time) {
    String[] pieces=time.split(":");
    
    return(Integer.parseInt(pieces[1]));
  }
 
  /**
   * Instantiates a new time picker preference.
   *
   * @param ctxt the ctxt
   * @param attrs the attrs
   */
  public TimePickerPreference(Context ctxt, AttributeSet attrs) {
    super(ctxt, attrs);
    
    setPositiveButtonText(ctxt.getResources().getString(R.string.time_picker_positive_text));
    setNegativeButtonText(ctxt.getResources().getString(R.string.time_picker_negative_text));
  }

  /* (non-Javadoc)
   * @see android.preference.DialogPreference#onCreateDialogView()
   */
  @Override
  protected View onCreateDialogView() {
    mPicker=new TimePicker(getContext());
    
    return(mPicker);
  }
  
  /* (non-Javadoc)
   * @see android.preference.DialogPreference#onBindDialogView(android.view.View)
   */
  @Override
  protected void onBindDialogView(View v) {
    super.onBindDialogView(v);
    
    mPicker.setCurrentHour(mLastHour);
    mPicker.setCurrentMinute(mLastMinute);
  }
  
  /* (non-Javadoc)
   * @see android.preference.DialogPreference#onDialogClosed(boolean)
   */
  @Override
  protected void onDialogClosed(boolean positiveResult) {
    super.onDialogClosed(positiveResult);

    if (positiveResult) {
      mLastHour=mPicker.getCurrentHour();
      mLastMinute=mPicker.getCurrentMinute();
      
      String time=String.valueOf(mLastHour)+":"+String.valueOf(mLastMinute);
      
      if (callChangeListener(time)) {
        persistString(time);
      }
    }
  }

  /* (non-Javadoc)
   * @see android.preference.Preference#onGetDefaultValue(android.content.res.TypedArray, int)
   */
  @Override
  protected Object onGetDefaultValue(TypedArray a, int index) {
    return(a.getString(index));
  }

  /* (non-Javadoc)
   * @see android.preference.Preference#onSetInitialValue(boolean, java.lang.Object)
   */
  @Override
  protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
    String time=null;
    
    if (restoreValue) {
      if (defaultValue==null) {
        time=getPersistedString("00:00");
      }
      else {
        time=getPersistedString(defaultValue.toString());
      }
    }
    else {
      time=defaultValue.toString();
    }
    
    mLastHour=getHour(time);
    mLastMinute=getMinute(time);
  }
  
  
}