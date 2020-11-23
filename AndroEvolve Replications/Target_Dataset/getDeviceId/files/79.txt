package com.physical.utils;

import android.Manifest.permission;
import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;

import com.physical.entity.DeviceInfo;

import java.util.Locale;

public class TelephonyUtils {

    private static final String EMULATOR_IMEI = "000000000000000";

    public static String getImei(Context context) {
        String imei = null;
        try {
            imei = ((TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId()
                    .toLowerCase(Locale.getDefault());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imei == null ? null : imei.replaceAll("[^\\da-zA-Z]*", "");
    }

    public static boolean isEmulator(Context context) {
        boolean isEmulator = false;
        if ((Build.MODEL.equals("sdk")) || (Build.MODEL.equals("google_sdk"))) {
            isEmulator = true;
        } else {
            TelephonyManager tm = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            String imei = tm.getDeviceId();
            if (imei == null || imei.equalsIgnoreCase(EMULATOR_IMEI)) {
                isEmulator = true;
            }
        }
        return isEmulator;
    }

    static DeviceInfo.TelephonyInfo getTelephonyInfo(Context context) {
        DeviceInfo.TelephonyInfo telephonyInfo = new DeviceInfo.TelephonyInfo();
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        // GET IMEI
        String imei = telephonyManager.getDeviceId();
        telephonyInfo.imei = ((imei == null) ? null : imei.toLowerCase(
                Locale.getDefault()).replaceAll("[^\\da-zA-Z]*", ""));
        // GET IMSI
        telephonyInfo.imsi = telephonyManager.getSubscriberId();
        telephonyInfo.simSerialNumber = telephonyManager.getSimSerialNumber();
        telephonyInfo.networkOperator = telephonyManager.getNetworkOperator();
        // TODO 屏蔽获取电话号码，因获取电话号码涉及用户隐私
        // telephonyInfo.phoneNumber = telephonyManager.getLine1Number();
        telephonyInfo.networkCountryIso = telephonyManager
                .getNetworkCountryIso();

        // GET STATION INFO
        if (PermissionUtils.hasPermission(context,
                permission.ACCESS_FINE_LOCATION)
                || PermissionUtils.hasPermission(context,
                permission.ACCESS_COARSE_LOCATION)) {
            // 获取网络类型
            int type = telephonyManager.getNetworkType();
            // 在中国，移动的2G是EGDE，联通的2G为GPRS，电信的2G为CDMA，电信的3G为EVDO
            // 中国电信为CTC
            // NETWORK_TYPE_EVDO_A是中国电信3G的getNetworkType
            // NETWORK_TYPE_CDMA电信2G是CDMA
            if (type == TelephonyManager.NETWORK_TYPE_EVDO_A
                    || type == TelephonyManager.NETWORK_TYPE_CDMA
                    || type == TelephonyManager.NETWORK_TYPE_1xRTT) {
                telephonyInfo.stationNet = "CDMA | NETWORK_TYPE_EVDO_A | NETWORK_TYPE_CDMA | NETWORK_TYPE_1xRTT | "
                        + type;
                CdmaCellLocation location = (CdmaCellLocation) telephonyManager
                        .getCellLocation();
                if (location != null) {
                    telephonyInfo.stationCellId = location.getBaseStationId();
                    telephonyInfo.stationLac = location.getNetworkId();
                }

            }
            // 移动2G卡 + CMCC + 2
            // type = NETWORK_TYPE_EDGE
            // 联通的2G经过测试 China Unicom 1 NETWORK_TYPE_GPRS
            else if (type == TelephonyManager.NETWORK_TYPE_EDGE
                    || type == TelephonyManager.NETWORK_TYPE_GPRS) {
                telephonyInfo.stationNet = "GSM | NETWORK_TYPE_EDGE | NETWORK_TYPE_GPRS | "
                        + type;
                GsmCellLocation location = (GsmCellLocation) telephonyManager
                        .getCellLocation();
                if (location != null) {
                    telephonyInfo.stationCellId = location.getCid();
                    telephonyInfo.stationLac = location.getLac();
                }
            }
        }
        return telephonyInfo;
    }
}
