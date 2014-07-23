package com.fasih.mozmeet.util;

// Mon Jul 07 12:03:49 GMT 2014
public interface Fields {
	
	
	// Field to access ParseObject's index passed in an Intent
	String PARSE_OBJECT = "object";
	
	// Used to parse the dates back and forth so that they
	// can be stored in the SharedPreferences and/or used
	// to highlight Caldroid
	String SIMPLE_DATE_FORMAT = "EEE MMM dd HH:mm:ss zzz yyyy";
	
	// Fields used to store settings
	String EVENTS_ATTENDING_LIST = "events_attending_list"; // the list in ParseObject
	String SAVED_EVENTS_CLASS_NAME = "saved_events_data"; // thh class name
	
	// Field to send a message to the developer
	String CONTACT_ME = "ContactMe";
	
	// Fields to send feedback
	String FEEDBACK_CLASS_NAME = "Feedback";
	String FEEDBACK_ASSOCIATED_WITH = "associated_with";
	String FEEDBACK_TEXT = "feedback_text";
	String FEEDBACK_RATING = "feedback_rating";
	String FEEDBACK_LIST = "feedback_list";
	
	// Field to subscribe to push notifications
	String CHANNEL = "events";
	
	
	// Fields used to access data from ParseObject
	String CLASS_NAME = "MozEvent";
	String EVENT_TITLE = "event_title";
	String EVENT_DATE = "event_date";
	String EVENT_TIME = "event_time";
	String EVENT_LOCATION = "event_location";
	String EVENT_DESCTIPTION = "event_description";
	String EVENT_ADDRESS = "event_address";
	String HIGHLIGHTED = "highlighted";
	String IMAGE_URL = "image_url";
	String PEOPLE_ATTENDING = "people_attending";
	String OBJECT_ID = "objectId";
	String CATEGORY = "category";
}
