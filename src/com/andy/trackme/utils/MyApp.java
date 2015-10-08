package com.andy.trackme.utils;

import android.app.Application;

public class MyApp extends Application{
	
	private boolean connected;
	
	public boolean isConnected(){
		return connected;
	}
	
	public void setConnected (boolean connected){
		this.connected = connected;
	}
}
