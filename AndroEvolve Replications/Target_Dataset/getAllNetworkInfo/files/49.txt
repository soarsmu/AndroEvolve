package cl.softmedia.movillitar.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

public class WifiConexionManager {

    public static boolean hasInternet(FragmentActivity activity) {

        boolean hasConnectedWifi = false;
        boolean hasConnectedMobile = false;

        try {
            ConnectivityManager cmManager = (ConnectivityManager) activity
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo[] netInfo = cmManager.getAllNetworkInfo();
            for (NetworkInfo net : netInfo) {
                if (net.getTypeName().equalsIgnoreCase("wifi")) {
                    if (net.isConnected()) {
                        hasConnectedWifi = true;
                    }
                }
                if (net.getTypeName().equalsIgnoreCase("mobile")) {
                    if (net.isConnected()) {
                        hasConnectedMobile = true;
                    }
                }
            }
        } catch (Exception e) {

        }
        return hasConnectedWifi || hasConnectedMobile;

    }

    public static boolean hasInternet(Fragment fragment) {

        boolean hasConnectedWifi = false;
        boolean hasConnectedMobile = false;

        try {
            ConnectivityManager cmManager = (ConnectivityManager) fragment.getActivity()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo[] netInfo = cmManager.getAllNetworkInfo();
            for (NetworkInfo net : netInfo) {
                if (net.getTypeName().equalsIgnoreCase("wifi")) {
                    if (net.isConnected()) {
                        hasConnectedWifi = true;
                    }
                }
                if (net.getTypeName().equalsIgnoreCase("mobile")) {
                    if (net.isConnected()) {
                        hasConnectedMobile = true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hasConnectedWifi || hasConnectedMobile;

    }


    public static boolean hasInternet(Activity activity) {

        boolean hasConnectedWifi = false;
        boolean hasConnectedMobile = false;

        try {
            ConnectivityManager cmManager = (ConnectivityManager) activity
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo[] netInfo = cmManager.getAllNetworkInfo();
            for (NetworkInfo net : netInfo) {
                if (net.getTypeName().equalsIgnoreCase("wifi")) {
                    if (net.isConnected()) {
                        hasConnectedWifi = true;
                    }
                }
                if (net.getTypeName().equalsIgnoreCase("mobile")) {
                    if (net.isConnected()) {
                        hasConnectedMobile = true;
                    }
                }
            }
        } catch (Exception e) {

        }
        return hasConnectedWifi || hasConnectedMobile;

    }
}
