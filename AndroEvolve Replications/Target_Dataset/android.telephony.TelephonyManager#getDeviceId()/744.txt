package com.yiyatech.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.location.LocationManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import com.yiyatech.utils.ext.MyDisplayMetrics;

import java.util.UUID;

/**
 * Created by zhangjia on 2016/5/12.
 */
public class DeviceUtils {
    public static boolean isGpsOpen(Context context) {
        LocationManager locationManager = (LocationManager) context.
                getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * 获取手机分辨率
     */
    public static String getDeviceResolution(Context context) {
        String width = String.valueOf(MyDisplayMetrics.getDisPlayMetrics().widthPixels);
        String height = String.valueOf(MyDisplayMetrics.getDisPlayMetrics().heightPixels);
        return width + "*" + height;
    }

    /**
     * 获取当前手机系统版本号
     *
     * @return 系统版本号
     */
    public static String getSystemVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * 获取手机型号
     *
     * @return 手机型号
     */
    public static String getSystemModel() {
        return android.os.Build.MODEL;
    }

    /**
     * 获取手机厂商
     *
     * @return 手机厂商
     */
    public static String getDeviceBrand() {
        return android.os.Build.BRAND;
    }

    /**
     * 获取手机IMEI(需要“android.permission.READ_PHONE_STATE”权限)
     *
     * @return 手机IMEI
     */
    @SuppressLint("MissingPermission")
    public static String getIMEI(Context ctx) {
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Activity.TELEPHONY_SERVICE);
        if (tm != null) {
            try {
                return tm.getDeviceId();
            } catch (Exception e) {
                return "ffffffffffffffffffffffff";
            }
        }
        return "ffffffffffffffffffffffff";
    }

    @SuppressLint("MissingPermission")
    public static String getDeviceId(Context context) {
        final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        return deviceUuid.toString();
    }
}
