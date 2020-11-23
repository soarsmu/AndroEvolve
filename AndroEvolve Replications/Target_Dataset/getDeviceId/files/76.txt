package jv.roundemlon.mobiletower;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textGsmCellLocation = (TextView)findViewById(R.id.gsmcelllocation);
        TextView textCID = (TextView)findViewById(R.id.cid);
        TextView textLAC = (TextView)findViewById(R.id.lac);


        TextView textDeviceID = (TextView)findViewById(R.id.deviceid);
        TextView textNetworkOperator = (TextView)findViewById(R.id.networkoperator);
        TextView textNetworkOperatorName = (TextView)findViewById(R.id.networkoperatorname);
        TextView textNetworkType = (TextView)findViewById(R.id.networktype);
        TextView textNetworkCountryIso = (TextView)findViewById(R.id.networkcountryiso);

        //retrieve a reference to an instance of TelephonyManager
        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);

        textDeviceID.setText(getDeviceID(telephonyManager));
        textNetworkOperator.setText("Network Operator(MCC+MNC): " + telephonyManager.getNetworkOperator());
        textNetworkOperatorName.setText("Network Operator Name: " + telephonyManager.getNetworkOperatorName());
        textNetworkType.setText("Network Type: " + getNetworkType(telephonyManager));
        textNetworkCountryIso.setText("Network Country Iso:" + telephonyManager.getNetworkCountryIso());


//remove comment when removing the nnetwork provided code
   //     TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
//        try{
        GsmCellLocation cellLocation = (GsmCellLocation)telephonyManager.getCellLocation();
//        }catch (Exception e){
//            Log.d("Debud","Error");
//        }

        int cid = cellLocation.getCid();
        int lac = cellLocation.getLac();
        textGsmCellLocation.setText(cellLocation.toString());
        textCID.setText("gsm cell id: " + String.valueOf(cid));
        textLAC.setText("gsm location area code: " + String.valueOf(lac));


        Log.d("CellLocation", cellLocation.toString());
        Log.d("GSM CELL ID",  String.valueOf(cid));
        Log.d("GSM Location Code", String.valueOf(lac));
    }


    String getNetworkType(TelephonyManager phonyManager){
        int type = phonyManager.getNetworkType();

        switch(type){
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                return "NETWORK_TYPE_UNKNOWN";
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return "NETWORK_TYPE_GPRS";
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return "NETWORK_TYPE_EDGE";
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return "NETWORK_TYPE_UMTS";
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return "NETWORK_TYPE_HSDPA";
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return "NETWORK_TYPE_HSUPA";
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return "NETWORK_TYPE_HSPA";
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return "NETWORK_TYPE_CDMA";
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                return "NETWORK_TYPE_EVDO_0";
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                return "NETWORK_TYPE_EVDO_0";
  /* Since: API Level 9
   *  case TelephonyManager.NETWORK_TYPE_EVDO_B:
   *  return "NETWORK_TYPE_EVDO_B";
   */
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return "NETWORK_TYPE_1xRTT";
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return "NETWORK_TYPE_IDEN";
  /* Since: API Level 11
   *  case TelephonyManager.NETWORK_TYPE_LTE:
   * return "NETWORK_TYPE_LTE";
   */
  /* Since: API Level 11
   *  case TelephonyManager.NETWORK_TYPE_EHRPD:
   *  return "NETWORK_TYPE_EHRPD";
   */
            default:
                return "unknown";

        }
    }

    String getDeviceID(TelephonyManager phonyManager){

        String id = phonyManager.getDeviceId();

        if (id == null){
            id = "not available";
        }

        int phoneType = phonyManager.getPhoneType();
        switch(phoneType){
            case TelephonyManager.PHONE_TYPE_NONE:
                return "NONE: " + id;

            case TelephonyManager.PHONE_TYPE_GSM:
                return "GSM: IMEI=" + id;

            case TelephonyManager.PHONE_TYPE_CDMA:
                return "CDMA: MEID/ESN=" + id;

  /*
   *  for API Level 11 or above
   *  case TelephonyManager.PHONE_TYPE_SIP:
   *   return "SIP";
   */

            default:
                return "UNKNOWN: ID=" + id;
        }

    }



}
