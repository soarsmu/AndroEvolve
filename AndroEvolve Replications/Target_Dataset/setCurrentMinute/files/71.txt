/*
Copyright (c) 2012-2016.

Juergen Key. Alle Rechte vorbehalten.

Weiterverbreitung und Verwendung in nichtkompilierter oder kompilierter Form,
mit oder ohne Veraenderung, sind unter den folgenden Bedingungen zulaessig:

   1. Weiterverbreitete nichtkompilierte Exemplare muessen das obige Copyright,
die Liste der Bedingungen und den folgenden Haftungsausschluss im Quelltext
enthalten.
   2. Weiterverbreitete kompilierte Exemplare muessen das obige Copyright,
die Liste der Bedingungen und den folgenden Haftungsausschluss in der
Dokumentation und/oder anderen Materialien, die mit dem Exemplar verbreitet
werden, enthalten.
   3. Weder der Name des Autors noch die Namen der Beitragsleistenden
duerfen zum Kennzeichnen oder Bewerben von Produkten, die von dieser Software
abgeleitet wurden, ohne spezielle vorherige schriftliche Genehmigung verwendet
werden.

DIESE SOFTWARE WIRD VOM AUTOR UND DEN BEITRAGSLEISTENDEN OHNE
JEGLICHE SPEZIELLE ODER IMPLIZIERTE GARANTIEN ZUR VERFUEGUNG GESTELLT, DIE
UNTER ANDEREM EINSCHLIESSEN: DIE IMPLIZIERTE GARANTIE DER VERWENDBARKEIT DER
SOFTWARE FUER EINEN BESTIMMTEN ZWECK. AUF KEINEN FALL IST DER AUTOR
ODER DIE BEITRAGSLEISTENDEN FUER IRGENDWELCHE DIREKTEN, INDIREKTEN,
ZUFAELLIGEN, SPEZIELLEN, BEISPIELHAFTEN ODER FOLGENDEN SCHAEDEN (UNTER ANDEREM
VERSCHAFFEN VON ERSATZGUETERN ODER -DIENSTLEISTUNGEN; EINSCHRAENKUNG DER
NUTZUNGSFAEHIGKEIT; VERLUST VON NUTZUNGSFAEHIGKEIT; DATEN; PROFIT ODER
GESCHAEFTSUNTERBRECHUNG), WIE AUCH IMMER VERURSACHT UND UNTER WELCHER
VERPFLICHTUNG AUCH IMMER, OB IN VERTRAG, STRIKTER VERPFLICHTUNG ODER
UNERLAUBTE HANDLUNG (INKLUSIVE FAHRLAESSIGKEIT) VERANTWORTLICH, AUF WELCHEM
WEG SIE AUCH IMMER DURCH DIE BENUTZUNG DIESER SOFTWARE ENTSTANDEN SIND, SOGAR,
WENN SIE AUF DIE MOEGLICHKEIT EINES SOLCHEN SCHADENS HINGEWIESEN WORDEN SIND.
*/
package de.elbosso.serveravailabilityalarm;

import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.telephony.SmsManager;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.EditText;
import android.widget.RemoteViews;
import android.widget.TimePicker;
import android.widget.Toast;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.Date;

public class MainActivity extends Activity {
    public static final String PREFS_NAME = "serveravailabilityalarm.MyPrefsFile";
    private DataUpdateReceiver dataUpdateReceiver;
    private ReportHelper reportHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String urlString = settings.getString("url", null);
        if(urlString!=null)
            ((EditText)findViewById(R.id.editText)).setText(urlString);
        int perc=25;
        if(settings.contains("perc"))
            perc=settings.getInt("perc",perc);
        ((EditText)findViewById(R.id.editText2)).setText("" + perc);
        int age=3;
        if(settings.contains("age"))
            age=settings.getInt("age",age);
        ((EditText)findViewById(R.id.editText3)).setText(""+age);
        int smsbeforeh=9;
        if(settings.contains("smsbeforeh"))
            smsbeforeh=settings.getInt("smsbeforeh",smsbeforeh);
        ((TimePicker)findViewById(R.id.timePicker)).setCurrentHour(smsbeforeh);
        int smsbeforem=0;
        if(settings.contains("smsbeforem"))
            smsbeforem=settings.getInt("smsbeforem",smsbeforem);
        ((TimePicker)findViewById(R.id.timePicker)).setCurrentMinute(smsbeforem);
        int smsafterh=16;
        if(settings.contains("smsafterh"))
            smsafterh=settings.getInt("smsafterh",smsafterh);
        ((TimePicker)findViewById(R.id.timePicker3)).setCurrentHour(smsafterh);
        int smsafterm=0;
        if(settings.contains("smsafterm"))
            smsafterm=settings.getInt("smsafterm",smsafterm);
        ((TimePicker)findViewById(R.id.timePicker3)).setCurrentMinute(smsafterm);
        int smsremindh=7;
        if(settings.contains("smsremindh"))
            smsremindh=settings.getInt("smsremindh",smsremindh);
        ((TimePicker)findViewById(R.id.timePicker4)).setCurrentHour(smsremindh);
        int smsremindm=30;
        if(settings.contains("smsremindm"))
            smsremindm=settings.getInt("smsremindm",smsremindm);
        ((TimePicker)findViewById(R.id.timePicker4)).setCurrentMinute(smsremindm);
        String jabber_host = settings.getString("jabber_host", null);
        if(jabber_host!=null)
            ((EditText)findViewById(R.id.editText8)).setText(jabber_host);
        String jabber_user = settings.getString("jabber_user", null);
        if(jabber_user!=null)
            ((EditText)findViewById(R.id.editText6)).setText(jabber_user);
        String jabber_pass = settings.getString("jabber_pass", null);
        if(jabber_pass!=null)
            ((EditText)findViewById(R.id.editText7)).setText(jabber_pass);
        int jabber_port=5222;
        if(settings.contains("jabber_port"))
            age=settings.getInt("jabber_port",jabber_port);
        ((EditText)findViewById(R.id.editText9)).setText(""+jabber_port);

        ((TimePicker)findViewById(R.id.timePicker)).setIs24HourView(true);
        ((TimePicker)findViewById(R.id.timePicker3)).setIs24HourView(true);
        ((TimePicker)findViewById(R.id.timePicker4)).setIs24HourView(true);
        reportHelper=new ReportHelper(this);
        Thread.setDefaultUncaughtExceptionHandler(reportHelper);
    }

    @Override
    protected void onStop() {
        super.onStop();
        persistFormData();
    }
    private void persistFormData()
    {
        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("url", ((EditText) findViewById(R.id.editText)).getText().toString());
        editor.putInt("perc", java.lang.Integer.parseInt(((EditText) findViewById(R.id.editText2)).getText().toString()));
        editor.putInt("age", java.lang.Integer.parseInt(((EditText) findViewById(R.id.editText3)).getText().toString()));
        editor.putInt("smsbeforeh", ((TimePicker)findViewById(R.id.timePicker)).getCurrentHour());
        editor.putInt("smsbeforem", ((TimePicker)findViewById(R.id.timePicker)).getCurrentMinute());
        editor.putInt("smsafterh", ((TimePicker)findViewById(R.id.timePicker3)).getCurrentHour());
        editor.putInt("smsafterm", ((TimePicker)findViewById(R.id.timePicker3)).getCurrentMinute());
        editor.putInt("smsremindh", ((TimePicker)findViewById(R.id.timePicker4)).getCurrentHour());
        editor.putInt("smsremindm", ((TimePicker)findViewById(R.id.timePicker4)).getCurrentMinute());
        editor.putInt("jabber_port",  java.lang.Integer.parseInt(((EditText) findViewById(R.id.editText9)).getText().toString()));
        editor.putString("jabber_host", ((EditText) findViewById(R.id.editText8)).getText().toString());
        editor.putString("jabber_user", ((EditText) findViewById(R.id.editText6)).getText().toString());
        editor.putString("jabber_pass", ((EditText) findViewById(R.id.editText7)).getText().toString());

        // Commit the edits!
        editor.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (dataUpdateReceiver == null)
            dataUpdateReceiver = new DataUpdateReceiver();
        IntentFilter intentFilter = new IntentFilter(WidgetService.REFRESH_DATA_INTENT);
        registerReceiver(dataUpdateReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (dataUpdateReceiver != null)
            unregisterReceiver(dataUpdateReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        boolean signalled=settings.getBoolean("signalled",false);
        MenuItem mi = menu.findItem(R.id.action_acknowledge);
        mi.setEnabled(signalled);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_settings:
                openSettings();
                return true;
            case R.id.action_acknowledge:
                acknowledge();
                return true;
            case R.id.action_test:
                sendTestSms();
                return true;
            case R.id.action_testjabber:
                sendTestJabberMessage();
                return true;
            case R.id.action_knowledgedb:
                openKnowledgeDB();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void sendTestJabberMessage() {
        persistFormData();
        ContactList contactList=Utilities.getContacts(getContentResolver());
        String FILENAME = "hello_file";
        java.io.InputStream is=null;
        java.io.InputStreamReader isr=null;
        java.io.BufferedReader br=null;
        for(Contact c:contactList.getContacts())
        {
            c.setSelected(false);
        }
        try
        {
            is=openFileInput(FILENAME);
            isr=new InputStreamReader(is);
            br=new BufferedReader(isr);
            java.lang.String line=br.readLine();
            while(line!=null)
            {
                for(Contact c:contactList.getContacts())
                {
                    if(c.getLookupKey().equals(line))
                        c.setSelected(true);
                }


                line=br.readLine();
            }
            int position=0;
        }
        catch(IOException exp)
        {
        }
        finally {
            try
            {
                if(br!=null)
                    br.close();
                if(isr!=null)
                    isr.close();
                if(is!=null)
                    is.close();
            }
            catch(IOException exp)
            {

            }
        }
        int count=0;
        for(Contact c:contactList.getContacts())
        {
            if((c.isSelected())&&(c.getJabber()!=null))
            {
                Toast.makeText(this, "sending test jabber message to " + c.getDisplayName(), Toast.LENGTH_SHORT).show();
                //http://stackoverflow.com/questions/6343166/how-to-fix-android-os-networkonmainthreadexception
                new SendJabberMessageTask(MainActivity.this).execute("huhu from serveravailability",c.getJabber());

                ++count;
            }
        }
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu)
    {
        if(featureId == Window.FEATURE_ACTION_BAR && menu != null){
            if(menu.getClass().getSimpleName().equals("MenuBuilder")){
                try{
                    Method m = menu.getClass().getDeclaredMethod(
                            "setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                }
                catch(NoSuchMethodException e){
                    //Log.e(TAG, "onMenuOpened", e);
                }
                catch(Exception e){
                    throw new RuntimeException(e);
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    private void sendTestSms() {
        ContactList contactList=Utilities.getContacts(getContentResolver());
        String FILENAME = "hello_file";
        java.io.InputStream is=null;
        java.io.InputStreamReader isr=null;
        java.io.BufferedReader br=null;
        for(Contact c:contactList.getContacts())
        {
            c.setSelected(false);
        }
        try
        {
            is=openFileInput(FILENAME);
            isr=new InputStreamReader(is);
            br=new BufferedReader(isr);
            java.lang.String line=br.readLine();
            while(line!=null)
            {
                for(Contact c:contactList.getContacts())
                {
                    if(c.getLookupKey().equals(line))
                        c.setSelected(true);
                }


                line=br.readLine();
            }
            int position=0;
        }
        catch(IOException exp)
        {
        }
        finally {
            try
            {
                if(br!=null)
                    br.close();
                if(isr!=null)
                    isr.close();
                if(is!=null)
                    is.close();
            }
            catch(IOException exp)
            {

            }
        }
        int count=0;
        for(Contact c:contactList.getContacts())
        {
            if((c.isSelected())&&(c.getPhoneNumber()!=null))
            {
                Toast.makeText(this, "sending test sms to " + c.getDisplayName(), Toast.LENGTH_SHORT).show();
                SmsManager sms = SmsManager.getDefault();
                sms.sendTextMessage(c.getPhoneNumber(), null, "ServerAvailabilityAlarm application test message "+ DateFormat.format("MMMM dd, yyyy hh:mm:ss", new Date()).toString(), null, null);
                ++count;
            }
        }
    }

    private void acknowledge() {
         SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("signalled", false);

        // Commit the edits!
        editor.commit();
        RemoteViews view = new RemoteViews(getPackageName(), R.layout.appwidget);
        view.setTextViewText(R.id.textView4, "");
        view.setImageViewResource(R.id.imageView, R.drawable.button_hkchen);
        // Push update for this widget to the home screen
        ComponentName thisWidget = new ComponentName(this, WidgetProvider.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        manager.updateAppWidget(thisWidget, view);
        invalidateOptionsMenu();
    }

    private void openSettings()
    {
        Intent i = new Intent(getApplicationContext(), ContactListActivity.class);
        startActivity(i);
    }
    private void openKnowledgeDB()
    {
        Intent i = new Intent(getApplicationContext(), KnowledgeDBListActivity.class);
        startActivity(i);
    }

    private class DataUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(WidgetService.REFRESH_DATA_INTENT)) {
                invalidateOptionsMenu();
            }
        }
    }
}