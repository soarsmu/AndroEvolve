package am.ze.wookoo.myapplication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TimePicker;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;

public class AlarmActivity extends AppCompatActivity {
    public static AlarmManager alarm_manager;
    TimePicker alarm_timepicker;
    Context context;
    public static  PendingIntent pendingIntent;
    public static Intent my_intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        alarm_timepicker = findViewById(R.id.picker_date);
        alarm_timepicker.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);
        this.context = this;
        alarm_manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        final Calendar calendar = Calendar.getInstance();
        my_intent = new Intent(this.context,Alarm_Reciver.class);
        ArrayList<Button> week_btns = new ArrayList<Button>();
        week_btns.add((Button)findViewById(R.id.alarm_sun));
        week_btns.add((Button)findViewById(R.id.alarm_mon));
        week_btns.add((Button)findViewById(R.id.alarm_tue));
        week_btns.add((Button)findViewById(R.id.alarm_wen));
        week_btns.add((Button)findViewById(R.id.alarm_thu));
        week_btns.add((Button)findViewById(R.id.alarm_fri));
        week_btns.add((Button)findViewById(R.id.alarm_sat));

        for (final Button btn : week_btns){
            final Typeface type = btn.getTypeface();
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(btn.isSelected()){
                        btn.setSelected(false);
                        btn.setTypeface(type, Typeface.BOLD);
                    }
                    else{
                        btn.setSelected(true);

                        btn.setTypeface(type, Typeface.NORMAL);
                    }
                }
            });

        }


        Button save_btn = findViewById(R.id.alarm_save);
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                calendar.set(Calendar.HOUR_OF_DAY,alarm_timepicker.getCurrentHour());
                calendar.set(Calendar.MINUTE,alarm_timepicker.getCurrentMinute());
                calendar.set(Calendar.SECOND,0);
                int hour = alarm_timepicker.getCurrentHour();
                int minute = alarm_timepicker.getCurrentMinute();
                Toast.makeText(AlarmActivity.this, hour + "시 " + minute + "분 에 알람이 울립니다." ,Toast.LENGTH_SHORT).show();

                my_intent.putExtra("state","alarm on");
                pendingIntent = PendingIntent.getBroadcast(AlarmActivity.this, 0, my_intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

                alarm_manager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                        pendingIntent);



                Intent mIntent = new Intent(AlarmActivity.this,RecordActivity.class);

                StringBuffer sb = new StringBuffer();
                sb.append("UPDATE user SET ALARM = ");

                String data;
                if(alarm_timepicker.getCurrentHour() > 12 ){
                    data = String.format(" Alarm %d : %02d PM", (alarm_timepicker.getCurrentHour() - 12),alarm_timepicker.getCurrentMinute());
                }else if(alarm_timepicker.getCurrentHour() == 12){
                    data = String.format(" Alarm %d : %02d PM", (alarm_timepicker.getCurrentHour()),alarm_timepicker.getCurrentMinute());
                }else if(alarm_timepicker.getCurrentHour() == 0){
                    data = String.format(" Alarm %d : %02d AM", (12),alarm_timepicker.getCurrentMinute());
                }
                else {
                    data = String.format(" Alarm %d : %02d AM", (alarm_timepicker.getCurrentHour()),alarm_timepicker.getCurrentMinute());
                }
                startActivity(mIntent);
                finish();

            }





        });

    }
}
