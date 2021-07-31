package com.example.liammc.yarn.networking;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.location.GnssStatus;
import android.location.GpsStatus;
import android.location.LocationManager;
import android.os.Build;
import android.util.Log;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.liammc.yarn.dialogs.LocationDialog;
import com.example.liammc.yarn.utility.PermissionTools;

public class LocationServiceListener {

    private final String TAG = "LocationServiceListener";
    private LocationDialog locationDialog;
    private AppCompatActivity activity;
    private FragmentManager fm;

    public LocationServiceListener(AppCompatActivity _activity,int PERMISSION_REQUEST_CODE){
        this.locationDialog = new LocationDialog();
        this.activity = _activity;
        this.fm = _activity.getSupportFragmentManager();

        this.init(_activity,PERMISSION_REQUEST_CODE);
    }



    //region Init

    public void init(final  Activity _activity,int PERMISSION_REQUEST_CODE){

        if(PermissionTools.requestLocationPermissions(_activity,PERMISSION_REQUEST_CODE)){

            LocationManager lm = (LocationManager) _activity.getSystemService(Context.LOCATION_SERVICE);

             if(Build.VERSION.SDK_INT  >=  Build.VERSION_CODES.N) listener24(lm);
             else listener(lm);
        }
    }

    //endregion

    //region Private Methods

    @TargetApi(16)
    private void listener(LocationManager lm){
        try{
            lm.addGpsStatusListener(new android.location.GpsStatus.Listener()
            {
                public void onGpsStatusChanged(int event)
                {
                    switch(event)
                    {
                        case GpsStatus.GPS_EVENT_STARTED:
                            locationDialog.dismissDialog(activity);
                            break;
                        case GpsStatus.GPS_EVENT_STOPPED:
                            locationDialog.alert(fm,TAG);
                            break;
                    }
                }
            });
        }catch(SecurityException e){
            Log.e(TAG,e.getMessage());
        }
    }

    @TargetApi(24)
    private void listener24(LocationManager lm){
        try{
            lm.registerGnssStatusCallback(new GnssStatus.Callback() {
                @Override
                public void onStarted() {
                    locationDialog.dismissDialog(activity);
                    super.onStarted();
                }

                @Override
                public void onStopped() {
                    locationDialog.alert(fm,TAG);
                    super.onStopped();
                }
            });

        }catch(SecurityException e){
            Log.e(TAG,e.getMessage());
        }
    }

    //endregion
}
