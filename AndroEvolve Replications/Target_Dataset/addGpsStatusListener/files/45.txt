package com.movementinsome.kernel.location.trace;

import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.CoordinateConverter;
import com.amap.api.maps.model.LatLng;
import com.movementinsome.caice.util.BaiduCoordinateTransformation;
import com.movementinsome.kernel.location.LocationInfoExt;
import com.movementinsome.kernel.util.DeviceCtrlTools;
import com.movementinsome.kernel.util.MyDateTools;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class Gps {
	private Location location = null;
	private LocationManager locationManager = null;
	private Context context = null;
	private long minTime = 10;
	private float minDistance = 0;
	private Geocoder geocoder;
	private int satellites = 0;
	private int timeToFirstFix = 0;

	/**
	 * 初始化
	 * 
	 * @param ctx
	 */
	public Gps(Context ctx) {
		context = ctx;
		locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			//DeviceCtrlTools.openGpsSwitch(ctx);
			DeviceCtrlTools.toggleGPS(ctx);
		}
		location = locationManager.getLastKnownLocation(getProvider());
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				minTime, minDistance, locationListener);
		// 绑定监听状态
		locationManager.addGpsStatusListener(gpsListener);
		geocoder = new Geocoder(context);
	}

	public Gps(Context ctx, long minTime, float minDistance) {
		context = ctx;
		this.minTime = minTime;
		this.minDistance = minDistance;
		locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			//DeviceCtrlTools.openGpsSwitch(ctx);
			DeviceCtrlTools.toggleGPS(ctx);			
		}
		String provider = getProvider();
		if (provider != null) {
			location = locationManager.getLastKnownLocation(provider);
		} else {
			List<String> providers = locationManager.getAllProviders();

			if (location == null) {
				for (String p : providers) {
					try {
						Thread.sleep(1000);
						if (locationManager.isProviderEnabled(p))
							location = locationManager.getLastKnownLocation(p);
						if (location != null)
							break;
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
			}

		}
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				minTime, minDistance, locationListener);
		
		locationManager.addGpsStatusListener(gpsListener);
		geocoder = new Geocoder(context);

		// 绑定监听状态
		// locationManager.addGpsStatusListener(gpsListener);

		/*if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			location = locationManager.getLastKnownLocation(getProvider());
			if (location == null) {
				// List<String> list=lm.getAllProviders();
				locationManager.requestLocationUpdates(
						LocationManager.GPS_PROVIDER, minTime, minDistance,
						locationListener);
				location = locationManager
						.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				if (location != null) {
					locationManager.requestLocationUpdates(
							LocationManager.NETWORK_PROVIDER, minTime,
							minDistance, locationListener);
					location = locationManager
							.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				}
			}
		}*/
	}

	// 获取Location Provider
	private String getProvider() {
		/*
		 * // 构建位置查询条件 Criteria criteria = new Criteria();
		 * 
		 * // 查询精度：高 criteria.setAccuracy(Criteria.ACCURACY_FINE); // 是否查询海拨：否
		 * criteria.setAltitudeRequired(true); // 是否查询方位角 : 否
		 * criteria.setBearingRequired(true); // 是否允许付费：是
		 * criteria.setCostAllowed(true); // 电量要求：低
		 * criteria.setPowerRequirement(Criteria.POWER_LOW);
		 */

		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);// 设置为最大精度
		criteria.setAltitudeRequired(true);// 要求海拔信息
		criteria.setBearingRequired(true);// 要求方位信息
		criteria.setBearingAccuracy(Criteria.ACCURACY_HIGH);// 要求方位信息 的精确度
		criteria.setCostAllowed(true);// 产生开销  通信费用
		criteria.setPowerRequirement(Criteria.POWER_MEDIUM);// 对电量的要求中等，POWER_LOW（低）
		criteria.setSpeedAccuracy(criteria.ACCURACY_FINE);// 对速度的精确度
		criteria.setHorizontalAccuracy(Criteria.ACCURACY_FINE);// 对水平的精确度
		criteria.setSpeedRequired(true);// 要求速度信息
		criteria.setVerticalAccuracy(Criteria.ACCURACY_MEDIUM);// 对垂直精度中，高程不是很重要。.ACCURACY_HIGH（高）
		// String providerFalse = locationManager.getBestProvider(criteria,
		// false);// 找到最好的Provider不管是否能用。
		// String providerTrue = locationManager.getBestProvider(criteria,
		// true);// 找到最好的能用的Provider。
		// 返回最合适的符合条件的provider，第2个参数为true说明 , 如果只有一个provider是有效的,则返回当前provider
		return locationManager.getBestProvider(criteria, true);
	}

	private LocationListener locationListener = new LocationListener() {
		// 位置发生改变后调用
		public void onLocationChanged(Location l) {
			if (l != null) {
				location = l;
			}
		}

		// provider 被用户关闭后调用
		public void onProviderDisabled(String provider) {
			location = null;
		}

		// provider 被用户开启后调用
		public void onProviderEnabled(String provider) {
			if (locationManager == null)
				return ;
			Location l = locationManager.getLastKnownLocation(provider);
			if (l != null) {
				location = l;
			}

		}

		// provider 状态变化时调用
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

	};

	public LocationInfoExt getLocation() {
		if (timeToFirstFix==0){
			return null;
		}
		if (location != null) {

	
			List<Address> addList = null;
			String addressName = "";
			String zoning = "";
			try {
				addList = geocoder.getFromLocation(location.getLatitude(),
						location.getLongitude(), 1); // 解析经纬度
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (addList != null && addList.size() > 0) {
	            // Get the first address
	            Address address = addList.get(0);
	            /*
	            * Format the first line of address (if available),
	            * city, and country name.
	            */
	            addressName = address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "";
	            
	            String mcityName = (address.getAdminArea()==null?"":address.getAdminArea())+address.getLocality();
				
				//不同地方是什么值还未经证实，目前只知道大城市采用区划 addres.getSubLocality();
				if (null != address.getSubAdminArea()){
					zoning = address.getSubAdminArea();
				} else if (null != address.getSubLocality()){
					zoning = address.getSubLocality();
				}
				
				addressName = addressName.replaceFirst(mcityName, "");
	         } 
			
			/*if (addList != null && addList.size() > 0) {
				String mcityName = "";
				for (int i = 0; i < addList.size(); i++) {
					Address addres = addList.get(i);
					// mcityName += addres.getLocality();

					mcityName = (addres.getAdminArea()==null?"":addres.getAdminArea())+addres.getLocality();
					
					//不同地方是什么值还未经证实，目前只知道大城市采用区划 addres.getSubLocality();
					if (null != addres.getSubAdminArea()){
						zoning = addres.getSubAdminArea();
					} else if (null != addres.getSubLocality()){
						zoning = addres.getSubLocality();
					}
					
					addressName = addres.getAddressLine(0).replaceFirst(mcityName, "");
					
					if (!"中国".equals(addressName)){
						break;
					}
				}
			}*/

/*			// 获取卫星颗数的默认最大值
			satellites = 0;
			for (Iterator itr = gpsStatus.getSatellites().iterator(); itr
					.hasNext();) {
				satellites++;
			}
			
			// 获取卫星颗数的默认最大值  
            int maxSatellites = gpsStatus.getMaxSatellites();  
            // 创建一个迭代器保存所有卫星  
            Iterator<GpsSatellite> iters = gpsStatus.getSatellites()  
                    .iterator();  
            satellites = 0;  
            while (iters.hasNext() && satellites <= maxSatellites) {  
                GpsSatellite s = iters.next();  
                satellites++;  
            } */
			AMapLocation aMapLocation=new AMapLocation(location);

			LocationInfoExt locationInfo = new LocationInfoExt(
					location.getLatitude(), location.getLongitude(),
					location.getAltitude(), location.getAccuracy(),
					location.getSpeed(), location.getBearing(),
					MyDateTools.long2String(location.getTime(),
							MyDateTools.S_DATE_FORMAT), 0, 0,
					location.getProvider(), addressName, satellites,zoning);
			LatLng latLng_wgs84=new LatLng(location.getLatitude(),location.getLongitude());
			LatLng latLng_bd09=BaiduCoordinateTransformation.toGcj02(latLng_wgs84.longitude,latLng_wgs84.latitude, CoordinateConverter.CoordType.GPS);
			locationInfo.setLongitude_gcj02(latLng_bd09.longitude);
			locationInfo.setLatitude_gcj02(latLng_bd09.latitude);
			return locationInfo;
		} else {
			return null;
		}

	}

	// 状态监听
	private GpsStatus.Listener gpsListener = new GpsStatus.Listener() {
		public void onGpsStatusChanged(int event) {
			// 获取当前状态
			GpsStatus gpsStatus = locationManager.getGpsStatus(null);
			switch (event) {
			// 第一次定位
			case GpsStatus.GPS_EVENT_FIRST_FIX:
				// Log.i(TAG, "第一次定位");
				timeToFirstFix = gpsStatus.getTimeToFirstFix();
				break;
			// 卫星状态改变
			case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
				// Log.i(TAG, "卫星状态改变");

				// 获取卫星颗数的默认最大值
				// 获取卫星颗数的默认最大值  
	            int maxSatellites = gpsStatus.getMaxSatellites();  
	            // 创建一个迭代器保存所有卫星  
	            Iterator<GpsSatellite> iters = gpsStatus.getSatellites()  
	                    .iterator();  
	            satellites = 0;  
	            while (iters.hasNext() && satellites <= maxSatellites) {  
	                GpsSatellite s = iters.next();  
	                satellites++;  
	            } 
				break;
			// 定位启动
			case GpsStatus.GPS_EVENT_STARTED:
				// Log.i(TAG, "定位启动");
				break;
			// 定位结束
			case GpsStatus.GPS_EVENT_STOPPED:
				// Log.i(TAG, "定位结束");
				break;
			}
		};
	};

	public void closeLocation() {
		if (locationManager != null) {
			if (locationListener != null) {
				locationManager.removeUpdates(locationListener);
				locationManager.removeGpsStatusListener(gpsListener);
				locationListener = null;
				gpsListener = null;
			}
			locationManager = null;
		}
	}
}
