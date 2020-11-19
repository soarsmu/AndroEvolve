package com.company;

public class test {
    public void addGpsStatusListener(GpsStatus.Listener listener) {
        mGpsStatusListener = listener;
        boolean tempFunctionReturnValue;
        Listener parameterVariable0 = mGpsStatusListener;
        LocationManager classNameVariable = mLocationManager;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            Callback newParameterVariable0 = callback;
            tempFunctionReturnValue = classNameVariable.registerGnssStatusCallback(newParameterVariable0);
        } else {
            tempFunctionReturnValue = classNameVariable.addGpsStatusListener(parameterVariable0);
        }
    }
}
