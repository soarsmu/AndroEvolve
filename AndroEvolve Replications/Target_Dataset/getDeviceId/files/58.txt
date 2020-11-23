package com.example.customview;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

import java.lang.reflect.Method;


public class DeviceInfo {
    private static final String TAG = "DeviceInfo";

    public static void reqPermission(Activity activity){
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_PHONE_STATE}, 0);
    }
    public static String getImeiNew(Context context) {
        String imei = null;

        try {
            if (context != null) {
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                int state= PermissionChecker.checkSelfPermission(context,
                        "android.permission.READ_PHONE_STATE");
                Log.i(TAG, "getImeiNew: state = "+ state);
                if (telephonyManager != null &&
                        state == PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= 26) {
                        try {
                            Method method = telephonyManager.getClass().getMethod("getImei", new Class[0]);
                            method.setAccessible(true);
                            imei = (String) method.invoke(telephonyManager, new Object[0]);
                            Log.i(TAG, "getImeiNew: 反射 获取 imei = "+ imei);
                        } catch (Exception e) {
                            Log.w(TAG, "getImeiNew: 11", e);
                        }
                        if (TextUtils.isEmpty(imei)) {
                            imei = telephonyManager.getDeviceId();
                            Log.i(TAG, "getImeiNew: getDeviceId = "+ imei);
                        }
                    } else {
                        imei = telephonyManager.getDeviceId();
                        Log.i(TAG, "getImeiNew: 小26的 getDeviceId() = "+ imei);
                    }
                }
            }
        } catch (Exception e) {
            Log.w(TAG, "getImeiNew: 22 ", e);
        }

        return imei;
    }
}
