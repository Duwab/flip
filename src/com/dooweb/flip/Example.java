package com.dooweb.flip;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class Example extends Activity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //?? MyApplication.setCurrentActivity(this); >> should set if none is
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	    //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//setContentView(R.layout.starter);
		//Button btn = (Button) findViewById(R.id.btn_id);
    }
    protected void onResume() {
        super.onResume();
        MyApplication.setCurrentActivity(this);
    }
    protected void onPause() {
    	MyApplication.clearReferences(this);
        super.onPause();
    }
    protected void onDestroy() {        
    	MyApplication.clearReferences(this);
        super.onDestroy();
    }
}
