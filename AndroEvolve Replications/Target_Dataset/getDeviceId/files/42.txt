package com.rideeaze.services.telephone;

import android.content.Context;
import android.telephony.TelephonyManager;

public class DriverTelephoneService {

    protected Context context;
    protected TelephonyManager telephonyManager;

    public DriverTelephoneService(Context context) {
        this.context = context;
        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }

    public String getDeviceId() {
        return telephonyManager.getDeviceId();
    }

    public String getSimSerialNumber() {
        return telephonyManager.getDeviceId();
    }
}
