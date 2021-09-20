package com.ludovic.crespeau.tabswithswitchtab;

/**
 * Created by crespeau on 24/11/2016.
 */

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class SecondTab extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TextView tv = new TextView(getActivity());
        tv.setText("Second Tab");
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(Color.WHITE);
        tv.setWidth(LayoutParams.MATCH_PARENT);
        tv.setHeight(LayoutParams.MATCH_PARENT);
        tv.setBackgroundColor(Color.YELLOW);
        tv.setTextAppearance(getActivity(),
                android.R.style.TextAppearance_Large);
        return tv;
    }

}