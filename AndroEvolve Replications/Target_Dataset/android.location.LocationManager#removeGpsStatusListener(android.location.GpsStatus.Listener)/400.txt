////////////////////////////////////////////////////////////////////////////////
//
//  Location - An Android location app.
//
//  Copyright (C) 2015	Bill Farmer
//
//  This program is free software; you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation; either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU General Public License for more details.
//
//  You should have received a copy of the GNU General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
//  Bill Farmer	 william j farmer [at] yahoo [dot] co [dot] uk.
//
///////////////////////////////////////////////////////////////////////////////

package org.billthefarmer.location;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapAdapter;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.CopyrightOverlay;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.mylocation.SimpleLocationOverlay;

import uk.me.jstott.jcoord.LatLng;
import uk.me.jstott.jcoord.OSRef;

public class Main extends Activity
    implements LocationListener, GpsStatus.Listener
{
    private static final String TAG = "Location";

    private static final int STOP_MODE = 0;
    private static final int START_MODE = 1;
    private static final int TRACK_MODE = 2;

    private static final int SHORT_DELAY = 5000;
    private static final int LONG_DELAY = 10000;

    private StatusView statusView;
    private MapView map;

    private LocationManager locationManager;
    private DateFormat dateFormat;

    private SimpleLocationOverlay simpleLocation;
    private TextOverlay leftOverlay;
    private TextOverlay rightOverlay;

    private int mode = START_MODE;

    private boolean located;
    private boolean scrolled;
    private boolean zoomed;
    
    // Called when the activity is first created.
    @Override
    @SuppressWarnings("deprecation")
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Context context = getApplicationContext();
        Configuration.getInstance()
            .load(context, PreferenceManager
                  .getDefaultSharedPreferences(context));
        setContentView(R.layout.main);

	// Get the views
	statusView = (StatusView)findViewById(R.id.status);

	// Set the user agent
	// org.osmdroid.tileprovider.constants.OpenStreetMapTileProviderConstants
	//     .setUserAgentValue(BuildConfig.APPLICATION_ID);

	// Get the map
        map = (MapView)findViewById(R.id.map);
	if (map != null)
	{
	    // Set up the map
	    map.setTileSource(TileSourceFactory.MAPNIK);
	    map.setBuiltInZoomControls(true);
	    map.setMultiTouchControls(true);

	    List<Overlay> overlayList = map.getOverlays();

	    // Add the overlays
	    CopyrightOverlay copyright =
		new CopyrightOverlay(this);
	    overlayList.add(copyright);
	    copyright.setAlignBottom(true);
	    copyright.setAlignRight(false);

	    ScaleBarOverlay scale = new ScaleBarOverlay(map);
	    scale.setAlignBottom(true);
	    scale.setAlignRight(true);
	    overlayList.add(scale);

	    Bitmap bitmap = BitmapFactory
		.decodeResource(getResources(), R.drawable.center);
	    simpleLocation = new SimpleLocationOverlay(bitmap);
	    overlayList.add(simpleLocation);

            leftOverlay = new TextOverlay(this);
            overlayList.add(leftOverlay);
	    leftOverlay.setAlignBottom(false);
	    leftOverlay.setAlignRight(false);

            rightOverlay = new TextOverlay(this);
            overlayList.add(rightOverlay);
	    rightOverlay.setAlignBottom(false);
	    rightOverlay.setAlignRight(true);

            map.setMapListener(new MapAdapter()
                {
                    public boolean onScroll(ScrollEvent event)
                    {
                        if (located)
                        {
                            IGeoPoint point = map.getMapCenter();
                            if (zoomed)
                                scrolled = true;

                            double lat = point.getLatitude();
                            double lng = point.getLongitude();

                            Location location = new Location("MapView");
                            location.setLatitude(lat);
                            location.setLongitude(lng);
                            showLocation(location);
                        }

                        return true;
                    }
                });
	}

	// Acquire a reference to the system Location Manager
	locationManager =
	    (LocationManager)getSystemService(LOCATION_SERVICE);

	dateFormat = DateFormat.getDateTimeInstance();
    }

    // On resume

    @Override
    protected void onResume()
    {
	super.onResume();

	Location location =
	    locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

	if (location != null)
	    showLocation(location);

	switch (mode)
	{
	case START_MODE:
	case TRACK_MODE:
	    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
						   SHORT_DELAY, 0, this);
	    locationManager.addGpsStatusListener(this);
	    break;
	}
    }

    // On pause

    @Override
    protected void onPause()
    {
	super.onPause();

	locationManager.removeUpdates(this);
	locationManager.removeGpsStatusListener(this);
    }

    // On create options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
	// Inflate the menu; this adds items to the action bar if it
	// is present.

	MenuInflater inflater = getMenuInflater();
	inflater.inflate(R.menu.main, menu);

	return true;
    }

    // onPrepareOptionsMenu
    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        menu.findItem(R.id.action_off).setEnabled(mode != STOP_MODE);
        menu.findItem(R.id.action_locate).setEnabled(mode != START_MODE);
        menu.findItem(R.id.action_track).setEnabled(mode != TRACK_MODE);
        return true;
    }

    // On options item selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
	// Get id

	int id = item.getItemId();
	switch (id)
	{
        case R.id.action_off:
            mode = STOP_MODE;
            locationManager.removeUpdates(this);
            locationManager.removeGpsStatusListener(this);
            break;

	case R.id.action_locate:
            mode = START_MODE;
            locationManager.removeUpdates(this);
            locationManager.removeGpsStatusListener(this);
            located = false;

            Location location = locationManager
                .getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (location != null)
                showLocation(location);

            locationManager
                .requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                        SHORT_DELAY, 0, this);
            locationManager.addGpsStatusListener(this);
            break;

	case R.id.action_track:
            mode = TRACK_MODE;
            locationManager.removeUpdates(this);
            locationManager.removeGpsStatusListener(this);
            located = false;

            location = locationManager
                .getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (location != null)
                showLocation(location);

            locationManager
                .requestLocationUpdates(LocationManager.GPS_PROVIDER,
                                        SHORT_DELAY, 0, this);
            locationManager.addGpsStatusListener(this);
            break;

	default:
	    return false;
	}

        invalidateOptionsMenu();
	return true;
    }

    // Show location

    private void showLocation(Location location)
    {
	float  acc = location.getAccuracy();
	double lat = location.getLatitude();
	double lng = location.getLongitude();
	double alt = location.getAltitude();

	String latString = Location.convert(lat, Location.FORMAT_SECONDS);
	String lngString = Location.convert(lng, Location.FORMAT_SECONDS);

        List<String> rightList = new ArrayList<String>();
        rightList.add(String.format(Locale.getDefault(),
                                   "%s, %s", latString, lngString));
        rightList.add(String.format(Locale.getDefault(),
                                       "Altitude: %1.0fm", alt));
        rightList.add(String.format(Locale.getDefault(),
                                       "Accuracy: %1.0fm", acc));
        rightOverlay.setText(rightList);

	long   time = location.getTime();

        String date;
        if (scrolled)
            date = dateFormat.format(new Date());

        else
            date = dateFormat.format(new Date(time));

        List<String> leftList = new ArrayList<String>();
        leftList.add(date);

	LatLng coord = new LatLng(lat, lng);
	coord.toOSGB36();
	OSRef OSCoord = coord.toOSRef();

	if (OSCoord.isValid())
	{
	    double east = OSCoord.getEasting();
	    double north = OSCoord.getNorthing();
	    String OSString = OSCoord.toSixFigureString();

            leftList.add(OSString);
            leftList.add(String.format(Locale.getDefault(),
                                       "%1.0f, %1.0f", east, north));
	}

        leftOverlay.setText(leftList);
        map.invalidate();
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onLocationChanged(Location location)
    {
	IMapController mapController = map.getController();

	// Zoom map once
	if (!zoomed)
	{
	    mapController.setZoom(14);
	    zoomed = true;
	}

	// Get point
	GeoPoint point = new GeoPoint(location);

	// Centre map once
	if (!located)
	{
	    mapController.setCenter(point);
	    located = true;
	}

	// Unless tracking
	else if (mode == TRACK_MODE)
	    mapController.setCenter(point);

	// Set location
	simpleLocation.setLocation(point);

        if (scrolled)
            map.postDelayed(new Runnable()
                {
                    // run
                    @Override
                    public void run()
                    {
                        scrolled = false;
                    }
                }, LONG_DELAY);

        else
            showLocation(location);
  }

    @Override
    public void onGpsStatusChanged(int event)
    {
	GpsStatus status = null;

	switch (event)
	{
	case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
	    status = locationManager.getGpsStatus(null);

	    if (statusView != null)
		statusView.updateStatus(status);

	    break;
	}
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {}
}
