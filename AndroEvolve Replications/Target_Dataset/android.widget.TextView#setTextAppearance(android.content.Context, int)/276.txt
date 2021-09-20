package com.zhiyicx.common.utils;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.widget.TextView;

import skin.support.content.res.SkinCompatResources;

/**
 * @Describe
 * @Author Jungle68
 * @Date 2017/6/28
 * @Contact master.jungle68@gmail.com
 */

public class SkinUtils {


    /**
     * 暂不开放
     * set text color for skin support .
     *
     * @param textView target view
     * @param resId    the color id to set
     */
//    @SuppressWarnings("ResourceAsColor")
//    public static void setCompatTextColor(TextView textView, int resId) {
//        textView.setTextColor(resId);
//
//    }

    public static void setTextColor(TextView textView, @ColorRes int resId) {
        textView.setTextColor(getColor(resId));
    }

    public static void setTextAppearance(TextView textView, int resId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            textView.setTextAppearance(resId);
        } else {
            textView.setTextAppearance(textView.getContext(), resId);
        }
    }

    public static int getColor(int resId) {
        return SkinCompatResources.getInstance().getColor(resId);
    }

    public Drawable getDrawable(int resId) {
        return SkinCompatResources.getInstance().getDrawable(resId);
    }

    public Drawable getMipmap(int resId) {
        return SkinCompatResources.getInstance().getDrawable(resId);
    }

    public ColorStateList getColorStateList(int resId) {
        return SkinCompatResources.getInstance().getColorStateList(resId);
    }
}
