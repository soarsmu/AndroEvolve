package com.msi.shortwave;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.AdapterView.OnItemSelectedListener;

public class time extends Fragment {
	
	private Button b1;
	private Button searchbutton;
	static final int TIME_DIALOG_ID = 0;
	private String selhour;
	private String selmin;
	private String sellang;
	private String seltargetid ="";
	private String sellangid ="";

	private String seltarget = "";
	public ArrayList<String> targetids = new ArrayList<String>();
	public ArrayList<String> langids = new ArrayList<String>();

	

	   @Override
	   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	      // Inflate the layout for this fragment
	      View v = inflater.inflate(R.layout.time, container, false);
	      
	      Log.i("SW", "RESUME");
	      TimePicker timeselect = (TimePicker) v.findViewById(R.id.timepickertime);
	      SimpleDateFormat f = new SimpleDateFormat("HH");
	  	f.setTimeZone(TimeZone.getTimeZone("UTC"));
	  	
	      SimpleDateFormat m = new SimpleDateFormat("mm");
	  	m.setTimeZone(TimeZone.getTimeZone("UTC"));
	  	timeselect.setIs24HourView(true);
	  	timeselect.setCurrentHour(Integer.valueOf(f.format(new Date())));
	  	timeselect.setCurrentMinute(Integer.valueOf(m.format(new Date())));
	  	f = new SimpleDateFormat("HHmm");
	  	f.setTimeZone(TimeZone.getTimeZone("UTC"));
	  	
	  	selhour = (f.format(new Date()).substring(0,2));
	  	selmin = (f.format(new Date()).substring(2,4));
	  	
	  	searchbutton = (Button) v.findViewById(R.id.bsearchtime);
		searchbutton.setOnClickListener(new View.OnClickListener() {
		public void onClick(View v) {
			
			TimePicker timeselect = (TimePicker) getActivity().findViewById(R.id.timepickertime);

			
			String strmin =  Integer.toString(timeselect.getCurrentMinute());
			String strhour =  Integer.toString(timeselect.getCurrentHour());
			if (strmin.length() == 1){
			strmin = "0" + strmin;
			}
			if (strhour.length() == 1){
				strhour = "0" + strhour;
				}
			
			selmin = strmin;
			selhour = strhour;
			
	    	
		    	FragmentTransaction ft = getFragmentManager().beginTransaction();

		    	Fragment mFragmentR=new results ();
		    	
		    	
		    	
		    	 Bundle bund1 =  new Bundle();
			bund1.putString("station", "");
			
			
			Spinner mySpn = (Spinner) getActivity().findViewById(R.id.targetspin);
			int spnItem = mySpn.getSelectedItemPosition();
				
			seltarget = targetids.get(spnItem);
			mySpn = (Spinner) getActivity().findViewById(R.id.langspin);
			spnItem = mySpn.getSelectedItemPosition();
			sellang = langids.get(spnItem);
			
			
			sellangid = sellang;
			seltargetid = seltarget;


		    	bund1.putString("time", selhour+selmin);
		    	bund1.putString("target", seltarget);
		    	bund1.putString("language", sellang);
		    	bund1.putString("freq", "");
			mFragmentR.setArguments(bund1);
			  if ((getResources().getConfiguration().screenLayout &      Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {     
			        ft.replace(R.id.fragment_content2, mFragmentR, null);
			    }else if((getResources().getConfiguration().screenLayout &      Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE) {     

			        ft.replace(R.id.fragment_content2, mFragmentR, null);
			    }else{
		    	ft.replace(R.id.fragment_content, mFragmentR, null);
			    }
			ft.addToBackStack(null);

			ft.commit();
	  	
		}});
		
		targetids.add("999");
	
		Spinner spinner = (Spinner) v.findViewById(R.id.targetspin);
		ArrayAdapter<String> adapterForSpinner = new ArrayAdapter<String>(getActivity(),
        		android.R.layout.simple_spinner_item);
  		                adapterForSpinner.setDropDownViewResource
  		(android.R.layout.simple_spinner_dropdown_item);
  		          	adapterForSpinner.add(getString(R.string.alltargets));
  		              spinner.setAdapter(adapterForSpinner); 
	    
  		         	SQLiteDatabase myDB= null;
	      	        	
  		      	    myDB = getActivity().openOrCreateDatabase("shortwave", getActivity().MODE_PRIVATE, null);
  		          	Cursor c = myDB.rawQuery("SELECT target, code FROM targets;",null);
  		          	
  		              if(c.moveToFirst()){
  		                  do{
  		                  	adapterForSpinner.add(c.getString(0));
  		                  	targetids.add(c.getString(1));
  		                  } while(c.moveToNext()); }

  		              c.close();
  		             
  		              
  		              
  		              
	    spinner.setOnItemSelectedListener(new OnItemSelectedListener() {


		    public void onItemSelected(AdapterView<?> parent,
		        View view, int pos, long id) {
		    	
		    	
		    
		     	seltarget = Integer.toString(pos);
		    
		    	
		   
		    }

		    public void onNothingSelected(AdapterView<?> parent) {
		      // Do nothing.
		    }
		
	    });
	    
	    langids.add("999");
	    langids.add("");

		Spinner langspinner = (Spinner) v.findViewById(R.id.langspin);
		ArrayAdapter<String> adapterForLangSpinner = new ArrayAdapter<String>(getActivity(),
        		android.R.layout.simple_spinner_item);
  		                adapterForSpinner.setDropDownViewResource
  		(android.R.layout.simple_spinner_dropdown_item);
  		              adapterForLangSpinner.add(getString(R.string.anylanguage));
  		            adapterForLangSpinner.add(getString(R.string.notvoicelanguage));
  		            langspinner.setAdapter(adapterForLangSpinner); 
	    
  		         	
	      	        	
  		          c = myDB.rawQuery("SELECT language, code FROM languages",null);
  		          	
  		              if(c.moveToFirst()){
  		                  do{
  		                	adapterForLangSpinner.add(c.getString(0));
  		                  	langids.add(c.getString(1));
  		                  } while(c.moveToNext()); }

  		              c.close();
  		              myDB.close();
  		              
  		              
  		              
  		     langspinner.setOnItemSelectedListener(new OnItemSelectedListener() {


		    public void onItemSelected(AdapterView<?> parent,
		        View view, int pos, long id) {
		    	
		    	
		    
		     	sellang = Integer.toString(pos);
		    
		    	
		   
		    }

		    public void onNothingSelected(AdapterView<?> parent) {
		      // Do nothing.
		    }
		
	    });
	    
	
	  	return v;
	  	
	
	   }
	   
	   public time() {

	   }
	        
	}