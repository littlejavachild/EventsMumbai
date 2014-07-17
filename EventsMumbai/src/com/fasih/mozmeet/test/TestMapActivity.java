package com.fasih.mozmeet.test;

import java.text.DateFormat;
import java.util.Date;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fasih.mozmeet.MozMeetApplication;
import com.fasih.mozmeet.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class TestMapActivity extends Activity{
	// Google Map
    private GoogleMap googleMap;
    private MapFragment mapFragment;
    
    // filler data
	private final String TITLE = "Mozilla WebMaker Event";
	private final String LOCATION = "IIT Bombay";
	private final String DESCRIPTION = "Welcome to Wemaker — a Mozilla project dedicated to helping you create something amazing on the web. Our tools, events and teaching guides allow webmakers to not only create the content that makes the web great, but — perhaps more importantly — understand how the web works. With this knowledge, we can make a web without limits. That's the philosophy behind webmaker.org. We've built everything so you can see how it works, take it apart and remix it. Enjoy!";
	private DateFormat dateInstance = DateFormat.getDateInstance(DateFormat.MEDIUM);
	private DateFormat timeInstance = DateFormat.getTimeInstance(DateFormat.MEDIUM);
	private Typeface typeface = MozMeetApplication.newInstance().getTypeface();
	private Date now = new Date();
    
	// latitude and longitude for IIT Mumbai
	private double latitude = 19.1336;
	private double longitude = 72.9154;
	private LatLng latLng = new LatLng(latitude, longitude);
	private MarkerOptions marker = new MarkerOptions().position(latLng).title(TITLE);
	
	// UI elements
	TextView eventTitle = null;
	TextView eventDate = null;
	TextView eventTime = null;
	TextView eventLocation = null;
	TextView eventDescription = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_map);
		mapFragment = ((MapFragment)getFragmentManager().findFragmentById(R.id.map));
		
		eventTitle = (TextView) findViewById(R.id.eventTitle);
		eventDate = (TextView) findViewById(R.id.eventDate);
		eventTime = (TextView) findViewById(R.id.eventTime);
		eventLocation = (TextView) findViewById(R.id.eventLocation);
		eventDescription = (TextView) findViewById(R.id.eventDescription);
		
		eventTitle.setTypeface(typeface,Typeface.BOLD);
		eventDate.setTypeface(typeface,Typeface.BOLD);
		eventTime.setTypeface(typeface,Typeface.BOLD);
		eventLocation.setTypeface(typeface,Typeface.BOLD);
		eventDescription.setTypeface(typeface,Typeface.BOLD);
		
		eventTitle.setText(TITLE);
		eventLocation.setText(LOCATION);
		eventDescription.setText(DESCRIPTION);
		eventDate.setText(dateInstance.format(now));
		eventTime.setText(timeInstance.format(now));
		
	}
	
	@Override
	public void onResume(){
		super.onResume();
		googleMap = mapFragment.getMap();
		if(googleMap != null){
			
			final CameraUpdate location = CameraUpdateFactory.newLatLng(latLng);
			final CameraUpdate zoom = CameraUpdateFactory.zoomBy(8.0f);
			
			googleMap.setOnMapLoadedCallback(new OnMapLoadedCallback(){
				@Override
				public void onMapLoaded() {
					googleMap.animateCamera(location, 1000, new GoogleMap.CancelableCallback() {
						@Override
						public void onFinish() {
							googleMap.animateCamera(zoom,1000, new GoogleMap.CancelableCallback() {
								@Override
								public void onFinish() {}
								@Override
								public void onCancel() {}
							});
						}
						@Override
						public void onCancel() {
							// NOTHING
						}
					});
					googleMap.setTrafficEnabled(true);
					googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
					googleMap.setMyLocationEnabled(false);
					googleMap.getUiSettings().setZoomControlsEnabled(true);
					googleMap.getUiSettings().setZoomGesturesEnabled(true);
					googleMap.getUiSettings().setCompassEnabled(true);
					googleMap.getUiSettings().setRotateGesturesEnabled(true);
					googleMap.addMarker(marker);
				}
			});
			
		}else{
			Toast.makeText(getApplicationContext(), "Maps Not Created", Toast.LENGTH_SHORT).show();
		}
		
	}
}
