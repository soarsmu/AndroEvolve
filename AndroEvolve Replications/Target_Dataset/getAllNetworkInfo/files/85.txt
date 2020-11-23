package edu.neu.android.wocketslib.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkMonitor {
	private static final String TAG = "NetworkMonitor";
	
	// TODO cleanup this method and the next one to avoid duplication
    //TODO This entire method needs revamping 
    //There is a bug in Android that causes isConnected to fail sometimes without toggling wifi 
	public static void logNetworkStatus(Context aContext)
	{
	    boolean haveConnectedWifi = false;
	    boolean haveConnectedMobile = false;

	    ConnectivityManager connectivityManager = (ConnectivityManager) aContext.getSystemService(Context.CONNECTIVITY_SERVICE);
	   	    
	    NetworkInfo[] netInfo = connectivityManager.getAllNetworkInfo();
	    for (NetworkInfo ni : netInfo) {
	    	// This can fail on some phones (e.g. yifei's phone) 
	        if (ni.getType() == ConnectivityManager.TYPE_WIFI)
	            if (ni.isConnected())
	                haveConnectedWifi = true;
	        if (ni.getType() == ConnectivityManager.TYPE_MOBILE)
	            if (ni.isConnected())
	                haveConnectedMobile = true;
	    }
	    	    
	    NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
	    boolean isConnected = ((activeNetwork != null) && activeNetwork.isConnectedOrConnecting());
	    
	    Log.o(TAG, (isConnected ? "isNetwork" : "noNetwork"), (haveConnectedWifi ? "WIFI" : "noWIFI"), (haveConnectedMobile ? "MobileData" : "noMobileData")); 	    
	}

	public static boolean isNetworkAvailable(Context aContext) 
	{
	    boolean haveConnectedWifi = false;
	    boolean haveConnectedMobile = false;

	    ConnectivityManager connectivityManager = (ConnectivityManager) aContext.getSystemService(Context.CONNECTIVITY_SERVICE);
//	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo(); 
	    
	    
	    NetworkInfo[] netInfo = connectivityManager.getAllNetworkInfo();
	    //connectivityManager.getActiveNetworkInfo(); 
	    for (NetworkInfo ni : netInfo) {
	    	// This can fail on some phones (e.g. yifei's phone) 
	        if (ni.getType() == ConnectivityManager.TYPE_WIFI)
	            if (ni.isConnected())
	                haveConnectedWifi = true;
	        if (ni.getType() == ConnectivityManager.TYPE_MOBILE)
	            if (ni.isConnected())
	                haveConnectedMobile = true;
	    }
	    
//	    if (Globals.IS_DEBUG)
//	    	Log.d(TAG, "NETWORK STATUS. WIFI: " + haveConnectedWifi + " MOBILE: " + haveConnectedMobile);  


	    //TODO This entire method needs revamping 
	    //There is a bug in Android that causes isConnected to fail sometimes without toggling wifi 
	    
	    NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
	    boolean isConnected = ((activeNetwork != null) && activeNetwork.isConnectedOrConnecting());
	    	    
	    return isConnected || haveConnectedWifi || haveConnectedMobile;	
//		return activeNetworkInfo != null;
	} 

	
	public static boolean isWIFIAvailable(Context aContext) 
	{
		ConnectivityManager connectivityManager = (ConnectivityManager) aContext.getSystemService(Context.CONNECTIVITY_SERVICE);
	    
	    NetworkInfo[] netInfo = connectivityManager.getAllNetworkInfo();
	    for (NetworkInfo ni : netInfo) {
	        if (ni.getTypeName().equalsIgnoreCase("WIFI"))
	            if (ni.isConnected())
	            	return true; 
	    }
	    return false; 
	} 

	public static boolean isMobileAvailable(Context aContext) 
	{
		ConnectivityManager connectivityManager = (ConnectivityManager) aContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		    
		NetworkInfo[] netInfo = connectivityManager.getAllNetworkInfo();
		for (NetworkInfo ni : netInfo) {
			if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
		         if (ni.isConnected())
		          	return true;
		}
		return false; 
	} 
}
