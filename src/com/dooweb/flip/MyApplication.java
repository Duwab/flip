package com.dooweb.flip;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

public class MyApplication extends Application{

    private static Context context;
    private static Activity currActivity = null;

    public void onCreate(){
        super.onCreate();
        MyApplication.context = getApplicationContext();
    	Login.init();
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
    
    public static Activity getCurrentActivity(){
    	return currActivity;
    }

    public static void setCurrentActivity(Activity currentActivity){
    	currActivity = currentActivity;
    }
    
    public static void clearReferences(Activity activityOut){
    	if (currActivity != null && currActivity.equals(activityOut))
    		setCurrentActivity(null);
    }
}
