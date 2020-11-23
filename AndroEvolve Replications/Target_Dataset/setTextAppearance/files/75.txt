package com.hzgzh.naturegasheat;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class DataDisplayControl extends LinearLayout {
    Context mContext;
    ViewHolder vh = null;

    public DataDisplayControl(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        setOrientation(LinearLayout.HORIZONTAL);
        vh = new ViewHolder();
        vh.name = new TextView(context);
        vh.symbol = new TextView(context);

        LayoutParams lp = new LinearLayout.LayoutParams(0,
                LayoutParams.WRAP_CONTENT, 1);
        vh.name.setLayoutParams(new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, (float) 1.7));
        vh.symbol.setLayoutParams(lp);

        vh.name.setTextAppearance(context, android.R.style.TextAppearance_Small);
        vh.symbol.setTextAppearance(context, android.R.style.TextAppearance_Small);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Custom);
        int indexCount = a.getIndexCount();
        for (int i = 0; i < indexCount; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.Custom_type:
                    // ��ȡmyText����ֵ
                    String myType = a.getString(attr);
                    // �õ�ֵ��Ϳ��԰�����Ҫ��ʹ���ˣ��������Ǹ�Button���ð�ť��ʾ����
                    if (myType.equals("text")) {
                        vh.value = new TextView(mContext);
                        vh.value.setLayoutParams(lp);
                        addView(vh.name);
                        addView(vh.symbol);
                        addView(vh.value);
                        vh.value.setTextAppearance(context, android.R.style.TextAppearance_Small);
                    }
                    if (myType.equals("edit")) {
                        vh.evalue = new EditText(mContext);
                        vh.evalue.setLayoutParams(lp);
                        addView(vh.name);
                        addView(vh.symbol);
                        addView(vh.evalue);
                        vh.evalue.setTextAppearance(context, android.R.style.TextAppearance_Small);
                    }
                    if (myType.equals("spinner")) {
                        vh.svalue = new Spinner(mContext);
                        vh.svalue.setLayoutParams(lp);
                        addView(vh.name);
                        addView(vh.symbol);
                        addView(vh.svalue);
                    }
            }
        }
        a.recycle();
        vh.unit = new TextView(mContext);
        vh.unit.setLayoutParams(lp);
        addView(vh.unit);
        vh.unit.setTextAppearance(context, android.R.style.TextAppearance_Small);

    }

    public String getName() {
        return vh.name.getText().toString();
    }

    public void setName(String name) {
        vh.name.setText(name);
    }

    public String getSymbol() {
        return vh.symbol.getText().toString();
    }

    public void setSymbol(String symbol) {
        vh.symbol.setText(symbol);
    }

    public String getTextValue() {
        return vh.value.getText().toString();
    }

    public void setTextValue(String value) {
        vh.value.setText(value);
    }

    public void setUnit(String unit) {
        vh.unit.setText(unit);
    }

    public String getEditValue() {
        if (vh.evalue != null)
            return vh.evalue.getText().toString();
        return null;
    }

    public void setEditValue(String value) {
        vh.evalue.setText(value);
    }

    public String getSpinnerValue() {
        return (String) vh.svalue.getSelectedItem();
    }

    public void setSpinnerValue(String[] str) {
        if (vh.svalue != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, str);
            vh.svalue.setAdapter(adapter);
        }
    }

    class ViewHolder {
        public TextView name;
        public TextView symbol;
        public TextView value;
        public TextView unit;
        public EditText evalue;
        public Spinner svalue;
    }

}
