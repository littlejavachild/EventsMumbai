package com.fasih.mozmeet.test;


import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.fasih.mozmeet.MozMeetApplication;
import com.fasih.mozmeet.R;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class TestActivity extends FragmentActivity {
	@Override
	public void onCreate(Bundle SavedInstanceState){
		super.onCreate(SavedInstanceState);
		setContentView(R.layout.activity_home);
		
		// stuff related to action bar
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.psts_tab_background)));
		
		// stuff related to viewpager and its slider
		ViewPager pager = (ViewPager) findViewById(R.id.pager);
		PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		pager.setAdapter(new TestPagerAdapter(getSupportFragmentManager()));
		tabs.setViewPager(pager);
		tabs.setTypeface(MozMeetApplication.newInstance().getTypeface(), Typeface.BOLD);
		
		// stuff related to the navdrawer
		ListView leftDrawer = (ListView) findViewById(R.id.left_drawer);
		leftDrawer.setAdapter(new com.fasih.mozmeet.adapter.NavigationListAdapter());
		
		// stuff related to the little list of events at the bottom
////		ListView dailyEventsList = (ListView) findViewById(R.id.dailyEventsList);
//		dailyEventsList.setAdapter(new TestSmallListAdapter());
//		dailyEventsList.setOnItemClickListener( new OnItemClickListener(){
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//					long arg3) {
//				Intent mapActivity = new Intent(TestActivity.this, TestMapActivity.class);
//				startActivity(mapActivity);
//			}
//			
//		});
	}
}
