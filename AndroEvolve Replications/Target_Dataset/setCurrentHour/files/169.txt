package ray.droid.com.droidalarmclock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.provider.SyncStateContract;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TimePicker;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private Context context;
    private TimePicker timePicker;
    private long segundos = 1000;
    private long minutos = segundos * 5;
    private long horas = minutos * 60;

    private CheckBox chkSeg;
    private CheckBox chkTer;
    private CheckBox chkQua;
    private CheckBox chkQui;
    private CheckBox chkSex;
    private CheckBox chkSab;
    private CheckBox chkDom;
    private Button btnAgendar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timePicker = (TimePicker) findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);
        
        context = getBaseContext();

        chkSeg = (CheckBox) findViewById(R.id.chkSeg);
        chkTer = (CheckBox) findViewById(R.id.chkTer);
        chkQua = (CheckBox) findViewById(R.id.chkQua);
        chkQui = (CheckBox) findViewById(R.id.chkQui);
        chkSex = (CheckBox) findViewById(R.id.chkSex);
        chkSab = (CheckBox) findViewById(R.id.chkSab);
        chkDom = (CheckBox) findViewById(R.id.chkDom);

        btnAgendar = (Button) findViewById(R.id.btnAgendar);

        btnAgendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Others.CancelAlarm(context);
                SetAlarm();
            }
        });

        LoadPreferences();
    }

    private void LoadPreferences()
    {

        try {

            String daysOfWeek = DroidPreferences.GetString(context, "DaysOfWeek");

            if (daysOfWeek.contains("1")) {
                chkDom.setChecked(true);
            }
            if (daysOfWeek.contains("2")) {
                chkSeg.setChecked(true);
            }
            if (daysOfWeek.contains("3")) {
                chkTer.setChecked(true);
            }
            if (daysOfWeek.contains("4")) {
                chkQua.setChecked(true);
            }
            if (daysOfWeek.contains("5")) {
                chkQui.setChecked(true);
            }
            if (daysOfWeek.contains("6")) {
                chkSex.setChecked(true);
            }
            if (daysOfWeek.contains("7")) {
                chkSab.setChecked(true);
            }

            int prefHour = DroidPreferences.GetInteger(context, "timePickerHour");
            int prefMinute = DroidPreferences.GetInteger(context, "timePickerMinute");

            timePicker.setCurrentHour(prefHour);
            timePicker.setCurrentMinute(prefMinute);


        }
        catch (Exception ex)
        {
            Log.d("DroidAlarmClock", "LoadPreferences - Erro: " + ex.getMessage());
        }
    }

    private ArrayList<Integer> SetDaysOfWeek() {
        ArrayList<Integer> days = new ArrayList<>();

        if (chkDom.isChecked()) {
            days.add(1);
        }
        if (chkSeg.isChecked()) {
            days.add(2);
        }
        if (chkTer.isChecked()) {
            days.add(3);
        }
        if (chkQua.isChecked()) {
            days.add(4);
        }
        if (chkQui.isChecked()) {
            days.add(5);
        }
        if (chkSex.isChecked()) {
            days.add(6);
        }
        if (chkSab.isChecked()) {
            days.add(7);
        }
        return days;
    }

    private void SetAlarm() {
        try {
            AlarmManager alarmMgr;
            PendingIntent alarmIntent;

            alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            Intent intent = new Intent(context, AlarmBroadcast.class);

            ArrayList<Integer> daysOfWeek = SetDaysOfWeek();
            intent.putIntegerArrayListExtra("DaysOfWeek",daysOfWeek );
            DroidPreferences.SetString(context, "DaysOfWeek", daysOfWeek.toString());
            DroidPreferences.SetInteger(context, "timePickerHour", timePicker.getCurrentHour());
            DroidPreferences.SetInteger(context, "timePickerMinute", timePicker.getCurrentMinute());

            alarmIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            Calendar calendar = Calendar.getInstance();

            boolean addDay = false;

            if (timePicker.getCurrentHour() < calendar.getTime().getHours()) {
                addDay = true;
            } else if (timePicker.getCurrentHour() == calendar.getTime().getHours()) {

                if (timePicker.getCurrentMinute() < calendar.getTime().getMinutes()) {
                    addDay = true;
                }
            }
            calendar.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
            calendar.set(Calendar.MINUTE, timePicker.getCurrentMinute());
            if (addDay) {
                calendar.add(Calendar.DATE, 1);
            }

            alarmMgr.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
            Toast.makeText(context, "Alarme agendado com sucesso! ", Toast.LENGTH_LONG).show();

            Log.d("DroidAlarmClock", "Alarme agendado em " + DateFormat.format("yyyyMMdd HH:mm", calendar.getTime()).toString());
            finish();
        } catch (Exception ex) {
            Toast.makeText(context, "Não foi possivel agendar o alarme. " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
