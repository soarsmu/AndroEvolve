package com.min.framework.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.UUID;

/**
 * Created by cg123 on 2015/8/20.
 */
public class DeviceId {
    public static String getDeviceId(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String uuid;

        uuid = sp.getString("device_id", null);
        if (uuid != null)
            return uuid;

        try {
            uuid = getUniqueId(context);
        } catch (Exception e) {
            Log.e(DeviceId.class.getName(), e.getMessage(), e);
            uuid = UUID.randomUUID().toString();
        }
        String md5 = AESUtil.md5(uuid);
        sp.edit().putString("device_id", md5).apply();

        return md5;
    }

    private static String getUniqueId(Context context) {
        final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

//        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
//        String deviceId = deviceUuid.toString();
        return tmDevice + tmSerial + androidId;
    }
}
