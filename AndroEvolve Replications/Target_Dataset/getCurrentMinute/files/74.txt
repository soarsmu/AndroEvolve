package com.vinh.alarmclockandroid.activities;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.vinh.alarmclockandroid.R;
import com.vinh.alarmclockandroid.activities.AlarmReceiver;
import com.vinh.alarmclockandroid.database.QuizModel;
import com.vinh.alarmclockandroid.database_giobaothuc.DatabaseHandle;
import com.vinh.alarmclockandroid.database_giobaothuc.GioBaoThucModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.SimpleTimeZone;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button btnStart;
    Button btnStop;

    TextView tvTimePicker;
    TimePicker timePicker;
    Calendar calendar;
    AlarmManager alarmManager;
    Spinner spSubject;

    ImageView iv_acceptAlarm1;
    ImageView iv_acceptAlarm2;
    ImageView iv_backAlarm1;
    ImageView iv_backAlarm2;

    Intent intent;

    Intent intentDatBaoThucThanhCong;

    private int topic;

    private GioBaoThucModel quizModel;

    List<GioBaoThucModel> quizList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        DatabaseHandle.getInstance(this).insertTable();

        //Log.d("list", "onCreate: " + DatabaseHandle.getInstance(this).getListGioBaoThuc().size());

        loadUI();

        addSpiner();

        timePicker.setIs24HourView(true);

        calendar = Calendar.getInstance();

        //cho phep truy cap va he thong bao dong
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        intent = new Intent(MainActivity.this, AlarmReceiver.class);

        btnStart.setOnClickListener(this);
        iv_acceptAlarm1.setOnClickListener(this);
        iv_acceptAlarm2.setOnClickListener(this);
        iv_backAlarm1.setOnClickListener(this);
        iv_backAlarm2.setOnClickListener(this);

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };

        //intentDatBaoThucThanhCong = new Intent(getApplicationContext(), DatBaoThucThanhCong.class);


    }

    private void addSpiner() {
        final String[] arrSubject = {"Java", "Đố vui", "IQ"};
        spSubject = (Spinner) findViewById(R.id.spinner_topic);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arrSubject);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spSubject.setAdapter(adapter);

        spSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (arrSubject[position].equals("Java")) {
                    topic = 1;
                } else if (arrSubject[position].equals("Đố vui")) {
                    topic = 2;
                } else {
                    topic = 3;
                    Log.d("topic", "topic: " + topic);
                }
                Log.d("topic", "topic: " + topic);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                topic = 1;
            }
        });
    }

    private void setAlarmDate() {
        int ngaycach = 1;

        AlarmHandle.setAlarm(timePicker.getCurrentHour(), timePicker.getCurrentMinute(), MainActivity.this, ngaycach);
        Log.d("abcd", "setAlarmDate: " + timePicker.getCurrentHour() + " : " + timePicker.getCurrentMinute());
    }

    private void updateLabel() {
        String myFormat = "dd/MM/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

    }


    private void loadUI() {
        btnStart = (Button) findViewById(R.id.bt_start);
        tvTimePicker = (TextView) findViewById(R.id.tv_timePicker);
        timePicker = (TimePicker) findViewById(R.id.timepicker);

        iv_acceptAlarm1 = (ImageView) findViewById(R.id.bt_acceptAlarm1);
        iv_acceptAlarm2 = (ImageView) findViewById(R.id.bt_acceptAlarm2);
        iv_backAlarm1 = (ImageView) findViewById(R.id.bt_backAlarm1);
        iv_backAlarm2 = (ImageView) findViewById(R.id.bt_backAlarm2);
//        btnStop = (Button) findViewById(R.id.bt_stop);

        quizList = DatabaseHandle.getInstance(this).getListGioBaoThuc();
        String gio = "";
        String phut = "";
        if (quizList.size() != 0) {
            gio = String.valueOf(quizList.get(0).getHour());
            phut = String.valueOf(quizList.get(0).getMinute());

            if (quizList.get(0).getHour() < 10) {
                gio = "0" + gio;
            }

            if (quizList.get(0).getMinute() < 10) {
                phut = "0" + phut;
            }

            if (quizList.size() != 0) {
                tvTimePicker.setText(gio + " : " + phut);
            }
        }

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_start:
                calendar.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
                calendar.set(Calendar.MINUTE, timePicker.getCurrentMinute());

                int gio = timePicker.getCurrentHour();
                int phut = timePicker.getCurrentMinute();

                String gio_sting = String.valueOf(gio);
                String phut_string = String.valueOf(phut);

                if (phut < 10) {
                    phut_string = "0" + phut_string;
                }

                if (gio < 10) {
                    gio_sting = "0" + gio_sting;
                }

                //khac voi intent thuong la no se luon ton tai ke ca khi ung dung ket thuc

                tvTimePicker.setText(gio_sting + " : " + phut_string);

                //toast ra man hinh
                //xu li lay time to wake up:
                int realHours = Calendar.getInstance().getTime().getHours();
                int realMinute = Calendar.getInstance().getTime().getMinutes();
                int realTime = realHours * 60 + realMinute;

                int alarmTime = gio * 60 + phut;

                int totalMinuteTimewakeup = alarmTime - realTime;
                if (totalMinuteTimewakeup < 0) {
                    totalMinuteTimewakeup += 24 * 60;
                }

                int hourTimewakeup = totalMinuteTimewakeup / 60;
                int minuteTimewakeup = totalMinuteTimewakeup % 60;
                String hourTimewakeup_string = String.valueOf(hourTimewakeup);
                String minuteTimewakeup_string = String.valueOf(minuteTimewakeup);

                if (hourTimewakeup < 10) {
                    hourTimewakeup_string = "0" + hourTimewakeup_string;
                }
                if (minuteTimewakeup < 10) {
                    minuteTimewakeup_string = "0" + minuteTimewakeup_string;
                }

                if (totalMinuteTimewakeup == 0) {
                    Toast.makeText(MainActivity.this, "Time to Wake up: 24 hours!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, "Time to Wake up: " + hourTimewakeup_string + " hours, " + minuteTimewakeup_string + " minutes ", Toast.LENGTH_SHORT).show();
                }

//                setAlarmDate();
//                DatabaseHandle.getInstance(this).deleteTable();
//                DatabaseHandle.getInstance(this).insertTable(timePicker.getCurrentHour(), timePicker.getCurrentMinute(), 1, topic);
//
//                startActivity(intentDatBaoThucThanhCong);


                break;

//            case R.id.bt_stop:
//                DatabaseHandle.getInstance(this).deleteTable();
//                break;

            case R.id.bt_acceptAlarm1:
            case R.id.bt_acceptAlarm2:

                setAlarmDate();
                DatabaseHandle.getInstance(this).deleteTable();

                DatabaseHandle.getInstance(this).insertTable(timePicker.getCurrentHour(), timePicker.getCurrentMinute(), 1, topic);
                super.onBackPressed();
                //startActivity(intentDatBaoThucThanhCong);
                break;

            case R.id.bt_backAlarm1:
            case R.id.bt_backAlarm2:
                super.onBackPressed();

                break;
            default:
                break;
        }
    }
}
