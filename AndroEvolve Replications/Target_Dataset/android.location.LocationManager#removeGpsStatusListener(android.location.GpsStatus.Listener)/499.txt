package com.android.camera.v66;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;

import java.util.Iterator;

public class CarSpeedMonitor {
    
    private static final String TAG = "CarSpeedMonitor";
    
    private Context mContext;
    private LocationManager mLocationManager;
    private ISpeedChangeListener mSpeedListener;
    private IGPSStatusChangeListener mGpsStatusListener;
    
    private boolean mUpdateLocationFlag = false;
    private boolean mLocationEnable = false;
    private boolean mFirstFixed = false;
    private boolean mSatelliteAvavilable = true;
    
    private int mState = 0;
    
    private LocationListener mLocationListener = new LocationListener() {
        
        @Override
        public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
            mState = arg1;
            switch (arg1) {
                case LocationProvider.AVAILABLE:
                    // mSpeedListener.onSpeedChange(0, arg1);
                    mLocationEnable = true;
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    if (mSpeedListener != null) {
                        mSpeedListener.onSpeedChange(0, arg1, 0.0, 0.0);
                    }
                    mLocationEnable = false;
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    if (mSpeedListener != null) {
                        mSpeedListener.onSpeedChange(0, arg1, 0.0, 0.0);
                    }
                    mLocationEnable = false;
                    break;
                default:
                    break;
            }
        }
        
        @Override
        public void onProviderEnabled(String arg0) {
            
        }
        
        @Override
        public void onProviderDisabled(String arg0) {
            if (mSpeedListener != null) {
                mSpeedListener.onSpeedChange(0, mState, 0.0, 0.0);
            }
        }
        
        @Override
        public void onLocationChanged(Location location) {
            // TODO Auto-generated method stub
            if (location != null) {
                float speed = location.getSpeed();
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();
                if (mSpeedListener != null) {
                    mSpeedListener.onSpeedChange(speed, 0,longitude,latitude);
                }
                /*
                 * if (mFirstFixed && mSatelliteAvavilable) {
                 * mSpeedListener.onSpeedChange(speed, 0); } else {
                 * mSpeedListener.onSpeedChange(-1, 0); }
                 */
            } else {
                if (mSpeedListener != null) {
                    mSpeedListener.onSpeedChange(0, -0,0.0,0.0);
                }
            }
        }
    };
    
    private GpsStatus.Listener mSelfGpsStatusListener = new GpsStatus.Listener() {
        
        @Override
        public void onGpsStatusChanged(int arg0) {
            int hasSignal = 0;
            switch (arg0) {
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    mFirstFixed = true;
                    break;
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                    
                    GpsStatus gpsStatus = mLocationManager.getGpsStatus(null);
                    
                    if (gpsStatus == null) {
                        // to do
                    } else {
                        Iterator<GpsSatellite> satellites = gpsStatus.getSatellites().iterator();
                        
                        if (satellites == null) {
                            // nothing
                        } else {
                            int used = 0;
                            
                            while (satellites.hasNext()) {
                                GpsSatellite satellite = satellites.next();
                                
                                if (satellite.getSnr() > 10) {
                                    used++;
                                    hasSignal = 1;
                                }
                            }
                            
                            /*
                             * if (used >= 3) { mSatelliteAvavilable = true; }
                             * else { mSatelliteAvavilable = false;
                             * mSpeedListener.onSpeedChange(-3, mState); }
                             */
                        }
                        
                    }
                    break;
                case GpsStatus.GPS_EVENT_STARTED:
                    break;
                case GpsStatus.GPS_EVENT_STOPPED:
                    break;
                default:
                    break;
            }
            if (mGpsStatusListener != null) {
                mGpsStatusListener.onStatusChange(hasSignal);
            }
            if (hasSignal == 0 && mSpeedListener != null) {
                mSpeedListener.onSpeedChange(0, -0,0.0,0.0);
            }
        }
    };
    
    public interface ISpeedChangeListener {
        public void onSpeedChange(float speed, int status, double longitude, double latitude);
    }
    
    public interface IGPSStatusChangeListener {
        public void onStatusChange(int hasSignal);
    }
    
    public CarSpeedMonitor(Context context) {
        mContext = context;
        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
    }
    
    public void setSpeedChangeListener(ISpeedChangeListener observer) {
        mSpeedListener = observer;
    }
    
    public void setGpsStatusChangeListener(IGPSStatusChangeListener observer) {
        mGpsStatusListener = observer;
    }
    
    @SuppressLint("NewApi")
    public void startSpeedDection() {
        if (mUpdateLocationFlag) {
            return;
        }
        
        mLocationManager.addGpsStatusListener(mSelfGpsStatusListener);
        mUpdateLocationFlag = true;
        // 获取gps是否打开
        boolean gpsEnabled = Settings.Secure.isLocationProviderEnabled(
                mContext.getContentResolver(), LocationManager.GPS_PROVIDER);
        
        // 如果没有，则打开gps
        if (gpsEnabled == false) {
            Settings.Secure.setLocationProviderEnabled(mContext.getContentResolver(),
                    LocationManager.GPS_PROVIDER, true);
        }
        
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setBearingRequired(false);
        criteria.setAltitudeRequired(false);
        criteria.setCostAllowed(false);
        criteria.setSpeedRequired(true);
        criteria.setSpeedAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        
        String provider = mLocationManager.getBestProvider(criteria, true);
        Log.d("CarSpeedMonitor", "location provider " + provider);
        
        mLocationManager.requestLocationUpdates(provider, 50, 0F, mLocationListener);
    }
    
    public void stopSpeedDection() {
        mUpdateLocationFlag = false;
        // mHandler.removeMessages(MSG_UPDATE_LOCATION);
        mLocationManager.removeGpsStatusListener(mSelfGpsStatusListener);
        mLocationManager.removeUpdates(mLocationListener);
    }
}
