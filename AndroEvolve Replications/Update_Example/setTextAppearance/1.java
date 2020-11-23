package com.example.ahmed.popularmovies.utils;

import android.content.Context;
import android.os.Build;
import android.widget.TextView;

/**
 * Created by ahmed on 1/3/16.
 */
public class DynamicViewsCreator {
    public static TextView createLabel(Context context,int res)
    {
        TextView textView = new TextView(context);
        textView.setText(res);
        int resid = android.R.style.TextAppearance_Large;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        	resid = android.R.style.TextAppearance_Material_Large;
            textView.setTextAppearance(resid);
        }
        else {
            textView.setTextAppearance(context, resid);
        }

        return textView;
    }
}
