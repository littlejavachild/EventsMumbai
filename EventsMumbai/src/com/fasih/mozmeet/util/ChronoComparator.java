package com.fasih.mozmeet.util;

import java.util.Comparator;
import java.util.Date;

import com.parse.ParseException;
import com.parse.ParseObject;

public class ChronoComparator implements Comparator<ParseObject>{
	@Override
	public int compare(ParseObject event1, ParseObject event2) {
		Date date1 = new Date();
		Date date2 = new Date();
		try {
			date1 = event1.fetchIfNeeded().getDate(Fields.EVENT_DATE);
			date2 = event2.fetchIfNeeded().getDate(Fields.EVENT_DATE);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date1.compareTo(date2);
	}
}
