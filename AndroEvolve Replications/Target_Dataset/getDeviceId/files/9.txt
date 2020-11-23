package com.zss.library.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.UUID;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

/**
 * 设备工具类
 * 
 * @author zm
 * 
 */
public class DeviceUtils {

	/**
	 * 获取设备ID
	 * 
	 * @param context
	 * @return 机器码
	 */
	public static String getDeviceID(Context context) {
		TelephonyManager manager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return manager.getDeviceId();
	}

	/**
	 * 获取设备唯一UUID
	 * 
	 * @param mContext
	 * @return UUID
	 */
	public static String getUUID(Context mContext) {
		final TelephonyManager tm = (TelephonyManager) mContext
				.getSystemService(Context.TELEPHONY_SERVICE);
		final String tmDevice, tmSerial, androidId;
		tmDevice = "" + tm.getDeviceId();
		tmSerial = "" + tm.getSimSerialNumber();
		androidId = ""
				+ android.provider.Settings.Secure.getString(
						mContext.getContentResolver(),
						android.provider.Settings.Secure.ANDROID_ID);
		UUID deviceUuid = new UUID(androidId.hashCode(),
				((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
		return deviceUuid.toString().replaceAll("-", "");
	}

	/**
	 * 获取IMEI号，可能为空
	 * 
	 * @param mContext
	 * @return String
	 */
	public static String getIMEI(Context mContext) {
		if (mContext == null) {
			return "";
		}
		TelephonyManager telephonyManager = (TelephonyManager) mContext
				.getSystemService(Context.TELEPHONY_SERVICE);
		if (telephonyManager == null) {
			return "";
		}
		String imei = telephonyManager.getDeviceId();
		if (TextUtils.isEmpty(imei)) {
			imei = "";
		}
		return imei;
	}

}
