package suventure.nikhil.com.profile;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;
import android.util.Log;

public class CheckWifiAndMobileData
{
    private static String TAG="CheckWifiAndMobileData";
    public static boolean IsConnected(Context context)
    {
        ConnectivityManager connectivity = (ConnectivityManager)context. getSystemService(Context.CONNECTIVITY_SERVICE);
        int type=ConnectivityManager.TYPE_WIFI;
        NetworkInfo[] info = connectivity.getAllNetworkInfo();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            NetworkRequest.Builder request = new NetworkRequest.Builder();
            request.addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
            //request.addTransportType(NetworkCapabilities.TRANSPORT_WIFI);

            connectivity.registerNetworkCallback(request.build(), new ConnectivityManager.NetworkCallback() {

                @Override
                public void onAvailable(Network network) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        ConnectivityManager.setProcessDefaultNetwork(network);
                    }
                }
            });
        }
        //ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            //NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
            {
                for (int i = 0; i < info.length; i++)
                {
                    //Log.v("MyTag",info[i].toString());
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean IsWiFiConnected(Context context)
    {
        Log.v(TAG,"ISWiFiConnected");
        final ConnectivityManager connManager = (ConnectivityManager)context. getSystemService(Context.CONNECTIVITY_SERVICE);
        int type=ConnectivityManager.TYPE_WIFI;
        NetworkInfo[] info = connManager.getAllNetworkInfo();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

        NetworkRequest.Builder request = new NetworkRequest.Builder();
            request.removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
        request.addTransportType(NetworkCapabilities.TRANSPORT_WIFI);

            connManager.registerNetworkCallback(request.build(), new ConnectivityManager.NetworkCallback() {

            @Override
            public void onAvailable(Network network) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                    ConnectivityManager.setProcessDefaultNetwork(network);
                }
            }
        });
        }

        if (info != null)
        {
            for (int i = 0; i < info.length; i++)
            {
                if (info[i].getType() == type)
                {
                    return true;
                }
            }
        }

        return false;
    }


}
