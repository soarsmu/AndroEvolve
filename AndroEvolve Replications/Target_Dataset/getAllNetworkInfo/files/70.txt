package com.kayac.lobi.libnakamap.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectivityUtil {
    public static boolean isWifi(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        if (connectivityManager != null) {
            for (NetworkInfo info : connectivityManager.getAllNetworkInfo()) {
                if (1 == info.getType() && info.isConnected()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        if (connectivityManager == null) {
            return false;
        }
        for (NetworkInfo info : connectivityManager.getAllNetworkInfo()) {
            if (info.isConnected() && info.isAvailable()) {
                return true;
            }
        }
        return false;
    }
}
