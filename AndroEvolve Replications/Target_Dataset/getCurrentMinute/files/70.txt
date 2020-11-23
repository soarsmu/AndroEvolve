package com.ibm.bluelist;

import android.app.Activity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import android.app.Activity;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

public class selecttime extends Activity implements OnClickListener{
	TimePicker tp1;
	TextView itv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.selecttime);
System.out.println("Getting Text view");		
		 itv=(TextView) findViewById(R.id.game);
		Button b=(Button) findViewById(R.id.ne);
		tp1=(TimePicker) findViewById(R.id.timePicker1);
		
		Intent i=getIntent();
		String product=i.getStringExtra("product");
		System.out.println("Selected product: " + product);
		itv.setText(product);
		final Calendar c=Calendar.getInstance();
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute =c.get(Calendar.MINUTE);
		tp1.setCurrentHour(hour);
		tp1.setCurrentMinute(minute);
		b.setOnClickListener(this);
		
	}
	public void onClick(View v)
	{
		System.out.println("inside button click");
		int hour,minute;
		System.out.println("Selected time h: " + tp1.getCurrentHour());
		System.out.println("Selected time m: " + tp1.getCurrentMinute());
		String sres=itv.getText().toString();
		Intent i=new Intent(getApplicationContext(),lastscreen.class);
		i.putExtra("a", sres);
		int a=tp1.getCurrentHour();
		String ti;
		if(a>=12)
			ti = new Integer(a).toString()+":"+tp1.getCurrentMinute().toString()+"PM";
		else
			ti = new Integer(a).toString()+":"+tp1.getCurrentMinute().toString()+"AM";
		i.putExtra("b", ti);
		startActivity(i);
	}

	}
