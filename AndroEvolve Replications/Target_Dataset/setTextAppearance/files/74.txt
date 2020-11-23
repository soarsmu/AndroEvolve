package org.imogene.android.widget;

import org.imogene.android.database.ImogBeanCursor;
import org.imogene.android.template.R;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ImogBeanCursorAdapter extends CursorAdapter {

	private final Drawable mColor;
	private final int mLayoutId;

	public ImogBeanCursorAdapter(Context context, Cursor c, Drawable color, int layoutId) {
		super(context, c);
		mColor = color;
		mLayoutId = layoutId;
	}

	public ImogBeanCursorAdapter(Context context, Cursor c, Drawable color, boolean multiple) {
		this(context, c, color, multiple ? R.layout.imog__entity_row_multiple : R.layout.imog__entity_row);
	}

	public String getItemStringId(int position) {
		ImogBeanCursor<?> c = (ImogBeanCursor<?>) getItem(position);
		if (c != null) {
			return c.getId();
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		// Good old trick, we need to return something for ListView.CHOICE_MODE_MULTIPLE to work in ListAdapter
		return position;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ImogBeanCursor<?> c = (ImogBeanCursor<?>) cursor;

		view.findViewById(android.R.id.background).setBackgroundDrawable(mColor);

		TextView main = (TextView) view.findViewById(android.R.id.text1);
		TextView secondary = (TextView) view.findViewById(android.R.id.text2);

		main.setText(null);
		secondary.setText(null);

		if (c.getFlagRead()) {
			view.setBackgroundResource(android.R.drawable.list_selector_background);
			main.setTextAppearance(context, android.R.style.TextAppearance_Medium);
			main.setTypeface(Typeface.DEFAULT);
			secondary.setTextAppearance(context, android.R.style.TextAppearance_Small);
			secondary.setTypeface(Typeface.DEFAULT);
		} else {
			view.setBackgroundResource(R.drawable.imog__list_selector_background_inverse);
			main.setTextAppearance(context, android.R.style.TextAppearance_Medium_Inverse);
			main.setTypeface(Typeface.DEFAULT_BOLD);
			secondary.setTextAppearance(context, android.R.style.TextAppearance_Small_Inverse);
			secondary.setTypeface(Typeface.DEFAULT_BOLD);
		}

		ImageView icon = (ImageView) view.findViewById(android.R.id.icon);
		if (icon != null) {
			icon.setImageResource(android.R.drawable.stat_notify_sync);
			icon.setVisibility(c.getFlagSynchronized() ? View.GONE : View.VISIBLE);
		}

		main.setText(c.getMainDisplay(context));
		String sec = c.getSecondaryDisplay(context);
		if (TextUtils.isEmpty(sec.trim())) {
			secondary.setVisibility(View.GONE);
		} else {
			secondary.setVisibility(View.VISIBLE);
			secondary.setText(sec);
		}
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);
		return inflater.inflate(mLayoutId, parent, false);
	}
}
