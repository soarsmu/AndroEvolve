package com.example.test1;

import java.util.ArrayList;

import beans.DoubleDatabase;
import beans.Player;
import beans.SingleDatabase;
import android.os.Bundle;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class ScoresActivity extends  Activity {

	TextView sec10,sec20,sec30;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scores);
		sec30=(TextView) findViewById(R.id.sec30);
		sec20=(TextView) findViewById(R.id.sec20);
		sec10=(TextView) findViewById(R.id.sec10);
		editvalues();
		initTabs();
		
//Typeface titleFont = Typeface.createFromAsset(this.getAssets(),"ROBOTOCONDENSED-REGULAR.TTF");
		
		//sec30.setTypeface(titleFont);
		//sec20.setTypeface(titleFont);
		//sec10.setTypeface(titleFont);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		 menu.add(Menu.NONE, Menu.FIRST, Menu.NONE, "Clear Scores");
		
		 return (super.onCreateOptionsMenu(menu));
	}
	
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if(item.getItemId()==Menu.FIRST)
		{
			SingleDatabase sd=new SingleDatabase(getApplicationContext());
			DoubleDatabase dd=new DoubleDatabase(getApplicationContext());
			sd.delete();
			dd.delete();
			editvalues();
			
		}
		return super.onOptionsItemSelected(item);
	}
	

	public void editvalues()
	{
		SingleDatabase sd=new SingleDatabase(getApplicationContext());
		int score=sd.getData(30);
		sec30.setText("Your High Score is : "+score);
		score=sd.getData(20);
		sec20.setText("Your High Score is : "+score);
		score=sd.getData(10);
		sec10.setText("Your High Score is : "+score);
		//
		//30 SECONDS
		//
		DoubleDatabase d=new DoubleDatabase(getApplicationContext());
		
		ArrayList<Player> list=d.getData(30);
//		ScrollView s=new ScrollView(this);
		
		TableLayout t1=(TableLayout)findViewById(R.id.tl3);
		
		if(list.size()>0)
		{
			for(int i=0;i<list.size();i+=2)
				{
				TableRow newRow = new TableRow(this);
				
				TextView na=new TextView(getApplicationContext());
				na.setTextAppearance(this,android.R.attr.textAppearanceLarge );
				na.setTextColor(getResources().getColor(android.R.color.black));
				na.setText(list.get(i).getPlayerName());
				na.setGravity(0x11);
				
				newRow.addView(na);
				TextView s1=new TextView(getApplicationContext());
				s1.setTextAppearance(this,android.R.attr.textAppearanceLarge );
				s1.setTextColor(getResources().getColor(android.R.color.black));
				s1.setText(String.valueOf(list.get(i).getPlayerScore()));
				s1.setGravity(0x11);
				newRow.addView(s1);
				
				TextView na2=new TextView(this);
				na2.setTextAppearance(this,android.R.attr.textAppearanceLarge );
				na2.setTextColor(getResources().getColor(android.R.color.black));
				na2.setText(list.get(i+1).getPlayerName());
				na2.setGravity(0x11);
				
				newRow.addView(na2);
				
				TextView s2=new TextView(this);
				s2.setTextAppearance(this,android.R.attr.textAppearanceLarge );
				s2.setTextColor(getResources().getColor(android.R.color.black));
				s2.setText(String.valueOf(list.get(i+1).getPlayerScore()));
				s2.setGravity(0x11);
				newRow.addView(s2);
				
				t1.addView(newRow);
				}
		}
		else
		{
			
			t1.removeAllViews();
			
		}
			//
		//  10 SECONDS
		// 
		list=d.getData(10);
	
				TableLayout t3=(TableLayout)findViewById(R.id.tl1);
		if(list.size()>0)
		{		
				for(int i=0;i<list.size();i+=2)
				{
				TableRow newRow = new TableRow(this);
				
				TextView na=new TextView(getApplicationContext());
				na.setTextAppearance(this,android.R.attr.textAppearanceLarge );
				na.setTextColor(getResources().getColor(android.R.color.black));
				na.setText(list.get(i).getPlayerName());
				na.setGravity(0x11);
				
				newRow.addView(na);
				TextView s1=new TextView(getApplicationContext());
				s1.setTextAppearance(this,android.R.attr.textAppearanceLarge );
				s1.setTextColor(getResources().getColor(android.R.color.black));
				s1.setText(String.valueOf(list.get(i).getPlayerScore()));
				s1.setGravity(0x11);
				newRow.addView(s1);
				
				TextView na2=new TextView(this);
				na2.setTextAppearance(this,android.R.attr.textAppearanceLarge );
				na2.setTextColor(getResources().getColor(android.R.color.black));
				na2.setText(list.get(i+1).getPlayerName());
				na2.setGravity(0x11);
				
				newRow.addView(na2);
				
				TextView s2=new TextView(this);
				s2.setTextAppearance(this,android.R.attr.textAppearanceLarge );
				s2.setTextColor(getResources().getColor(android.R.color.black));
				s2.setText(String.valueOf(list.get(i+1).getPlayerScore()));
				s2.setGravity(0x11);
				newRow.addView(s2);
				
				t3.addView(newRow);
				}
		}
		else
		{
			
			t3.removeAllViews();
			
		}
		//
		// 20 SECONDS
		//
			list=d.getData(20);
		
				TableLayout t2=(TableLayout)findViewById(R.id.tl2);
		if(list.size()>0)
		{
				for(int i=0;i<list.size();i+=2)
				{
				TableRow newRow = new TableRow(this);
				
				TextView na=new TextView(getApplicationContext());
				na.setTextAppearance(this,android.R.attr.textAppearanceLarge );
				na.setTextColor(getResources().getColor(android.R.color.black));
				na.setText(list.get(i).getPlayerName());
				na.setGravity(0x11);
				
				newRow.addView(na);
				TextView s1=new TextView(getApplicationContext());
				s1.setTextAppearance(this,android.R.attr.textAppearanceLarge );
				s1.setTextColor(getResources().getColor(android.R.color.black));
				s1.setText(String.valueOf(list.get(i).getPlayerScore()));
				s1.setGravity(0x11);
				newRow.addView(s1);
				
				TextView na2=new TextView(this);
				na2.setTextAppearance(this,android.R.attr.textAppearanceLarge );
				na2.setTextColor(getResources().getColor(android.R.color.black));
				na2.setText(list.get(i+1).getPlayerName());
				na2.setGravity(0x11);
				
				newRow.addView(na2);
				
				TextView s2=new TextView(this);
				s2.setTextAppearance(this,android.R.attr.textAppearanceLarge );
				s2.setTextColor(getResources().getColor(android.R.color.black));
				s2.setText(String.valueOf(list.get(i+1).getPlayerScore()));
				s2.setGravity(0x11);
				newRow.addView(s2);
				
				t2.addView(newRow);
				}
		}
		else
		{
			
			t2.removeAllViews();
			
			
		}
		
		
		
		
	}
	
	
	
	public void initTabs()
	{
		TabHost tab_host = (TabHost) findViewById(android.R.id.tabhost);
        // don't forget this setup before adding tabs from a tabhost using a xml view or you'll get an nullpointer exception
        tab_host.setup(); 

        TabSpec ts1 = tab_host.newTabSpec("TAB_DATE");
        ts1.setIndicator("10Sec");
        ts1.setContent(R.id.tab1);
        tab_host.addTab(ts1);

        TabSpec ts2 = tab_host.newTabSpec("TAB_GEO");
        ts2.setIndicator("20Sec");
        ts2.setContent(R.id.tab2);
        tab_host.addTab(ts2);

        TabSpec ts3 = tab_host.newTabSpec("TAB_TEXT");
        ts3.setIndicator("30Sec");
        ts3.setContent(R.id.tab3);
        tab_host.addTab(ts3);

        tab_host.setCurrentTab(2);

	}
}
