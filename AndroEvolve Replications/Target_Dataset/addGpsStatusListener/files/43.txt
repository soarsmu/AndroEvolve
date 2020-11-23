package com.beastbikes.android.modules.cycling.activity.util;

import android.content.Context;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.LocationManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by caoxiao on 16/4/22.
 */
public abstract class GpsStatusObserve implements GpsStatus.Listener {

    private LocationManager locationListener;
    private List<GpsSatellite> numSatelliteList = new ArrayList<>(); // 卫星信号
    private static final Logger logger = LoggerFactory.getLogger(GpsStatusObserve.class);
//    private int count;

    private boolean hasLocation;

    public GpsStatusObserve(Context context) {
        locationListener = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public void onGpsStatusChanged(int event) {
        switch (event) {
            //第一次定位
            case GpsStatus.GPS_EVENT_FIRST_FIX:
                logger.info("第一次定位");
                break;
            //卫星状态改变
            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                GpsStatus status = locationListener.getGpsStatus(null); // 取当前状态
                updateGpsStatus(event, status);
                break;
            //定位启动
            case GpsStatus.GPS_EVENT_STARTED:
                logger.info("定位启动");
                break;
            //定位结束
            case GpsStatus.GPS_EVENT_STOPPED:
                logger.info("定位结束");
                break;
        }
    }

    private void updateGpsStatus(int event, GpsStatus status) {
        if (status == null) {
        } else if (event == GpsStatus.GPS_EVENT_SATELLITE_STATUS) {
            Iterator<GpsSatellite> it = status.getSatellites().iterator();
            numSatelliteList.clear();
            int gpsStatus = 0;//总信噪比之和
            while (it.hasNext()) {
                GpsSatellite s = it.next();
                if (s.getSnr() > 0.0) {
                    //信号信噪比不为0计入GPS信号
                    numSatelliteList.add(s);
                    gpsStatus += s.getSnr();
                }
            }
            if (gpsStatus > 150) {
                this.onLocationSuccess();
                this.hasLocation = true;
            } else if (hasLocation) {
                this.onLocationFailed();
                this.hasLocation = false;
            }
        }
    }

    public abstract void onLocationSuccess();

    public abstract void onLocationFailed();

    public void addGpsStatusListener() {
        locationListener.addGpsStatusListener(this);
    }

    public void removeGpsStatusListener() {
        locationListener.removeGpsStatusListener(this);
    }

}
