/**
 * 
 */
package com.zhonghong.autotest.client;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import com.zhonghong.autotest.bean.ATConst;
import com.zhonghong.autotest.bean.ATEvent;
import com.zhonghong.autotest.service.c11.ProcessManager;

import android.content.Context;
import android.location.GpsSatellite;
import android.location.GpsStatus.Listener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;

/**
 * @author YC
 * @time 2016-12-7 上午10:33:11
 * TODO:
 */
public class GPSManager extends ABSClient{

	private LocationManager mLocationManager;
	
	/**
	 * @param processManager
	 */
	public GPSManager(ProcessManager processManager) {
		super(processManager);
	}
	
	@Override
	public void onObjectCreate() {
		super.onObjectCreate();
		mLocationManager = (LocationManager) mProcessManager.getATService()
				.getSystemService(Context.LOCATION_SERVICE);
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 
				1000, 0, 
				mLocationListener);
		mLocationManager.addGpsStatusListener(mGPSListener);
	}
	
	@Override
	public void onObjectDestory() {
		mLocationManager.removeUpdates(mLocationListener);
		mLocationManager.removeGpsStatusListener(mGPSListener);
		super.onObjectDestory();
	}


	private LocationListener mLocationListener = new LocationListener() {
		
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			
		}
	};
	
	private Listener mGPSListener = new Listener() {
		
		@Override
		public void onGpsStatusChanged(int event) {
			
		}
	};

	@Override
	public boolean onDispatch(ATEvent event) {
		if (event.type == ATConst.GPS.TYPE_GPS){
			return onProcess(event.cmd, event.info);
		}
		return false;
	}
	
	@Override
	protected void onResponse(int cmd, String info) {
		if (mProcessManager != null){
			ATEvent atEvent = new ATEvent(ATConst.GPS.TYPE_GPS, cmd, info);
			mProcessManager.onResponseFromClient(atEvent);
		}
	}
	
	private boolean onProcess(int cmd, String info) {
		switch (cmd) {
		case ATConst.GPS.CMD_GPS_SET_GPS_ON_OFF:
			boolean bOn = false;
			if ("on".equals(info)){
				bOn = true;
			}
			if (turnOnOffGPS(bOn)){
				onResponse(ATConst.GPS.CMD_GPS_SET_GPS_ON_OFF, "ok");
			}
			else{
				onResponse(ATConst.GPS.CMD_GPS_SET_GPS_ON_OFF, "notok");
			}
			return true;
		case ATConst.GPS.CMD_GPS_GET_GPS_INFO:
			//TODO 没实现
			break;
		case ATConst.GPS.CMD_GPS_GET_GPS_NUMBER:
			if ("used_num".equals(info)){
				onResponse(ATConst.GPS.CMD_GPS_GET_GPS_TIME, "used_num::" + getStatelliteUsedNum());
			}
			else if ("visible_num".equals(info)){
				onResponse(ATConst.GPS.CMD_GPS_GET_GPS_TIME, "visible_num::" + getSatelliteTotalNum());
			}
			return true;
		case ATConst.GPS.CMD_GPS_GET_GPS_TIME:
			String time = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss")
			.format(new Date(getGPSTime()));
			onResponse(ATConst.GPS.CMD_GPS_GET_GPS_TIME, time);
			return true;

		default:
			break;
		}
		return false;
	}
	
	

	private boolean isGPSOpen(){
		return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}
	
	private boolean turnOnOffGPS(boolean on){
		if (on){
			if (!isGPSOpen()){
				mLocationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true);
				return true;
			}
		}
		else{
			if (isGPSOpen()){
				mLocationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, false);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 卫星总数
	 * @return
	 */
	private int getSatelliteTotalNum(){
		int count = 0;
		Iterator<GpsSatellite> iterator = mLocationManager.getGpsStatus(null).getSatellites().iterator();
		while(iterator.hasNext()){
			if (iterator.next().getSnr() != 0){
				count++;
			}
	     }
		return count;
	}
	
	/**
	 * 使用中的卫星个数
	 * @return
	 */
	private int getStatelliteUsedNum(){
		int count = 0;
		Iterator<GpsSatellite> iterator = mLocationManager.getGpsStatus(null).getSatellites().iterator();
		while(iterator.hasNext()){
			GpsSatellite satellite = iterator.next();
			if (satellite.usedInFix()){
				count++;
			}
	     }
		return count;
	}
	
	/**
	 * get UTC Time since 1970.1.1
	 * @return
	 */
	private long getGPSTime(){
		Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		return location.getTime();
	}
	
	/**
	 * m/s
	 * @return
	 */
	private float getGPSSpeed(){
		Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		return location.getSpeed();
	}

	
}
