package com.pratham.pravin.vision;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.support.multidex.MultiDex;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.pratham.pravin.vision.gps.EventBusMSG;

import org.greenrobot.eventbus.EventBus;

public class VisionApplication extends Application implements GpsStatus.Listener, LocationListener {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private static final int NOT_AVAILABLE = -100000;
    private static final int GPS_DISABLED = 0;
    private static final int GPS_OUTOFSERVICE = 1;
    private static final int GPS_TEMPORARYUNAVAILABLE = 2;
    private static final int GPS_SEARCHING = 3;
    private static final int GPS_STABILIZING = 4;
    private static final int GPS_OK = 5;

    // Preferences Variables
    private long prefGPSupdatefrequency = 20000L;
    // Singleton instance
    private static VisionApplication singleton;
    public static Location location;
    public Context context;

    public static VisionApplication getInstance() {
        return singleton;
    }

    private int GPSStatus = GPS_SEARCHING;
    private LocationManager mlocManager = null;             // GPS LocationManager
    private int _NumberOfSatellites = 0;
    private int _NumberOfSatellitesUsedInFix = 0;

    final Handler gpsunavailablehandler = new Handler();
    Runnable unavailr = new Runnable() {

        @Override
        public void run() {
            if ((GPSStatus == GPS_OK) || (GPSStatus == GPS_STABILIZING)) {
                GPSStatus = GPS_TEMPORARYUNAVAILABLE;
                EventBus.getDefault().post(EventBusMSG.UPDATE_FIX);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = new VisionApplication();
        context = this;
        getLocation(this);
    }

    public void getLocation(Context mcontext) {
        context = mcontext;
        mlocManager = (LocationManager) mcontext.getSystemService(Context.LOCATION_SERVICE);     // Location Manager
        if (ContextCompat.checkSelfPermission(mcontext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mlocManager.addGpsStatusListener(VisionApplication.this);
            mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, prefGPSupdatefrequency, 0, VisionApplication.this); // Requires Location update
        }
    }

    public void updateSats() {
        try {
            if ((mlocManager != null) && (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
                GpsStatus gs = mlocManager.getGpsStatus(null);
                int sats_inview = 0;    // Satellites in view;
                int sats_used = 0;      // Satellites used in fix;

                if (gs != null) {
                    Iterable<GpsSatellite> sats = gs.getSatellites();
                    for (GpsSatellite sat : sats) {
                        sats_inview++;
                        if (sat.usedInFix()) sats_used++;
                        //Log.w("VisionApplication", "[#] GPSApplication.java - updateSats: i=" + i);
                    }
                    _NumberOfSatellites = sats_inview;
                    _NumberOfSatellitesUsedInFix = sats_used;
                } else {
                    _NumberOfSatellites = NOT_AVAILABLE;
                    _NumberOfSatellitesUsedInFix = NOT_AVAILABLE;
                }
            } else {
                _NumberOfSatellites = NOT_AVAILABLE;
                _NumberOfSatellitesUsedInFix = NOT_AVAILABLE;
            }
        } catch (NullPointerException e) {
            _NumberOfSatellites = NOT_AVAILABLE;
            _NumberOfSatellitesUsedInFix = NOT_AVAILABLE;
            Log.w("VisionApplication", "[#] GPSApplication.java - updateSats: Caught NullPointerException: " + e);
        }
    }


    @Override
    public void onGpsStatusChanged(int event) {
        switch (event) {
            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                Log.d("gps:::", "satellite");
                // TODO: get here the status of the GPS, and save into a GpsStatus to be used for satellites visualization;
                // Use GpsStatus getGpsStatus (GpsStatus status)
                // https://developer.android.com/reference/android/location/LocationManager.html#getGpsStatus(android.location.GpsStatus)
                updateSats();
                break;
        }

    }

    @Override
    public void onLocationChanged(Location loc) {
        //if ((loc != null) && (loc.getProvider().equals(LocationManager.GPS_PROVIDER)) {
        if (loc != null) {      // Location data is valid
            //Log.w("VisionApplication", "[#] GPSApplication.java - onLocationChanged: provider=" + loc.getProvider());
            location = loc;
            Log.d("onLocationChanged:", "" + loc.getTime());
            Log.d("onLocationChanged:", "" + loc.getAltitude());
            Log.d("onLocationChanged:", "" + loc.getLatitude());
            Log.d("onLocationChanged:", "" + loc.getLongitude());
            EventBus.getDefault().post(EventBusMSG.UPDATE_TRACK);
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        switch (status) {
            case LocationProvider.OUT_OF_SERVICE:
                //Log.w("VisionApplication", "[#] GPSApplication.java - GPS Out of Service");
                gpsunavailablehandler.removeCallbacks(unavailr);            // Cancel the previous unavail countdown handler
                GPSStatus = GPS_OUTOFSERVICE;
                EventBus.getDefault().post(EventBusMSG.UPDATE_FIX);
                //Toast.makeText( getApplicationContext(), "GPS Out of Service", Toast.LENGTH_SHORT).show();
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                //Log.w("VisionApplication", "[#] GPSApplication.java - GPS Temporarily Unavailable");
                gpsunavailablehandler.removeCallbacks(unavailr);            // Cancel the previous unavail countdown handler
                GPSStatus = GPS_TEMPORARYUNAVAILABLE;
                EventBus.getDefault().post(EventBusMSG.UPDATE_FIX);
                //Toast.makeText( getApplicationContext(), "GPS Temporarily Unavailable", Toast.LENGTH_SHORT).show();
                break;
            case LocationProvider.AVAILABLE:
                gpsunavailablehandler.removeCallbacks(unavailr);            // Cancel the previous unavail countdown handler
                //Log.w("VisionApplication", "[#] GPSApplication.java - GPS Available: " + _NumberOfSatellites + " satellites");
                break;
        }

    }

    @Override
    public void onProviderEnabled(String provider) {
        GPSStatus = GPS_SEARCHING;
        EventBus.getDefault().post(EventBusMSG.UPDATE_FIX);

    }

    @Override
    public void onProviderDisabled(String provider) {
        GPSStatus = GPS_DISABLED;
        EventBus.getDefault().post(EventBusMSG.UPDATE_FIX);

    }
}
