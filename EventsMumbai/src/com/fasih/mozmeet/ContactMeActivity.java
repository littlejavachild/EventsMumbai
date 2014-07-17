package com.fasih.mozmeet;

import java.nio.charset.Charset;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fasih.mozmeet.util.Fields;
import com.fasih.mozmeet.util.PrefUtil;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

public class ContactMeActivity extends Activity {
	
	private String MESSAGE_KEY = "message";
	private String EMAIL_KEY = "email";
	private TextView contactMeLabel = null;
	private EditText message = null;
	private EditText email = null;
	private TextView privacyLabel = null;
	private Button send = null;
	private Typeface roboto = MozMeetApplication.newInstance().getTypeface();
	
	private final String EMAIL_PATTERN = 
			"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	private Pattern pattern;
	private Matcher matcher;
	// We only allow the user to send a message if the message takes
	// atleast 15 seconds to type. Proof of being a human.
	private boolean isSendingAllowed = false;
	// The actual timer
	private CountDownTimer timer = null;
	//------------------------------------------------------------------------------
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_me);
		
		contactMeLabel = (TextView) findViewById(R.id.contactFormLabel);
		message = (EditText) findViewById(R.id.message);
		email = (EditText) findViewById(R.id.email);
		privacyLabel = (TextView) findViewById(R.id.privacyLabel);
		send = (Button) findViewById(R.id.send);
		
		contactMeLabel.setTypeface(roboto,Typeface.BOLD);
		message.setTypeface(roboto,Typeface.BOLD);
		email.setTypeface(roboto, Typeface.BOLD);
		privacyLabel.setTypeface(roboto, Typeface.BOLD);
		send.setTypeface(roboto,Typeface.BOLD);
		
		if(savedInstanceState != null){
			email.setText(savedInstanceState.getString(EMAIL_KEY));
			message.setText(savedInstanceState.getString(MESSAGE_KEY));
		}
		
		setButtonListener();
	}
	//------------------------------------------------------------------------------
	
	@Override
	public void onResume(){
		super.onResume();
		// Seems like a good idea to let the user know when he can use this
		// facility again
		if(!PrefUtil.isMessageAllowed(new Date(), getApplicationContext())){
			int hoursLeft = PrefUtil.getHoursLeft(new Date(), getApplicationContext());
			Resources res = getResources();
			String hoursLeftString = res.getQuantityString(R.plurals.hoursLeft, hoursLeft,hoursLeft);
			Toast toast = Toast.makeText(getApplicationContext(), hoursLeftString, Toast.LENGTH_SHORT);
			toast.show();
		}
		// We start out 15 second counter here
		timer = new CountDownTimer(15000, 1000) {
		     public void onTick(long millisUntilFinished) {}
		     public void onFinish() {
		        setSendingAllowed();
		     }
		  };
	}
	//------------------------------------------------------------------------------
	@Override
	public void onSaveInstanceState(Bundle outBundle){
		outBundle.putString(EMAIL_KEY, email.getText().toString());
		outBundle.putString(MESSAGE_KEY, message.getText().toString());
	}
	//------------------------------------------------------------------------------
	private void setButtonListener(){
		send.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(validated()){
					send();
				}
			}
		});
	}
	//------------------------------------------------------------------------------
	private boolean validated() {
		// To know if the user is typing too fast aka a bot
		if(!getSendingAllowed()){
			String emptyMessage = getResources().getString(R.string.not_a_bot);
			Toast toast = Toast.makeText(getApplicationContext(), emptyMessage, Toast.LENGTH_SHORT);
			toast.show();
			
			// Stop The Current Timer
			timer.cancel();
			// We start our 15 second counter again
			timer = new CountDownTimer(15000, 1000) {
			     public void onTick(long millisUntilFinished) {}
			     public void onFinish() {
			        setSendingAllowed();
			     }
			  };
			  timer.start();
			return false;
		}
		
		// We do not want the user to unnecessarily write a message
		// and then find out that he is not allowed to send a message
		if(!PrefUtil.isMessageAllowed(new Date(), getApplicationContext())){
			int hoursLeft = PrefUtil.getHoursLeft(new Date(), getApplicationContext());
			Resources res = getResources();
			String hoursLeftString = res.getQuantityString(R.plurals.hoursLeft, hoursLeft,hoursLeft);
			Toast toast = Toast.makeText(getApplicationContext(), hoursLeftString, Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
			toast.show();
			return false;
		}
		
		
		// If Pattern is null, we compile the pattern
		// We defer this compilation till the user clicks
		// send because it is time consuming
		if(pattern == null){
			pattern = Pattern.compile(EMAIL_PATTERN);
		}
		
		if(message.getText().toString().trim().isEmpty()){
			String emptyMessage = getResources().getString(R.string.message_empty);
			Toast toast = Toast.makeText(getApplicationContext(), emptyMessage, Toast.LENGTH_SHORT);
			toast.show();
			return false;
		}
		matcher = pattern.matcher(email.getText().toString().trim());
		if(!matcher.matches()){
			String emptyMessage = getResources().getString(R.string.email_invalid);
			Toast toast = Toast.makeText(getApplicationContext(), emptyMessage, Toast.LENGTH_SHORT);
			toast.show();
			return false;
		}
		
		return true;
	}
	//------------------------------------------------------------------------------
	private void send(){
		// disble the send button
		send.setEnabled(false);
		ParseObject messageFromUser = new ParseObject(Fields.CONTACT_ME);
		messageFromUser.put(EMAIL_KEY,email.getText().toString().trim());
		messageFromUser.put(MESSAGE_KEY,message.getText().toString().trim());
		messageFromUser.saveEventually(new SaveCallback(){
			@Override
			public void done(ParseException ex) {
				String message = "";
				Toast toast = null;
				if(ex == null){
					// Display A Message
					message = getResources().getString(R.string.message_sent);
					toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
					toast.show();
					// Clear The Fields
					clearFields();
					// Add the date to the last sent date
					PrefUtil.addMessageSentDate(new Date(), getApplicationContext());
				}else{
					message = getResources().getString(R.string.message_not_sent);
					toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
					toast.show();
				}
				send.setEnabled(true);
			}
		});
	}
	//------------------------------------------------------------------------------
	@Override
	public void onStop(){
		super.onStop();
		timer.cancel();
	}
	//------------------------------------------------------------------------------
	private void clearFields(){
		message.setText("");
		email.setText("");
	}
	//------------------------------------------------------------------------------
	synchronized private void setSendingAllowed(){
		isSendingAllowed = true;
	}
	//------------------------------------------------------------------------------
	synchronized private boolean getSendingAllowed(){
		return isSendingAllowed;
	}
}
