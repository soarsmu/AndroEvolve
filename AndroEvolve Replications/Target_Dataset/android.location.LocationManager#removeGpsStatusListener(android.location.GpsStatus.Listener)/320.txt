package com.android.TestMode;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.android.TestMode.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.telephony.SignalStrength;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author haojie.shi
 *  add 12.21
 */
public class GpsTestNew extends Activity  {
	
	static final String tag = "GpsTestNew"; // for Log
	public EngSqlite mEngSqlite;
	
	private TextView tv;  
    private LocationManager lm;  
    private Criteria criteria;  
    private Location location;  
    
	private TextView tv1;  
      
    
    private String starnumber = "Search into the number of satellites :0.0";
    /** Called when the activity is first created. */  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.gpstest);  
        mEngSqlite = EngSqlite.getInstance(this);  
        tv = (TextView)findViewById(R.id.tv);  
          
        tv1 = (TextView)findViewById(R.id.myTextView1); 
        
        lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);  
        setupBottom();
        if (!lm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER))  
        {  
            
            //GPS has been shut down, please to manually open GPS and try again!
            Toast.makeText(this, "GPS has been shut down, please to manually open GPS and try again!", Toast.LENGTH_SHORT).show(); 
            return;  
        }  
        else  
        {  
           
            Toast.makeText(this, "GPS positioning...", Toast.LENGTH_SHORT).show();  
        }  
          
        criteria = new Criteria();  
        criteria.setAccuracy(Criteria.ACCURACY_FINE);     
//        criteria.setAltitudeRequired(true);              
//        criteria.setBearingRequired(true);               
        criteria.setCostAllowed(true);                    
        criteria.setPowerRequirement(Criteria.POWER_LOW);  
          
        String provider = lm.getBestProvider(criteria, true);  
        location = lm.getLastKnownLocation(provider);  
        newLocalGPS(location);  
          
        lm.requestLocationUpdates(provider, 1*1000, 0, new locationListener());  
        lm.addGpsStatusListener(statusListener); 
       
    }  
      
 
    private List<GpsSatellite> numSatelliteList = new ArrayList<GpsSatellite>();   
      
    private final GpsStatus.Listener statusListener = new GpsStatus.Listener() {  
        public void onGpsStatusChanged(int event) {   
            LocationManager locationManager = (LocationManager) GpsTestNew.this.getSystemService(Context.LOCATION_SERVICE);  
            GpsStatus status = locationManager.getGpsStatus(null);
            String satelliteInfo = updateGpsStatus(event, status);  
            starnumber = satelliteInfo;
            Log.v(tag, "starnumber :"+starnumber);
            //tv_satellites.setText(null);  
           // tv_satellites.setText(satelliteInfo);  
        }  
    };  
  
    private String updateGpsStatus(int event, GpsStatus status) {  
        StringBuilder sb2 = new StringBuilder("");  
        if (status == null) {  
            sb2.append("Search into the number of satellites" +0);  
        } else if (event == GpsStatus.GPS_EVENT_SATELLITE_STATUS) {  
            int maxSatellites = status.getMaxSatellites();  
            Iterator<GpsSatellite> it = status.getSatellites().iterator();  
            numSatelliteList.clear();  
            int count = 0;  
            while (it.hasNext() && count <= maxSatellites) {  
                GpsSatellite s = it.next();  
                numSatelliteList.add(s);  
                count++;  
            }  
           
            sb2.append("Search into the number of satellites" + numSatelliteList.size()); 
        }  
          
        return sb2.toString();  
    }  
    
    class locationListener implements LocationListener  
    {  
  
        @Override  
        public void onLocationChanged(Location location) {  
           
            newLocalGPS(location);  
        }  
  
        @Override  
        public void onProviderDisabled(String provider) {  
             
            newLocalGPS(null);  
        }  
  
        @Override  
        public void onProviderEnabled(String provider) {  
             
              
        }  
  
        @Override  
        public void onStatusChanged(String provider, int status, Bundle extras) {  
        	Log.v(tag, "onStatusChanged () status :"+status);
        	newLocalGPS(null);  
        }  
          
    }  
      
    private void newLocalGPS(Location location)  
    {  
    	 Log.v(tag, "newLocalGPS () starnumber :"+starnumber);
        if (location!=null)  
        {  
        	double latitude,longitude,speed,altitude,bearing,satellites = 0.0;
//             latitude = location.getLatitude(); 
//             longitude = location.getLongitude(); 
             speed = location.getSpeed(); 
//             altitude = location.getAltitude();   
             bearing = location.getBearing(); 
             satellites = numSatelliteList.size();  
            
             
             Log.v(tag, "newLocalGPS () satellites :"+satellites);
             
           /*  double snr = 0.0;
             tv1.setText("");
             for(int i=0; i<numSatelliteList.size(); i++)
             {
            	 
            	 snr = numSatelliteList.get(i).getSnr();
            	 Log.v(tag, "newLocalGPS () snr :"+snr);
            	
            	 tv1.append("Signal Strength "+snr+"\n");
             }
             */
             
             tv1.setText("");
             List<String> resultList = new ArrayList<String>();
             for (int i = 0; i < numSatelliteList.size(); i++) {//[yeez_haojie add 3.7]
            	 resultList.add("Signal Strength "+String.valueOf(numSatelliteList.get(i).getSnr())+"\n");
            	 //String resulttext = String.valueOf(numSatelliteList.get(i).getSnr());
            	 String resulttext = resultList.get(i);
                 int lenth = resulttext.length();
                 Log.v(tag, "Signal :"+resulttext+" lenth :"+lenth);
                 SpannableStringBuilder style = new SpannableStringBuilder(resulttext);//[yeez_haojie add 3.7]
                 style.setSpan(new ForegroundColorSpan(Color.GREEN), 15, lenth, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                 tv1.append(style);
                 tv1.setTextSize(28);
            }
             
             
             double meters  = 0.0;
             meters = location.getAccuracy();
            // numSatelliteList.get(0)
             Log.v(tag, "newLocalGPS () meters :"+meters);
 
            tv.setText(
//            		"Precision :"+latitude+'\n'+  
//                    "Latitude :"+longitude+'\n'+  
                    "Speed :"+speed+"m/s"+'\n'+  
//                    "Elevation :"+altitude+"m"+'\n'+  
                    "Position :"+bearing+'\n'+
                    "Search into the number of satellites :"+
                    satellites+'\n'+
                    "Accuracy :"+meters)
                    ;  
        }  

        else  
        {  
             tv.setText("The GPS information is unknown or getting GPS information..."); 
        }  
    }  
    

  /*  public void onSignalStrengthsChanged(SignalStrength signalStrength)
    {

    	super.onSignalStrengthsChanged(signalStrength);

    Toast.makeText(getApplicationContext(), "Go to Firstdroid!!! GSM Cinr = "

    + String.valueOf(signalStrength.getGsmSignalStrength()), Toast.LENGTH_SHORT).show();

    }*/

    public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.d(tag,"onKeyDown keyCode:"+keyCode);
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			
			Log.d(tag,"onKeyDown !!!!!!!!!!keyCode:"+keyCode);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
    
	@Override
	protected void onStop() {
		/* may as well just finish since saving the state is not important for this toy app */
		finish();
		super.onStop();
	}
	
	private void closeGps () {
		lm.removeGpsStatusListener(statusListener);
		Settings.Secure.putString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED, LocationManager.GPS_PROVIDER);
        Settings.Secure.setLocationProviderEnabled(getContentResolver(), LocationManager.GPS_PROVIDER, false);
	}
  	
	private void setupBottom() {
	       Button b = (Button) findViewById(R.id.testpassed);
	       b.setOnClickListener(new View.OnClickListener() {
	           public void onClick(View v) {
	        	   closeGps();
	              //passed
	              setResult(RESULT_OK);
	              BaseActivity.NewstoreRusult(true, "Gps test",mEngSqlite);
	              finish();
	           }
	       });
	       
	       b = (Button) findViewById(R.id.testfailed);
	       b.setOnClickListener(new View.OnClickListener() {
	           public void onClick(View v) {
	        	   closeGps();
	              //failed
	              setResult(RESULT_FIRST_USER);
	              BaseActivity.NewstoreRusult(false, "Gps test",mEngSqlite);
	              finish();
	           }
	       });
	    }
}