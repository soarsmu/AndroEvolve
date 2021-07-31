package org.androidtown.ui.composite;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

import java.util.Calendar;

/**
 * 복합위젯 클래스 정의(LinearLayout을 상속받아서 아예 새로 정의)
 * 
 * @author Mike
 *
 */
public class DateTimePicker extends LinearLayout {


	//날짜선택
	private final DatePicker datePicker;

	//체크박스
	private final CheckBox enableTimeCheckBox;

	//시간선택
	private final TimePicker timePicker;

	//리스너 부분
	//MainActivity에서 리스너를 통해 날짜값이 변경된 것을 확인
	//===============================================================================================================
	/**
	 * 날짜나 시간이 바뀔 때 호출되는 리스너 새로 정의(인터페이스)
	 */
	public interface OnDateTimeChangedListener {
		void onDateTimeChanged(DateTimePicker view, int year, int monthOfYear, int dayOfYear, int hourOfDay, int minute);
	}
	
	/**
	 * 리스너 객체(클래스 내에서 인터페이스를 선언하고, 그 클래스 내에서 바로 쓰네..)
	 */
	private OnDateTimeChangedListener onDateTimeChangedListener;

	public void setOnDateTimeChangedListener(OnDateTimeChangedListener onDateTimeChangedListener){
		this.onDateTimeChangedListener = onDateTimeChangedListener;
	}

	//===============================================================================================================


	
	/**
	 * 생성자
	 * 
	 * @param context
	 */
	public DateTimePicker(final Context context){
		this(context, null); //this가 어디야?
	}
	
	/**
	 * 생성자
	 * 
	 * @param context
	 * @param attrs
	 */
	public DateTimePicker(Context context, AttributeSet attrs){
		super(context, attrs);
		
		// XML 레이아웃을 인플레이션함
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.datetimepicker, this, true);
		
		// 시간 정보 참조
		Calendar calendar = Calendar.getInstance(); //많이 쓰이는디.. 달력 정보를 가져오는건가?
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.DataTimePicker);
		final int _currentYear = a.getInt(R.styleable.DataTimePicker_year, calendar.get(Calendar.YEAR));
		final int _currentMonth = a.getInt(R.styleable.DataTimePicker_month, calendar.get(Calendar.MONTH));
		final int _currentDay = a.getInt(R.styleable.DataTimePicker_day, calendar.get(Calendar.DAY_OF_MONTH));
		final int _currentHour = a.getInt(R.styleable.DataTimePicker_hour, calendar.get(Calendar.HOUR_OF_DAY));
		final int _currentMinute = a.getInt(R.styleable.DataTimePicker_minute, calendar.get(Calendar.MINUTE));
		
		// 날짜선택 위젯 초기화
		datePicker = (DatePicker)findViewById(R.id.datePicker);
		datePicker.init(_currentYear, _currentMonth, _currentDay, new OnDateChangedListener() {
			public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				// 새로 정의한 리스너로 이벤트 전달
				if(onDateTimeChangedListener != null){
					Log.d("Fuck", "날짜가 변경");
					onDateTimeChangedListener.onDateTimeChanged(
							DateTimePicker.this, year, monthOfYear, dayOfMonth,
							timePicker.getCurrentHour(), timePicker.getCurrentMinute());
				}
			}
		});
		
		// 체크박스 이벤트 처리(MainActivity에서 처리하지 않고, 여기서 이렇게 하네..)
		enableTimeCheckBox = (CheckBox)findViewById(R.id.enableTimeCheckBox);
		enableTimeCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Log.d("Fuck", "체크박스 눌러짐:" + isChecked);
				timePicker.setEnabled(isChecked);
				timePicker.setVisibility((enableTimeCheckBox).isChecked()?View.VISIBLE:View.INVISIBLE);
			}
		});
		
		// 시간선택 위젯 이벤트 처리
		timePicker = (TimePicker)findViewById(R.id.timePicker);
		timePicker.setOnTimeChangedListener(new OnTimeChangedListener() {
			public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				Log.d("Fuck", "시간 변경됨");
				if(onDateTimeChangedListener != null) {
					onDateTimeChangedListener.onDateTimeChanged(
							DateTimePicker.this, datePicker.getYear(), 
							datePicker.getMonth(), datePicker.getDayOfMonth(), hourOfDay, minute);
				}
			}
		});
		
		timePicker.setCurrentHour(_currentHour);
		timePicker.setCurrentMinute(_currentMinute);
		timePicker.setEnabled(enableTimeCheckBox.isChecked());
		timePicker.setVisibility((enableTimeCheckBox.isChecked()?View.VISIBLE:View.INVISIBLE));
	}
	

	public void updateDateTime(int year, int monthOfYear, int dayOfMonth, int currentHour, int currentMinute){
		datePicker.updateDate(year, monthOfYear, dayOfMonth);
		timePicker.setCurrentHour(currentHour);
		timePicker.setCurrentMinute(currentMinute);
	}
	
	public void updateDate(int year, int monthOfYear, int dayOfMonth){
		datePicker.updateDate(year, monthOfYear, dayOfMonth);
	}
	
	public void setIs24HourView(final boolean is24HourView){
		timePicker.setIs24HourView(is24HourView);
	}


	//각종 값들의 반환(년, 월, 일, 시간, 분)
	//==============================================================================================
	public int getYear() {
		return datePicker.getYear();
	}
	public int getMonth() {
		return datePicker.getMonth();
	}
	public int getDayOfMonth() {
		return datePicker.getDayOfMonth();
	}
	public int getCurrentHour() {
		return timePicker.getCurrentHour();
	}
	public int getCurrentMinute() {
		return timePicker.getCurrentMinute();
	}
	//==============================================================================================

	public boolean enableTime() {
		return enableTimeCheckBox.isChecked();
	}
}
