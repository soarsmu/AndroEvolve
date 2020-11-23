package org.morphone.sense.location;

import android.content.Context;
import android.location.GpsStatus;
import android.location.LocationManager;

public class LocationSense implements LocationSenseInterface {

	LocationManager locationManager;
	MyGPSListener gpsListener;
	
	boolean isGPSOn = false;
	int gpsStatus = -1;
	
	public LocationSense(LocationManager loc){	
		this.locationManager = loc;
		
		gpsListener = new MyGPSListener();
		locationManager.addGpsStatusListener(gpsListener);
		
		// Init vars
		if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
			gpsStatus = GpsStatus.GPS_EVENT_STARTED;
			isGPSOn = true;
		}else{
			gpsStatus = GpsStatus.GPS_EVENT_STOPPED;
			isGPSOn = false;
		}
	}
	
	public LocationSense(Context context){	
		this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		
		gpsListener = new MyGPSListener();
		locationManager.addGpsStatusListener(gpsListener);
		
		// Init vars
		if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
			gpsStatus = GpsStatus.GPS_EVENT_STARTED;
			isGPSOn = true;
		}else{
			gpsStatus = GpsStatus.GPS_EVENT_STOPPED;
			isGPSOn = false;
		}
	}
	
	@Override
	public boolean isGPSOn() throws LocationSenseException{
		/* Old implementation
		try{
			isGPSOn = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			if(isGPSOn)
				return true;
			else 
				return false;
			
			return isGPSOn;
		} catch(Exception e){
	    	throw new LocationSenseException("Error while getting GPSon (" + e.getMessage() + ")");
	    }
		*/
		
		return isGPSOn;
	}
	
	@Override
	public int getCurrentProviderStatus() throws LocationSenseException{
		if(gpsStatus < 0)
			throw new LocationSenseException("Error while getting CurrentProviderStatus");
		else
			return gpsStatus;
	}
	
	private class MyGPSListener implements GpsStatus.Listener {
		
		@Override
		public void onGpsStatusChanged(int event) {
			gpsStatus = event;
			if(gpsStatus == GpsStatus.GPS_EVENT_STOPPED)
				isGPSOn = false;
			else
				isGPSOn = true;
	    }

	}
}
