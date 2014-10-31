package com.dooweb.flip.objects;


import android.util.Log;

public abstract class CallbackContext implements CallbackInterface{
	
	protected Object origin = null;
	
	public CallbackContext(Object ctx){
		origin = ctx;
	}
	
	@Override
	public void complete(DataObject res, HTTPStatus err){
		Log.v("CallbackContext res", res.toString());
		Log.v("CallbackContext err", err.toString());
	}
	
	@Override
	public void progress(double percent){
		
	}
	
	@Override
	public void start(){
		
	}
}
