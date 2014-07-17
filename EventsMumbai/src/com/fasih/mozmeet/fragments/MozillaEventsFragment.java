package com.fasih.mozmeet.fragments;

import java.util.List;

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
import com.fasih.mozmeet.util.Fields;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class MozillaEventsFragment extends Fragment{
	
	private static MozillaEventsFragment singleton = null;
	private static MozillaListAdapter adapter = null;
	private PullToRefreshListView mozillaEventsRefreshList = null;
	private TextView emptyView = null;
	private EventClickListener eventClickListener = null;
	
	//------------------------------------------------------------------------------
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		singleton = this;
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
		View view = inflater.inflate(R.layout.fragment_mozillaevents, container, false);
		mozillaEventsRefreshList = (PullToRefreshListView) view.findViewById(R.id.mozillaEventsRefreshList);
		emptyView = (TextView) view.findViewById(R.id.emptyView);
		emptyView.setTypeface(MozMeetApplication.newInstance().getTypeface(),Typeface.BOLD);
		mozillaEventsRefreshList.setEmptyView(emptyView);
		
		// Add a listener to know when the list has been pulled
		mozillaEventsRefreshList.setOnRefreshListener(new OnRefreshListener<ListView>() {
		    @Override
		    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		        // TODO show progress dialog
		    	onListPulled();
		    }
		});
		
		mozillaEventsRefreshList.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long id) {
				eventClickListener.onEventClicked(position);
			}
		});
		return view;
	}
	//------------------------------------------------------------------------------
	@Override
	public void onResume(){
		super.onResume();
		if(adapter != null){
			adapter.notifyDataSetChanged();
		}
	}
	//------------------------------------------------------------------------------
	public static MozillaEventsFragment newInstance(){
		if(singleton == null){
			singleton = new MozillaEventsFragment();
		}
		return singleton;
	}
	//------------------------------------------------------------------------------
	public void setAdapter(MozillaListAdapter ad){
		adapter = ad;
		mozillaEventsRefreshList.setAdapter(adapter);
	}
	//------------------------------------------------------------------------------
	public PullToRefreshListView getPullToRefreshList(){
		return mozillaEventsRefreshList;
	}
	//------------------------------------------------------------------------------
	private void onListPulled(){
		ParseQuery<ParseObject> query = ParseQuery.getQuery(Fields.CLASS_NAME);
		// TODO  show progress dialog
		query.findInBackground(new FindCallback<ParseObject>(){
			@Override
			public void done(List<ParseObject> eventList, ParseException e) {
				if( e == null ){
					// add the new events to be displayed
					EventUtil.setMozillaEvents(eventList);
					// save them to the local data store
					ParseObject.pinAllInBackground(eventList);
					// notify that new data has come
					adapter.notifyDataSetChanged();
				}else{
					// NOTHING
				}
				mozillaEventsRefreshList.onRefreshComplete();
			}
		});
	}
	//------------------------------------------------------------------------------
}
