package com.kethu.telephonicapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;

import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    private static final String TAG = "TelephnicActivity";
    String[] READ_PERMISSIONS = {Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_COARSE_LOCATION};

    @SuppressLint("HardwareIds")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textview);

        if (!PermissionChecker.checkPermission(MainActivity.this, READ_PERMISSIONS))
            PermissionChecker.reqPermissions(MainActivity.this, READ_PERMISSIONS);


        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);


        if (telephonyManager != null) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Call some material design APIs here
                Log.e(TAG,"Build.VERSION.SDK_INT "+Build.VERSION.SDK_INT);
                Log.e(TAG, "Single or Dula Sim " + telephonyManager.getPhoneCount());
                Log.e(TAG, "Default device ID " + telephonyManager.getDeviceId());
                Log.e(TAG, "Single 1 " + telephonyManager.getDeviceId(0));
                Log.e(TAG, "Single 2 " + telephonyManager.getDeviceId(1));


                String str = "Serial Number " + telephonyManager.getSimSerialNumber() + "\n SimCount " + telephonyManager.getPhoneCount() + "\n Device Id of Sim 1 " + telephonyManager.getDeviceId(0) + "\n Device Id of Sim2 " + telephonyManager.getDeviceId(1);

                textView.setText(str);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.e(TAG,"Build.VERSION.SDK_INT "+Build.VERSION.SDK_INT);
                Log.e(TAG, "Device  " + telephonyManager.getImei());
                Log.e(TAG, "slot 1 " + telephonyManager.getImei(0));
                Log.e(TAG, "slot 2 " + telephonyManager.getImei(1));
                Log.e(TAG, "Single or Dula Sim " + telephonyManager.getPhoneCount());
                textView.setText(telephonyManager.getImei());
            } else {
                Log.e(TAG,"Build.VERSION.SDK_INT "+Build.VERSION.SDK_INT);
                Log.e(TAG, "Serial Number " + telephonyManager.getSimSerialNumber());
                Log.e(TAG, "Device ID " + telephonyManager.getDeviceId());

            }


        }
    }


}