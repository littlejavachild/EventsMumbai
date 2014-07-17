package com.fasih.mozmeet.adapter;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fasih.mozmeet.MozMeetApplication;
import com.fasih.mozmeet.R;
import com.fasih.mozmeet.util.Fields;
import com.parse.ParseObject;
import com.squareup.picasso.Picasso;

public class MozillaListAdapter extends BaseAdapter {
	
	private List<ParseObject> data = null;
	
	public MozillaListAdapter(List<ParseObject> data){
		this.data = data;
	}
	
	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public int getViewTypeCount(){
		return 2;
	}
	
	@Override
	public int getItemViewType (int position){
		ParseObject event = data.get(position);
		boolean highlighted = event.getBoolean(Fields.HIGHLIGHTED);
		
		if(!highlighted) // 0 = small list item view
			return 0;
		else
			return 1; // 1 = large list item view
		
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ParseObject event = data.get(position);
		boolean highlighted = event.getBoolean(Fields.HIGHLIGHTED);
		String imageURL = event.getString(Fields.IMAGE_URL);
		
		if(convertView == null){
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			// If the event is not highlighted, we simple show the small list item
			if(!highlighted){
				convertView = inflater.inflate(R.layout.small_event_list_item, parent, false);
			}else{ // else we show the large one
				convertView = inflater.inflate(R.layout.large_event_list_item, parent, false);
			}
		}
		
		Typeface roboto = MozMeetApplication.newInstance().getTypeface();
		DateFormat dateIns = DateFormat.getDateInstance(DateFormat.MEDIUM);
		
		TextView eventDate = (TextView) convertView.findViewById(R.id.eventDate);
		TextView eventTime = (TextView) convertView.findViewById(R.id.eventTime);
		TextView eventTitle = (TextView) convertView.findViewById(R.id.eventTitle);
		ImageView eventImage = (ImageView) convertView.findViewById(R.id.eventImage);
		TextView eventAddress = (TextView) convertView.findViewById(R.id.eventAddress);
		TextView eventDescription = (TextView) convertView.findViewById(R.id.eventDescription);
		
		eventDate.setTypeface(roboto,Typeface.BOLD);
		eventTime.setTypeface(roboto,Typeface.BOLD);
		eventTitle.setTypeface(roboto,Typeface.BOLD);
		// the highlighted event has no "address" field to display
		if(!highlighted)
			eventAddress.setTypeface(roboto,Typeface.BOLD);
		// the highlighted events have "description" field
		if(highlighted)
			eventDescription.setTypeface(roboto,Typeface.BOLD);
		
		Date date = event.getDate(Fields.EVENT_DATE);
		eventDate.setText(dateIns.format(date));
		eventTime.setText(event.getString(Fields.EVENT_TIME));
		eventTitle.setText(event.getString(Fields.EVENT_TITLE));
		// the highlighted event has no "address" field to display
		if(!highlighted)
			eventAddress.setText(event.getString(Fields.EVENT_ADDRESS));
		// the highlighted events have "description" field
		if(highlighted)
			eventDescription.setText(event.getString(Fields.EVENT_DESCTIPTION));
		
		// finally, set the image
		Picasso.with(convertView.getContext()).load(imageURL).error(R.drawable.event).into(eventImage);

		return convertView;
	}

}
