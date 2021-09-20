package com.example.showweather.main;

import android.app.Service;
import android.content.Intent;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.util.Iterator;

/**
 * Created by Administrator on 2016/4/27 0027.
 */
public class LocationLocalService extends Service {
    private LocationManager mlocation;//用系统的GPS获取当前位置信息
    private static final int minTime = 1000 * 30;//刷新一次GPS信息的间隔（ms）
    private static final int minDistance = 0;//位置最小变化距离

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        initLocationGps();
        return super.onStartCommand(intent, flags, startId);
    }


    /**
     * 初始化GPS信息
     */
    private void initLocationGps() {
        mlocation = (LocationManager) getSystemService(getApplicationContext().LOCATION_SERVICE);

        mlocation.addGpsStatusListener(listener);
        mlocation.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, mLocationListener);
    }


    /**
     * 位置信息改变回调接口，当接到回调时，把实时的位置信息保存起来
     */
    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            double lon = location.getLongitude();//经度信息
            double lat = location.getLatitude();//纬度信息
            Log.i("ljwtest:", "实时定位数据:" + Double.toString(lon) + "__" + Double.toString(lat));
            Utils.setLocationCoordinate(getApplicationContext(), Double.toString(lon), Double.toString(lat));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                //GPS状态为可见时
                case LocationProvider.AVAILABLE:
                    Log.i("ljwtest:", "当前GPS状态为可见状态");
                    break;
                //GPS状态为服务区外时
                case LocationProvider.OUT_OF_SERVICE:
                    Log.i("ljwtest:", "当前GPS状态为服务区外状态");
                    break;
                //GPS状态为暂停服务时
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.i("ljwtest:", "当前GPS状态为暂停服务状态");
                    break;
            }
        }

        @Override
        public void onProviderEnabled(String provider) {
//            threadDisable = false;
        }

        @Override
        public void onProviderDisabled(String provider) {
//            threadDisable = true;
        }
    };

    //状态监听
    GpsStatus.Listener listener = new GpsStatus.Listener() {
        public void onGpsStatusChanged(int event) {
            switch (event) {
                //第一次定位
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    Log.i("ljwtest:", "第一次定位");
                    break;
                //卫星状态改变
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                    Log.i("ljwtest:", "卫星状态改变");
                    //获取当前状态
                    GpsStatus gpsStatus = mlocation.getGpsStatus(null);
                    //获取卫星颗数的默认最大值
                    int maxSatellites = gpsStatus.getMaxSatellites();
                    //创建一个迭代器保存所有卫星
                    Iterator<GpsSatellite> iters = gpsStatus.getSatellites().iterator();
                    int count = 0;
                    while (iters.hasNext() && count <= maxSatellites) {
                        GpsSatellite s = iters.next();
                        count++;
                    }
                    break;
                //定位启动
                case GpsStatus.GPS_EVENT_STARTED:
                    Log.i("ljwtest:", "定位启动");
                    break;
                //定位结束
                case GpsStatus.GPS_EVENT_STOPPED:
                    Log.i("ljwtest:", "定位结束");
                    break;
            }
        }

        ;
    };
}
