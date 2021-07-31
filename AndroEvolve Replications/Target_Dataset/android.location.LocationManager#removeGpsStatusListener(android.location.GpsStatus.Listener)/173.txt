package com.mapserverframework.lvn;

import android.content.Context;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * ロケーションコントローラクラス        
 * @author Ryuichi Tanaka
 * @since 0.0.1
 */
public class LocationController implements LocationListener, GpsStatus.Listener {
    /** ロケーションマネージャ */
    private LocationManager manager;
    /** コンテキスト */
    private Context context;
    
    /**
     * コンストラクタ
     * @param context コンテキスト
     */
    public LocationController(Context context) {
        this.context = context;
        manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        manager.addGpsStatusListener(this);
        update();
    }
    
    /**
     * GPSロケーションを開始
     */
    public void update() {
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, this);
    }
    
    /**
     * GPSロケーションを削除
     */
    public void remove() {
        manager.removeUpdates(this);
        manager.removeGpsStatusListener(this);
    }

    /**
     * 位置情報が変化したら実行されるコールバック
     * @param location ロケーションオブジェクト
     */
    @Override
    public void onLocationChanged(Location location) {
        if (context == null) {
            return;
        }
        LocationContainer bean = new LocationContainer();
        bean.setLongitude(location.getLongitude());
        bean.setLatitude(location.getLatitude());
        
        float speed = location.getSpeed() * 60 * 24 / 1000;
        bean.setSpeed(speed);
        
        // Activityに取得した位置情報を返す
        ((MainActivity) context).onLocationChanged(bean);
    }

    @Override
    public void onProviderDisabled(String arg0) {
        // TODO 自動生成されたメソッド・スタブ
        
    }

    @Override
    public void onProviderEnabled(String arg0) {
        // TODO 自動生成されたメソッド・スタブ
        
    }

    @Override
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
        // TODO 自動生成されたメソッド・スタブ
    }

    @Override
    public void onGpsStatusChanged(int status) {
        String statusText = "initialize";
        switch (status) {
        case GpsStatus.GPS_EVENT_FIRST_FIX:
            statusText = "received";
            break;
        case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
            statusText = "running";
            break;
        case GpsStatus.GPS_EVENT_STARTED:
            statusText = "started";
            break;
        case GpsStatus.GPS_EVENT_STOPPED:
            statusText = "stopped";
            break;
        }
        
        ((MainActivity) context).onGpsStatusChanged(statusText);
    }
}
