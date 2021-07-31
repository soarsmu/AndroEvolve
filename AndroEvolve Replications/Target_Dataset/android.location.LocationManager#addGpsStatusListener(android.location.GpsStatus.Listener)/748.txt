package com.summer.lib.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by ${viwmox} on 2016-08-11.
 */
public class GPSUtil {

    private static GPSUtil instance;

    private LocationManager lm;

    private String TAG = "TAG";

    public static GPSUtil getInstance() {
        if (instance == null) {
            instance = new GPSUtil();
        }
        return instance;
    }


    public void openGPSSettings(Activity context) {
        lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(context, "GPS模块正常 ", Toast.LENGTH_SHORT).show();
            getLocation(context);
            return;
        }
        Toast.makeText(context, "请开启 GPS！", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
        context.startActivityForResult(intent, 0); //此为设置完成后返回到获取界面

    }

    private void getLocation(final Context context) {
// 获取位置管理服务
        String serviceName = Context.LOCATION_SERVICE;
// 查找到服务信息
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE); // 高精度
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW); // 低功耗
        final String provider = lm.getBestProvider(criteria, true); // 获取 GPS信息

// 设置监听 *器，自动更新的最小时间为间隔 N秒(1秒为 1*1000，这样写主要为了方便 )或最小位移变化超过 N米
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lm.addGpsStatusListener(new GpsStatus.Listener() {

            @Override
            public void onGpsStatusChanged(int event) {
                Log.e("onGpsStatusChanged", "fdf");
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                Location location = lm.getLastKnownLocation(provider); // 通过GPS获取位置
                updateToNewLocation(location);
            }
        });
        lm.requestLocationUpdates(provider, 100, 0, new LocationListener() {
            Location l;

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                switch (status) {
//GPS状态为可见时
                    case LocationProvider.AVAILABLE:
                        Log.i(TAG, "当前 GPS状态为可见状态");
                        break;
//GPS状态为服务区外时
                    case LocationProvider.OUT_OF_SERVICE:
                        Log.i(TAG, "当前 GPS状态为服务区外状态 ");
                        break;
//GPS状态为暂停服务时
                    case LocationProvider.TEMPORARILY_UNAVAILABLE:
                        Log.i(TAG, "当前 GPS状态为暂停服务状态 ");
                        break;
                }
            }

            @Override
            public void onProviderEnabled(String provider) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                l = lm.getLastKnownLocation(provider);
//updateToNewLocation(l);
            }

            @Override
            public void onProviderDisabled(String provider) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                l = lm.getLastKnownLocation(provider);
//updateToNewLocation(l);

            }

            @Override
            public void onLocationChanged(Location location) {
                updateToNewLocation(l);

            }
        });
    }


    public void updateToNewLocation(Location location) {
        Log.e("updateToNewLocation", "fdf");
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
//Toast.makeText(this,latitude+"："+longitude,Toast.LENGTH_SHORT).show();
            LogUtil.E(TAG, "时间：" + location.getTime());
            LogUtil.E(TAG, "经度： " + location.getLongitude());
            LogUtil.E(TAG, "纬度： " + location.getLatitude());
            LogUtil.E(TAG, "海拔： " + location.getAltitude());
        }
    }
}
