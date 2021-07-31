package com.helloapp.testingapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.GnssStatus;
import android.location.GpsStatus;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class GpsTestActivity extends AppCompatActivity implements GpsStatus.Listener {

    private LocationManager mLocationManager;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps_test);

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        mLocationManager.addGpsStatusListener(this);



    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean isEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        Toast.makeText(GpsTestActivity.this, "isProviderEnabled::::::"  + isEnabled, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGpsStatusChanged(int i) {
        Toast.makeText(GpsTestActivity.this, "onGpsStatusChanged::::::", Toast.LENGTH_SHORT).show();
    }
}
