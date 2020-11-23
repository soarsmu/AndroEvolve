package com.component.preject.youlong.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import com.component.preject.youlong.base.BaseApplication;

import java.lang.reflect.Method;
import java.util.UUID;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.TELEPHONY_SERVICE;

/**
 * @Author: xiezhenggen
 * @Time: 2018 2018/12/4 15:46
 * @description: （设备参数）
 */
public class SystemDeviceParameter {
    /**
     * 获取设备ID
     */
    @SuppressLint("MissingPermission")
    public static String getDeviceId() {
        // 获取IMEI
        String getDeviceId = "";
        try {
            TelephonyManager telephonyManager = (TelephonyManager) BaseApplication.getContext().getSystemService(TELEPHONY_SERVICE);
            getDeviceId = telephonyManager.getDeviceId();
        } catch (SecurityException e) {
            e.printStackTrace();
            LogUtils.i("YYYY","getDeviceId=="+e.getMessage());
        }
        LogUtils.i("YYYY","getDeviceId=="+getDeviceId);

        return getDeviceId;
    }

    /**
     * 通过反射获取IMEI
     *
     * @return
     */
    public static String getMachineImei() {
        TelephonyManager manager = (TelephonyManager) BaseApplication.getContext().getSystemService(TELEPHONY_SERVICE);
        Class clazz = manager.getClass();
        String imei = "";
        try {
            Method getImei = clazz.getDeclaredMethod("getImei", int.class);
            //(int slotId) getImei.setAccessible(true);
            imei = (String) getImei.invoke(manager);
        } catch (Exception e) {
        }
        return imei;
    }


    /**
     * 获取手机IMSI
     */
    public static String getIMSI(Context context) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            //获取IMSI号
            @SuppressLint("MissingPermission") String imsi = telephonyManager.getSubscriberId();
            if (null == imsi) {
                imsi = "";
            }
            return imsi;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    /**
     * 获取吉列车机的唯一标示符
     *
     * @return
     */
    public static String getTuid() {
        return Settings.System.getString(BaseApplication.getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * 获取Mac地址
     *
     * @return
     */
    public static String getMacAddress() {
        WifiManager wm = (WifiManager) BaseApplication.getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        String m_szWLANMAC = wm.getConnectionInfo().getMacAddress();
        return wm.getConnectionInfo().getMacAddress();
    }

    //获取手机的唯一标识
    public static String getPhoneSign() {
        StringBuilder deviceId = new StringBuilder();
        // 渠道标志
        deviceId.append("a");
        try {
            //IMEI（imei）
            TelephonyManager tm = (TelephonyManager) BaseApplication.getContext().getSystemService(TELEPHONY_SERVICE);
            @SuppressLint("MissingPermission") String imei = tm.getDeviceId();
            if (!TextUtils.isEmpty(imei)) {
                deviceId.append("imei");
                deviceId.append(imei);
                return deviceId.toString();
            }
            //序列号（sn）
            @SuppressLint("MissingPermission") String sn = tm.getSimSerialNumber();
            if (!TextUtils.isEmpty(sn)) {
                deviceId.append("sn");
                deviceId.append(sn);
                return deviceId.toString();
            }
            //如果上面都没有， 则生成一个id：随机码
            String uuid = getUUID();
            if (!TextUtils.isEmpty(uuid)) {
                deviceId.append("id");
                deviceId.append(uuid);
                return deviceId.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            deviceId.append("id").append(getUUID());
        }
        return deviceId.toString();
    }

    /**
     * 得到全局唯一UUID
     */

    public static String getUUID() {
        String uuid = "";
        SharedPreferences mShare = BaseApplication.getContext().getSharedPreferences("uuid", MODE_PRIVATE);
        if (mShare != null) {
            uuid = mShare.getString("uuid", "");
        }
        if (TextUtils.isEmpty(uuid)) {
            uuid = UUID.randomUUID().toString();
            mShare.edit().putString("uuid", uuid).commit();
        }
        return uuid;
    }

    public static String getLine1Number() {
        //获取手机号码
        TelephonyManager tm = (TelephonyManager) BaseApplication.getContext().getSystemService(TELEPHONY_SERVICE);
        @SuppressLint("MissingPermission") String deviceid = tm.getDeviceId();//获取智能设备唯一编号
        @SuppressLint("MissingPermission") String te1 = tm.getLine1Number();//获取本机号码
        @SuppressLint("MissingPermission") String imei = tm.getSimSerialNumber();//获得SIM卡的序号
        @SuppressLint("MissingPermission") String imsi = tm.getSubscriberId();//得到用户Id
        LogUtils.i("TAG", "deviceid=" + deviceid + "  te1=" + te1 + "  imei=" + imei + "  imsi=" + imsi);
        return te1;
    }

    /**
     * 获取手机IMEI
     *
     * @return
     */
    public static final String getIMEI() {
        try {
            //实例化TelephonyManager对象
            TelephonyManager telephonyManager = (TelephonyManager) BaseApplication.getContext().getSystemService(TELEPHONY_SERVICE);
            //获取IMEI号
            @SuppressLint("MissingPermission") String imei = telephonyManager.getDeviceId();
            //在次做个验证，也不是什么时候都能获取到的啊
            if (imei == null) {
                imei = "";
            }
            return imei;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }

    /**
     * 获取手机IMSI
     */
    public static String getIMSI() {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) BaseApplication.getContext().getSystemService(TELEPHONY_SERVICE);
            //获取IMSI号
            @SuppressLint("MissingPermission") String imsi = telephonyManager.getSubscriberId();
            if (null == imsi) {
                imsi = "";
            }
            return imsi;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
