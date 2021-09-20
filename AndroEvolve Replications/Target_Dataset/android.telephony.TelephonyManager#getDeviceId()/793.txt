package com.example.finance.tradestrategy.utils;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.example.finance.tradestrategy.base.BaseApplication;

/**
 * Created by Administrator on 2017/6/13.
 */

public class ToolUserCheck {
//    private static final String phoneNum = "";    //去掉对手机号的验证：备用机使用，可能没有手机号
    private static final String phoneIMEI = "";

    public static boolean isRight() {
        if (ToolLog.isDebug) {
            return true;
        }
        try {
            TelephonyManager manager = (TelephonyManager) BaseApplication.mContext.getSystemService(Context.TELEPHONY_SERVICE);
            ToolLog.d("phone:" +manager.getLine1Number() + " deviceId:" + manager.getDeviceId());
            if (/*phoneNum.equals(manager.getLine1Number()) && */phoneIMEI.equals(manager.getDeviceId())) {
                return true;
            }
        } catch (Exception ex) {

        }

        return false;
    }
}
