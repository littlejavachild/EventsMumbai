package com.fasih.mozmeet.test;

import java.util.Calendar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.roomorama.caldroid.CaldroidFragment;

class TestPagerAdapter extends FragmentPagerAdapter {
	private static final String[] CONTENT = new String[] { "Upcoming", "My Calendar", "My Events" };
        public TestPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position == 0){
            	new com.fasih.mozmeet.fragments.MozillaEventsFragment();
            }else if(position == 1){
            	CaldroidFragment caldroidFragment = new CaldroidFragment();
            	Bundle args = new Bundle();
            	Calendar cal = Calendar.getInstance();
            	args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
            	args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
            	caldroidFragment.setArguments(args);
            	return caldroidFragment;
            }
            return new com.fasih.mozmeet.fragments.MozillaEventsFragment();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return CONTENT[position % CONTENT.length].toUpperCase();
        }

        @Override
        public int getCount() {
          return CONTENT.length;
        }
    }