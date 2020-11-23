package com.calculomatic;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class EditEventActivity extends Activity{
	private ContributorsDataSource contributorsdatasource;
	private UsersDataSource usersdatasource;
	private ParticipantsDataSource participantsdatasource;
	private EventsDataSource eventsdatasource;
	public List<Contributor> contributors = new ArrayList<Contributor>();
	public List<Participant> participants = new ArrayList<Participant>();
	public String eventName;
	public String eventPlace;
	public static final String LOG_TAG = "Calculomatic";
	public Integer id = 0;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ScrollView sv =new ScrollView(this);
		final LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(1);
		layout.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		
		final LinearLayout contributorLayout = new LinearLayout(this);
		contributorLayout.setOrientation(1);
		contributorLayout.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		
		final LinearLayout participantLayout = new LinearLayout(this);
		participantLayout.setOrientation(1);
		participantLayout.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		
		Intent intent = getIntent();
		Bundle b = intent.getExtras();
		final String eventid = b.get("event").toString();
		Log.v(LOG_TAG, eventid);
		contributorsdatasource = new ContributorsDataSource(this);
		contributorsdatasource.open();
		contributors = contributorsdatasource.contributorsForEvent(eventid);
		contributorsdatasource.close();
		
		participantsdatasource = new ParticipantsDataSource(this);
		participantsdatasource.open();
		participants = participantsdatasource.participantsForEvent(eventid);
		participantsdatasource.close();
		
		eventsdatasource = new EventsDataSource(this);
		eventsdatasource.open();
		Event e = eventsdatasource.getEventById(eventid);
		eventsdatasource.close();
		
		TextView te = new TextView(this);
		te.setTextAppearance(this, android.R.style.TextAppearance_Large);
		te.setText(e.getEvent().toString());
		layout.addView(te);
		
		TextView tp = new TextView(this);
		tp.setTextAppearance(this, android.R.style.TextAppearance_Medium);
		tp.setText(e.getPlace().toString());
		layout.addView(tp);
		
		TextView tv = new TextView(this);
		tv.setTextAppearance(this, android.R.style.TextAppearance_Large);
		tv.setText("List Of Contributors");
		layout.addView(tv);
		
		for(Contributor c : contributors) {
			usersdatasource = new UsersDataSource(this);
			usersdatasource.open();			
			TextView t1 = new TextView(this);
			t1.setTextAppearance(this, android.R.style.TextAppearance_Large);			
			t1.setText(usersdatasource.getUserFromUid(c.getuid()).getUsername());
			t1.setId(id);
			id++;
			EditText e1 = new EditText(this);
			e1.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			e1.setText(c.getamount().toString());
			e1.setId(id);
			id++;
			usersdatasource.close();
			contributorLayout.addView(t1);
			contributorLayout.addView(e1);			
		}		
		layout.addView(contributorLayout);
		TextView tpc = new TextView(this);
		tpc.setTextAppearance(this, android.R.style.TextAppearance_Large);
		tpc.setText("List Of Participants");
		layout.addView(tpc);
		
		for (Participant p : participants) {
			usersdatasource = new UsersDataSource(this);
			usersdatasource.open();			
			TextView t1 = new TextView(this);			
			t1.setTextAppearance(this, android.R.style.TextAppearance_Large);
			t1.setText(usersdatasource.getUserFromUid(p.getuid()).getUsername());
			t1.setId(id);
			id++;
			EditText e1 = new EditText(this);
			e1.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			e1.setText(p.getamount().toString());
			e1.setId(id);
			id++;
			usersdatasource.close();
			participantLayout.addView(t1);
			participantLayout.addView(e1);
		}
		layout.addView(participantLayout);
		Button save = new Button(this);
		save.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		save.setText("Save");
		layout.addView(save);
		save.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
            	Integer contributorCount = contributorLayout.getChildCount();
            	Integer participantCount = participantLayout.getChildCount();
            	int amount;
            	Contributor c;
            	Participant p;
            	String username;
            	Boolean b = true;
            	usersdatasource = new UsersDataSource(getApplicationContext());            	
            	usersdatasource.open();     
            	for(int i=0; i < contributorCount; i=i + 2) {            		            		
        			int id = contributorLayout.getChildAt(i).getId(); 
        			int id_editext = contributorLayout.getChildAt(i+1).getId();                	
    				c = new Contributor();
    				TextView view = (TextView) findViewById(contributorLayout.getChildAt(i).getId());
    				username = view.getText().toString();    				
    				c.setuid(usersdatasource.getUserFromUsername(username).getId());    				
    				EditText et = (EditText) findViewById(contributorLayout.getChildAt(i+1).getId());
    				amount = Integer.parseInt(et.getText().toString());    				
    				c.setamount(amount);    				   				    			
        			c.setEid(Long.parseLong(eventid));
    				b = b && UpdateContributor(c);
    				c.setEid(0);
    				c.setuid(0);
    				c.setamount(0);    				
            	}

            	for(int i=0; i < participantCount; i=i + 2) {
					p = new Participant();
					TextView tv = (TextView) findViewById(participantLayout.getChildAt(i).getId());
					username = tv.getText().toString();            				
					p.setuid(usersdatasource.getUserFromUsername(username).getId());
					EditText ev = (EditText) findViewById(participantLayout.getChildAt(i+1).getId());
					amount = Integer.parseInt(ev.getText().toString());	            				
					p.setamount(amount);					
					p.setEid(Long.parseLong(eventid));
					b = b && UpdateParticipant(p);
					p.setEid(0);
					p.setuid(0);
					p.setamount(0);					     	
            	}                	
            	
            	usersdatasource.close();
            	if(b.equals(true)) {
            		Intent intent = new Intent(getApplicationContext(), EventsActivity.class);                
                	startActivity(intent);
            	}
            	else {
            		Toast.makeText(getApplicationContext(), "Error occurred while updating -- Please try again", 10).show();
            	}
            }
        });
		sv.addView(layout);
		setContentView(sv);
	}
	
	public boolean UpdateContributor(Contributor c){		
		contributorsdatasource = new ContributorsDataSource(this);
		contributorsdatasource.open();
		int result = contributorsdatasource.updateContributor(c);
		contributorsdatasource.close();
		if(result == 1) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public boolean UpdateParticipant(Participant p) {
		participantsdatasource = new ParticipantsDataSource(this);
		participantsdatasource.open();
		int result = participantsdatasource.updateParticipant(p);
		participantsdatasource.close();		
		if(result == 1) {
			return true;
		}
		else {
			return false;
		}
	}
	
}
