package me.shyboy.mengma;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import me.shyboy.mengma.Common.Sign;
import me.shyboy.mengma.Common.SignConfig;
import me.shyboy.mengma.Common.User;
import me.shyboy.mengma.database.UserHelper;
import me.shyboy.mengma.methods.OkHttpUtil;


public class NewSignActivity extends Activity {

    private ImageButton bt_out;
    private Button bt_commit;
    private TimePicker tp_start,tp_end;
    private EditText et_decription;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_new_sign);
        bt_commit = (Button)findViewById(R.id.newsign_commit);
        et_decription = (EditText)findViewById(R.id.newsign_et_description);
        bt_out = (ImageButton)findViewById(R.id.newsign_out);
        bt_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initTimePicker();

        bt_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SignConfig.isNetworkConnected(NewSignActivity.this) == false)
                {
                    Toast.makeText(NewSignActivity.this,"凑 ~ ~ 没联网",Toast.LENGTH_SHORT).show();
                    return ;
                }
                String description = et_decription.getText().toString();
                if(description.length() == 0)
                {
                    Toast.makeText(NewSignActivity.this,"描述内容不能为空.",Toast.LENGTH_SHORT).show();
                    return;
                }
                String start_at = reFormatTime(tp_start.getCurrentHour())+":"
                        +reFormatTime(tp_start.getCurrentMinute())+":00";
                String end_at = reFormatTime(tp_end.getCurrentHour())+":"+
                        reFormatTime(tp_end.getCurrentMinute())+":00";
                User user = new UserHelper(NewSignActivity.this).getUser();
                Sign sign = new Sign(user.getSno(),start_at,end_at,description,user.getAccess_token());
                new OkHttpUtil(NewSignActivity.this).newSign(sign);
            }
        });


    }

    private String reFormatTime(int t)
    {
        if(t < 10)
            return "0"+t;
        return ""+t;
    }
    private void initTimePicker()
    {
        tp_start = (TimePicker)findViewById(R.id.newsign_time_start);
        tp_end = (TimePicker)findViewById(R.id.newsign_time_end);
        tp_start.setIs24HourView(true);
        tp_end.setIs24HourView(true);
        tp_end.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                int h_start = tp_start.getCurrentHour();
                int m_start = tp_start.getCurrentMinute();
                int timestart = h_start*100 + m_start;
                int hour = tp_end.getCurrentHour();
                int m = tp_end.getCurrentMinute();
                int timeend = hour*100 + m;
                if(timestart > timeend)
                {
                    Toast.makeText(NewSignActivity.this,"截止时间不合理",Toast.LENGTH_SHORT).show();
                    tp_end.setCurrentHour(h_start);
                    tp_end.setCurrentMinute(m_start);
                }
            }
        });
    }

}
