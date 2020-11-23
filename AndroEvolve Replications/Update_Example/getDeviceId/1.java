package test.draw.com.vascog.Others;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.List;

import test.draw.com.vascog.System.PhoneInfo;
import test.draw.com.vascog.System.VasCogApplication;

public class SystemFunc {
    public static boolean isApplicationSentToBackground(final Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    public static void SetPhoneInfo() throws SecurityException{
        try {
            PhoneInfo phoneInfo = new PhoneInfo();
            phoneInfo.setApp(VasCogApplication.getInstance().getPackageName());
            phoneInfo.setOs_version(android.os.Build.VERSION.RELEASE);
            phoneInfo.setVendor(new String(android.os.Build.BRAND.getBytes(), "UTF-8"));
            phoneInfo.setModel(android.os.Build.MODEL);
            phoneInfo.setAndroid_id(Settings.System.getString(VasCogApplication.getInstance().getContentResolver(), Settings.System.ANDROID_ID));
            Context context = VasCogApplication.getInstance();
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE ) == PackageManager.PERMISSION_GRANTED )
            {
            	String imei = "";
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    imei = tm.getImei();
                    phoneInfo.setImei(imei);
                } else {
                    imei = tm.getDeviceId();
                    phoneInfo.setImei(imei);
                }
            }else{
                phoneInfo.setImei(null);
            }
            VasCogApplication.setPhoneInfo(phoneInfo);

            Log.d("TestPhoneInfo", "App : " + phoneInfo.getApp());
            Log.d("TestPhoneInfo", "Os Version : " + phoneInfo.getOs_version());
            Log.d("TestPhoneInfo", "Vendor : " + phoneInfo.getVendor());
            Log.d("TestPhoneInfo", "Model : " + phoneInfo.getModel());
            Log.d("TestPhoneInfo", "Android id : " + phoneInfo.getAndroid_id());
            Log.d("TestPhoneInfo", "IMEI : " + phoneInfo.getImei());

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
