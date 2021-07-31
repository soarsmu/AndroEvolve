package com.nguyencuong.truyenfull.util;

import android.os.Build;
import android.text.Html;

public class TextUtils {

    @SuppressWarnings("deprecation")
    public static CharSequence styleTextHtml(String s) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(s, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(s);
        }
    }
}
