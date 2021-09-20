
package com.mediatek.factorymode.gps;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;

import android.util.Log;

public class GpsUtil {
    private Context mContext;

    private LocationManager mLocationManager;

    private String mProvider;

    private int mSatelliteNum;
    private int mSatelliteSNR35Num;
    private int mSatelliteSNR40Num;

    private int mGpsCurrentStatus;

    private List<Float> mSatelliteSignal = new ArrayList<Float>();

    public GpsUtil(Context context) {
        mContext = context;
        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        mProvider = LocationManager.GPS_PROVIDER;
        openGPS();
        mLocationManager.getLastKnownLocation(mProvider);
        mLocationManager.requestLocationUpdates(mProvider, 2000, 0, mLocationListener);
        mLocationManager.addGpsStatusListener(mGpsStatusListener);
        updateGpsStatus(0, null);
    }

    private void openGPS() {
        if (!mLocationManager.isProviderEnabled(mProvider)) {
            openGPSSetting();
        }
    }

    private void openGPSSetting() {
        Settings.Secure.setLocationProviderEnabled(mContext.getContentResolver(), mProvider, true);
    }

    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onLocationChanged(Location location) {
        }
    };

    private GpsStatus.Listener mGpsStatusListener = new GpsStatus.Listener() {

        @Override
        public void onGpsStatusChanged(int event) {
            GpsStatus gpsStatus = mLocationManager.getGpsStatus(null);
            updateGpsStatus(event, gpsStatus);
        }
    };

    private void updateGpsStatus(int event, GpsStatus gpsStatus) {
        if (gpsStatus == null) {
            mSatelliteNum = 0;
            mSatelliteSNR35Num = 0;
            mSatelliteSNR40Num = 0;
        } else if (event == GpsStatus.GPS_EVENT_SATELLITE_STATUS) {
            Iterator<GpsSatellite> it = gpsStatus.getSatellites().iterator();
            float snr = 0;
            //int prn = 0;
            //mSatelliteSignal.clear();
            //mSatelliteSNR35Num = 0;
            //mSatelliteSNR40Num = 0;
            while (it.hasNext()) {
                GpsSatellite satelite = it.next();
                snr = satelite.getSnr();
                //prn = satelite.getPrn();
                //mSatelliteSignal.add(snr);
                if (snr >= 30) {
                    mSatelliteSignal.add(snr);
                    mSatelliteNum++;
                }
                if (snr >= 35) {
                    mSatelliteSNR35Num++;
                }
                if (snr >= 40) {
                    mSatelliteSNR40Num++;
                }
            }
        }
        mGpsCurrentStatus = event;
    }

    public void closeLocation() {
        mLocationManager.removeGpsStatusListener(mGpsStatusListener);
        mLocationManager.removeUpdates(mLocationListener);
    }

    public int getSatelliteNumber() {
        return mSatelliteNum;
    }

    public int getSatelliteSNR35Number() {
        return mSatelliteSNR35Num;
    }

    public int getSatelliteSNR40Number() {
        return mSatelliteSNR40Num;
    }

    public String getSatelliteSignals() {
        String str = mSatelliteSignal.toString();
        return mSatelliteSignal.size() <= 0 ? "" : str.substring(1, str.length() - 1);
    }

    public int getGpsCurrentStatus() {
        return mGpsCurrentStatus;
    }

    public LocationManager getLocationManager() {
        return mLocationManager;
    }
}
