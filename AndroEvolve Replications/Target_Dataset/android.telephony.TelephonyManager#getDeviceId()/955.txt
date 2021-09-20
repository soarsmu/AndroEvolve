package com.yerdy.services.util;

import com.yerdy.services.logging.YRDLog;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

public class UDIDUtil {

	/**
	 * @author Chris
	 * 
	 *         Following permissions are needed to run new id version
	 *         <uses-permission
	 *         android:name="android.permission.READ_PHONE_STATE"/>
	 *         <uses-permission
	 *         android:name="android.permission.ACCESS_WIFI_STATE"/>
	 */

	public static String getUDIDNew(Context cxt) {

		String deviceId = polishResponse(getDeviceId(cxt));
		String droidId = polishResponse(getAndroidID(cxt));
		String macId = polishResponse(getMacAddress(cxt));
		String hash = DigestUtil.md5Hash(deviceId + droidId + macId);
		return "D" + hash;
	}

	/**
	 * @author Chris
	 * 
	 *         Following permissions are needed to run new id version
	 *         <uses-permission
	 *         android:name="android.permission.READ_PHONE_STATE"/>
	 */
	public static String getUDIDOld(Context cxt) {
		String udid = getDeviceId(cxt);
		if (udid == null || udid.trim().length() == 0)
			udid = getAndroidID(cxt);
		return udid;
	}

	public static String getDeviceId(Context cxt) {
		try {
			TelephonyManager tManager = (TelephonyManager) cxt.getSystemService(Context.TELEPHONY_SERVICE);
			String id = tManager.getDeviceId();
			return id;
		} catch (SecurityException e) {
			YRDLog.e(UDIDUtil.class, "Insufficient READ_PHONE_STATE Permissions");
			return "";
		}
	}

	public static String getAndroidID(Context cxt) {
		String androidID = Secure.getString(cxt.getContentResolver(), Secure.ANDROID_ID);
		return androidID;
	}

	public static String getMacAddress(Context cxt) {
		try {
			WifiManager wm = (WifiManager) cxt.getSystemService(Context.WIFI_SERVICE);
			String macAddress = wm.getConnectionInfo().getMacAddress();
			return macAddress.toUpperCase();
		} catch (SecurityException e) {
			YRDLog.e(UDIDUtil.class, "Insufficient ACCESS_WIFI_STATE Permissions");
		} catch (Exception e) {
		}
		return "";
	}

	private static String polishResponse(String str) {
		return (str == null) ? ("") : (str);
	}

}
