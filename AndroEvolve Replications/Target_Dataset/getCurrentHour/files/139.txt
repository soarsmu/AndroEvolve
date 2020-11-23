package com.dontburnthe.turkey;

import java.util.Calendar;
import java.util.LinkedList;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

public class TimeSelectDialog extends Dialog{
	
	int minute;
	int hour;
	int day;
	int month;
	int year;
	TimePicker timePicker;
	DatePicker datePicker;

	Data data = new Data();
	int longestRecipe = 0;
	LinkedList<Recipe> recipes = new LinkedList<Recipe>();
	
	Calendar mustStart = Calendar.getInstance(); 
	
	public TimeSelectDialog(Context context) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.custom_time_select_dialog);
		
		timePicker = (TimePicker) findViewById(R.id.timePicker1);
		datePicker = (DatePicker) findViewById(R.id.datePicker1);
		
		
		SharedPreferences settings = getContext().getSharedPreferences("veg", 0);
		String savedList = settings.getString("list", "");

		String[] listSplit = savedList.split(" ");
		
		for(int i = 0; i<listSplit.length; i++){
			for(int y = 0; y<listSplit[i].length(); y++){
				if(listSplit[i].charAt(y) == ','){
					String holdNumber = listSplit[i].substring(0, y);
					recipes.add(data.recipes.get(Integer.parseInt(holdNumber)));
				}
			}
		}
		
		//Get longest recipe
		for(int i = 0; i<recipes.size(); i++){
			int recipeLength = 0;
			
			for(int y = 0; y<recipes.get(i).steps.size(); y++){
				if(recipes.get(i).section.equals("main")){
					if(recipes.get(i).steps.get(y).time == 0f){
						String holdNumber = listSplit[i].substring(listSplit[i].lastIndexOf(',') + 1);
						recipes.get(i).steps.get(y).time = Integer.parseInt(holdNumber);
						recipeLength = recipeLength + recipes.get(i).steps.get(y).time;
					}
					else{
						recipeLength = recipeLength + recipes.get(i).steps.get(y).time;
					}	
				}
				else{
					recipeLength = recipeLength + recipes.get(i).steps.get(y).time;
				}
			}
			
			if(recipeLength > longestRecipe){
				longestRecipe = recipeLength;
			}
		}
		System.out.println("LONGEST: " + longestRecipe);
		longestRecipe = longestRecipe + 20;
		
		int hours = longestRecipe / 60; //since both are ints, you get an int
		int minutes = longestRecipe % 60;
		
		System.out.println(hours);
		System.out.println(minutes);
		
		timePicker.setCurrentHour(timePicker.getCurrentHour()+hours);
		if(timePicker.getCurrentMinute()+minutes >= 60){
			if(timePicker.getCurrentHour() == 23){
				timePicker.setCurrentMinute(timePicker.getCurrentMinute()+minutes);
				timePicker.setCurrentHour(0);
			}
			else{
				timePicker.setCurrentMinute(timePicker.getCurrentMinute()+minutes);
				timePicker.setCurrentHour(timePicker.getCurrentHour()+1);
			}
			
		}
		else{
			timePicker.setCurrentMinute(timePicker.getCurrentMinute()+minutes);
		}
		
		
		datePicker.clearFocus();
		timePicker.clearFocus();
		mustStart.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
		mustStart.set(Calendar.MINUTE, timePicker.getCurrentMinute());
		mustStart.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
		mustStart.set(Calendar.MONTH, datePicker.getMonth());
		mustStart.set(Calendar.YEAR, datePicker.getYear());
		
		Button dialogButton = (Button) findViewById(R.id.dialogButtonOK);
		dialogButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dismiss();
			}
		});
		
		Button cookButton = (Button) findViewById(R.id.button1);
		cookButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
				timePicker.clearFocus();
				hour   = timePicker.getCurrentHour();
				minute = timePicker.getCurrentMinute();
				
				datePicker.clearFocus();
				day = datePicker.getDayOfMonth();
				month = datePicker.getMonth();
				year = datePicker.getYear();
				
				Calendar selected = Calendar.getInstance();
				selected.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
				selected.set(Calendar.MINUTE, timePicker.getCurrentMinute());
				selected.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
				selected.set(Calendar.MONTH, datePicker.getMonth());
				selected.set(Calendar.YEAR, datePicker.getYear());
				
				if(selected.before(mustStart)){
					Toast.makeText(getContext(), "Oh no! The meal won't be ready if you start cooking now. Choose a later time to eat.", Toast.LENGTH_SHORT).show();
				}
				else{
					Intent intent = new Intent(getContext(), Timer.class);
					intent.putExtra("minute",minute);
					intent.putExtra("hour",hour);
					intent.putExtra("day",day);
					intent.putExtra("month",month);
					intent.putExtra("year",year);
					getContext().startActivity(intent);
				}
			}
		});		
	}
}
