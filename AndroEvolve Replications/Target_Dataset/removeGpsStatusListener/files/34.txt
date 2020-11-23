package com.mdu.gps;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

public class LocationService extends Service implements LocationListener, GpsStatus.Listener {

  static final long LOCATION_INTERVAL = 400;
  static final float LOCATION_MIN_DISTANCE = 1;

  LocationManager locationManager;
  String provider = LocationManager.GPS_PROVIDER;
  final IBinder locBinder = new LocBinder();
  LocationStore store = new LocationStore(this);
  boolean collecting = false;

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    super.onStartCommand(intent, flags, startId);

    return Service.START_STICKY;
  }

  @Override
  public void onCreate() {
    super.onCreate();

    locationManager = start();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();

    stop();
  }

  @Override
  public IBinder onBind(Intent arg0) {
    return locBinder;
  }

  boolean isCollecting() {
    return collecting;
  }

  void setCollecting(boolean b) {
    collecting = b;
    if(b)
      try {
        locationManager.requestLocationUpdates(provider, LOCATION_INTERVAL, LOCATION_MIN_DISTANCE, this);
        locationManager.addGpsStatusListener(this);
      } catch (SecurityException e) {
        U.E(this, e);
      }
    else
      try {
        locationManager.removeUpdates(this);
        locationManager.removeGpsStatusListener(this);
      } catch (SecurityException e) {
        U.E(this, e);
      }
  }

  LocationManager start() {
    LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

    try {
      Location location = lm.getLastKnownLocation(provider);
      if (location != null)
        this.onLocationChanged(location);
    } catch (SecurityException e) {
      U.E(this, e);
    }

    return lm;
  }

  void stop() {
    stopSelf();
    if (locationManager != null) {
      try {
        locationManager.removeUpdates(this);
        locationManager.removeGpsStatusListener(this);
      } catch (SecurityException e) {
        U.E(this, e);
      }
    }
  }

  // LocationListener
  @Override
  public void onLocationChanged(Location loc) {
    if(loc == null)
      return;
    store.store(loc);
    Context ctx = LocationService.this;
    U.M(ctx, ctx.getString(R.string.LEFT_RIGHT_ARROW) + " " + loc.getLongitude() + " " + ctx.getString(R.string.UP_DOWN_ARROW) + " " + loc.getLatitude());
  }

  @Override
  public void onProviderEnabled(String provider) {
    U.M(this, "Enabled new provider " + provider);
  }

  @Override
  public void onProviderDisabled(String provider) {
    U.M(this, "Disabled provider " + provider);
  }

  @Override
  public void onStatusChanged(String provider, int status, Bundle extras) {
    String s;
    switch (status) {
      case LocationProvider.OUT_OF_SERVICE:
        s = "OUT OF SERVICE";
        break;
      case LocationProvider.AVAILABLE:
        s = "AVAILABLE";
        break;
      case LocationProvider.TEMPORARILY_UNAVAILABLE:
        s = "TEMPORARILY UNAVAILABLE";
        break;
      default:
        s = "Unknown Status";
        break;
    }
    U.M(this, "Status changed " + provider + " " + s);
  }

  // GPS availability listener
  @Override
  public void onGpsStatusChanged(int i) {
    String s = null;
    switch(i) {
      case GpsStatus.GPS_EVENT_FIRST_FIX:
        s = "GPS_EVENT_FIRST_FIX";
        break;
      case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
        break;
      case GpsStatus.GPS_EVENT_STARTED:
        s = "GPS_EVENT_STARTED";
        break;
      case GpsStatus.GPS_EVENT_STOPPED:
        s = "GPS_EVENT_STOPPED";
        break;
      default:
        s = "Unknown Reason" + i;
        break;
    }
    if(s != null)
      U.M(this, s);
  }

  static boolean IsRunning(Context ctx) {
    ActivityManager manager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
    Class cls = LocationService.class;
    for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
      if (cls.getName().equals(service.service.getClassName())) {
        return true;
      }
    }
    return false;
  }

  public class LocBinder extends Binder {
    LocationService getService() {
      return LocationService.this;
    }
  }

}

