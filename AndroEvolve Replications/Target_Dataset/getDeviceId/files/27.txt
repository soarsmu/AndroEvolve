package ubc.yingying.reflection2test1;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.view.Menu;



/**
 * @Description: This is a test case as an extension of DroidBench-Reflection-Reflection2,
 * which further tests the ability of handling reflections of AmanDroid and DroidSafe.
 *
 * If there are two different concreteClass(1/2) objects called via Reflection,
 * can AmanDroid and DroidSafe differentiate the two derived classes, and report the correct flow?
 *
 * @Expected_Sources:
 * line 46: [leak] getDeviceId()
 * line 52: [no leak] getDeviceId()
 *
 * @Expected_Sink:
 * line 48: [leak] sendTextMessage(java.lang.String,java.lang.String,java.lang.String,android.app.PendingIntent,android.app.PendingIntent)
 * line 54: [no leak] sendTextMessage(java.lang.String,java.lang.String,java.lang.String,android.app.PendingIntent,android.app.PendingIntent)
 *
 * @Number_of_expected_leakages: 1
 *
 * @Flow_Paths:
 * Path1:
 * line 46: bc.imei = telephonyManager.getDeviceId() -->
 * line 48: sms.sendTextMessage("+49 1234", null, bc.foo(), null, null); --> leak
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try{

            TelephonyManager telephonyManager1 = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            BaseClass bc1 = (BaseClass) Class.forName("ubc.yingying.reflection2test1.ConcreteClass1").newInstance();
            bc1.imei = telephonyManager1.getDeviceId(); //Source: <android.telephony.TelephonyManager: java.lang.String getDeviceId()> -> _SOURCE_
            SmsManager sms1 = SmsManager.getDefault();
            sms1.sendTextMessage("+49 1111", null, bc1.foo(), null, null);  // Sink, leak: <android.telephony.SmsManager: void sendTextMessage(java.lang.String,java.lang.String,java.lang.String,android.app.PendingIntent,android.app.PendingIntent)> -> _SINK_

            TelephonyManager telephonyManager2 = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            BaseClass bc2 = (BaseClass) Class.forName("ubc.yingying.reflection2test1.ConcreteClass2").newInstance();
            bc2.imei = telephonyManager2.getDeviceId(); //Source:  <android.telephony.TelephonyManager: java.lang.String getDeviceId()> -> _SOURCE_
            SmsManager sms2 = SmsManager.getDefault();
            sms2.sendTextMessage("+49 2222", null, bc2.foo(), null, null); // Sink, no leak: <android.telephony.SmsManager: void sendTextMessage(java.lang.String,java.lang.String,java.lang.String,android.app.PendingIntent,android.app.PendingIntent)> -> _SINK_


        }catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }


}
