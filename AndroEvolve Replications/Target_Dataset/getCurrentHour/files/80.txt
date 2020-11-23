/*This is a course requirement for CS 192 Software Engineering II under the supervision of Asst. Prof. Ma. Rowena C. Solamo of the Department of Computer Science, College of Engineering, University of the Philippines, Diliman for the AY 2014-2015â€�.
 Neil Jonathan A. Joaquin
 David Relao*/

/*Code History:
Initial Code Authored by: David Relao*/

/* File Creation Date: April 28, 2015
    Development Group: Blue Navy Inc.
    Client Group: Purple McShort Shorts
    Purpose of file: Edits an event from the view and from the database.
*/

package com.example.retentionscheduler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;

import android.support.v7.app.ActionBarActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ClickableSpan;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class EditEvent extends ActionBarActivity implements OnClickListener{
	DataAccessObject dao = new DataAccessObject(this);
	Button button;
	String words = "";
	String temp1 = "", temp2 = "";
	boolean test = true;
	String time = "x";
	final Context context = this;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit);
		TextView view = (TextView) findViewById(R.id.t2);
		
		try {
			String data = dao.readFile("database");
			final SpannableString str = new SpannableString(data);
			final String[] state = data.split("\n");
			LinearLayout layout = (LinearLayout) findViewById(R.id.linearLayout1);
  			for (int i = 0; i < state.length; i++) {
  				
  				final String word = state[i];
  			        Button btnTag = new Button(this);
  			        btnTag.setText(state[i]);
  			        btnTag.setId(i);
  			      btnTag.setBackgroundColor(Color.parseColor("#81dedc"));
  			        layout.addView(btnTag);
  			      ((Button)findViewById(i)).setOnClickListener(new OnClickListener()
  			      {
  			     	 @Override
					public void onClick(View v) {
  			     		final Dialog dialog = new Dialog(context);
						dialog.setContentView(R.layout.edit_popup);
						dialog.setTitle("Event Information:");
						
						try {
							temp1 = word;
							temp2 = word;
							Toast.makeText(getApplicationContext(), word, Toast.LENGTH_LONG).show();
							
							System.out.println("Current file: "+word);
							String data1 = dao.readFile(word);
							TextView txt = (TextView) dialog.findViewById(R.id.info123);
							final String[] state1 = data1.split("\n");
							StringBuilder strB = new StringBuilder();
							for (int i=1; i<state1.length; i++) {
								strB.append(state1[i]);
								strB.append("\n");
							}
							txt.setText(strB.toString());
							
							Button editBut = (Button) dialog.findViewById(R.id.editt);
							editBut.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									final Dialog dialog1 = new Dialog(context);
									dialog1.setContentView(R.layout.edit_popup1);
									dialog1.setTitle("Edit Event Information");
									try {
										
										final EditText tname = (EditText) dialog1.findViewById(R.id.event123);
										final EditText tdesc = (EditText) dialog1.findViewById(R.id.desc123);
										tname.setText(state1[1]);
										tdesc.setText(state1[2]);
										
										final TimePicker timePicker = (TimePicker) dialog1.findViewById(R.id.tp1);
										String[] tiime = state1[state1.length-1].split(":");
										timePicker.setCurrentHour(Integer.parseInt(tiime[0]));
										final int hour = timePicker.getCurrentHour();
										timePicker.setCurrentMinute(Integer.parseInt(tiime[1].substring(0,2)));
										final int min = timePicker.getCurrentMinute();
										
										timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
											int hour = timePicker.getCurrentHour();
											 int min = timePicker.getCurrentMinute();
											 @Override
									            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
									                if(hour!= timePicker.getCurrentHour() || min !=timePicker.getCurrentMinute()){
									               	 hour = timePicker.getCurrentHour();
									               	 min = timePicker.getCurrentMinute();
									               	 if (min > 9) {
									               		 if (hour >= 12) {
									               			 time=(hour-12)+":"+min+" PM";
									               		 }
									               		 else {
									               			 time=hour+":"+min+" AM";
									               		 }
									               	 }
									               	 else {
									               		 if (hour >= 12) {
									               			 time=(hour-12)+":0"+min+" PM";
									               		 }
									               		 else {
									               			 time=hour+":0"+min+" AM";
									               		 }
									               	 }
									                }
									            }
									        });
										
										Button saveBut = (Button) dialog1.findViewById(R.id.save);
										saveBut.setOnClickListener(new OnClickListener() {
											@Override
											public void onClick(View v) {
												try {
													FileOutputStream fs1;
													if (tname.getText() != null) {
														state1[1] = ""+tname.getText().toString();
														temp2 = state1[1];	
													}
													
													if (tdesc.getText() != null) {
														state1[2] = ""+tdesc.getText().toString();
													}
													
													 //time=hour+":"+min;
													 if (!time.equals("x")) {
														 state1[state1.length-1] = time;
													 }
													
													Toast.makeText(getApplicationContext(), "Edit Successful!", Toast.LENGTH_LONG).show();
													
													if (temp2.equals(word)) {
														fs1 = context.openFileOutput(word+".txt", Context.MODE_PRIVATE);
													}
													else {
														fs1 = context.openFileOutput(temp2+".txt", Context.MODE_PRIVATE);
														test = false;
													}
													OutputStreamWriter myOutWriter1 = new OutputStreamWriter(fs1);
													for (final String recopy : state1) {
														myOutWriter1.append(recopy);
														myOutWriter1.append("\n");
													}
													myOutWriter1.close();
													fs1.close();
													
													if (Arrays.asList(state).contains(temp1)) {
														Arrays.asList(state).set(Arrays.asList(state).indexOf(temp1), temp2);
													}
													
													try {
														FileOutputStream fs2 = context.openFileOutput("database.txt", Context.MODE_PRIVATE);
														OutputStreamWriter myOutWriter2 = new OutputStreamWriter(fs2);
														for (final String recopy1 : state) {
															myOutWriter2.append(recopy1);
															myOutWriter2.append("\n");
														}
														myOutWriter2.close();
														fs2.close();
													} catch (Exception e) {}
													
													if (test == false) {
														File file = new File("/data/data/com.example.retentionscheduler/files/"+temp1+".txt");
														boolean deleted = file.delete();
													}
													
													dialog1.dismiss();
													dialog.dismiss();
													finish();
													startActivity(getIntent());
												} catch (Exception e) {}
											}
										});
										
										
										
									} catch (Exception e) {}
									dialog1.show();
								}
							});
							Button cancelBut = (Button) dialog.findViewById(R.id.cancell);
							cancelBut.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									dialog.dismiss();
								}
							});
							
						} catch (FileNotFoundException e) {}
						dialog.show();
					}
  			     	 
  			      });
  			}
  			      
			/*
			for (final String word : state) {
				
				final ClickableSpan clickableSpan = new ClickableSpan() {
					@Override
					public void onClick(View textView) {
						final Dialog dialog = new Dialog(context);
						dialog.setContentView(R.layout.edit_popup);
						dialog.setTitle("Event Information:");
						
						try {
							temp1 = word;
							temp2 = word;
							Toast.makeText(getApplicationContext(), word, Toast.LENGTH_LONG).show();
							
							System.out.println("Current file: "+word);
							String data1 = dao.readFile(word);
							TextView txt = (TextView) dialog.findViewById(R.id.info123);
							final String[] state1 = data1.split("\n");
							StringBuilder strB = new StringBuilder();
							for (int i=1; i<state1.length; i++) {
								strB.append(state1[i]);
								strB.append("\n");
							}
							txt.setText(strB.toString());
							
							Button editBut = (Button) dialog.findViewById(R.id.editt);
							editBut.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									final Dialog dialog1 = new Dialog(context);
									dialog1.setContentView(R.layout.edit_popup1);
									dialog1.setTitle("Edit Event Information");
									try {
										
										final EditText tname = (EditText) dialog1.findViewById(R.id.event123);
										final EditText tdesc = (EditText) dialog1.findViewById(R.id.desc123);
										tname.setText(state1[1]);
										tdesc.setText(state1[2]);
										
										final TimePicker timePicker = (TimePicker) dialog1.findViewById(R.id.tp1);
										String[] tiime = state1[state1.length-1].split(":");
										timePicker.setCurrentHour(Integer.parseInt(tiime[0]));
										final int hour = timePicker.getCurrentHour();
										timePicker.setCurrentMinute(Integer.parseInt(tiime[1].substring(0,2)));
										final int min = timePicker.getCurrentMinute();
										
										timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
											int hour = timePicker.getCurrentHour();
											 int min = timePicker.getCurrentMinute();
											 @Override
									            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
									                if(hour!= timePicker.getCurrentHour() || min !=timePicker.getCurrentMinute()){
									               	 hour = timePicker.getCurrentHour();
									               	 min = timePicker.getCurrentMinute();
									               	 if (min > 9) {
									               		 if (hour >= 12) {
									               			 time=(hour-12)+":"+min+" PM";
									               		 }
									               		 else {
									               			 time=hour+":"+min+" AM";
									               		 }
									               	 }
									               	 else {
									               		 if (hour >= 12) {
									               			 time=(hour-12)+":0"+min+" PM";
									               		 }
									               		 else {
									               			 time=hour+":0"+min+" AM";
									               		 }
									               	 }
									                }
									            }
									        });
										
										Button saveBut = (Button) dialog1.findViewById(R.id.save);
										saveBut.setOnClickListener(new OnClickListener() {
											@Override
											public void onClick(View v) {
												try {
													FileOutputStream fs1;
													if (tname.getText() != null) {
														state1[1] = ""+tname.getText().toString();
														temp2 = state1[1];	
													}
													
													if (tdesc.getText() != null) {
														state1[2] = ""+tdesc.getText().toString();
													}
													
													 //time=hour+":"+min;
													 if (!time.equals("x")) {
														 state1[state1.length-1] = time;
													 }
													
													Toast.makeText(getApplicationContext(), "Edit Successful!", Toast.LENGTH_LONG).show();
													
													if (temp2.equals(word)) {
														fs1 = context.openFileOutput(word+".txt", Context.MODE_PRIVATE);
													}
													else {
														fs1 = context.openFileOutput(temp2+".txt", Context.MODE_PRIVATE);
														test = false;
													}
													OutputStreamWriter myOutWriter1 = new OutputStreamWriter(fs1);
													for (final String recopy : state1) {
														myOutWriter1.append(recopy);
														myOutWriter1.append("\n");
													}
													myOutWriter1.close();
													fs1.close();
													
													if (Arrays.asList(state).contains(temp1)) {
														Arrays.asList(state).set(Arrays.asList(state).indexOf(temp1), temp2);
													}
													
													try {
														FileOutputStream fs2 = context.openFileOutput("database.txt", Context.MODE_PRIVATE);
														OutputStreamWriter myOutWriter2 = new OutputStreamWriter(fs2);
														for (final String recopy1 : state) {
															myOutWriter2.append(recopy1);
															myOutWriter2.append("\n");
														}
														myOutWriter2.close();
														fs2.close();
													} catch (Exception e) {}
													
													if (test == false) {
														File file = new File("/data/data/com.example.retentionscheduler/files/"+temp1+".txt");
														boolean deleted = file.delete();
													}
													
													dialog1.dismiss();
													dialog.dismiss();
													finish();
													startActivity(getIntent());
												} catch (Exception e) {}
											}
										});
										
										
										
									} catch (Exception e) {}
									dialog1.show();
								}
							});
							Button cancelBut = (Button) dialog.findViewById(R.id.cancell);
							cancelBut.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									dialog.dismiss();
								}
							});
							
						} catch (FileNotFoundException e) {}
						dialog.show();
					}
				};
				str.setSpan(clickableSpan, data.indexOf(word), data.indexOf(word) + word.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				
			}*/
			
			//System.out.println("HUH? "+temp1);
			
			
			
			//view.setText(str);
			//view.setMovementMethod(LinkMovementMethod.getInstance());
			
			
		} catch (Exception e){}
	}
		
	public void onClick(View v){
		
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is
		// present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}

