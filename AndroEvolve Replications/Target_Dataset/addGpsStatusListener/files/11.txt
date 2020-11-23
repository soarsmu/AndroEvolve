/* Original work: Copyright 2009 Google Inc. All Rights Reserved.
 
   Modified work: The original source code (AndroidNdt.java) comes from the NDT Android app
                  that is available from http://code.google.com/p/ndt/.
                  It's modified for the CalSPEED Android app by California 
                  State University Monterey Bay (CSUMB) on April 29, 2013.
*/

package gov.ca.cpuc.calspeed.android;

import android.content.Context;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;

import gov.ca.cpuc.calspeed.android.CalspeedFragment.LatLong;
import gov.ca.cpuc.calspeed.android.CalspeedFragment.NetworkLatLong;

import java.util.Iterator;

/**
 * Handle the location related functions and listeners.
 */
public class NdtLocation implements LocationListener {
  /**
   * Location variable, publicly accessible to provide access to geographic data.
   */
  public Location location;
  public Location networkLocation;
  public LocationManager locationManager;
  private Criteria criteria;
  public String bestProvider;
  public Boolean gpsEnabled;
  public Boolean networkEnabled;
  private Context context;
  private AndroidUiServices uiServices;
  public Location NetworkLastKnownLocation;
  public Location GPSLastKnownLocation;
  public NetworkLatLong networkLatLong;
  public LatLong latLongptr;
 


  /**
   * Passes context to this class to initialize members.
   * 
   * @param context context which is currently running
   */
  public NdtLocation(Context context,AndroidUiServices uiServices,NetworkLatLong networkLatLong,
		  LatLong latLongptr) {
	this.context = context;
	this.uiServices = uiServices;
	this.networkLatLong = networkLatLong;
	this.latLongptr = latLongptr;
    locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    Iterator<String> providers = locationManager.getAllProviders().iterator();
    location = null;
    networkLocation = new Location(LocationManager.NETWORK_PROVIDER);
    networkLocation.setLatitude(0.0);
    networkLocation.setLongitude(0.0);
    while(providers.hasNext()) {
        Log.v("debug", providers.next());
    }
    criteria = new Criteria();
    criteria.setAccuracy(Criteria.ACCURACY_FINE);
    criteria.setCostAllowed(true);
    criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);
    
    bestProvider = locationManager.getBestProvider(criteria, true);
    Log.v("debug","Best provider is:"+ bestProvider);
    addGPSStatusListener();
    addNetworkListener();
  }
  public void addGPSStatusListener(){
	 if (locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER)){ 
	    locationManager.addGpsStatusListener(onGpsStatusChange) ;
	    gpsEnabled = true;
	 }
	 else{
	    gpsEnabled = false;
	 }
  }
  public void removeGPSStatusListener(){
	  if (locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER)){
		  locationManager.removeGpsStatusListener(onGpsStatusChange);
		  gpsEnabled = false;
	  }
  }
  public void addNetworkListener(){
	  if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
		  locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 0, NetworkLocationListener);
		  networkEnabled = true;
	  }else{
		  networkEnabled = false;
	  }
  }
  public void stopNetworkListenerUpdates(){
	  if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
		  locationManager.removeUpdates(NetworkLocationListener);
		  networkEnabled = false;
	  }
  }
  public void startNetworkListenerUpdates(){
	  if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
		  locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 0, NetworkLocationListener);
		  networkEnabled = true;
	  }
  }
  //Define a listener that responds to Network location updates
  LocationListener NetworkLocationListener = new LocationListener() {
    public void onLocationChanged(Location location) {
 	
	    networkLatLong.updateNetworkLatitudeLongitude(location);
	    networkLocation.set(location);
    }
 
    public void onStatusChanged(String provider, int status, Bundle extras) {}
 
    public void onProviderEnabled(String provider) {}
 
    public void onProviderDisabled(String provider) {}
  };

  private final GpsStatus.Listener onGpsStatusChange=new GpsStatus.Listener()
  {
          public void onGpsStatusChanged(int event)
          {
                  switch( event )
                  {
                          case GpsStatus.GPS_EVENT_STARTED:
                                  // Started...
                        	  	startListen();
                        	  	latLongptr.updateLatitudeLongitude();
                        	  	if (Constants.DEBUG)
                        	  		Log.v("debug","GPS starting...\n");
                                  break ;
                          case GpsStatus.GPS_EVENT_FIRST_FIX:
                                  // First Fix...
                        	  if (Constants.DEBUG)
                        		  Log.v("debug","GPS first fix \n");
                        	  latLongptr.updateLatitudeLongitude();
                                  break ;
                          case GpsStatus.GPS_EVENT_STOPPED:
                                  // Stopped...
                        	  stopListen();
                        	  location = null;
                        	  latLongptr.updateLatitudeLongitude();
                        	  if (Constants.DEBUG)
                        		  Log.v("debug","GPS stopped.\n");
                                  break ;
                          case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                              GpsStatus xGpsStatus = locationManager.getGpsStatus(null) ;
                              Iterable<GpsSatellite> iSatellites = xGpsStatus.getSatellites() ;
                              Iterator<GpsSatellite> it = iSatellites.iterator() ;
                              while ( it.hasNext() )
                              {
                                      GpsSatellite oSat = (GpsSatellite) it.next() ;
                                      if (Constants.DEBUG)
                                    	  Log.v("debug","LocationActivity - onGpsStatusChange: Satellites: " +
                      oSat.getSnr() ) ;
                              }
                              break ; 
                  }
          }
  } ;

  @Override
  public void onLocationChanged(Location location) {
	  this.location = location;
	  latLongptr.updateLatitudeLongitude();

  }
 @Override
  public void onProviderDisabled(String provider) {
    stopListen();
    location = null;
    latLongptr.updateLatitudeLongitude();
  }

 @Override
  public void onProviderEnabled(String provider) {
    startListen();
    latLongptr.updateLatitudeLongitude();
  }
 @Override
  public void onStatusChanged(String provider, int status, Bundle extras) {
	 switch (status) {
		case LocationProvider.OUT_OF_SERVICE:
			if (Constants.DEBUG)
				Log.v("debug", "Status Changed: Out of Service");
			stopListen();
			location = null;
			break;
		case LocationProvider.TEMPORARILY_UNAVAILABLE:
			if (Constants.DEBUG)
				Log.v("debug", "Status Changed: Temporarily Unavailable");
			stopListen();
			location = null;
			break;
		case LocationProvider.AVAILABLE:
			if (Constants.DEBUG)
				Log.v("debug", "Status Changed: Available");
			startListen();
			break;
		}
  }

  /**
   * Stops requesting the location update.
   */
  public void stopListen() {

    locationManager.removeUpdates(this);
  }

  /**
   * Begins to request the location update.
   */
  public void startListen() {
    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0,
        this);
  }
}
