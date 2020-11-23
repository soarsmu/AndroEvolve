package com.baisi.whatsfreecall.utils.deviceinfoutils;

import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import java.net.NetworkInterface;
import java.net.SocketException;

/**
 * Created by MnyZhao on 2018/2/9.
 */

public class DeviceInfoUtils {
    public static DeviceInfoUtils deviceInfoUtils;

    public static DeviceInfoUtils getDeviceInfoUtils(Context context) {
        if (deviceInfoUtils == null) {
            deviceInfoUtils = new DeviceInfoUtils();
        }
        return deviceInfoUtils;
    }

    /*Deviceid*/
    public String getDeivceId(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        final String deviceId = telephonyManager.getDeviceId();
        final String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        String uuid = "";
        if (deviceId != null) {
            uuid = deviceId;
        } else {
            if (!"9774d56d682e549c".equals(androidId)) {
                uuid = androidId;
            } else {
//                uuid = UUID.randomUUID().toString();
                uuid = getUUID(context);
            }
        }
        return uuid;
    }
    /*Imei*/
    public String getImei(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager!=null) {
            if(telephonyManager.getDeviceId()!=null){
                return telephonyManager.getDeviceId();
            }
        }
        return "null";
    }
    /*imsi*/
    public  String getImsi(Context context){
        TelephonyManager telephonyManager= (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager!=null) {
            if(telephonyManager.getSubscriberId()!=null){
                return telephonyManager.getSubscriberId();
            }
        }
        return "null";
    }
    /*wifi MAC*/
    public String getMacAddress() {
        String macAddress = null;
        StringBuffer buf = new StringBuffer();
        NetworkInterface networkInterface = null;
        try {
            networkInterface = NetworkInterface.getByName("eth1");
            if (networkInterface == null) {
                networkInterface = NetworkInterface.getByName("wlan0");
            }
            if (networkInterface == null) {
                return "02:00:00:00:00:02";
            }
            byte[] addr = networkInterface.getHardwareAddress();
            for (byte b : addr) {
                buf.append(String.format("%02X:", b));
            }
            if (buf.length() > 0) {
                buf.deleteCharAt(buf.length() - 1);
            }
            macAddress = buf.toString();
        } catch (SocketException e) {
            e.printStackTrace();
            return "02:00:00:00:00:02";
        }
        return macAddress;
    }

    /*getAndroidId*/
    public String getAndroidID(Context context) {
        if (Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID)==null) {
            return "null";
        }
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

    }

    /*getuuid*/
    public String getUUID(Context context) {
        DeviceUuidFactory deviceUuidFacto = new DeviceUuidFactory(context);
        return deviceUuidFacto.getDeviceUuid().toString();
    }
}
