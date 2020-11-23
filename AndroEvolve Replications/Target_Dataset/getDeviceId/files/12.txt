package ubc.yingying.reflection2test4;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;


/**
 * @Description The benchmark is an extension of DroidBench-Reflection-Reflection2 and UBCBench-Reflection2Test3.
 *
 *  In Reflection2Test3 benchmark, only applied concreteClass1 and concreteClass2,
 *  in this testcase, four concrete classes are involved (concreteClass1/2 from baseClass and  concreteClass3/4 from baseClass2
 *
 *
 * @ExpectedSources:
 * line 60: getDeviceId()
 * line 62: getDeviceId()
 * line 64: getDeviceId()
 * line 66: getDeviceId()
 *
 *
 * @ExpectedSinks:
 * line 68: [leak] Log.i("concrete1,leak", concrete1.foo());
 * line 69: [no leak] Log.i("concrete2,no leak", concrete2.foo());
 * line 70: [leak] Log.i("concrete3,leak", concrete3.foo());
 * line 71: [no leak] Log.i("concrete4,no leak", concrete4.foo());
 *
 *
 * @NumberOfExpectedLeaks: 2
 *
 * @FlowPaths:
 * Path1:
 * line 60: concrete1.imei = telephonyManager.getDeviceId(); -->
 * line 68: Log.i("concrete1,leak", concrete1.foo()); --> leak
 *
 * Path2:
 * line 63: concrete3.imei = telephonyManager.getDeviceId(); -->
 * line 70: Log.i("concrete3,leak", concrete3.foo()); --> leak
 *
 */

public class MainActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            BaseClass concrete1 = (BaseClass) Class.forName("ubc.yingying.reflection2test4.ConcreteClass1").newInstance();
            BaseClass concrete2 = (BaseClass) Class.forName("ubc.yingying.reflection2test4.ConcreteClass2").newInstance();
            BaseClass2 concrete3 = (BaseClass2) Class.forName("ubc.yingying.reflection2test4.ConcreteClass3").newInstance();
            BaseClass2 concrete4 = (BaseClass2) Class.forName("ubc.yingying.reflection2test4.ConcreteClass4").newInstance();


            TelephonyManager telephonyManager1 = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            concrete1.imei = telephonyManager1.getDeviceId(); // source
            TelephonyManager telephonyManager2 = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            concrete2.imei = telephonyManager2.getDeviceId(); // source
            TelephonyManager telephonyManager3 = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            concrete3.imei = telephonyManager3.getDeviceId(); // source
            TelephonyManager telephonyManager4 = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            concrete4.imei = telephonyManager4.getDeviceId(); // source

            Log.i("concrete1,leak", concrete1.foo()); // sink, leak
            Log.i("concrete2,no leak", concrete2.foo()); // sink, no leak
            Log.i("concrete3,leak", concrete3.foo()); // sink, leak
            Log.i("concrete4,no leak", concrete4.foo()); // sink, no leak


        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }




}
