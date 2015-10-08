package com.andy.trackme;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreenActivity extends Activity {

	final String PREFS_NAME = "MyPrefsFile";
	SharedPreferences settings;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);
		settings = getSharedPreferences(PREFS_NAME, 0);
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				if (settings.getBoolean("my_first_time", true)) {
				    //the app is being launched for first time, do something        
					Intent i = new Intent(SplashScreenActivity.this,TutoActivity.class);
					startActivity(i);
				    }
				else{
					Intent i2 = new Intent(SplashScreenActivity.this,MainActivity.class);
					startActivity(i2);	
				}
			    settings.edit().putBoolean("my_first_time", false).commit();
				overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
				finish();
			}
		}, 4000);
		
	}
}
