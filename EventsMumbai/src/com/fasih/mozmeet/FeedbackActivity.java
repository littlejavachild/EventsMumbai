package com.fasih.mozmeet;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fasih.mozmeet.util.ActionBarUtil;
import com.fasih.mozmeet.util.EventUtil;
import com.fasih.mozmeet.util.Fields;
import com.fasih.mozmeet.views.GoogleIORatingBar;
import com.parse.ParseObject;

public class FeedbackActivity extends Activity {
	
	private TextView feedBackFormLabel = null;
	private GoogleIORatingBar gioRatingBar = null;
	private EditText feedback = null;
	private Button submit = null;
	private ParseObject event;
	
	private static final String FEEDEBACK_KEY = "feedback_key";
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback);
		
		// Change the ActionBar color
		ActionBarUtil.setActionBarColor(getActionBar(), getApplicationContext());
		ActionBarUtil.setActionBarTypeface(this);
		
		init();
		setListeners();
		
		if(savedInstanceState != null){
			String text = savedInstanceState.getString(FEEDEBACK_KEY);
			feedback.setText(text);
		}
	}
	//------------------------------------------------------------------------------
	private void init(){
		// Find references to the UI elements
		feedBackFormLabel = (TextView) findViewById(R.id.feedbackFormLabel);
		gioRatingBar = (GoogleIORatingBar) findViewById(R.id.gioRatingBar);
		feedback = (EditText) findViewById(R.id.feedback);
		submit = (Button) findViewById(R.id.submit);
		
		// Set their font
		Typeface roboto = MozMeetApplication.newInstance().getTypeface();
		feedBackFormLabel.setTypeface(roboto, Typeface.BOLD);
		feedback.setTypeface(roboto,Typeface.BOLD);
		submit.setTypeface(roboto, Typeface.BOLD);
		
		// Find the event for which the feedback screen is shown
		Intent intent = getIntent();
		int position = intent.getIntExtra(Fields.PARSE_OBJECT, -1);
		if(position != -1){
			event = EventUtil.getMozillaEvents().get(position);
		}
	}
	//------------------------------------------------------------------------------
	private void setListeners(){
		submit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int rating = gioRatingBar.getRating();
				// If the user has not selected a rating,
				// we will prompt them to select a rating
				if(rating == 0){
					String text = getResources().getString(R.string.please_select_rating);
					Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
					toast.show();
					return;
				}
				String feedbackText = feedback.getText().toString();
				String associatedWith = event.getObjectId();
				ParseObject feedback = new ParseObject(Fields.FEEDBACK_CLASS_NAME);
				feedback.put(Fields.FEEDBACK_ASSOCIATED_WITH, associatedWith);
				feedback.put(Fields.FEEDBACK_TEXT, feedbackText);
				feedback.put(Fields.FEEDBACK_RATING, rating);
				EventUtil.saveFeedback(feedback);
				// Show a toast for some visual feedback
				String text = getResources().getString(R.string.thank_you_feedback);
				Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
				toast.show();
				// End of this activity
				finish();
			}
		});
	}
	//------------------------------------------------------------------------------
	@Override
	public void onSaveInstanceState(Bundle outState){
		outState.putSerializable(FEEDEBACK_KEY, feedback.getText().toString());
		super.onSaveInstanceState(outState);
	}
	//------------------------------------------------------------------------------
}
