package com.dark.droid.features;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.os.Build;

public class FeatureList extends ActionBarActivity {
	public String[] getStrings()
	{
		ArrayList<String> strings = new ArrayList<String>();
		
		strings.add(PackageManager.FEATURE_APP_WIDGETS);
		strings.add(PackageManager.FEATURE_AUDIO_LOW_LATENCY);
		strings.add(PackageManager.FEATURE_BLUETOOTH);
		strings.add(PackageManager.FEATURE_BLUETOOTH_LE);
		strings.add(PackageManager.FEATURE_CAMERA);
		strings.add(PackageManager.FEATURE_CAMERA_ANY);
		strings.add(PackageManager.FEATURE_CAMERA_AUTOFOCUS);
		strings.add(PackageManager.FEATURE_CAMERA_FLASH);
		strings.add(PackageManager.FEATURE_CAMERA_FLASH);
		strings.add(PackageManager.FEATURE_CAMERA_FRONT);
		strings.add(PackageManager.FEATURE_CONSUMER_IR);
		strings.add(PackageManager.FEATURE_DEVICE_ADMIN);
		strings.add(PackageManager.FEATURE_FAKETOUCH);
		strings.add(PackageManager.FEATURE_FAKETOUCH_MULTITOUCH_DISTINCT);
		strings.add(PackageManager.FEATURE_HOME_SCREEN);
		strings.add(PackageManager.FEATURE_INPUT_METHODS);
		strings.add(PackageManager.FEATURE_LIVE_WALLPAPER);
		strings.add(PackageManager.FEATURE_LOCATION);
		strings.add(PackageManager.FEATURE_LOCATION_NETWORK);
		strings.add(PackageManager.FEATURE_MICROPHONE);
		strings.add(PackageManager.FEATURE_NFC);
		
		strings.add(PackageManager.FEATURE_NFC_HOST_CARD_EMULATION);
		strings.add(PackageManager.FEATURE_SCREEN_LANDSCAPE);
		strings.add(PackageManager.FEATURE_SCREEN_PORTRAIT);
		strings.add(PackageManager.FEATURE_SENSOR_ACCELEROMETER);
		strings.add(PackageManager.FEATURE_SENSOR_BAROMETER);
		strings.add(PackageManager.FEATURE_SENSOR_COMPASS);
		
		strings.add(PackageManager.FEATURE_SENSOR_GYROSCOPE);
		strings.add(PackageManager.FEATURE_SENSOR_LIGHT);
		strings.add(PackageManager.FEATURE_SENSOR_PROXIMITY);
		strings.add(PackageManager.FEATURE_SENSOR_STEP_COUNTER);
		strings.add(PackageManager.FEATURE_SENSOR_STEP_DETECTOR);
		strings.add(PackageManager.FEATURE_SIP);
		
		strings.add(PackageManager.FEATURE_SIP_VOIP);
		strings.add(PackageManager.FEATURE_TELEPHONY);
		strings.add(PackageManager.FEATURE_TELEPHONY_CDMA);
		strings.add(PackageManager.FEATURE_TELEPHONY_GSM);
		strings.add(PackageManager.FEATURE_TELEVISION);
		strings.add(PackageManager.FEATURE_TOUCHSCREEN);
		
		strings.add(PackageManager.FEATURE_TOUCHSCREEN_MULTITOUCH);
		strings.add(PackageManager.FEATURE_TOUCHSCREEN_MULTITOUCH_DISTINCT);
		strings.add(PackageManager.FEATURE_TOUCHSCREEN_MULTITOUCH_JAZZHAND);
		strings.add(PackageManager.FEATURE_USB_ACCESSORY);
		strings.add(PackageManager.FEATURE_USB_HOST);
		strings.add(PackageManager.FEATURE_WIFI);
		strings.add(PackageManager.FEATURE_WIFI_DIRECT);
		return strings.toArray(new String[strings.size()]);
	}
	public Feature[] getFeatures() {
		ArrayList<Feature> features = new ArrayList<Feature>();
		for (String s : getStrings()) {
			features.add(new Feature(s, this.getPackageManager()));
		}
		return features.toArray(new Feature[features.size()]);
	}
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feature_list);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		TextView t = (TextView) findViewById(R.id.editText);
		t.setScroller(new Scroller(this));
		t.setVerticalScrollBarEnabled(true);
		t.setMovementMethod(new ScrollingMovementMethod());
		Feature[] features = getFeatures();
		String text = "<h1><b><u>All</u></b><h1>";
		for(Feature feature : features)
		{
			text += "<p>" + feature.msg + "</p>";
		}
		t.setText(Html.fromHtml(text));
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_activity_actions, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		TextView t = (TextView) findViewById(R.id.editText);
		Feature[] features = getFeatures();
		int id = item.getItemId();
		if (id == R.id.action_all) {
			String text = "<h1><b><u>All</u></b><h1>";
			for(Feature feature : features)
			{
				text += "<p>" + feature.msg + "</p>";
			}
			t.setText(Html.fromHtml(text));
			return true;
		}
		if (id == R.id.action_on_off) {
			String text = "<h1><b><u>On</u></b><h1>";
			for(Feature feature : features)
			{
				if(feature.has)text += "<p>" + feature.msg + "</p>";
			}
			text += "<h1><b><u>Off</u></b><h1>";
			for(Feature feature : features)
			{
				if(!feature.has)text += "<p>" + feature.msg + "</p>";
			}
			t.setText(Html.fromHtml(text));
			return true;
		}
		if (id == R.id.action_software_hardware) {
			String text = "<h1><b><u>Software</u></b><h1>";
			for(Feature feature : features)
			{
				if(feature.software)text += "<p>" + feature.msg + "</p>";
			}
			text += "<h1><b><u>Hardware</u></b><h1>";
			for(Feature feature : features)
			{
				if(!feature.software)text += "<p>" + feature.msg + "</p>";
			}
			t.setText(Html.fromHtml(text));
			return true;
		}
		if (id == R.id.action_camera) {
			String text = "<h1><b><u>Camera</u></b><h1>";
			for(Feature feature : features)
			{
				if(feature.camera)text += "<p>" + feature.msg + "</p>";
			}
			t.setText(Html.fromHtml(text));
			return true;
		}
		if (id == R.id.action_sensor) {
			String text = "<h1><b><u>Sensor</u></b><h1>";
			for(Feature feature : features)
			{
				if(feature.sensor)text += "<p>" + feature.msg + "</p>";
			}
			t.setText(Html.fromHtml(text));
			return true;
		}
		if (id == R.id.action_screen) {
			String text = "<h1><b><u>Screen</u></b><h1>";
			for(Feature feature : features)
			{
				if(feature.screen)text += "<p>" + feature.msg + "</p>";
			}
			t.setText(Html.fromHtml(text));
			return true;
		}
		if (id == R.id.action_connectivity) {
			String text = "<h1><b><u>Connectivity</u></b><h1>";
			for(Feature feature : features)
			{
				if(feature.connectivity)text += "<p>" + feature.msg + "</p>";
			}
			t.setText(Html.fromHtml(text));
			return true;
		}
		if (id == R.id.action_misc) {
			String text = "<h1><b><u>Misc</u></b><h1>";
			for(Feature feature : features)
			{
				if(!feature.camera && !feature.screen && !feature.sensor && !feature.connectivity)text += "<p>" + feature.msg + "</p>";
			}
			t.setText(Html.fromHtml(text));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_feature_list,
					container, false);
			return rootView;
		}
	}

}
