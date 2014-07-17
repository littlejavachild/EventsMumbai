package com.fasih.mozmeet.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.fasih.mozmeet.MozMeetApplication;
import com.fasih.mozmeet.R;
import com.fasih.mozmeet.adapter.MozillaListAdapter;
import com.fasih.mozmeet.util.EventClickListener;
import com.fasih.mozmeet.util.EventUtil;
import com.parse.ParseObject;

public class MyEventsFragment extends Fragment{
	
	private static MyEventsFragment singleton = null;
	private static MozillaListAdapter adapter = null;
	private static ListView myEventsList = null;
	private static TextView emptyView = null;
	private EventClickListener eventClickListener = null;
	
	//------------------------------------------------------------------------------
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	}
	//------------------------------------------------------------------------------
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		try{
			eventClickListener = (EventClickListener) getActivity();
		}catch(ClassCastException e){
			throw new ClassCastException(e.getMessage());
		}
	}
	//------------------------------------------------------------------------------
	@Override
	public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_my_event, container, false);
		myEventsList = (ListView) view.findViewById(R.id.myEventsList);
		emptyView = (TextView) view.findViewById(R.id.emptyView);
		emptyView.setTypeface(MozMeetApplication.newInstance().getTypeface(),Typeface.BOLD);
		myEventsList.setEmptyView(emptyView);
		setListOnClickListener();
		return view;
	}
	//------------------------------------------------------------------------------
	public static MyEventsFragment newInstance(){
		if(singleton == null){
			singleton = new MyEventsFragment();
		}
		return singleton;
	}
	//------------------------------------------------------------------------------
	public void setAdapter(MozillaListAdapter ad){
		adapter = ad;
		if(myEventsList != null){
			myEventsList.setAdapter(adapter);
		}
	}
	//------------------------------------------------------------------------------
	private void setListOnClickListener(){
		myEventsList.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View item, int position,
					long id) {
				ParseObject myEvent = EventUtil.getUserEvents().get(position);
				int actualPosition = EventUtil.getMozillaEvents().indexOf(myEvent);
				eventClickListener.onEventClicked(actualPosition + 1);
			}
			
		});
	}
	//------------------------------------------------------------------------------
}
