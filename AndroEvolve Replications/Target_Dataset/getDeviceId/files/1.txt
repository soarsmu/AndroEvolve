package com.adid_service.external_lib.external_code_lib.IdGenerators;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

import com.adid_service.external_lib.external_code_lib.GUID.GUID;

import static android.content.Context.TELEPHONY_SERVICE;

public class MixIDGenerator extends IDGenerator implements IGenerator {
    @SuppressLint("MissingPermission")
    @Override
    public GUID generateId() {
        String devicIMEI1 = "none";
        String devicIMEI2 = "none";
        String devicIMEI3 = "none";
        String androidID  = "none";


        if (context != null) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            } else {
                devicIMEI1 = telephonyManager.getDeviceId();
                devicIMEI2 = telephonyManager.getDeviceId();
                devicIMEI3 = telephonyManager.getDeviceId();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    devicIMEI1 = telephonyManager.getPhoneCount() >= 1  ? telephonyManager.getDeviceId(1) : "none";
                    devicIMEI2 = telephonyManager.getPhoneCount() >= 2  ? telephonyManager.getDeviceId(2) : "none";
                    devicIMEI3 = telephonyManager.getPhoneCount() >= 3  ? telephonyManager.getDeviceId(3) : "none";
                }
            }
            androidID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        }

        String pseudoID = "" + Build.BRAND + "" + Build.DEVICE + "" + Build.MANUFACTURER + "" + Build.MODEL + "" + Build.PRODUCT;

        GUID id = new GUID("fake_gaid_" + (devicIMEI1 + devicIMEI2 + devicIMEI3 + androidID + pseudoID).hashCode());
        return id;
    }
}
