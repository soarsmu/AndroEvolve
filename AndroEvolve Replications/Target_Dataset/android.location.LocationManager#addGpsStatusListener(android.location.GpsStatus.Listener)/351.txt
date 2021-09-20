package com.example.llocation;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.Iterator;

public class MyLocation {

    private static final String TAG = "MyLocation";

    private Context context;

    private OnLocation result;

    private int Statellites = 0;

    private String[] permission = {Manifest.permission.ACCESS_FINE_LOCATION};

    public void setOnLocation(OnLocation location) {
        this.result = location;
    }

    private int index = 0;
    private GpsStatus.Listener listener = new GpsStatus.Listener() {
        @Override
        public void onGpsStatusChanged(int event) {
            switch (event) {
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    Log.e(TAG, "第一次定位");
                    break;
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                    Log.e(TAG, "卫星状态改变");

                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        result.OnFailed(NO_LOCATION_PERMISSION);
                        return;
                    }
                    GpsStatus gpsStatus = lm.getGpsStatus(null);
                    int maxStatellites = gpsStatus.getMaxSatellites();
                    Iterator<GpsSatellite> iters = gpsStatus.getSatellites().iterator();
                    int count = 0;
                    while (iters.hasNext() && count <= maxStatellites) {
                        GpsSatellite s = iters.next();
                        count++;
                    }
                    if (index > 10 && count < 5) {
                        result.OnFailed(NO_FIND_SATELLITE);
                        index = 0;
                        return;
                    }
                    index++;
                    Statellites = count;
                    System.out.println("搜索到：" + count + "颗卫星");
                    break;
                case GpsStatus.GPS_EVENT_STARTED:
                    result.OnStart();
                    Log.e(TAG, "定位启动");
                    break;
                case GpsStatus.GPS_EVENT_STOPPED:
                    result.OnFinish();
                    Log.e(TAG, "定位结束");
                    break;
            }
        }
    };

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (location == null) {
                result.OnFailed(FAILED_LOCATION);
                return;
            }
            LocationData locationData = new LocationData(type, location, Statellites);
            result.OnSuccess(locationData);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                //GPS状态为可见时
                case LocationProvider.AVAILABLE:
                    break;
                //GPS状态为服务区外时
                case LocationProvider.OUT_OF_SERVICE:
                    result.OnFailed(OUT_OF_SERVICE);
                    break;
                //GPS状态为暂停服务时
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    result.OnPause();
                    break;
            }
        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private LocationManager lm;
    static MyLocation Instance;

    //初始化,判断权限
    private MyLocation(Context context) {
        this.context = context;
    }

    public static MyLocation getInstance(Context context) {
        if (Instance == null) {
            synchronized (MyLocation.class) {
                if (Instance == null) {
                    Instance = new MyLocation(context);
                }
            }
        }
        return Instance;
    }

    // 判断是否缺少权限
    private boolean lacksPermission(String permission) {
        return ContextCompat.checkSelfPermission(context, permission) ==
                PackageManager.PERMISSION_DENIED;
    }

    public void destroy() {
        lm.removeUpdates(locationListener);
    }

    //没有定位权限
    private static final int NO_LOCATION_PERMISSION = 556;
    //没有找到卫星
    private static final int NO_FIND_SATELLITE = 697;
    //定位失败
    private static final int FAILED_LOCATION = 491;
    //定位服务不在服务区
    private static final int OUT_OF_SERVICE = 387;

    //开始定位
    public void startLocation() {
        lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            result.OnFailed(NO_LOCATION_PERMISSION);
            return;
        }

        lm.addGpsStatusListener(listener);

        lm.requestLocationUpdates(type, time, distance, locationListener);

    }

    //定位间隔
    private int time = 5000;

    //单位秒
    public void setTime(int time) {
        this.time = time * 1000;
        Log.e(TAG, "setTime: ");
    }

    //位置间隔,单位:m
    private int distance = 10;

    public void setDistance(int distance) {
        this.distance = distance;
    }

    //默认GPS
    private String type = LocationManager.GPS_PROVIDER;
    public static final String GPS = LocationManager.GPS_PROVIDER;
    public static final String NETWORK = LocationManager.NETWORK_PROVIDER;

    public void Type(String type) {
        this.type = type;
    }
}
