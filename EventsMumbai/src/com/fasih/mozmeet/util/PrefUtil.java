package com.fasih.mozmeet.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefUtil {
	private static final String PREFS_FILE = "prefs";
	private static final String LAST_MEASSAGE_DATE = "lastMessageDate";
	private static final String INSTALLATION_SAVED = "installation_saved";
	//------------------------------------------------------------------------------
	public static boolean isMessageAllowed(Date now, Context context){
		SharedPreferences prefs = context.getSharedPreferences(PREFS_FILE, 0);
		// Maybe that this is the first time the user is sending a message.
		// In that case, last message date will be null
		if(prefs.getString(LAST_MEASSAGE_DATE, null) == null){
			return true;
		}
		// If this is the second or third time or more,
		// We need to check if 4 hours have passed
		// The date will be stored in the format EEE MMM dd HH:mm:ss zzz yyyy
		SimpleDateFormat sdf = new SimpleDateFormat(Fields.SIMPLE_DATE_FORMAT);
		String lastMessageDateString = prefs.getString(LAST_MEASSAGE_DATE, new Date().toString());
		try {
			Date lastMessageDate = sdf.parse(lastMessageDateString);
			long sec = (now.getTime() - lastMessageDate.getTime()) / 1000;
			int hours = (int) (sec / 3600);
			if(hours >= 4)
				return true;
			else
				return false;
		} catch (ParseException e) {
			
		}
		return false;
	}
	//------------------------------------------------------------------------------
	public static void addMessageSentDate(Date now, Context context){
		SharedPreferences prefs = context.getSharedPreferences(PREFS_FILE, 0);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(LAST_MEASSAGE_DATE, now.toString());
		editor.commit();
	}
	//------------------------------------------------------------------------------
	public static int getHoursLeft(Date now, Context context){
		SharedPreferences prefs = context.getSharedPreferences(PREFS_FILE, 0);
		SimpleDateFormat sdf = new SimpleDateFormat(Fields.SIMPLE_DATE_FORMAT);
		String lastMessageDateString = prefs.getString(LAST_MEASSAGE_DATE, new Date().toString());
		try {
			Date lastMessageDate = sdf.parse(lastMessageDateString);
			long sec = (lastMessageDate.getTime() - now.getTime()) / 1000;
			int hours = (int) (sec / 3600);
			
			hours = 4 - hours;
			
			return hours;
		} catch (ParseException e) {
			return 4;
		}
	}
	//------------------------------------------------------------------------------
	public static boolean isInstallationSaved(Context context){
		boolean saved = false;
		SharedPreferences prefs = context.getSharedPreferences(PREFS_FILE, 0);
		saved = prefs.getBoolean(INSTALLATION_SAVED, false);
		return saved;
	}
	//------------------------------------------------------------------------------
	public static void setInstallationSaved(Context context){
		SharedPreferences prefs = context.getSharedPreferences(PREFS_FILE, 0);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(INSTALLATION_SAVED, true);
		editor.commit();
	}
	//------------------------------------------------------------------------------
}
