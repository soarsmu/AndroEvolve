package ch.supsi.ist.camre.paths;


import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Fragment;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import ch.supsi.ist.camre.paths.data.Point;
import ch.supsi.ist.camre.paths.data.Position;


/**
 * A simple {@link Fragment} subclass.
 */
public class WalkerActivityFooter extends Fragment  implements GoogleApiClient.ConnectionCallbacks ,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, GpsStatus.Listener{

    private long lastFix;

    //TextView status;
    View view;

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
    public static final int UPDATE_INTERVAL_IN_SECONDS = 5;
    // The fastest update frequency, in seconds
    private static final int FASTEST_INTERVAL_IN_SECONDS = 1;

    // Update frequency in milliseconds
    private static final long UPDATE_INTERVAL =
            MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;

    // A fast frequency ceiling in milliseconds
    private static final long FASTEST_INTERVAL =
            MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;

    private List<LocationListener> locationListeners = new ArrayList<LocationListener>();
    private List<GpsStatus.Listener> gpsStatusListeners = new ArrayList<GpsStatus.Listener>();
    private List<GpsListener> gpsListeners = new ArrayList<GpsListener>();
    private List<BestPositionListener> bestPositionListeners = new ArrayList<BestPositionListener>();


    public interface GpsListener {
        public void onGpsStatusChanged(int event, Location location, GpsStatus gpsStatus);
    }

    public interface BestPositionListener {
        public static int CONNECTING = 0;
        public static int FIRSTFIX = 1;
        public static int CALCULATING = 2;
        public void onUpdates(int event, Position position, int accuracy);
        public void onResult(int event, Location location, GpsStatus gpsStatus);
    }

    public WalkerActivityFooter() {
        // Required empty public constructor
        lastFix = 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_walker_activity_footer, container, false);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        //System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> WalkerActivityFooter: onCreate");

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
                .setInterval(UPDATE_INTERVAL);
                //.setFastestInterval(FASTEST_INTERVAL);

        locationManager = (LocationManager) getActivity()
                .getSystemService(WalkerActivityTest.LOCATION_SERVICE);

    }


    public Position getPosition(Location location){
        double lon = location.getLongitude();
        double lat = location.getLatitude();
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        df.setTimeZone(tz);
        Position pos = new Position();
        pos.setAltitude(location.getAltitude());
        pos.setHeading(0.0);
        pos.setGeometry(new Point(lon, lat));
        pos.setTimestamp(df.format(new Date()));
        return pos;
    }

    public void connectGPS(){
        // Connect the client.
        if(!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }

    public void disconnectGps(){
        if(mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();
        }
    }

    public Location getLastKnownGpsLocation(){
        return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }

    public Position getLastKnownGpsPosition(){
        return this.getPosition(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
    }

    @Override
    public void onStart() {
        //System.out.println("WalkerActivityFooter: onStart");
        //this.connectGPS();
        super.onStart();
    }

    @Override
    public void onStop() {
        //System.out.println("WalkerActivityFooter: onStop");
        super.onStop();
    }


    @Override
    public void onPause() {
        //System.out.println("WalkerActivityFooter: onStop");
        this.disconnectGps();
        super.onPause();
    }

    public void addFusionLocationListener(LocationListener listener){
        //System.out.println("WalkerActivityFooter, Adding FusionLocationListener: " + listener.getClass().getCanonicalName());
        if(!locationListeners.contains(listener)) {
            locationListeners.add(listener);
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.requestLocationUpdates(
                        mGoogleApiClient, mLocationRequest, listener);
            }
        }
    }

    public void addCustomGpsListener(GpsListener listener){
        //System.out.println("WalkerActivityFooter, Adding GpsListener: " + listener.getClass().getCanonicalName());
        if(!gpsListeners.contains(listener)) {
            gpsListeners.add(listener);
        }
    }

    public void addGpsStatusListener(GpsStatus.Listener listener){
        //System.out.println("WalkerActivityFooter, Adding GpsStatus: " + listener.getClass().getCanonicalName());
        if(!gpsStatusListeners.contains(listener)) {
            gpsStatusListeners.add(listener);
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                locationManager.addGpsStatusListener(listener);
            }
        }
    }

    public void removeFusionLocationListener(LocationListener listener){
        //System.out.println("WalkerActivityFooter, Removing FusionLocationListener: " + listener.getClass().getCanonicalName());
        if(locationListeners.contains(listener)) {
            locationListeners.remove(listener);
            if(mGoogleApiClient!=null && mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(
                        mGoogleApiClient, listener);
            }
        }
    }

    public void removeGpsListener(GpsListener listener){
        //System.out.println("WalkerActivityFooter, Removing GpsListener: " + listener.getClass().getCanonicalName());
        if(gpsListeners.contains(listener)) {
            gpsListeners.remove(listener);
        }
    }

    public void removeGpsStatusistener(GpsStatus.Listener listener){
        //System.out.println("WalkerActivityFooter, Removing GpsStatus: " + listener.getClass().getCanonicalName());
        if(gpsStatusListeners.contains(listener)) {
            gpsStatusListeners.remove(listener);
            locationManager.removeGpsStatusListener(listener);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        //System.out.println("WalkerActivityFooter, LocationWorker: onConnected");
        if(locationListeners.size()>0){
            for (LocationListener listener: locationListeners){
                LocationServices.FusedLocationApi.requestLocationUpdates(
                        mGoogleApiClient, mLocationRequest, listener);
            }
        }
        if(gpsStatusListeners.size()>0){
            for (GpsStatus.Listener listener: gpsStatusListeners) {
                locationManager.addGpsStatusListener(listener);
            }
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
        locationManager.addGpsStatusListener(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        //System.out.println("WalkerActivityFooter: onLocationChanged");
        double lon = location.getLongitude();
        double lat = location.getLatitude();
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        df.setTimeZone(tz);
        Position pos = new Position();
        pos.setAltitude(location.getAltitude());
        pos.setHeading(0.0);
        pos.setGeometry(new Point(lon, lat));
        pos.setTimestamp(df.format(new Date()));
    }

    @Override
    public synchronized void onGpsStatusChanged(int event) {

        //System.out.println("onGpsStatusChanged..");

        ImageView icon = (ImageView) view.findViewById(R.id.gpsicon);
        GpsStatus gpsStatus = locationManager.getGpsStatus(null);
        TextView status = (TextView) view.findViewById(R.id.walker_status_gps);


        switch (event) {
            case GpsStatus.GPS_EVENT_STARTED:
                icon.setImageResource(R.drawable.ic_action_map_note);
                status.setText("GPS is connecting.. please wait.");
                //System.out.println("GPS  is connecting...");
                break;
            case GpsStatus.GPS_EVENT_STOPPED:
                icon.setImageResource(R.drawable.ic_action_map_end);
                status.setText("GPS disabled");
                //System.out.println("GPS disabled");
                break;
            case GpsStatus.GPS_EVENT_FIRST_FIX:
                icon.setImageResource(R.drawable.ic_action_map_start);
                status.setText("GPS connected");
                //System.out.println("GPS connected !!!!! ");
                break;
            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                //System.out.println("GPS status received, ttff: " + gpsStatus.getTimeToFirstFix());
                int satCnt = 0;
                int satFixCnt = 0;
                Iterator<GpsSatellite> satellites = gpsStatus.getSatellites().iterator();
                while (satellites.hasNext()){
                    GpsSatellite satellite = satellites.next();
                    satCnt++;
                    if(satellite.usedInFix()){
                        satFixCnt++;
                    }
                }
                if (gpsStatus.getTimeToFirstFix() > 0) {
                    Location gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    //System.out.println("Listeners: " + gpsListeners.size());
                    status.setText("GPS: Accuracy " + gpsLocation.getAccuracy() + "m Altitude " + Math.round(gpsLocation.getAltitude()) + "m Satellites "+satFixCnt+"/"+satCnt);
                    if(gpsListeners.size()>0) {
                        for (GpsListener listener : gpsListeners) {
                            listener.onGpsStatusChanged(event, gpsLocation,  gpsStatus);
                        }
                    }
                }else{
                    icon.setImageResource(R.drawable.ic_action_map_note);
                    status.setText("GPS is connecting.. please wait. Satellites "+satFixCnt+"/"+satCnt);
                    //System.out.println("GPS is connecting.. please wait. Satellites " + satFixCnt + "/" + satCnt);
                }
                break;
            default:
                //System.out.println("Event Unknown.. :[*.*]:" + event);
                status.setText("Event Unknown.. :[*.*]:" + event);
                break;
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        //System.out.println("GeolocationWorker: onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //System.out.println("GeolocationWorker: onConnectionFailed");
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
}
