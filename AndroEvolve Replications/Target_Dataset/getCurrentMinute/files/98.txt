package com.hemanshu95.android.silentit;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;


public class TimeTaker extends ActionBarActivity {

    Button ok;
    String pos;
    Bundle data;
    TimePicker tp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_taker);
        ok=(Button)findViewById(R.id.bokt);
        tp=(TimePicker)findViewById(R.id.timePicker);

        Intent intent=getIntent();
        //ok=(Button)findViewById(R.id.bokt);
        if(intent!=null && intent.hasExtra("data"))
        {
            data= intent.getParcelableExtra("data");

        }

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String x=tp.getCurrentHour()+":"+tp.getCurrentMinute();
                if(data.getString("position").equals("1"))
                {
                    data.putString("timestarth",Integer.toString(tp.getCurrentHour()));
                    data.putString("timestartm",Integer.toString(tp.getCurrentMinute()));

                }
                else
                {

                    data.putString("timeendh",Integer.toString(tp.getCurrentHour()));
                    data.putString("timeendm",Integer.toString(tp.getCurrentMinute()));

                }

                Intent intent = new Intent(getApplication(),Input.class)
                        .putExtra("data",data);
                        //.putExtra("hours",tp.getCurrentHour())
                        //.putExtra("minutes",tp.getCurrentMinute());
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_time_taker, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
