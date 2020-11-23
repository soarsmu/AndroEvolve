package com.micronet.obc5_gpstest;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by brigham.diaz on 1/18/2017.
 */

public class Gps implements LocationListener, GpsStatus.Listener {
    private final String TAG = "Gps";
    private static Gps gps;
    private static final long INTERVAL_ONE_SECOND = 1000; // minimum time interval between location updates, in milliseconds
    private LocationManager locationManager = null;
    private NmeaListener nmeaListener = null;

    // allow multiple listeners for the following events
    private List<OnNmeaUpdateListener> onNmeaUpdateListeners;
    private List<OnLocationUpdateListener> onLocationUpdateListeners;
    private List<OnGpsStatusListener> onGpsStatusListeners;

    /**
     * required default constructor
     */
    public Gps() {
    }

    public static Gps get(Context appContext) {
        if (gps == null) {
            gps = new Gps(appContext.getApplicationContext());
        } else if(gps.locationManager == null) {
            gps.addLocationListener(appContext);
        }
        return gps;
    }

    private Gps(Context appContext) {
        onNmeaUpdateListeners = new ArrayList<>();
        onLocationUpdateListeners = new ArrayList<>();
        onGpsStatusListeners = new ArrayList<>();
        addLocationListener(appContext);

    }

    /// Start Event handlers
    public void setOnNmeaUpdateListeners(Fragment fragment) {
        onNmeaUpdateListeners.add((OnNmeaUpdateListener) fragment);
    }

    public interface OnNmeaUpdateListener {
        void onNmeaUpdate(String nmea, long timestamp);
    }

    public void setOnLocationUpdateListeners(Fragment fragment) {
        onLocationUpdateListeners.add((OnLocationUpdateListener) fragment);
    }

    public void setOnLocationUpdateListener(Service service) {
        onLocationUpdateListeners.add((OnLocationUpdateListener) service);
    }

    public interface OnLocationUpdateListener {
        void onLocationUpdate(Location location, String locationStr);
    }

    public void setOnGpsStatusListeners(Fragment fragment) {
        onGpsStatusListeners.add((OnGpsStatusListener) fragment);
    }

    public void removeLocationUpdateListener(Fragment fragment) {
        onLocationUpdateListeners.remove((OnLocationUpdateListener)fragment);
        if(!keepGpsListenerAlive()) {
            removeLocationListener();
        }
    }

    public void removeLocationUpdateListener(Service service) {
        onLocationUpdateListeners.remove((OnLocationUpdateListener)service);
        if(!keepGpsListenerAlive()) {
            removeLocationListener();
        }
    }

    public void removeGpsStatusListener(Fragment fragment) {
        onGpsStatusListeners.remove((OnGpsStatusListener)fragment);
        if(!keepGpsListenerAlive()) {
            removeLocationListener();
        }
    }

    public void removeGpsStatusListener(Service service) {
        onGpsStatusListeners.remove((OnGpsStatusListener)service);
        if(!keepGpsListenerAlive()) {
            removeLocationListener();
        }
    }

    public void removeNmeaUpdateListener(Fragment fragment) {
        onNmeaUpdateListeners.remove((OnNmeaUpdateListener)fragment);
        if(!keepGpsListenerAlive()) {
            removeLocationListener();
        }
    }

    public void removeNmeaUpdateListener(Service service) {
        onNmeaUpdateListeners.remove((OnNmeaUpdateListener)service);
        if(!keepGpsListenerAlive()) {
            removeLocationListener();
        }
    }


    public interface OnGpsStatusListener {
        void onStatusChanged(String status);
    }
    /// End Event handlers

    private void addLocationListener(Context appContext) {
        locationManager = (LocationManager) appContext.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(appContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Unable to register LocationListener, ACCESS_FINE_LOCATION denied.");
            // TODO: Show error on ui
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, INTERVAL_ONE_SECOND, 0, this);
        Log.i(TAG, "Requesting Location Updates Successful");
        locationManager.addGpsStatusListener(this);
        Log.i(TAG, "Requesting Gps Status Updates Successful");

        if (nmeaListener == null) {
            nmeaListener = new NmeaListener();
            boolean nmeaResult = locationManager.addNmeaListener(nmeaListener);
            Log.i(TAG, "Requesting NMEA Updates " + (nmeaResult ? "Successful" : "Failed"));
        }
    }

    private boolean keepGpsListenerAlive() {
        return onNmeaUpdateListeners.size() + onLocationUpdateListeners.size() + onGpsStatusListeners.size() > 0;
    }

    private class NmeaListener implements GpsStatus.NmeaListener {

        @Override
        public void onNmeaReceived(long timestamp, String nmea) {
            //Log.i(TAG, String.format("T:%d, %s", timestamp, nmea));
            for(OnNmeaUpdateListener listener : onNmeaUpdateListeners) {
                if(nmea.startsWith("$GPGGA")) {
                    nmeaParts.gpgga = Arrays.asList(nmea.split(","));
                }
                if(nmea.startsWith("$GPGSA")) {
                    nmeaParts.gpgsa = Arrays.asList(nmea.split(","));
                }
                listener.onNmeaUpdate(nmea, timestamp);
            }
        }
    }

    private NmeaParts nmeaParts = new NmeaParts();

    private class NmeaParts {
        public List<String> gpgga = null;
        public List<String> gpgsa = null;
    }

    public void removeLocationListener() {
        if (locationManager != null) {
            try {
                // remove location listener
                locationManager.removeUpdates(this);
                // remove gps status listener
                locationManager.removeGpsStatusListener(this);
                // remove nmea listener
                locationManager.removeNmeaListener(nmeaListener);
                onNmeaUpdateListeners.clear();
                onLocationUpdateListeners.clear();
                onGpsStatusListeners.clear();
            } catch (SecurityException e) {
                e.printStackTrace();
            }


            locationManager = null;
            Log.i(TAG, "Deregistered Location Listener");
        }
    }


    @Override
    public void onGpsStatusChanged(int event) {
        GpsStatus gpsStatus;
        switch (event) {
            case GpsStatus.GPS_EVENT_FIRST_FIX:
                Log.d(TAG, "GPS_EVENT_FIRST_FIX");
                gpsStatus = locationManager.getGpsStatus(null);
                Log.d(TAG, String.format("TTFF=%s", gpsStatus.getTimeToFirstFix()));
                break;
            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                Log.d(TAG, "GPS_EVENT_SATELLITE_STATUS");
                gpsStatus = locationManager.getGpsStatus(null);

                StringBuilder satellites = new StringBuilder();
                satellites.append("SAT #\tAZIMUTH\t\tELEVATION\tPRN\t\tSNR (db)\t\tUSED IN FIX\n");
                int numSat = 0;
                int numSatUsed = 0;

                if(gpsStatus != null && gpsStatus.getSatellites() != null) {
                    for (GpsSatellite satellite : gpsStatus.getSatellites()) {
                        satellites.append(String.format("%d\t\t\t%03.0f°\t\t\t%02.0f°\t\t\t%d\t\t\t%09.6f\t\t%s\n", ++numSat,
                                satellite.getAzimuth(), satellite.getElevation(), satellite.getPrn(), satellite.getSnr(),
                                satellite.usedInFix() ? "YES (" + (++numSatUsed) + ")" : "NO"));
                    }
                }

                String statusUpdate = String.format("SAT\t\t\tTIME TO FIRST FIX\n(%d/%d)\t\t%s\n\n%s", numSatUsed, numSat, Utils.formatTimespan(gpsStatus.getTimeToFirstFix()),satellites);
                Log.d(TAG, statusUpdate);
                for(OnGpsStatusListener listener : onGpsStatusListeners) {
                    listener.onStatusChanged(statusUpdate);
                }
                break;
            case GpsStatus.GPS_EVENT_STARTED:
                Log.d(TAG, "GPS_EVENT_STARTED");
                break;
            case GpsStatus.GPS_EVENT_STOPPED:
                Log.d(TAG, "GPS_EVENT_STOPPED");
                break;
        }
    }



    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, location.toString());

        int numSatellites = location.getExtras().getInt("satellites", -1);

        StringBuilder locationStr = new StringBuilder();
        locationStr.append("LATITUDE\t\tLONGITUDE\t\tACCURACY\tTIME\t\t\t\tALTITUDE\n");
        locationStr.append(String.format("%09.6f\t\t%10.6f\t%06.3fm\t\t%s\t\t%05.1fm\n\n",
                location.getLatitude(),
                location.getLongitude(),
                location.getAccuracy(),
                Utils.formatDate(location.getTime()),
                location.getAltitude()));
        locationStr.append("VELOCITY\tPROVIDER\tBEARING\t\tPDOP\t\tHDOP\t\tVDOP\t\tMODE\n");
        locationStr.append(String.format("%06.3fm/s\t%s\t\t\t%05.2f\t\t%s\t\t%s\t\t%s\t\t%s\n",
                location.getSpeed(),
                location.getProvider().toUpperCase(),
                location.getBearing(),
                nmeaParts.gpgsa != null ? nmeaParts.gpgsa.get(15) : "",
                nmeaParts.gpgsa != null ? nmeaParts.gpgsa.get(16) : "",
                nmeaParts.gpgsa != null ? nmeaParts.gpgsa.get(17).substring(0, 3) : "",
                nmeaParts.gpgsa != null ? nmeaParts.gpgsa.get(2) : "",
                numSatellites));


        for(OnLocationUpdateListener listener : onLocationUpdateListeners) {
            listener.onLocationUpdate(location, locationStr.toString());
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.i(TAG, provider);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.i(TAG, provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.i(TAG, provider);
    }
}