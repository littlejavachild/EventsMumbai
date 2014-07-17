package com.fasih.mozmeet.adapter;

import java.util.Date;
import java.util.List;

import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.fasih.mozmeet.MozMeetApplication;
import com.fasih.mozmeet.R;
import com.fasih.mozmeet.fragments.MozillaEventsFragment;
import com.fasih.mozmeet.fragments.MyEventsFragment;
import com.fasih.mozmeet.util.EventUtil;
import com.fasih.mozmeet.util.Fields;
import com.parse.ParseObject;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

public class CustomPagerAdapter extends FragmentPagerAdapter {

	private static final String[] CONTENT = new String[] { "Upcoming", "Calendar", "My Events" };
	private static CaldroidFragment calSingleton = null;
	private static CaldroidListener listener = null;
	private static TextView simpleTextView = null;
	private static Toast toast;
	//------------------------------------------------------------------------------
    public CustomPagerAdapter(FragmentManager fm) {
    	super(fm);
    	createCaldroidListener();
    }
    //------------------------------------------------------------------------------
    public void setCalendarFragment(CaldroidFragment cal){
    	calSingleton = cal;
    	cal.setCaldroidListener(listener);
    }
    //------------------------------------------------------------------------------
    @Override
    public Fragment getItem(int position) {
    	highlightCaldroidDates();
        if(position == 0){
        	return MozillaEventsFragment.newInstance();
        }else if(position == 1){
        	return calSingleton;
        }else if(position == 2){
//        	return new com.fasih.mozmeet.test.TestFragment();
        	return MyEventsFragment.newInstance();
        }
        return new com.fasih.mozmeet.test.TestFragment();
    }
    //------------------------------------------------------------------------------
    @Override
    public CharSequence getPageTitle(int position) {
        return CONTENT[position % CONTENT.length].toUpperCase();
    }
    //------------------------------------------------------------------------------
    @Override
    public int getCount() {
      return CONTENT.length;
    }
    //------------------------------------------------------------------------------
    private void highlightCaldroidDates(){
    	List<ParseObject> mozillaEvents = EventUtil.getMozillaEvents();
    	Log.v("caldroid_dates", "Mozilla Events List " + mozillaEvents.size());
    	for(ParseObject event : mozillaEvents){
    		Date date = event.getDate(Fields.EVENT_DATE);
    		Log.v("caldroid_dates", "  " + date);
    		calSingleton.setBackgroundResourceForDate(R.color.event_highlight_green, date);
    	}
    	calSingleton.refreshView();
    }
    //------------------------------------------------------------------------------
    private void createCaldroidListener(){
    	listener = new CaldroidListener() {

    	    @Override
    	    public void onSelectDate(Date date, View view) {
    	        EventUtil.findEventsOnADate(date);
    	        List<ParseObject> eventsOnADate = EventUtil.getAllEventsOnADate();
    	        
    	        if(simpleTextView == null){
    	        	LayoutInflater inflater = LayoutInflater.from(view.getContext());
    	        	simpleTextView = (TextView) inflater.inflate(android.R.layout.simple_list_item_1, null, false);
    	        	simpleTextView.setTypeface(MozMeetApplication.newInstance().getTypeface(),Typeface.BOLD);
    	        	simpleTextView.setTextColor(view.getResources().getColor(R.color.caldroid_darker_gray));
    	        	simpleTextView.setGravity(Gravity.CENTER);
    	        	
    	        	toast =  new Toast(view.getContext());
    	        	toast.setView(simpleTextView);
    	        	toast.setDuration(4 * 1000); // 4 seconds
        	        toast.setGravity(Gravity.BOTTOM, 0, 30);
    	        }
    	        
    	        String text = "";
    	        for(ParseObject event : eventsOnADate){
    	        	String title = event.getString(Fields.EVENT_TITLE) + " from " ;
    	        	String time = event.getString(Fields.EVENT_TIME);
    	        	text = text.concat(title).concat(time).concat("\n");
    	        }
    	        if(!text.isEmpty()){
    	        	simpleTextView.setText(text);
        	        toast.show();
    	        }
    	    }

    	    @Override
    	    public void onChangeMonth(int month, int year) {
    	    	highlightCaldroidDates();
    	    }

    	    @Override
    	    public void onLongClickDate(Date date, View view) {
    	        
    	    }

    	    @Override
    	    public void onCaldroidViewCreated() {
    	        
    	    }

    	};
    }
}
