package com.example.device;

import java.util.HashMap;
import java.util.Map;

import com.example.device.bean.Satellite;
import com.example.device.util.Utils;
import com.example.device.widget.CompassView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by ouyangshen on 2016/11/4.
 */
public class FindSmellActivity extends AppCompatActivity {
	private final static String TAG = "FindSmellActivity";
	private TextView tv_satellite;
	private CompassView cv_satellite;
	private Map<Integer, Satellite> mapSatellite = new HashMap<Integer, Satellite>();
	private LocationManager mLocationMgr;
	private Criteria mCriteria = new Criteria();
	private Handler mHandler = new Handler();
	private boolean bLocationEnable = false;
	private String mLocationType="";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find_smell);
		tv_satellite = (TextView) findViewById(R.id.tv_satellite);
		cv_satellite = (CompassView) findViewById(R.id.cv_satellite);
		initLocation();
		mHandler.postDelayed(mRefresh, 100);
	}

	private void initLocation() {
		mLocationMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mCriteria.setAccuracy(Criteria.ACCURACY_FINE);
		mCriteria.setAltitudeRequired(true);
		mCriteria.setBearingRequired(true);
		mCriteria.setCostAllowed(true);
		mCriteria.setPowerRequirement(Criteria.POWER_LOW);
		
		String bestProvider = mLocationMgr.getBestProvider(mCriteria, true);
		if (bestProvider == null) {
			bestProvider = LocationManager.NETWORK_PROVIDER;
		}
		if (mLocationMgr.isProviderEnabled(bestProvider)) {
			mLocationType = bestProvider;
    		beginLocation(bestProvider);
			bLocationEnable = true;
		} else {
			bLocationEnable = false;
		}
	}

	@SuppressLint("DefaultLocale")
	private void setLocationText(Location location) {
		if (location != null) {
			String desc = String.format("当前定位类型：%s，定位时间：%s" +
					"\n经度：%f，纬度：%f\n高度：%d米，精度：%d米", 
					mLocationType, Utils.getNowTime(),
					location.getLongitude(), location.getLatitude(), 
					Math.round(location.getAltitude()), Math.round(location.getAccuracy()));
			tv_satellite.setText(desc);
		} else {
			Log.d(TAG, "暂未获取到定位对象");
		}
	}

    private void beginLocation(String method) {
		mLocationMgr.requestLocationUpdates(method, 300, 0, mLocationListener);
		Location location = mLocationMgr.getLastKnownLocation(method);
		setLocationText(location);
		mLocationMgr.addGpsStatusListener(mStatusListener);
    }

	// 位置监听器
	private LocationListener mLocationListener = new LocationListener() {
		@Override
		public void onLocationChanged(Location location) {
			setLocationText(location);
		}

		@Override
		public void onProviderDisabled(String arg0) {
		}

		@Override
		public void onProviderEnabled(String arg0) {
		}

		@Override
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		}
	};

	private Runnable mRefresh = new Runnable() {
		@Override
		public void run() {
			if (bLocationEnable == false) {
				initLocation();
				mHandler.postDelayed(this, 1000);
			}
		}
	};

	@Override
	protected void onDestroy() {
		if (mLocationMgr != null) {
			mLocationMgr.removeGpsStatusListener(mStatusListener);
			mLocationMgr.removeUpdates(mLocationListener);
		}
		super.onDestroy();
	}
	
	private GpsStatus.Listener mStatusListener = new GpsStatus.Listener() {

		@Override
		public void onGpsStatusChanged(int event) {
			GpsStatus gpsStatus = mLocationMgr.getGpsStatus(null);
			switch (event) {
			case GpsStatus.GPS_EVENT_FIRST_FIX:
				break;
			case GpsStatus.GPS_EVENT_SATELLITE_STATUS: // 周期的报告卫星状态
				// 得到所有收到的卫星的信息，包括 卫星的高度角、方位角、信噪比、和伪随机号（及卫星编号）
				Iterable<GpsSatellite> satellites = gpsStatus.getSatellites();
				for (GpsSatellite satellite : satellites) {
					/*
					 * satellite.getElevation(); //卫星的仰角 (卫星的高度)
					 * satellite.getAzimuth(); //卫星的方位角
					 * satellite.getSnr(); //卫星的信噪比
					 * satellite.getPrn(); //卫星的伪随机码，可以认为就是卫星的编号
					 * satellite.hasAlmanac(); //卫星是否有年历表
					 * satellite.hasEphemeris(); //卫星是否有星历表
					 * satellite.usedInFix(); //卫星是否被用于近期的GPS修正计算
					 */
					Satellite item = new Satellite();
					item.seq = satellite.getPrn();
					item.signal = Math.round(satellite.getSnr());
					item.elevation = Math.round(satellite.getElevation());
					item.azimuth = Math.round(satellite.getAzimuth());
					item.time = Utils.getNowDateTime();
					if (item.seq <= 64 || (item.seq >= 120 && item.seq <= 138)) {
						item.nation = "美国";
						item.name = "GPS";
					} else if (item.seq >= 201 && item.seq <= 237) {
						item.nation = "中国";
						item.name = "北斗";
					} else if (item.seq >= 65 && item.seq <= 89) {
						item.nation = "俄罗斯";
						item.name = "格洛纳斯";
					} else {
						item.nation = "其他";
						item.name = "未知";
					}
					mapSatellite.put(item.seq, item);
				}
				cv_satellite.setSatelliteMap(mapSatellite);
			case GpsStatus.GPS_EVENT_STARTED:
				break;
			case GpsStatus.GPS_EVENT_STOPPED:
				break;
			default:
				break;
			}
		}
		
	};

}
