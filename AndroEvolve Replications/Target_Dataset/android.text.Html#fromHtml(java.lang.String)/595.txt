package defpackage;

import android.os.Build.VERSION;
import android.text.Html;
import android.text.Spanned;

/* renamed from: jrw reason: default package */
public final class jrw {
    public static Spanned a(String str) {
        if (VERSION.SDK_INT >= 24) {
            return Html.fromHtml(str, 0);
        }
        return Html.fromHtml(str);
    }
}
