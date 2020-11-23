package com.indtel.mcf.utils.security;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.lang.ref.WeakReference;

/**
 * Author       : Arvindo Mondal
 * Created on   : 17-06-2019
 * Email        : arvindo@aiprog.in
 * Company      : AIPROG
 * Designation  : Programmer
 * About        : I am a human can only think, I can't be a person like machine which have lots of memory and knowledge.
 * Quote        : No one can measure limit of stupidity but stupid things bring revolutions
 * Strength     : Never give up
 * Motto        : To be known as great Mathematician
 * Skills       : Algorithms and logic
 * Website      : www.aiprog.in
 */
public class DeviceId {

    private static final int REQUEST_READ_PHONE_STATE = 10;

    private DeviceId(){}

    public static String GetDeviceId(WeakReference<Context> contextReference){
        return getDeviceId(contextReference);
//        return androidId(contextReference.get());
    }

    @SuppressLint("HardwareIds")
    private static String getDeviceId(WeakReference<Context> contextReference) {

        int permissionCheck = ContextCompat.checkSelfPermission(contextReference.get(),
                Manifest.permission.READ_PHONE_STATE);

        Context context = contextReference.get();
        Activity activity = (Activity) context;
        String deviceId = "";

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    REQUEST_READ_PHONE_STATE);
        } else {
            //TODO
            TelephonyManager telephonyManager = (TelephonyManager)
                    contextReference.get().getSystemService(Context.TELEPHONY_SERVICE);
//            deviceId = telephonyManager.getDeviceId();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                deviceId = telephonyManager.getImei();
            } else {
                deviceId = telephonyManager.getDeviceId();
            }
        }

//        String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        return deviceId;
    }

    @SuppressLint("HardwareIds")
    private String getIMEI(Context context) {

        String IMEI = "";

        int permissionCheck = ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_PHONE_STATE);

        Activity activity = (Activity) context;

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    REQUEST_READ_PHONE_STATE);
        } else {
            //TODO
            TelephonyManager telephonyManager = (TelephonyManager)
                    context.getSystemService(Context.TELEPHONY_SERVICE);
            IMEI = telephonyManager.getDeviceId();
        }

//        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//        IMEI = "IMEI:"+telephonyManager.getDeviceId();

        return IMEI;
    }

    @SuppressLint("HardwareIds")
    private static String androidId(Context context){
        return Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    private static boolean CheckPermission(Context context, String Permission) {
        return ContextCompat.checkSelfPermission(context,
                Permission) == PackageManager.PERMISSION_GRANTED;
    }
}
