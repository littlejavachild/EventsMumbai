package com.fasih.mozmeet.util;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

public class EventUtil {
	
	private static List<ParseObject> mozillaEvents;
	private static List<ParseObject> userEvents;
	private static List<ParseObject> eventsOnADate;
	private static List<ParseObject> feedback;
	private static ParseObject userEventsInDb = null;
	private static SaveCallback callback = null;
	private static ChronoComparator comparator = null;
	private static CategoryComparator catComparator = null;
	private static String cat = null;
	/**
	 * Used to initialize the EventUtil.
	 * This ensures that we never get NPE
	 * when setting adapters
	 */
	public static void initialize(){
		mozillaEvents = new ArrayList<ParseObject>();
		eventsOnADate = new ArrayList<ParseObject>();
		
		userEventsInDb = new ParseObject(Fields.SAVED_EVENTS_CLASS_NAME);
		userEvents = new ArrayList<ParseObject>();
		userEventsInDb.put(Fields.EVENTS_ATTENDING_LIST,userEvents);
		feedback = new ArrayList<ParseObject>();
		
		comparator = new ChronoComparator();
		catComparator = new CategoryComparator();
		
		
		callback = new SaveCallback(){
			@Override
			public void done(ParseException e) {
				if( e != null ){
					Log.v("localdatastore", e.getMessage() + " ");
				}
			}
		};
	}
	//------------------------------------------------------------------------------
	/**
	 * Used to fetch user events from the database
	 */
	public static void getUserEventsFromDatabase(){
		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(Fields.SAVED_EVENTS_CLASS_NAME);
		query.fromLocalDatastore();
		query.findInBackground(new FindCallback<ParseObject>(){
			@Override
			public void done(List<ParseObject> saved, ParseException e) {
				if(e == null){
					if(saved.size() > 0){
						try {
							userEventsInDb = saved.get(0);
							userEvents = (List<ParseObject>) userEventsInDb.fetchIfNeeded().get(Fields.EVENTS_ATTENDING_LIST);
							Log.v("user_events", Integer.valueOf(userEvents.size()).toString());
						} catch (ParseException e1) {
							e1.printStackTrace();
						}
						Collections.sort(userEvents, comparator);
					}
				}
			}
		});
	}
	//------------------------------------------------------------------------------
	/**
	 * Used to load feedbacks that the user has provided, from the database
	 */
	public static void getFeedbackFromDatabase(){
		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(Fields.FEEDBACK_CLASS_NAME);
		query.fromLocalDatastore();
		query.findInBackground(new FindCallback<ParseObject>(){
			@Override
			public void done(List<ParseObject> feed, ParseException e) {
				if(e == null){
					if(feed.size() > 0){
						feedback.addAll(feed);
					}
				}
			}
		});
	}
	//------------------------------------------------------------------------------
	/**
	 * Used to add a feedback to the database
	 * @param event The event for which the feedback is being submitted
	 */
	public static void saveFeedback(ParseObject feed){
		// Save to local data store and server
		feed.pinInBackground(callback);
		feed.saveEventually();
		// also add it to the in-memory list of feedback
		feedback.add(feed);
	}
	//------------------------------------------------------------------------------
	/**
	 * Used to check if a feedback has been provided for a given event
	 * @param event
	 * @return
	 */
	public static boolean containsFeedback(ParseObject event){
		String objectId = event.getObjectId();
		for(ParseObject feed : feedback){
			String associatedWith = feed.getString(Fields.FEEDBACK_ASSOCIATED_WITH);
			if(associatedWith.equalsIgnoreCase(objectId))
				return true;
		}
		return false;
	}
	//------------------------------------------------------------------------------
	/**
	 * Used to add a new event that the user is looking forward to
	 * @param event
	 */
	public static void addEvent(ParseObject event){
		// add the event
		userEvents.add(event);
		// save it to the database
		userEventsInDb.pinInBackground(callback);
	}
	//------------------------------------------------------------------------------
	/**
	 * Used to remove an event that the user was previously looking forward to
	 * @param event
	 */
	public static void removeEvent(ParseObject event){
		// remove the event
		userEvents.remove(event);
		// save it to the database
		userEventsInDb.pinInBackground(callback);
	}
	//------------------------------------------------------------------------------
	/**
	 * Used to know whether the user is interested in a given event
	 * @param event
	 * @return
	 */
	public static boolean containsEvent(ParseObject event){
		return userEvents.contains(event);
	}
	
	//------------------------------------------------------------------------------
	/**
	 * Used to update the list of upcoming Mozilla events
	 * @param retrievedMozillaEvents
	 * 		List of events loaded from the Internet or from local data store.
	 * 		The data has already been saved to local data store so there is no 
	 * 		need to save it again
	 */
	public static void setMozillaEvents(List<ParseObject> retrievedMozillaEvents){
		
		if(mozillaEvents.size() == 0){
			mozillaEvents.addAll(retrievedMozillaEvents);
//			Collections.sort(mozillaEvents, comparator);
			EventUtil.sort("All");
			return;
		}
		
		HashMap<String,String> oldKeys = new HashMap<String, String>();
		// find all the old keys
		for(ParseObject event : mozillaEvents){
			oldKeys.put(event.getString(Fields.OBJECT_ID), "exists");
		}
		
		for(int i = 0; i < retrievedMozillaEvents.size(); i++){
			ParseObject event = retrievedMozillaEvents.get(i);
			String newKey = event.getString(Fields.OBJECT_ID);
			// Let's see if the new Key returns something from our hashmap
			String data = (String) oldKeys.get(newKey);
			// A new key will not return any data
			if(data == null){
				mozillaEvents.add(event);
			}
		}
		Collections.sort(mozillaEvents, comparator);
	}
	//------------------------------------------------------------------------------
	/**
	 * Returns a list containing all the events the user is interested in
	 * @return list  of events user is interested in
	 */
	public static List<ParseObject> getUserEvents(){
		return userEvents;
	}
	//------------------------------------------------------------------------------
	/**
	 * Used to get all the Mozilla events on a given day
	 * @return
	 */
	public static List<ParseObject> getMozillaEvents(){
		return mozillaEvents;
	}
	//------------------------------------------------------------------------------
	/**
	 * Used to get a list of all the events that fall on a particular day
	 * Call it ONLY after calling findEventsOnADate()
	 * @return
	 */
	public static List<ParseObject> getAllEventsOnADate(){
		return eventsOnADate;
	}
	//------------------------------------------------------------------------------
	/**
	 * Used to get all the events that fall on a given day
	 */
	public static void findEventsOnADate(Date date){
		// Clear the list first
		eventsOnADate.clear();
		
		// We are just comparing the dates so we need a formatter for it
		DateFormat dateInstance = DateFormat.getDateInstance(DateFormat.MEDIUM);
		
		// For every upcoming event
		for(ParseObject event : mozillaEvents){
			// We get the MEDIUM representation of both the dates
			String eventDate = dateInstance.format(event.getDate(Fields.EVENT_DATE));
			String comparingDate = dateInstance.format(date);
			
			// If both of them are equal, we add it to the eventsOnDate list
			if(eventDate.equalsIgnoreCase(comparingDate)){
				eventsOnADate.add(event);
			}
		}
	}
	//------------------------------------------------------------------------------
	/**
	 * Used to know if the event is over. This will be sued to enable the feedback
	 * system
	 * @param event
	 * @return
	 */
	public static boolean eventBeforeToday(ParseObject event){
		Date today = new Date();
		Date eventDate = event.getDate(Fields.EVENT_DATE);
		return eventDate.before(today);
	}
	//------------------------------------------------------------------------------
	public static boolean sort(String category){
		boolean changed = false;
		if(category.equals("All")){
			cat = category;
			Collections.sort(mozillaEvents, comparator);
			changed = true;
		}else{
			cat = category;
			// We find all the events that match the given category
			List<ParseObject> matchingEvents = new ArrayList<ParseObject>();
			for(int i = 0; i < mozillaEvents.size(); i++){
				ParseObject event = mozillaEvents.get(i);
				Log.v("COMPARING ", event.getString(Fields.CATEGORY));
				// If it matches the category,
				if(event.getString(Fields.CATEGORY).trim().equals(category)){
					// We remove it from the main list
					// and add it to the matchingEvents list
					Log.v("REMOVING EVENT ", event.getString(Fields.EVENT_TITLE));
					mozillaEvents.remove(i);
					matchingEvents.add(event);
					changed = true;
				}
			}
			Collections.sort(matchingEvents, comparator);
			Collections.sort(mozillaEvents, comparator);
			// Add the events back to the main list,
			// although at the start
			mozillaEvents.addAll(0, matchingEvents);
		}
		return changed;
	}
	//------------------------------------------------------------------------------
	public static void sortAfterRefreshCompleted(){
		EventUtil.sort(cat);
	}
	//------------------------------------------------------------------------------
}
