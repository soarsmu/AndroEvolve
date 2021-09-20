package com.cyou.common.utils;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/**
 * Description:
 * Copyright  : Copyright (c) 2015
 * Company    : 北京畅游天下网络科技有限公司
 * Author     : liujianguang
 * Date       : 2016/1/6 11:34
 */
public class DeviceUtils {

    private static TelephonyManager telephonyManager;
    
    public static String getIMEI(Context context) {
        if (telephonyManager == null) {
            telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        }
        return telephonyManager.getDeviceId();
    }

    public static String getDeviceInfo(Context context) {
        try {
            org.json.JSONObject json = new org.json.JSONObject();
            android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);

            String device_id = tm.getDeviceId();

            android.net.wifi.WifiManager wifi =
                    (android.net.wifi.WifiManager) context.getSystemService(Context.WIFI_SERVICE);

            String mac = wifi.getConnectionInfo().getMacAddress();
            json.put("mac", mac);

            if (TextUtils.isEmpty(device_id)) {
                device_id = mac;
            }

            if (TextUtils.isEmpty(device_id)) {
                device_id = android.provider.Settings.Secure.getString(context.getContentResolver(),
                        android.provider.Settings.Secure.ANDROID_ID);
            }

            json.put("device_id", device_id);

            return json.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getSN(Context context) {
        if (telephonyManager == null) {
            telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        }
        return telephonyManager.getSimSerialNumber();
    }

    public static String getPhoneModel() {

        return android.os.Build.MODEL + ","
                + android.os.Build.VERSION.SDK_INT + ","
                + android.os.Build.VERSION.RELEASE;
    }

    /**
     * deviceID的组成为：渠道标志+识别符来源标志+hash后的终端识别符
     * <p/>
     * 渠道标志为：
     * 1，andriod（a）
     * <p/>
     * 识别符来源标志：
     * 1， wifi mac地址（wifi）；
     * 2， IMEI（imei）；
     * 3， 序列号（sn）；
     * 4， id：随机码。若前面的都取不到时，则随机生成一个随机码，需要缓存。
     *
     * @param context
     * @return
     */
    public static String getDeviceId(Context context) {
        StringBuilder deviceId = new StringBuilder();
        // 渠道标志
        deviceId.append("a");
        try {
            //wifi mac地址
            WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifi.getConnectionInfo();
            String wifiMac = info.getMacAddress();
            if (!TextUtils.isEmpty(wifiMac)) {
                deviceId.append("wifi");
                deviceId.append(wifiMac);
                LogUtils.e("getDeviceId : ", deviceId.toString());
                return deviceId.toString();
            }

            //IMEI（imei）
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String imei = tm.getDeviceId();
            if (!TextUtils.isEmpty(imei)) {
                deviceId.append("imei");
                deviceId.append(imei);
                LogUtils.e("getDeviceId : ", deviceId.toString());
                return deviceId.toString();
            }

            //序列号（sn）
            String sn = tm.getSimSerialNumber();
            if (!TextUtils.isEmpty(sn)) {
                deviceId.append("sn");
                deviceId.append(sn);
                LogUtils.e("getDeviceId : ", deviceId.toString());
                return deviceId.toString();
            }

//            //如果上面都没有， 则生成一个id：随机码
//            String uuid = getUUID(context);
//            if (!TextUtils.isEmpty(uuid)) {
//                deviceId.append("id");
//                deviceId.append(uuid);
//                LogUtils.e("getDeviceId : ", deviceId.toString());
//                return deviceId.toString();
//            }
        } catch (Exception e) {
            e.printStackTrace();
//            deviceId.append("id").append(getUUID(context));
        }

        LogUtils.e("getDeviceId : ", deviceId.toString());

        return deviceId.toString();

    }


//    /**
//     * 得到全局唯一UUID
//     */
//    public static String getUUID(Context context) {
//        String uuid = null;
//        SharedPreferences mShare = getSysShare(context, "sysCacheMap");
//        if (mShare != null) {
//            uuid = mShare.getString("uuid", "");
//        }
//
//        if (TextUtils.isEmpty(uuid)) {
//            uuid = UUID.randomUUID().toString();
//            saveSysMap(context, "sysCacheMap", "uuid", uuid);
//        }
//
//        LogUtils.e("getDeviceId", "getUUID : " + uuid);
//        return uuid;
//    }

}
