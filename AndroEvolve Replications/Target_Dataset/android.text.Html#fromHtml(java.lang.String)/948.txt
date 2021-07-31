package example.com.fan.utils;

import android.text.Html;
import android.widget.TextView;

/**
 * Created by lian on 2017/5/25.
 */
public class TextViewColorUtils {
    /**
     * 改变字体颜色方法;
     *
     * @param tv    view控件;
     * @param cstr  改变的字符串；
     * @param ostr  不改变的字符串;
     * @param color 改变字符串的颜色;
     */
    public static void setTextColor(TextView tv, String cstr, String ostr, String color) {

        String str = "<font color='" + color + "'>" + cstr + "</font>" + ostr;
        tv.setText(Html.fromHtml(str));
    }

    /**
     * 改变字体颜色方法(同时缩小不改变颜色字符串的字体大小);
     *
     * @param tv
     * @param cstr
     * @param ostr
     * @param color
     */
    public static void setTextColor1(TextView tv, String cstr, String ostr, String color) {

        String str = "<font color='" + color + "'>" + cstr + " <small>" + ostr + "</small></font>";
        tv.setText(Html.fromHtml(str));
    }

    public static void setTextColor2(TextView tv, String cstr, String ostr, String color) {

        String str = cstr + "<font color='" + color + "'>" + " <small>" + ostr + "</small></font>";
        tv.setText(Html.fromHtml(str));
    }

}
