package com.fasih.mozmeet;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.crashlytics.android.Crashlytics;
import com.fasih.mozmeet.adapter.CustomPagerAdapter;
import com.fasih.mozmeet.adapter.MozillaListAdapter;
import com.fasih.mozmeet.adapter.NavigationListAdapter;
import com.fasih.mozmeet.fragments.MozillaEventsFragment;
import com.fasih.mozmeet.fragments.MyEventsFragment;
import com.fasih.mozmeet.util.ActionBarUtil;
import com.fasih.mozmeet.util.DialogUtil;
import com.fasih.mozmeet.util.EventClickListener;
import com.fasih.mozmeet.util.EventUtil;
import com.fasih.mozmeet.util.Fields;
import com.fasih.mozmeet.util.Page;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.roomorama.caldroid.CaldroidFragment;

public class HomeActivity extends FragmentActivity implements EventClickListener{
	
	private static String PAGE_NUM_KEY = "page_number";
	private int currentPageNumber= 0;
	private ListView leftDrawer = null;
	private ViewPager pager = null;
	private PagerSlidingTabStrip tabs = null;
	
	private MozillaListAdapter mozillaEventsAdapter = null;
	private MozillaListAdapter myEventsAdapter = null;
	
	private CustomPagerAdapter pagerAdapter = null;
	
	private MozillaEventsFragment eventsFragment = null;
	private MyEventsFragment myEventsFragment = null;
	
	private int lastScrolledPage = -1;
	//------------------------------------------------------------------------------
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Crashlytics.start(this);
		setContentView(R.layout.activity_home);
		
		// Set ActionBar Color
		ActionBarUtil.setActionBarColor(getActionBar(), getApplicationContext());
		ActionBarUtil.setActionBarTypeface(this);
		
		// Retrieve an reference to the UI elements
		leftDrawer = (ListView) findViewById(R.id.left_drawer);
		pager = (ViewPager) findViewById(R.id.pager);
		tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		leftDrawer.setAdapter(new NavigationListAdapter());
		
		if(savedInstanceState == null){
        	pager.setCurrentItem(currentPageNumber, true);
        }else{
        	currentPageNumber = savedInstanceState.getInt(PAGE_NUM_KEY);
        }
		
		pagerAdapter = new CustomPagerAdapter(getSupportFragmentManager());
    	// Set pager adapter and sliding strips
        pager.setAdapter(pagerAdapter);
        tabs.setViewPager(pager);
        tabs.setTypeface(MozMeetApplication.newInstance().getTypeface(), Typeface.BOLD);
        
        // Initialize the adapters, except the myEventsAdapter
		mozillaEventsAdapter = new MozillaListAdapter(EventUtil.getMozillaEvents());
        
		// Initialize the Fragment
        eventsFragment = MozillaEventsFragment.newInstance();
        myEventsFragment = MyEventsFragment.newInstance();
        
        // Set pager adapter and sliding strips
        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);
        pagerAdapter.setCalendarFragment(CaldroidFragment.newInstance("", month, year));
        pager.setAdapter(pagerAdapter);
        tabs.setViewPager(pager);
        
        loadMozillaEvents();
        setPageChangeListener();
        setLeftDrawerListListener();
	}
	//------------------------------------------------------------------------------
	@Override
	public void onResume(){
		super.onResume();
		MozMeetApplication.newInstance().setHomeActivityVisible(true);
	}
	//------------------------------------------------------------------------------
	@Override
	public void onStop(){
		super.onStop();
		MozMeetApplication.newInstance().setHomeActivityVisible(false);
	}
	//------------------------------------------------------------------------------
	/**
	 * Used to load the MozEvent from the server or the local data store
	 */
	private void loadMozillaEvents(){
		loadMozillaEventsFromServer();
	}
	//------------------------------------------------------------------------------
	/**
	 * Used to load Mozilla Events from server
	 */
	private void loadMozillaEventsFromServer(){
		ParseQuery<ParseObject> query = ParseQuery.getQuery(Fields.CLASS_NAME);
		DialogUtil.showLoadingDialog(getSupportFragmentManager());
		// First try fetching the data from the server
		query.findInBackground(new FindCallback<ParseObject>() {
		    public void done(List<ParseObject> eventList, ParseException e) {
		        if (e == null) {
		        	// add the new events to be displayed
		            EventUtil.setMozillaEvents(eventList);
		            // save them to the local data store
		            ParseObject.pinAllInBackground(eventList);
		            // set the adapter
		            eventsFragment.setAdapter(mozillaEventsAdapter);
		            DialogUtil.hideLoadingDialog();
		        } else {
		        	// If for some reason you could not fetch it from the server,
		        	// fetch it from the local data store
		        	loadMozillaEventsFromLocalDataStore();
		        }
		    }
		});
	}
	//------------------------------------------------------------------------------
	/**
	 * Used to load Mozilla events from local data store in case Internet is not
	 * available or when returning from MapActivity
	 */
	private void loadMozillaEventsFromLocalDataStore(){
		ParseQuery<ParseObject> localQuery = ParseQuery.getQuery(Fields.CLASS_NAME);
    	localQuery.fromLocalDatastore();
    	localQuery.findInBackground(new FindCallback<ParseObject>(){
			@Override
			public void done(List<ParseObject> eventListFromLocal,
					ParseException ex) {
				if(ex == null){
					// remove old events first
					cleanse(eventListFromLocal);
					EventUtil.setMozillaEvents(eventListFromLocal);
					// set the adapter
		            eventsFragment.setAdapter(mozillaEventsAdapter);
					DialogUtil.hideLoadingDialog();
				}else{
					DialogUtil.hideLoadingDialog();
					Toast.makeText(getApplicationContext(), "Could Not Fetch Data", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
	//------------------------------------------------------------------------------
	/**
	 * Removes events which are more than 3 days old, from the local datastore
	 * @param events
	 */
	private void cleanse(List<ParseObject> events){
		for(int i = 0; i < events.size(); i++){
			ParseObject event = events.get(i);
			Date eventDate = event.getDate(Fields.EVENT_DATE);
			Date now = new Date();
			long diff = now.getTime() - eventDate.getTime();
			long millisInADay = 1000 * 60 * 60 * 24;
			long days = diff / millisInADay;
			if(days >= 3){
				event.unpinInBackground(new DeleteCallback(){
					@Override
					public void done(ParseException e) {
						if(e != null){
							Toast toast = Toast.makeText(getApplicationContext(), 
									"Error: " + e.getMessage(), 
									Toast.LENGTH_SHORT);
							toast.show();
						}
					}
				});
			}
		}
	}
	//------------------------------------------------------------------------------
	/**
	 * This is used to know when the user has clicked on an event. This Activity
	 * is registered as a listener so that other Fragments may notify it when
	 * the event item inside them has been clicked
	 */
	@Override
	public void onEventClicked(int position) {
		Intent mapActivity = new Intent(HomeActivity.this,MapActivity.class);
		mapActivity.putExtra(Fields.PARSE_OBJECT, position-1);
		startActivity(mapActivity);
	}
	//------------------------------------------------------------------------------
	/**
	 * Used to listen to any page changes
	 */
	private void setPageChangeListener(){
		tabs.setOnPageChangeListener(new OnPageChangeListener(){

			@Override
			public void onPageScrollStateChanged(int arg0) {
				
			}

			@Override
			public void onPageScrolled(int position, float arg1, int arg2) {
				if(myEventsAdapter == null){
					myEventsAdapter = new MozillaListAdapter(EventUtil.getUserEvents());
					myEventsAdapter.notifyDataSetChanged();
				}else{
					myEventsFragment.setAdapter(myEventsAdapter);
					myEventsAdapter.notifyDataSetChanged();
				}
			}

			@Override
			public void onPageSelected(int position) {
				lastScrolledPage = position;
				if(position == Page.UPCOMING_EVENT){
					eventsFragment.setAdapter(mozillaEventsAdapter);
					mozillaEventsAdapter.notifyDataSetChanged();
				}else if(position == Page.MY_EVENTS){
					if(myEventsAdapter == null){
						myEventsAdapter = new MozillaListAdapter(EventUtil.getUserEvents());
						myEventsAdapter.notifyDataSetChanged();
					}
				}
			}
		});
	}
	//------------------------------------------------------------------------------
	private void setLeftDrawerListListener(){
		
		leftDrawer.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(position == 1){
					pager.setCurrentItem(Page.UPCOMING_EVENT);
				}else if(position == 2){
					pager.setCurrentItem(Page.MY_CALENDAR);
				}else if(position == 3){
					pager.setCurrentItem(Page.MY_EVENTS);
				}else if(position == 4){
					Intent privacyPolicy = new Intent(HomeActivity.this,PrivacyPolicyActivity.class);
					startActivity(privacyPolicy);
				}else if(position == 5){
					Intent contactMe = new Intent(HomeActivity.this,ContactMeActivity.class);
					startActivity(contactMe);
				}else if(position == 6){
					final String packageName = getPackageName();
					Uri market = Uri.parse("market://details?id=" + packageName);
					try{
						startActivity(new Intent(Intent.ACTION_VIEW,market));
					}catch(ActivityNotFoundException e){
						Uri browser = Uri.parse("http://play.google.com/store/apps/details?id=" + packageName);
						startActivity(new Intent(Intent.ACTION_VIEW,browser));
					}
				}
				// TODO add more options
			}
		});
	}
}