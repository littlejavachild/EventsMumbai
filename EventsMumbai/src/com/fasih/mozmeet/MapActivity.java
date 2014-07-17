package com.fasih.mozmeet;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fasih.mozmeet.util.ActionBarUtil;
import com.fasih.mozmeet.util.CloudFunctions;
import com.fasih.mozmeet.util.EventUtil;
import com.fasih.mozmeet.util.Fields;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

public class MapActivity extends Activity {
	
	// Google Map
    private GoogleMap googleMap;
    private MapFragment mapFragment;
    
    // UI elements
    private TextView eventTitle = null;
    private TextView eventDate = null;
    private TextView eventTime = null;
    private TextView eventLocation = null;
    private TextView eventDescription = null;
    private TextView eventDescriptionLabel = null;
    private TextView eventDateLabel = null;
    private Button attend = null;
 	
 	// Lat and Long
 	private LatLng latLng = null;
	private MarkerOptions marker = null;
 	
	// Roboto FOnt
	private Typeface typeface = MozMeetApplication.newInstance().getTypeface();
	
	// Parse Event retrieved from the intent
	private ParseObject event = null;
	private Date date = null;
	private String title = null;
	private String time =  null;
	private String description = null;
	private String address = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_map);
		
		// Set ActionBar Color
		ActionBarUtil.setActionBarColor(getActionBar(), getApplicationContext());
		ActionBarUtil.setActionBarTypeface(this);
		
		mapFragment = ((MapFragment)getFragmentManager().findFragmentById(R.id.map));
		
		eventTitle = (TextView) findViewById(R.id.eventTitle);
		eventDate = (TextView) findViewById(R.id.eventDate);
		eventTime = (TextView) findViewById(R.id.eventTime);
		eventLocation = (TextView) findViewById(R.id.eventLocation);
		eventDescription = (TextView) findViewById(R.id.eventDescription);
		eventDescriptionLabel = (TextView) findViewById(R.id.descriptionLabel);
		eventDateLabel = (TextView) findViewById(R.id.dateLabel);
		attend = (Button) findViewById(R.id.attend);
		
		eventTitle.setTypeface(typeface,Typeface.BOLD);
		eventDate.setTypeface(typeface,Typeface.BOLD);
		eventDateLabel.setTypeface(typeface,Typeface.BOLD);
		eventTime.setTypeface(typeface,Typeface.BOLD);
		eventLocation.setTypeface(typeface,Typeface.BOLD);
		eventDescription.setTypeface(typeface,Typeface.BOLD);
		eventDescriptionLabel.setTypeface(typeface,Typeface.BOLD);
		attend.setTypeface(typeface,Typeface.BOLD);
		
		Intent intent = getIntent();
		int position = intent.getIntExtra(Fields.PARSE_OBJECT, -1);
		
		if(position != -1){
			event = EventUtil.getMozillaEvents().get(position);
			title = event.getString(Fields.EVENT_TITLE);
			date = event.getDate(Fields.EVENT_DATE);
			time = event.getString(Fields.EVENT_TIME);
			description = event.getString(Fields.EVENT_DESCTIPTION);
			address = event.getString(Fields.EVENT_ADDRESS);
			
			ParseGeoPoint geoPoint = event.getParseGeoPoint(Fields.EVENT_LOCATION);
			double lat = geoPoint.getLatitude();
			double lng = geoPoint.getLongitude();
			latLng = new LatLng(lat, lng);
			marker = new MarkerOptions().position(latLng).title(title);
			
			displayGoogleMap();
		}
		addButtonListener();
		setButtonColor();
	}
	//------------------------------------------------------------------------------
	@Override
	public void onResume(){
		super.onResume();
		DateFormat fmt = DateFormat.getDateInstance(DateFormat.MEDIUM);
		eventTitle.setText(title);
		eventDate.setText(fmt.format(date));
		eventTime.setText(time);
		eventLocation.setText(address);
		eventDescription.setText(description);
		Linkify.addLinks(eventDescription, Linkify.WEB_URLS);
	}
	//------------------------------------------------------------------------------
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.social_share, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	//------------------------------------------------------------------------------
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		
		case R.id.setAReminder:
			setAReminder();
			return true;
		case R.id.socialShare:
			shareEvent();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	//------------------------------------------------------------------------------
	private void displayGoogleMap(){
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
	//------------------------------------------------------------------------------
	private void addButtonListener(){
		attend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = getIntent();
				int position = intent.getIntExtra(Fields.PARSE_OBJECT, -1);
				if(position != -1){
					ParseObject event = EventUtil.getMozillaEvents().get(position);
					boolean contains = EventUtil.containsEvent(event);
					if(!contains){ // if the event has not been added
						EventUtil.addEvent(event); // add it
						makeCloudCall(); // update the cloud database
						setButtonColor();
					}else{
						EventUtil.removeEvent(event); // else, remove it
						makeCloudCall();
						setButtonColor();
					}
				}
			}
		});
	}
	//------------------------------------------------------------------------------
	private void setButtonColor(){
		Intent intent = getIntent();
		int position = intent.getIntExtra(Fields.PARSE_OBJECT, -1);
		if(position != -1){
			ParseObject event = EventUtil.getMozillaEvents().get(position);
			boolean contains = EventUtil.containsEvent(event);
			if(!contains){ // if it does not contain the event, let user be able to add the event
				attend.setBackgroundColor(getResources().getColor(R.color.attend_green));
				attend.setText(getResources().getString(R.string.attend));
			}else{
				attend.setBackgroundColor(getResources().getColor(R.color.not_attend_red));
				attend.setText(getResources().getString(R.string.skip));
			}
		}
	}
	//------------------------------------------------------------------------------
	private void shareEvent(){
		DateFormat fmt = DateFormat.getDateInstance(DateFormat.SHORT);
		String message = title + "\n@" + address + "\non " + fmt.format(date) + "\n\n" + description;
		
		Intent sharingIntent = new Intent(Intent.ACTION_SEND);
		sharingIntent.setType("text/plain");
		sharingIntent.putExtra(Intent.EXTRA_TEXT, message);
		try{
			startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share)));
		}catch(ActivityNotFoundException e){
			Toast.makeText(getApplicationContext(), 
					"Unable To Share Event. No Suitable Application Found", 
					Toast.LENGTH_SHORT).show();
		}
	}
	//------------------------------------------------------------------------------
	private void setAReminder() {
		Intent calendar = new Intent(Intent.ACTION_EDIT);
		calendar.setType("vnd.android.cursor.item/event");
		calendar.putExtra("beginTime",date.getTime());
		calendar.putExtra("allDay", false);
		calendar.putExtra("eventLocation", address);
		calendar.putExtra("description", description);
		calendar.putExtra("rrule", "FREQ=YEARLY;COUNT=1");
		calendar.putExtra("title", title);
		try{
			startActivity(Intent.createChooser(calendar, getResources().getString(R.string.reminder)));
		}catch(ActivityNotFoundException e){
			Toast.makeText(getApplicationContext(), 
					"Unable To Share Event. No Suitable Application Found", 
					Toast.LENGTH_SHORT).show();
		}
	}
	//------------------------------------------------------------------------------
	private void makeCloudCall(){
		HashMap<String,Object> params = new HashMap<String, Object>();
//		String attendString = getResources().getString(R.id.attend);
		String skipString = getResources().getString(R.string.skip);
		String buttonText = attend.getText().toString();
		int count = 1;
		
		if(buttonText.equals(skipString)){
			count = -1;
		}
		
		
		params.put(Fields.OBJECT_ID, event.getObjectId());
		params.put("count", Integer.toString(count));
		
		ParseCloud.callFunctionInBackground(CloudFunctions.UPDATE_ATTENDEES, params, new FunctionCallback<Integer>(){
			@Override
			public void done(Integer arg0, ParseException arg1) {
				// NOTHING !!
			}
		});
	}
	//------------------------------------------------------------------------------
}
