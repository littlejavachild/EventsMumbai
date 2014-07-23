package com.fasih.mozmeet.util;

import java.util.Comparator;
import java.util.Date;

import android.util.Log;

import com.parse.ParseObject;

public class CategoryComparator implements Comparator<ParseObject> {
	private String category = null;
	//------------------------------------------------------------------------------
	public CategoryComparator(String cat){
		this.category = cat;
	}
	//------------------------------------------------------------------------------
	public CategoryComparator() {
		
	}
	//------------------------------------------------------------------------------
	public  void setCategory(String cat){
		this.category = cat;
	}
	//------------------------------------------------------------------------------
	@Override
	public int compare(ParseObject event1, ParseObject event2) {
		String cat1 = event1.getString(Fields.CATEGORY);
		String cat2 = event2.getString(Fields.CATEGORY);
		Date date1 = event1.getDate(Fields.EVENT_DATE);
		Date date2 = event1.getDate(Fields.EVENT_DATE);
		int c1 = cat1.equals(category) ? 1 : 0;
		int c2 = cat2.equals(category) ? 1 : 0;
		return c1 - c2;
	}
}
