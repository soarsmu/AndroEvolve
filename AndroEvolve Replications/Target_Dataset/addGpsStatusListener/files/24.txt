package ch.supsi.ist.camre.paths;

import android.app.Fragment;
import android.content.IntentSender;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class GeolocationWorker extends Fragment implements GoogleApiClient.ConnectionCallbacks ,
        GoogleApiClient.OnConnectionFailedListener, GpsStatus.Listener{

    private final static int
            CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private LocationManager locationManager;
    private GoogleApiClient mGoogleApiClient;

    //private ActivityRecognitionClient mActivityRecognitionClient;

    // Define an object that holds accuracy and frequency parameters
    LocationRequest mLocationRequest;

    // Milliseconds per second
    private static final int MILLISECONDS_PER_SECOND = 1000;

    // Update frequency in seconds
    public static final int UPDATE_INTERVAL_IN_SECONDS = 4;
    // The fastest update frequency, in seconds
    private static final int FASTEST_INTERVAL_IN_SECONDS = 2;

    // Update frequency in milliseconds
    private static final long UPDATE_INTERVAL =
            MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;

    // A fast frequency ceiling in milliseconds
    private static final long FASTEST_INTERVAL =
            MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;

    private ArrayList<LocationListener> locationListeners = new ArrayList<LocationListener>();
    private ArrayList<GpsStatus.Listener> gpsListeners = new ArrayList<GpsStatus.Listener>();

    public GeolocationWorker() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> GeolocationWorker: onCreateView");
        View view = inflater.inflate(R.layout.fragment_geolocation, container, false);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> GeolocationWorker: onCreate");

        super.onCreate(savedInstanceState);

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        /*
        >> http://blog.lemberg.co.uk/fused-location-provider
        Priority	    Typical location update interval	Battery drain per hour (%)	Accuracy
        **************  ********************************    **************************  ***********
        HIGH_ACCURACY	5 seconds	                        7.25%	                    ~10 meters
        BALANCED_POWER	20 seconds	                        0.6%	                    ~40 meters
        NO_POWER	    N/A	                                small	                    ~1 mile
        */

        // Create the LocationRequest object
        // Use high accuracy
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                //.setNumUpdates(1)
                .setFastestInterval(FASTEST_INTERVAL);

        locationManager = (LocationManager) getActivity().getSystemService(WalkerActivityTest.LOCATION_SERVICE);

        /*mActivityRecognitionClient =
                new ActivityRecognitionClient(getActivity(), new GooglePlayServicesClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {

                    }

                    @Override
                    public void onDisconnected() {

                    }
                }, this);*/

        setRetainInstance(true);
    }

    public void connectGPS(){
        // Connect the client.
        mGoogleApiClient.connect();
    }

    public void disconnectGps(){
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onStart() {
        System.out.println("LocationWorker: onStart");
        super.onStart();
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> GeolocationWorker: onStart");
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        System.out.println("LocationWorker: onStop");
        super.onStop();
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> GeolocationWorker: onStop");
        mGoogleApiClient.disconnect();
    }

    public void addFusionLocationListener(LocationListener listener){
        if(!locationListeners.contains(listener)) {
            locationListeners.add(listener);
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.requestLocationUpdates(
                        mGoogleApiClient, mLocationRequest, listener);
            }
        }
    }


    public void addGpsStatusListener(GpsStatus.Listener listener){
        if(!gpsListeners.contains(listener)) {
            gpsListeners.add(listener);
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                locationManager.addGpsStatusListener(listener);
            }
        }
    }

    public void removeFusionLocationListener(LocationListener listener){
        if(locationListeners.contains(listener)) {
            locationListeners.remove(listener);
            if(mGoogleApiClient!=null && mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(
                        mGoogleApiClient, listener);
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        System.out.println("LocationWorker: onConnected");
        if(locationListeners.size()>0){
            for (LocationListener listener: locationListeners){
                LocationServices.FusedLocationApi.requestLocationUpdates(
                        mGoogleApiClient, mLocationRequest, listener);
            }
        }
        if(gpsListeners.size()>0){
            for (GpsStatus.Listener listener: gpsListeners) {
                locationManager.addGpsStatusListener(listener);
            }
        }
        locationManager.addGpsStatusListener(this);
    }

    @Override
    public void onGpsStatusChanged(int event) {

        switch (event){

            case GpsStatus.GPS_EVENT_STARTED:
                System.out.println(" >>> GpsStatus.GPS_EVENT_STARTED");
                break;
            case GpsStatus.GPS_EVENT_STOPPED:
                System.out.println(" >>> GpsStatus.GPS_EVENT_STOPPED");
                break;
            case GpsStatus.GPS_EVENT_FIRST_FIX:
                System.out.println(" >>> GpsStatus.GPS_EVENT_FIRST_FIX");
                break;
            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                System.out.println(" >>> GpsStatus.GPS_EVENT_SATELLITE_STATUS");
                break;

        }

        GpsStatus st = locationManager.getGpsStatus(null);

        Location gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location netLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        if(gpsLocation!=null){
            System.out.println("  > Time gps: " + sdf.format(new Date(gpsLocation.getTime())));
        }

        if(netLocation!=null){
            System.out.println("  > Time net: " + sdf.format(new Date(netLocation.getTime())));
        }


        /*System.out.println("GPS Coordinates: " +
                Double.toString(gpsLocation.getLatitude()) + "," +
                Double.toString(gpsLocation.getLongitude()));
        System.out.println("  > Provider: " + gpsLocation.getProvider());
        System.out.println("  > Accuracy ("+gpsLocation.hasAccuracy()+"): " + gpsLocation.getAccuracy());
        System.out.println("  > Altitude ("+gpsLocation.hasAltitude()+"): " + gpsLocation.getAltitude());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        System.out.println("  > Time: " + sdf.format(new Date(gpsLocation.getTime())));

        System.out.println("NET Coordinates: " +
                Double.toString(netLocation.getLatitude()) + "," +
                Double.toString(netLocation.getLongitude()));
        System.out.println("  > Provider: " + netLocation.getProvider());
        System.out.println("  > Accuracy ("+netLocation.hasAccuracy()+"): " + netLocation.getAccuracy());
        System.out.println("  > Altitude ("+netLocation.hasAltitude()+"): " + netLocation.getAltitude());
        System.out.println("  > Time: " + sdf.format(new Date(netLocation.getTime())));

        System.out.println("**************************************************************************");
        System.out.println("DISTANCE: " + gpsLocation.distanceTo(netLocation)+ "m");
        System.out.println("**************************************************************************");
        */

    }

    @Override
    public void onConnectionSuspended(int i) {
        System.out.println("GeolocationWorker: onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        getActivity(),
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            //showErrorDialog(connectionResult.getErrorCode());
        }
    }

    private boolean servicesConnected() {

        // Check that Google Play services is available
        int resultCode =
        GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());

        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
        // In debug mode, log the status
        Log.d(GeolocationWorker.class.getName(), "play_services_available");

        // Continue
        return true;
        // Google Play services was not available for some reason
        } else {
        // Display an error dialog
        //Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(), 0);
        //if (dialog != null) {
        //    ErrorDialogFragment errorFragment = new ErrorDialogFragment();
        //    errorFragment.dia(dialog);
        //    errorFragment.show(getSupportFragmentManager(), LocationUtils.APPTAG);
        //}
        Log.d(GeolocationWorker.class.getName(), "play_services_un_available");

        return false;
        }
    }

}
