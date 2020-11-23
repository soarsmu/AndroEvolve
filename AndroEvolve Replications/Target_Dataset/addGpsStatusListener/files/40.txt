package com.example.takenotes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.GpsStatus.Listener;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Wgc_Speed extends Activity {

	private LocationManager locationManager;
	private GpsStatus gpsstates;
	private List<GpsSatellite> numSatelliteList = new ArrayList<GpsSatellite>(); // 卫星信号
	private TextView sure_num_satellite;
	private TextView relation;
	private TextView position;
	private ImageView speed;
	private boolean gps;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wgc_speed);

		sure_num_satellite = (TextView) findViewById(R.id.sure_num_satellite);
		relation = (TextView) findViewById(R.id.relation);
		position = (TextView) findViewById(R.id.position);
		speed = (ImageView) findViewById(R.id.speed);
		
		locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		if(gps){
			String currentProvider = locationManager.getProvider(LocationManager.GPS_PROVIDER).getName();
			locationManager.requestLocationUpdates(currentProvider, 2000, 1, locationListener);
			locationManager.addGpsStatusListener(gpsListener);
		}
	}
	
	private GpsStatus.Listener gpsListener = new Listener() {
		
		@Override
		public void onGpsStatusChanged(int event) {
			// TODO Auto-generated method stub
			gpsstates = locationManager.getGpsStatus(null);
			switch (event) {
			case GpsStatus.GPS_EVENT_FIRST_FIX:
				
				break;
			case GpsStatus.GPS_EVENT_STARTED:
				
				break;
			case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
				Iterator<GpsSatellite> it = gpsstates.getSatellites().iterator();
				numSatelliteList.clear();
				int count=0;
				while(it.hasNext() && count < gpsstates.getMaxSatellites()){
					GpsSatellite s = it.next();
					numSatelliteList.add(s);
					count++;
				}
				sure_num_satellite.setText("已校准:"+String.valueOf(numSatelliteList.size()));
				break;
			default:
				break;
			}
		}
	};
	
	private LocationListener locationListener = new LocationListener() {
		
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
			gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			if(!gps){
				return;
			}
			position.setText("速度:" + String.valueOf(location.getSpeed()) + " m/s");
			relation.setText("方向:" + String.valueOf(location.getBearing()));
			
			speed.setRotation((float) (location.getSpeed()*3.6));
			
		}
	};
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		if(gps){
			String currentProvider = locationManager.getProvider(LocationManager.GPS_PROVIDER).getName();
			locationManager.requestLocationUpdates(currentProvider, 2000, 1, locationListener);
			locationManager.addGpsStatusListener(gpsListener);
		}else{
			Toast.makeText(Wgc_Speed.this, "GPS 未打开", Toast.LENGTH_SHORT).show();
			locationManager.removeGpsStatusListener(gpsListener);
			locationManager.removeUpdates(locationListener);
		}
	}
	
}
