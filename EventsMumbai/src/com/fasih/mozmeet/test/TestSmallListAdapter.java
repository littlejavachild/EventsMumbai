package com.fasih.mozmeet.test;

import java.text.DateFormat;
import java.util.Date;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fasih.mozmeet.MozMeetApplication;
import com.fasih.mozmeet.R;

public class TestSmallListAdapter extends BaseAdapter {
	
	private static final String TITLE = "Mozilla WebMaker Event";
	private static final String LOCATION = "IIT Bombay";
	
	@Override
	public int getCount() {
		return 1;
	}

	@Override
	public Object getItem(int position) {
		return TITLE;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		DateFormat dateInstance = DateFormat.getDateInstance(DateFormat.MEDIUM);
		DateFormat timeInstance = DateFormat.getTimeInstance(DateFormat.MEDIUM);
		Typeface typeface = MozMeetApplication.newInstance().getTypeface();
		Date now = new Date();
		
		
		TextView eventDate = null;
		TextView eventTime = null;
		TextView eventTitle = null;
		TextView eventLocation = null;
		
		if(convertView == null){
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			convertView = inflater.inflate(R.layout.small_event_list_item, parent, false);
		}
		
		eventDate = (TextView) convertView.findViewById(R.id.eventDate);
		eventTime = (TextView) convertView.findViewById(R.id.eventTime);
		eventTitle = (TextView) convertView.findViewById(R.id.eventTitle);
		eventLocation = (TextView) convertView.findViewById(R.id.eventLocation);
		
		eventDate.setTypeface(typeface);
		eventTime.setTypeface(typeface);
		eventTitle.setTypeface(typeface);
		eventLocation.setTypeface(typeface);
		
		eventDate.setText(dateInstance.format(now));
		eventTime.setText(timeInstance.format(now));
		eventTitle.setText(TITLE);
		eventLocation.setText(LOCATION);

		return convertView;
	}

}
