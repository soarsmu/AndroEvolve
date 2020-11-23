package com.hu.quantumshare;

import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.PushService;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

public class CreateTaskFragment extends Fragment implements OnClickListener {
	Context context;
	SharedPreferences prefs;
	Spinner needs;
	TimePicker time;
	EditText place;
	EditText instructions;
	Button submit;

	public CreateTaskFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		context = getActivity().getApplicationContext();
		View rootView = inflater.inflate(R.layout.fragment_createtask,
				container, false);
		needs = (Spinner) rootView.findViewById(R.id.needs);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				context, R.array.needs,R.layout.spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(R.layout.spinner_item);
		// Apply the adapter to the spinner
		needs.setAdapter(adapter);

		time = (TimePicker) rootView.findViewById(R.id.time);
		//time.setIs24HourView(false);
		place = (EditText) rootView.findViewById(R.id.place);
		instructions = (EditText) rootView.findViewById(R.id.instructions);
		submit = (Button) rootView.findViewById(R.id.submit);
		submit.setOnClickListener(this);
		return rootView;
	}

	@Override
	public void onClick(View v) {
		ParseInstallation.getCurrentInstallation().saveInBackground();
		ParseQuery query = ParseInstallation.getQuery();
		PushService.setDefaultPushCallback(context, AcceptActivity.class);
		// Notification for Android users
		query.whereEqualTo("deviceType", "android");
		ParsePush androidPush = new ParsePush();
		// JSONObject data = new JSONObject();
		ParseUser currentUser = ParseUser.getCurrentUser();
		if (currentUser == null) {
			Log.e("Parse error", "parse could not find the current user");
		} else {
			// store the task in the database
			ParseObject task = new ParseObject("Task");
			task.put("owner", currentUser.getUsername());
			task.put("needs",
					needs.getItemAtPosition(needs.getSelectedItemPosition())
							.toString());
			if(time.getCurrentHour() >9 )
			{
				task.put("time", time.getCurrentHour() + ":"+time.getCurrentMinute());
			}
			else
			{
				task.put("time", "0"+time.getCurrentHour() + ":"+time.getCurrentMinute());
			}
			/*
			if(time.getCurrentHour() == 0)
			{
				task.put("hour", ""+12 );
				task.put("minute", time.getCurrentMinute());
				task.put("time","AM");
			}
			else if(time.getCurrentHour() >13)
			{
				Integer num = (time.getCurrentHour()-12);
				if(num > 9)
				{
					task.put("hour", ""+ num);
				}
				else
				{
					task.put("hour", "0"+num);
				}
				task.put("minute",time.getCurrentMinute());
				task.put("time","PM");
			}
			else if(time.getCurrentHour() > 9)
			{
				task.put("hour", ""+time.getCurrentHour());
				task.put("minute", time.getCurrentMinute());
				task.put("time","AM");
				
			}
			else
			{
				task.put("hour", "0"+time.getCurrentHour() );
				task.put("minute", time.getCurrentMinute());
				task.put("time","AM");
				
			}*/
			task.put("place",place.getText().toString());
			task.put("taker", "no one");
			task.put("instructions", instructions.getText().toString());
			task.saveInBackground();
			// stops the push notification from being sent to the sender
			query.whereNotEqualTo("username", currentUser.getUsername());
			// sending push notification
			androidPush.setMessage(currentUser.getUsername() + " needs "
					+ task.get("needs")+" at " + time.getCurrentHour() + ":"
					+ time.getCurrentMinute());
			androidPush.setQuery(query);
			androidPush.sendInBackground();
			Log.d("Push notification", "task sent");
		}
		Toast.makeText(context, "task sent", Toast.LENGTH_SHORT).show();
	}
}
