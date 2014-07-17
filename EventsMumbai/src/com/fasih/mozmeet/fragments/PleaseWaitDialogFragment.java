package com.fasih.mozmeet.fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.TextView;

import com.fasih.mozmeet.MozMeetApplication;
import com.fasih.mozmeet.R;

public class PleaseWaitDialogFragment extends DialogFragment{
	
	public PleaseWaitDialogFragment(){
		
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		return inflateTextView(inflater, container);
//		return inflateGifView(inflater, container);
	}
	
	private View inflateGifView(LayoutInflater inflater,ViewGroup container){
		View view = inflater.inflate(R.layout.fragment_please_wait, container, false);
		WebView gifPlayer = (WebView) view.findViewById(R.id.gifPlayer);
		gifPlayer.getSettings().setJavaScriptEnabled(true);
		gifPlayer.loadUrl("file:///android_asset/img/preloader_7.gif");
		gifPlayer.setBackgroundColor(0x00000000);
		
		// http://stackoverflow.com/questions/9698410/position-of-dialogfragment-in-android
		getDialog().getWindow().setGravity(Gravity.CENTER);
		getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		getDialog().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		return view;
	}
	
	private View inflateTextView(LayoutInflater inflater,ViewGroup container){
		View view = inflater.inflate(R.layout.fragment_please_wait_text, container, false);
		TextView loading = (TextView) view.findViewById(R.id.loading);
		loading.setTypeface(MozMeetApplication.newInstance().getTypeface());
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		
		return view;
	}
	
}
