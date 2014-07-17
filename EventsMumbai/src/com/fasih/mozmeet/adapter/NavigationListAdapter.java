package com.fasih.mozmeet.adapter;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fasih.mozmeet.MozMeetApplication;
import com.fasih.mozmeet.R;

public class NavigationListAdapter extends BaseAdapter {
	
	private static String[] titles = {"MozMeet Mumbai",
									  "Upcoming",
									  "My Calendar",
									  "My Events",
									  "Privacy Policy",
									  "Contact Me",
									  "Rate This App"};
	//----------------------------------------------------------------------------------------
	@Override
	public int getCount() {
		return titles.length;
	}
	//----------------------------------------------------------------------------------------
	@Override
	public Object getItem(int index) {
		return titles[index];
	}
	//----------------------------------------------------------------------------------------
	@Override
	public long getItemId(int index) {
		return index;
	}
	//----------------------------------------------------------------------------------------
	@Override
	public boolean isEnabled(int position){
		// Disable the banner. It should not be clickable.
		if(position == 0){
			return false;
		}
		return true;
	}
	//----------------------------------------------------------------------------------------
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		Typeface typeface = MozMeetApplication.newInstance().getTypeface();
		TextView navDrawerListItemTitle = null;
		TextView bannerTitle = null;
		// If the convertView is null, we need to inflate a new View
		// Inflating the view depends upon the index because
		// we also have a banner in the list
		if(convertView == null){
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			if(position == 0){
				convertView = inflater.inflate(R.layout.list_item_moz_banner, parent, false);
				bannerTitle = (TextView) convertView.findViewById(R.id.banner_title);
				bannerTitle.setTypeface(typeface,Typeface.BOLD);
			}else{
				convertView = inflater.inflate(R.layout.navdrawer_list_item, parent, false);
			}
		}
		
		// The banner is pre-created in XML so we do not have to make any changes
		if(position != 0){
			navDrawerListItemTitle = (TextView) convertView.findViewById(R.id.navdrawer_list_item_title);
			navDrawerListItemTitle.setText(titles[position]);
			navDrawerListItemTitle.setTypeface(typeface, Typeface.BOLD);
		}
		
		return convertView;
	}

}
