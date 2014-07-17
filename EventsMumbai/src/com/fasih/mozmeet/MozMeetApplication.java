package com.fasih.mozmeet;
import android.app.Application;
import android.graphics.Typeface;

import com.crashlytics.android.Crashlytics;
import com.fasih.mozmeet.util.EventUtil;
import com.fasih.mozmeet.util.Fields;
import com.fasih.mozmeet.util.PrefUtil;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.PushService;
import com.parse.SaveCallback;


public class MozMeetApplication extends Application {
	private static MozMeetApplication singleton = null;
	private Typeface roboto = null;
	private boolean homeActivityVisible = false;
	// http://www.androidhive.info/2013/08/android-working-with-google-maps-v2/
	// http://www.donnfelker.com/android-rounded-corners-with-a-beveldrop-shadow/
	
	//--------------------------------------------------------------------------------------------------
	/**
	 * Called once during the lifecycle of the application
	 * Used to initialize those resources that must be 
	 * initialized once and once only
	 */
	@Override
	public void onCreate(){
		singleton = this;
		createTypeface();
		// Allow Crashlytics Answers
		Crashlytics.getInstance().setDebugMode(true);
		// Enable Crashlytics crash reporting
		Crashlytics.start(this);
		
		// The following line enables localdatastore
		Parse.enableLocalDatastore(this);
		// The following line triggers the initialization of Parse API
		Parse.initialize(this, "EefTaZQLw1t1V1Tm3qmZKmFf5dfHO4qkvSussAn8", "tjydqpH6uSijPBUORHJIAvc1RfZQbR8yzsOex5y8");
		// The following saves the current Installation to Parse.
		if(!PrefUtil.isInstallationSaved(getApplicationContext())){
			ParseInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
				@Override
				public void done(ParseException e) {
					if(e == null){
						PrefUtil.setInstallationSaved(getApplicationContext());
					}
				}
			});
		}
		// Defines who receives the push
		PushService.setDefaultPushCallback(this, HomeActivity.class);
		// The following line subscribes the user to push notification
		PushService.subscribe(getApplicationContext(), Fields.CHANNEL, HomeActivity.class);
		EventUtil.initialize();
		EventUtil.getUserEventsFromDatabase();
	}
	//--------------------------------------------------------------------------------------------------
	synchronized public void setHomeActivityVisible(boolean visibility){
		homeActivityVisible = visibility;
	}
	//--------------------------------------------------------------------------------------------------
	synchronized public boolean getHomeActivityVisible(){
		return homeActivityVisible;
	}
	//--------------------------------------------------------------------------------------------------
	/**
	 * Used to lead the Roboto-Thin font from the assets
	 */
	private void createTypeface(){
		roboto = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf");
	}
	//--------------------------------------------------------------------------------------------------
	/**
	 * Used to obtain a reference to the Application object
	 * @return a reference to the Application singleton
	 */
	public static MozMeetApplication newInstance(){
		return singleton;
	}
	//--------------------------------------------------------------------------------------------------
	/**
	 * Used to obtain a reference to the Typeface
	 * @return a reference to the Typeface
	 */
	public Typeface getTypeface(){
		return roboto;
	}
	//--------------------------------------------------------------------------------------------------
}
