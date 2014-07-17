package com.fasih.mozmeet.util;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.widget.TextView;

import com.fasih.mozmeet.MozMeetApplication;
import com.fasih.mozmeet.R;

public class ActionBarUtil {
	
	public static void setActionBarColor(ActionBar bar, Context context){
		// Set ActionBar color
		bar.setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.psts_tab_background)));	
	}
	
	public static void setActionBarTypeface(Activity activity){
		int titleId = activity.getResources().getIdentifier("action_bar_title", "id",
	            "android");
	    TextView yourTextView = (TextView) activity.findViewById(titleId);
	    yourTextView.setTypeface(MozMeetApplication.newInstance().getTypeface(),Typeface.BOLD);
	}
}
