package com.example.NLSUbiPos.context;

import java.util.Iterator;

import android.content.Context;
import android.hardware.SensorManager;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class GPSAdmin {

	private int GPSN;
	private double GPSSNR;
	private double maxSNR;
	private boolean GPSOK;
    LocationManager locationManager;
    LocationListener locationListener;
  
    
	public GPSAdmin(Context context){
		locationManager=(LocationManager)context.getSystemService(Context.LOCATION_SERVICE);	
		locationManager.addGpsStatusListener(listener);
	}
	
	GpsStatus.Listener listener = new GpsStatus.Listener() {
		@Override
		public void onGpsStatusChanged(int event) {
            switch (event) {
            case GpsStatus.GPS_EVENT_FIRST_FIX:
                break;
            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                GpsStatus gpsStatus=locationManager.getGpsStatus(null);
                int maxSatellites = gpsStatus.getMaxSatellites();
                Iterator<GpsSatellite> iters = gpsStatus.getSatellites().iterator();
                double E=0;
                int count = 0;
                while (iters.hasNext() && count <= maxSatellites) {     
                    GpsSatellite s = iters.next();
                    if(s.usedInFix()){
                    E+=s.getSnr();
                    count++;    } 
                    if (s.getSnr() > maxSNR){
		        		   maxSNR = (int) s.getSnr();
		        			
		        	   }   
                }   
                if (count==0) GPSSNR=0;
                else GPSSNR=E/count;
                GPSN=count;
                System.out.println("Satelite N:"+count+", SNR:"+GPSSNR);
                GPSOK=true;
                
                break;
            case GpsStatus.GPS_EVENT_STARTED:
                break;
            case GpsStatus.GPS_EVENT_STOPPED:
                break;
            }
		}
	};
	
	

	public int getGPSN()
	{
		return GPSN;
	}
	
	
	public double getmaxSNR()
	{
	return maxSNR;	
	}
	
	
	public double getSNR(){
		return GPSSNR;
	}
	
	
	public boolean checkGPSOK()
	{
		return GPSOK;
	}
	
	



	
	
}
