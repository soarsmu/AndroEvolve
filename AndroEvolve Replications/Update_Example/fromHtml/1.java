package com.soshified.soshified.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;

/**
 * Utility methods for text modification and formatting
 */
public class TextUtils {

    public TextUtils() {}

    public static String fromHtml(String originalString)
    {
    	Spanned spanned;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
        	spanned = Html.fromHtml(originalString, 0);
        }
        else {
        	spanned = Html.fromHtml(originalString);
        }
    	return spanned.toString().trim();
    }
    
    public static String formatStringRes(Context context, int resId, String[] strings)
    {
        return String.format(context.getString(resId), (Object)strings);
    }

    public static String validateImageUrl(String url)
    {
        return ( url != null && url.contains(" ") ) ? url.replaceAll(" ", "%20") : url;
    }
}
