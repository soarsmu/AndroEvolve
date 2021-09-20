package com.tgnourse.aprs;

import java.util.Calendar;
import java.util.List;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;

public class SensorDataCollector {
	
	// We need to keep track of all of these listeners so we can de-register them later.
	private GpsStatus.Listener gpsStatusListener;
	private GpsStatus.NmeaListener nmeaListener;
	private LocationListener locationListener;
	private LocationManager locationManager;
	
	// We need to keep track of all the sensor listeners too.
	private SensorManager sensorManager;
	private SensorEventListener sensorListener;
	
	// Most recent sensor values.
	Location lastLocation;
	
	public SensorDataCollector(LocationManager locationManager, SensorManager sensorManager) {
        // Create the various location listeners.
        this.locationManager = locationManager;
        gpsStatusListener = new MyGpsStatusListener();
        nmeaListener = new MyNmeaListener();
        locationListener = new MyLocationListener();
        
        // Create the sensor listeners.
        this.sensorManager = sensorManager;
        sensorListener = new MySensorListener();
	}
	
	public long getTime() {
		return System.currentTimeMillis();
	}
	
	public double[] getLocation() {
		if (lastLocation == null) {
			double[] zero = { (double) 0, (double) 0, 110001 };
			return zero;
		}

		double[] real = {lastLocation.getLatitude(), lastLocation.getLongitude(), 110001};
		return real;
		// double[] gps = { (double) 37.422, (double) -122.084, (double) 110001 };
	}
	
	public float[] getAccel() {
		float[] accel = { (float) 4.67, (float) 19.20, (float) -2.55 };
		return accel;
	}
	
	public float[] getGyro() {
		float[] gyro = { (float) 0.02, (float) -4.98, (float) -0.51 };
		return gyro;
	}
	
	public float[] getMag() {
		float[] mag = { (float) 21.22, (float) -38.71, (float) -43.06 };
		return mag;
	}
	
	public float[] getTemp() {
		float[] temp = { (float) 35.1 };
		return temp;
	}
	
	public float[] getPress() {
		float[] press = { (float) 1001.23 };
		return press;
	}
	
	public float[]  getLight() {
		float[] light = { (float) 3284 };
		return light;
	}
	public void registerListeners() {
    	// Re-register all of the listeners.
        if (!locationManager.addGpsStatusListener(gpsStatusListener)) {
        	Util.error("Couldn't add Gps Status Listener!");
        }
        
        if (!locationManager.addNmeaListener(nmeaListener)) {
        	Util.error("Couldn't add Nmea Listener!");
        }
        
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        
        // Register to listen to all the sensors.
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
        for (Sensor sensor : sensors) {
        	Util.log("Registering listener for " + sensor.getName());
        	sensorManager.registerListener(sensorListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
	}

	public void removeListeners() {
    	// Remove the location listeners.
    	locationManager.removeGpsStatusListener(gpsStatusListener);
    	locationManager.removeNmeaListener(nmeaListener);
    	locationManager.removeUpdates(locationListener);
    	
    	// Remove the sensor listen
    	sensorManager.unregisterListener(sensorListener);
	}
	
    private class MyLocationListener implements LocationListener {

		public void onLocationChanged(Location location) {
			Util.log("New location: " + location);
			String line = "GPS," + System.currentTimeMillis() + "," + 
				location.getAccuracy() + "," + location.getAltitude() + "," + 
				location.getBearing() + "," + location.getLatitude() + "," + 
				location.getLongitude() + "," + location.getSpeed() + "," + 
				location.getTime();
			Util.log(line);
			lastLocation = location;
		}

		public void onProviderDisabled(String provider) {
			Util.log("Provider disabled: " + provider);
		}

		public void onProviderEnabled(String provider) {
			Util.log("Provider enabled: " + provider);
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			Util.log("Provider status changed: " + provider);
			switch (status) {
				case LocationProvider.AVAILABLE:
					Util.log(provider + " is available.");
				case LocationProvider.TEMPORARILY_UNAVAILABLE:
					Util.log(provider + " is temporarily unavailable.");
				case LocationProvider.OUT_OF_SERVICE:
					Util.log(provider + " is out of service.");
			}
		}
    }
    
    private class MyGpsStatusListener implements GpsStatus.Listener {
		public void onGpsStatusChanged(int event) {
			switch (event) {
				case GpsStatus.GPS_EVENT_FIRST_FIX:
					Util.log("First fix");
					break;
				case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
					Util.log("Satellite status");
					break;
				case GpsStatus.GPS_EVENT_STARTED:
					Util.log("Started");
					break;
				case GpsStatus.GPS_EVENT_STOPPED:
					Util.log("Stopped");
					break;
			}
			// Request a new GpsStatus object instead of having one filled in.
			GpsStatus status = locationManager.getGpsStatus(null);
			Util.log("Max Satellites: " + status.getMaxSatellites());
			Util.log("Time to First Fix: " + status.getTimeToFirstFix());
		}
    }
    
    private class MyNmeaListener implements GpsStatus.NmeaListener {
		public void onNmeaReceived(long timestamp, String nmea) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(timestamp);
			Util.log(calendar.getTime().toString() + "] " + nmea);
		}
    }
    
    private class MySensorListener implements SensorEventListener {
    	
    	private double getMagnitude(float x, float y, float z) {
    		return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
    	}

		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			Util.log("Sensor accuracy changed " + sensor.getName() + ", " + accuracy);
		}

		public void onSensorChanged(SensorEvent event) {
			StringBuffer values = new StringBuffer();
			for (float value : event.values) {
				values.append(value);
				values.append(':');
			}
			String line = "SNS," + System.currentTimeMillis() + "," +
				event.sensor.getType() + "," + "\"" + event.sensor.getName() + "\"," +
				event.timestamp + "," + event.accuracy + "," + values; 
			// Util.log(line);
			
			if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER && event.values.length == 3) {
				double force = getMagnitude(event.values[0], event.values[1], event.values[2]);
			}
		}	
    }
}
