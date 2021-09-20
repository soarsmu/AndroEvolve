package com.hilfritz.mvp.util;

import android.text.Html;
import android.text.Spanned;

/**
 * Created by Hilfritz Camallere on 13/3/17.
 * PC name herdmacbook1
 */

public class HtmlUtil {

    /**
     * http://stackoverflow.com/questions/37904739/html-fromhtml-deprecated-in-android-n
     * @param html String
     * @return
     */
    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html){
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html,Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }
}
