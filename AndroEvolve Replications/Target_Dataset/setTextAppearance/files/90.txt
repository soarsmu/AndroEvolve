package jp.mau.jitakukeibi.view;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TwoLinesCheckView extends LinearLayout {
	/** ViewGroup.LayoutParams.WRAP_CONTENT */
	private final static int 		WRAP_CONTENT 		= ViewGroup.LayoutParams.WRAP_CONTENT;
	/** ViewGroup.LayoutParams.MATCH_PARENT */
	@SuppressLint("InlinedApi")
	private final static int		MATCH_PARENT		= ViewGroup.LayoutParams.MATCH_PARENT;
	private Context 					_context;
	private LinearLayout 				_textLayer;
	private TextView 					_title;
	private TextView					_value;
	private CheckBox 					_box;
	private ArrayList<View>				_relatives;

	public TwoLinesCheckView(Context context) {
		super(context);
		this.setOrientation(LinearLayout.HORIZONTAL);
		this.setPadding(6,6,6,6);
		_context = context;
		_relatives = new ArrayList<View>();
		_textLayer = new LinearLayout (_context);
		_title = new TextView (_context);
		_value = new TextView (_context);
		_box = new CheckBox	(_context);

		_title.setTextAppearance(_context, android.R.style.TextAppearance_Large);
		_value.setTextAppearance(_context, android.R.style.TextAppearance_Small);

		_textLayer.setOrientation(LinearLayout.VERTICAL);
		_title.setGravity(Gravity.CENTER_VERTICAL);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
		_textLayer.addView(_title, params);
		params = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
		_textLayer.addView(_value, params);

		params = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT, 1);
		this.addView(_textLayer, params);

		_box.setGravity(Gravity.BOTTOM);
		_box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				setChecked(isChecked);
			}
		});
		params = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT, 0);
		this.addView(_box, params);
	}

	/** チェックする/はずす */
	public void setChecked (boolean checked) {
		_box.setChecked(checked);
		_textLayer.setEnabled(checked);
		_value.setTextAppearance(_context, (checked) ? android.R.style.TextAppearance_Small : android.R.style.TextAppearance_Small_Inverse);
		// 関連付けられたViewのEnableを変更
		for (int i = 0; i < _relatives.size(); i ++) {
			_relatives.get(i).setEnabled(checked);
		}
	}

	/**
	 * チェックボックスの状態を取得する
	 * @return チェックされているか
	 */
	public boolean isChecked () {
		return _box.isChecked();
	}

	/**
	 * タイトルを設定する
	 * @param str タイトル文字列
	 */
	public void setTitle (String str) {
		_title.setText(str);
	}

	/**
	 * タイトルを設定する
	 * @param id タイトルリソースID
	 */
	public void setTitle (int id) {
		_title.setText(_context.getString (id));
	}

	/**
	 * 値の文字列を設定する
	 * @param str 値文字列
	 */
	public void setValue (String str) {
		_value.setText(str);
	}

	/**
	 * 文字列部分をクリックした時の挙動を設定する
	 */
	public void setOnClickListener (View.OnClickListener listener) {
		_textLayer.setOnClickListener(listener);
	}

	/** チェックに他のViewも関連付ける */
	public void setRelatives (View view) {
		_relatives.add(view);
	}

	/** 利用可否を変更する */
	public void setEnabled (boolean enabled) {
		_textLayer.setEnabled(enabled);
		_box.setEnabled(enabled);
		if (enabled) {
			_value.setTextAppearance(_context, (_box.isChecked()) ? android.R.style.TextAppearance_Small : android.R.style.TextAppearance_Small_Inverse);
			_title.setTextAppearance(_context, android.R.style.TextAppearance_Large);
		} else {
			_value.setTextAppearance(_context,android.R.style.TextAppearance_Small_Inverse);
			_title.setTextAppearance(_context, android.R.style.TextAppearance_Large_Inverse);
		}
	}
}
