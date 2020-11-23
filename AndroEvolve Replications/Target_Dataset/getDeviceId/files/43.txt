package com.olaappathon.main;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

/**
 * The Class Platform.
 * 
 * @author Anki
 * @version $Revision: 1.0 $
 */
public class Platform {
	/**
	 * Method getProcessId.
	 * 
	 * @return int
	 */
	public static int getProcessId() {
		return android.os.Process.myPid();
	}

	public static String getDeviceId() {
		final TelephonyManager telephonyManager = (TelephonyManager) OlaAppathon.getContext().getSystemService(Context.TELEPHONY_SERVICE);
		String deviceId = telephonyManager.getDeviceId();

		if (deviceId != null) {

			return Integer.toHexString(deviceId.hashCode());
		} else {
			return "";
		}
	}

	/**
	 * Method getDeviceId.
	 * 
	 * @return String
	 */
	/*
	 * public static String getDeviceId() { String deviceId = ""; try { deviceId = Integer.toHexString(DeviceInfo.getDeviceId()); } catch (Exception e) {
	 * e.printStackTrace(); } return deviceId; }
	 *//**
	 * Method getStandardDeviceId.
	 * 
	 * @return String
	 */
	/*
	 * public static String getStandardDeviceId() {
	 * 
	 * String deviceId = "0"; try { final TelephonyManager telephonyManager = (TelephonyManager)
	 * SnapSecure.getContext().getSystemService(Context.TELEPHONY_SERVICE); deviceId = telephonyManager.getDeviceId(); } catch (Exception e) {
	 * e.printStackTrace(); deviceId = "0"; }
	 * 
	 * return deviceId;
	 * 
	 * }
	 *//**
	 * Method getAndroidId.
	 * 
	 * @return String
	 */
	/*
	 * public static String getAndroidId() { return Secure.getString(SnapSecure.getContext().getContentResolver(), Secure.ANDROID_ID); }
	 *//**
	 * Method getDeviceName.
	 * 
	 * @return String
	 */
	/*
	 * public static String getDeviceName() { return DeviceInfo.getDeviceName(); }
	 */
	/**
	 * Method getOSVersion.
	 * 
	 * @return String
	 */
	public static String getOSVersion() {
		return Build.VERSION.RELEASE;
	}

	/**
	 * Method getApiVersion.
	 * 
	 * @return int
	 */
	public static int getDeviceApiVersion() {
		return Build.VERSION.SDK_INT;
	}

	/**
	 * Method getOSName.
	 * 
	 * @return String
	 */
	public static String getOSName() {
		return "Android";
	}

	/**
	 * Method getDefaultEmail.
	 * 
	 * @return String
	 */
	/*
	 * public static String getDefaultEmail() { for (Account item : AccountManager.get(SnapSecure.getContext()).getAccounts()) { if
	 * (item.type.equals("com.google") && StringHelper.isValidEmail(item.name)) return item.name; }
	 * 
	 * return ""; }
	 *//**
	 * Method isSimulator.
	 * 
	 * @return boolean
	 */
	/*
	 * public static boolean isSimulator() { TelephonyManager manager = (TelephonyManager) SnapSecure.getContext().getSystemService(Context.TELEPHONY_SERVICE);
	 * String deviceId = manager.getDeviceId(); return (deviceId == null) || deviceId.equals("000000000000000"); }
	 */
	/**
	 * Method toPixel.
	 * 
	 * @param context
	 *            Context
	 * @param value
	 *            int
	 * 
	 * @return int
	 */
	public static int toPixel(Context context, int value) {
		final float scale = context.getResources().getDisplayMetrics().density;
		int pixels = (int) (value * scale + 0.5f);
		return pixels;
	}

	/**
	 * Method getIMSI.
	 * 
	 * @return String
	 */
	/*
	 * public static String getIMSI() { TelephonyManager manager = (TelephonyManager) SnapSecure.getContext().getSystemService(Context.TELEPHONY_SERVICE);
	 * return manager.getSubscriberId(); }
	 */
	/**
	 * Checks if is google play services available.
	 * 
	 * @param context
	 *            the context
	 * @return true, if is google play services available
	 */
	public static boolean isGooglePlayServicesAvailable(Context context) {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
		return (ConnectionResult.SUCCESS == resultCode);
	}
}
