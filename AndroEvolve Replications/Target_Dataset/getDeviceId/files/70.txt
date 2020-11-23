package com.shoppin.customer.utils;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.UUID;

public class UniqueId {

	private static final String TAG = UniqueId.class.getSimpleName();

	public static String getUniqueId(Context context) {

		String uniqueId = null;
		String tmDevice = "";
		String tmSerial = "";
		String androidId = "";

		// Get id from TelephonyManager
		// might get null values in tablet
		try {
			TelephonyManager mTelephony = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			if (mTelephony != null) {
				if (mTelephony.getDeviceId() != null) {
					Log.e(TAG,
							"mTelephony.getDeviceId() = "
									+ mTelephony.getDeviceId());

					tmDevice = "" + mTelephony.getDeviceId();

				}

				if (mTelephony.getDeviceId() != null) {
					Log.e(TAG, "mTelephony.getSimSerialNumber() = "
							+ mTelephony.getSimSerialNumber());
					tmSerial = "" + mTelephony.getSimSerialNumber();
				}

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// ANDROID_ID
		// Not to be a unique
		try {
			androidId = ""
					+ android.provider.Settings.Secure.getString(
							context.getContentResolver(),
							android.provider.Settings.Secure.ANDROID_ID);
			Log.e(TAG, "androidId = " + androidId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Generate hash from above values
		// if TelephonyManager is going to return null and ANDROID_ID and also
		// then we will have hash of empty string
		try {
			UUID deviceUuid = new UUID(androidId.hashCode(),
					((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
			uniqueId = deviceUuid.toString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Log.e(TAG, "uniqueId = " + uniqueId);

		return uniqueId;
	}
}
