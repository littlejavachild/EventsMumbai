package com.fasih.mozmeet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.fasih.mozmeet.util.ActionBarUtil;
import com.fasih.mozmeet.util.MyTagHandler;
//------------------------------------------------------------------------------
public class GetInvolvedActivity extends Activity {
	
	private TextView studentAmbassadorTitle = null;
	private TextView studentAmbassadorDescription = null;
	private Button become = null;
	private Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.mozilla.org/en-US/contribute/studentambassadors/"));
	// http://stackoverflow.com/questions/3150400/html-list-tag-not-working-in-android-textview-what-can-i-do/3150456
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_get_involved);
		
		// set the action bar color
		ActionBarUtil.setActionBarColor(getActionBar(), getApplicationContext());
		ActionBarUtil.setActionBarTypeface(this);
		
		studentAmbassadorTitle = (TextView) findViewById(R.id.studentsAmbassadorTitle);
		studentAmbassadorDescription = (TextView) findViewById(R.id.studentsAmbassadorDescription);
		become = (Button) findViewById(R.id.become);
		
		Typeface roboto = MozMeetApplication.newInstance().getTypeface();
		
		studentAmbassadorDescription.setTypeface(roboto,Typeface.BOLD);
		studentAmbassadorTitle.setTypeface(roboto, Typeface.BOLD);
		become.setTypeface(roboto, Typeface.BOLD);
		
		// read the text file
		BufferedReader reader = null;
		String text = "";
		String line = "";
		try{
			InputStreamReader in = new InputStreamReader(getAssets().open("txt/get_involved.txt"), "UTF-8");
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
		studentAmbassadorDescription.setText(span);
		setButtonListener();
	}
	//------------------------------------------------------------------------------
	
	private void setButtonListener(){
		become.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {
				try{
					startActivity(intent);
				}catch(ActivityNotFoundException e){
					
				}
			}
		});
	}
	
}
