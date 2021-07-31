package cl.fredyapp.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

public class Utils {
    /**TODO
     * MEJORAR PARA VERSIONES SUPERIORES DE ANDROID, PRODUCE UNA ERROR
     * @param ctx
     * @return
     */
    @SuppressLint("WrongConstant")
    public static String getIMEI(Context ctx) {
        String IMEI;
        TelephonyManager telephonyManager;
        if (Build.VERSION.SDK_INT >= 23) {
            if (ctx.checkSelfPermission("android.permission.READ_PHONE_STATE") == 0) {
                telephonyManager = (TelephonyManager) ctx.getSystemService("phone");
                assert telephonyManager != null;
                if (telephonyManager.getDeviceId() != null) {
                    IMEI = telephonyManager.getDeviceId();
                } else {
                    IMEI = Settings.Secure.getString(ctx.getContentResolver(), "android_id");
                }
            } else {
                IMEI = "";
            }
        } else {
            telephonyManager = (TelephonyManager) ctx.getSystemService("phone");
            assert telephonyManager != null;
            if (telephonyManager.getDeviceId() != null) {
                IMEI = telephonyManager.getDeviceId();
            } else {
                IMEI = Settings.Secure.getString(ctx.getContentResolver(), "android_id");
            }
        }


        return IMEI;
    }
}
