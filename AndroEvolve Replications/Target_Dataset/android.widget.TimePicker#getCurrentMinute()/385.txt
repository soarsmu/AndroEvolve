package com.example.timepicker;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

public class MainActivity extends Activity
{

	TimePicker tp;
	Button b;
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        tp=(TimePicker)findViewById(R.id.timePicker1);
       //If time picker wants show 24 hours format[without AM/PM] write true inside
    	tp.setIs24HourView(true);
        b=(Button)findViewById(R.id.button1);
        
        b.setOnClickListener(new OnClickListener() 
        {
			
			@Override
			public void onClick(View v) 
			{
				// TODO Auto-generated method stub
//				int hour = tp.getCurrentHour();
//				int min =tp.getCurrentMinute();

				String hour = String.valueOf(tp.getCurrentHour());
				String min =String.valueOf(tp.getCurrentMinute());
			
				
				Toast.makeText(MainActivity.this, "The Current Time is " +hour +" : " +min , Toast.LENGTH_LONG).show();
				
			}
		});
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
