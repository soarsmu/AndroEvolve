package com.shyky.library.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;

import com.shyky.library.BaseApplication;

/**
 * 设备相关工具类
 *
 * @author Copyright(C)2011-2017 Shyky Studio.
 * @version 1.1
 * @email sj1510706@163.com
 * @date 2017/1/2
 * @since 1.0
 */
public final class DeviceUtil {
    /**
     * 构造方法私有化
     */
    private DeviceUtil() {

    }

    public static String getDeviceId() {
        return getDeviceId(BaseApplication.getContext());
    }

    public static String getDeviceId(@NonNull Context context) {
        final TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }

    public static String getImei() {
        return getImei(BaseApplication.getContext());
    }

    public static String getImei(@NonNull Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static String getDeviceModel() {
        return android.os.Build.MODEL;
    }

    /**
     * 获取状态栏高度
     *
     * @param activity
     * @return
     */
    public static int getStatusBarHeight(@NonNull Activity activity) {
        final Rect localRect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
        int statusHeight = localRect.top;
        if (0 == statusHeight) {
            try {
                final Class<?> localClass = Class.forName("com.android.internal.R$dimen");
                Object localObject = localClass.newInstance();
                int i5 = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString());
                statusHeight = activity.getResources().getDimensionPixelSize(i5);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusHeight;
    }
}