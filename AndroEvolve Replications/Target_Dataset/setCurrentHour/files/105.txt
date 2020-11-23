package com.picky.timerangeselector;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 选择时间范围对话框，可设置最大值和最小值
 */

public class TimeRangeSelectorDialog extends Dialog {
    private static final String TAG = "TimeRangeSelector";
    private Context context;

    private String startTime;
    private String endTime;

    private int screenWidth;

    private TimePicker timePickerStart;
    private TimePicker timePickerEnd;

    private View cancelBtn, submitBtn;

    private ConfirmAction confirmAction;
    private String leastTime;//最小可选时间
    private String maxTime;//最大可选时间


    private NumberPicker hourStart;//开始时间---小时
    private NumberPicker minStart;//开始时间----分钟
    private String minHour;//开始时间---小时

    private NumberPicker hourEnd;//结束时间----小时
    private NumberPicker minEnd;//结束时间----分钟
    private String maxHour;//结束时间---小时


    private String minMinute;
    private String maxMinutes;

    public TimeRangeSelectorDialog(Context context, String currentTime, String leastTime, String maxTime, ConfirmAction confirmAction) {
        super(context, R.style.dialog);

        this.context = context;
        List<String> strings = getRegEx(currentTime, "\\d+:\\d+");
        if (!isNull(strings) && strings.size() >= 2) {
            this.startTime = getRegEx(currentTime, "\\d+:\\d+").get(0);
            this.endTime = getRegEx(currentTime, "\\d+:\\d+").get(1);
        }

        this.maxTime = maxTime;

        this.leastTime = leastTime;

        this.confirmAction = confirmAction;

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        screenWidth = metrics.widthPixels - getDensityValue(80, context);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_time_range_picker, null);
        setContentView(view);
        getWindow().setLayout(screenWidth, LinearLayout.LayoutParams.WRAP_CONTENT);

        initView();
        initData();
        setEvent();

    }

    private void initView() {

        timePickerStart = findViewById(R.id.timePickerStart);
        timePickerEnd = findViewById(R.id.timePickerEnd);
        cancelBtn = findViewById(R.id.cancelBtn);
        submitBtn = findViewById(R.id.submitBtn);

    }

    private void initData() {
        timePickerStart.setIs24HourView(true);
        timePickerEnd.setIs24HourView(true);
        timePickerStart.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);
        timePickerEnd.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);

        setTimePickerDividerColor(timePickerStart);
        setTimePickerDividerColor(timePickerEnd);
        if (EmptyUtils.isNotEmpty(startTime) && EmptyUtils.isNotEmpty(endTime)) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                timePickerStart.setHour(Integer.parseInt(startTime.substring(0, startTime.indexOf(":"))));
                timePickerStart.setMinute(Integer.parseInt(startTime.substring(startTime.indexOf(":") + 1)));

                timePickerEnd.setHour(Integer.parseInt(endTime.substring(0, endTime.indexOf(":"))));
                timePickerEnd.setMinute(Integer.parseInt(endTime.substring(endTime.indexOf(":") + 1)));
            } else {
                timePickerStart.setCurrentHour(Integer.parseInt(startTime.substring(0, startTime.indexOf(":"))));
                timePickerStart.setCurrentMinute(Integer.parseInt(startTime.substring(startTime.indexOf(":") + 1)));

                timePickerEnd.setCurrentHour(Integer.parseInt(endTime.substring(0, endTime.indexOf(":"))));
                timePickerEnd.setCurrentMinute(Integer.parseInt(endTime.substring(endTime.indexOf(":") + 1)));
            }

        }

        setMin();
        setMax();

        timePickerStart.setOnTimeChangedListener((timePicker, hourOfDay, minute) -> {

            if (Integer.parseInt(minHour) < hourOfDay) {

                minStart.setMinValue(0);
            } else {

                minStart.setMinValue(Integer.parseInt(minMinute));
            }
            String h = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
            String m = minute < 10 ? "0" + minute : "" + minute;
            startTime = h + ":" + m;
        });

        timePickerEnd.setOnTimeChangedListener((timePicker, hourOfDay, minute) -> {
            if (Integer.parseInt(maxHour) > hourOfDay) {
                minEnd.setMaxValue(59);
            } else {
                minEnd.setMaxValue(Integer.parseInt(maxMinutes));
            }
            String h = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
            String m = minute < 10 ? "0" + minute : "" + minute;
            endTime = h + ":" + m;
        });
    }

    private void setMin() {
        Resources systemResources = Resources.getSystem();
        int hourNumberPickerId = systemResources.getIdentifier("hour", "id", "android");
        int minuteNumberPickerId = systemResources.getIdentifier("minute", "id", "android");
        hourStart = timePickerStart.findViewById(hourNumberPickerId);
        minStart = timePickerStart.findViewById(minuteNumberPickerId);
        minHour = leastTime.substring(0, leastTime.indexOf(":"));
        minMinute = leastTime.substring(leastTime.indexOf(":") + 1);
        minStart.setMinValue(Integer.parseInt(minMinute));
        hourStart.setMinValue(Integer.parseInt(minHour));
    }

    private void setMax() {
        Resources systemResources = Resources.getSystem();
        int hourNumberPickerId = systemResources.getIdentifier("hour", "id", "android");
        int minuteNumberPickerId = systemResources.getIdentifier("minute", "id", "android");
        hourEnd = timePickerEnd.findViewById(hourNumberPickerId);
        minEnd = timePickerEnd.findViewById(minuteNumberPickerId);
        maxHour = maxTime.substring(0, maxTime.indexOf(":"));
        maxMinutes = maxTime.substring(maxTime.indexOf(":") + 1);
        hourEnd.setMaxValue(Integer.parseInt(maxHour));
        //只有当前的小时等于设置的最大时间的小时时，设置最大可选分钟
        if (Integer.parseInt(endTime.substring(0, endTime.indexOf(":"))) == Integer.parseInt(maxTime.substring(0, maxTime.indexOf(":")))) {
            minEnd.setMaxValue(Integer.parseInt(maxMinutes));
        }


    }

    private void setEvent() {

        cancelBtn.setOnClickListener(v -> {

            confirmAction.onLeftClick();
            dismiss();
        });

        submitBtn.setOnClickListener(v -> {

            confirmAction.onRightClick(startTime, endTime);
            dismiss();
        });

        this.setCanceledOnTouchOutside(true);

    }

    private void setTimePickerDividerColor(TimePicker timePicker) {
        LinearLayout llFirst = (LinearLayout) timePicker.getChildAt(0);
        LinearLayout mSpinners = (LinearLayout) llFirst.getChildAt(1);
        for (int i = 0; i < mSpinners.getChildCount(); i++) {
            if (mSpinners.getChildAt(i) instanceof NumberPicker) {
                Field[] pickerFields = NumberPicker.class.getDeclaredFields();
                setPickerMargin((NumberPicker) mSpinners.getChildAt(i));
                for (Field pf : pickerFields) {
                    if (pf.getName().equals("mSelectionDivider")) {
                        pf.setAccessible(true);
                        try {
                            pf.set(mSpinners.getChildAt(i), new ColorDrawable());
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        } catch (Resources.NotFoundException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }
            }
        }
    }

    /**
     * 设置picker之间的间距
     */
    private void setPickerMargin(NumberPicker picker) {
        LinearLayout.LayoutParams p = (LinearLayout.LayoutParams) picker.getLayoutParams();
        p.setMargins(-getDensityValue(16, context), 0, -getDensityValue(16, context), 0);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
            p.setMarginStart(-getDensityValue(16, context));
            p.setMarginEnd(-getDensityValue(16, context));
        }
    }

    public interface ConfirmAction {

        void onLeftClick();

        void onRightClick(String startTime, String endTime);
    }

    public static int getDensityValue(float value, Context activity) {

        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();

        return (int) Math.ceil(value * displayMetrics.density);
    }

    public static List<String> getRegEx(String input, String regex) {
        List<String> stringList = new ArrayList<>();
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(input);
        while (m.find())
            stringList.add(m.group());

        return stringList;
    }

    public static boolean isNull(List<?> list) {

        boolean result = false;

        if (null == list) {

            result = true;
        } else {
            if (list.size() == 0) {

                result = true;
            }
        }
        return result;
    }
}
