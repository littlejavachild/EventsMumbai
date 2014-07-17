package com.fasih.mozmeet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.fasih.mozmeet.util.ActionBarUtil;
import com.fasih.mozmeet.util.MyTagHandler;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.widget.TextView;

public class PrivacyPolicyActivity extends Activity{
	
	private TextView privacyPolicyTitle = null;
	private TextView privacyPolicyText = null;
	private Typeface roboto = MozMeetApplication.newInstance().getTypeface();
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_privacy_policy);
		
		// set the action bar color
		ActionBarUtil.setActionBarColor(getActionBar(), getApplicationContext());
		ActionBarUtil.setActionBarTypeface(this);
		
		privacyPolicyTitle = (TextView) findViewById(R.id.privacyPolicyTitle);
		privacyPolicyText = (TextView) findViewById(R.id.privacyPolicyText);
		privacyPolicyText.setTypeface(roboto,Typeface.BOLD);
		privacyPolicyText.setTypeface(roboto, Typeface.BOLD);
	}
	
	@Override
	public void onResume(){
		super.onResume();
		// read the text file
		BufferedReader reader = null;
		String text = "";
		String line = "";
		try{
			InputStreamReader in = new InputStreamReader(getAssets().open("txt/privacy.txt"), "UTF-8");
			reader = new BufferedReader(in);
			while( line != null ){
				line = reader.readLine();
				text = text.concat(line);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(reader!= null){
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		Spannable span = (Spannable) Html.fromHtml(text,null,new MyTagHandler());
		privacyPolicyText.setText(span);
	}
}
