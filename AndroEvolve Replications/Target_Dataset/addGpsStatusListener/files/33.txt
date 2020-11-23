package com.Android.CodeInTheAir.Device.Query;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import com.Android.CodeInTheAir.Global.AppContext;
import com.Android.CodeInTheAir.Types.LocationInfo;
import com.Android.CodeInTheAir.Types.SatelliteInfo;

public class Sensor_GPS 
{
	private static LocationManager locationManager;
	
	private static final int QUICK_PERIOD = 1000;
	
	public static void init()
    {
    	locationManager = (LocationManager)AppContext.context.getSystemService(Context.LOCATION_SERVICE);
    }
	
    public static boolean isEnabled()
    {
    	return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
    
    public static LocationInfo getLocation()
    {    	
    	return LocationInfo.toLocationInfo(getLastKnownLocation());
    }
    
    public static double getLat()
    {
    	Location location = getLastKnownLocation();
    	if (location != null)
    	{
    		return location.getLatitude();
    	}
    	return 0;
    }
    
    public static double getLng()
    {
    	Location location = getLastKnownLocation();
    	if (location != null)
    	{
    		return location.getLongitude();
    	}
    	return 0;
    }
    
    public static double getAcc()
    {
    	Location location = getLastKnownLocation();
    	if (location != null)
    	{
    		return location.getAccuracy();
    	}
    	return 0;
    }
    
    public static double getAlt()
    {
    	Location location = getLastKnownLocation();
    	if (location != null)
    	{
    		return location.getAltitude();
    	}
    	return 0;
    }   
    
    public static double getSpeed()
    {
    	Location location = getLastKnownLocation();
    	if (location != null)
    	{
    		return location.getSpeed();
    	}
    	return 0;
    }
    
    public static double getDir()
    {
    	Location location = getLastKnownLocation();
    	if (location != null)
    	{
    		return location.getBearing();
    	}
    	return 0;
    }
    
    public static long getTime()
    {
    	Location location = getLastKnownLocation();
    	if (location != null)
    	{
    		return location.getTime();
    	}
    	return 0;
    }
    
    public static List<SatelliteInfo> getSatellites()
    {
    	List<SatelliteInfo> satellites = new ArrayList<SatelliteInfo>();
    	
    	GpsStatus gpsStatus = locationManager.getGpsStatus(null);
    	if (gpsStatus != null)
    	{
	    	Iterator<GpsSatellite> i = gpsStatus.getSatellites().iterator();
	    	while (i.hasNext())
	    	{
	    		GpsSatellite s = i.next();
	    		SatelliteInfo si = new SatelliteInfo(s.getAzimuth(), s.getElevation(), 
	    				s.getPrn(), s.getSnr(), s.hasAlmanac(), s.hasEphemeris(), s.usedInFix());
	    		satellites.add(si);
	    	}
    	}
    	return satellites;
    }
    
    public static int getTimeToFix()
    {
    	GpsStatus gpsStatus = locationManager.getGpsStatus(null);
    	
    	if (gpsStatus != null)
    	{
    		return gpsStatus.getTimeToFirstFix();
    	}
    	
    	return -1;
    }
    
    public static GpsStatus getGPSStatus()
    {
    	return locationManager.getGpsStatus(null);
    }  
    
    /* Private methods */
    
    private static Location getLastKnownLocation()
    {
    	Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    	return location;
    }
    
    /* Listener and Events */
       
    /* Location listener */
    public static void addLocationListener(LocationListener locationListener)
    {
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, QUICK_PERIOD, 0, locationListener);
    }
    
    public static void addLocationListener(LocationListener locationListener, int period, float distance)
    {
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, period, distance, locationListener);
    }
    
    public static void removeLocationListener(LocationListener locationListener)
    {
    	locationManager.removeUpdates(locationListener);
    }
    
    /* State listener */
    public static void addGpsStatusListener(GpsStatus.Listener listener)
    {
		locationManager.addGpsStatusListener(listener);
    }
    
    public static void removeGpsStatusListener(GpsStatus.Listener listener)
    {
    	locationManager.removeGpsStatusListener(listener);
    }
}
