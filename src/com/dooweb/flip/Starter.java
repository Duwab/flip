package com.dooweb.flip;

import com.dooweb.flip.objects.CallbackContext;
import com.dooweb.flip.objects.DataObject;
import com.dooweb.flip.objects.HTTPStatus;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class Starter extends Activity{
	protected View slider = null;
	protected View progress = null;
	protected int screenWidth = 0;
	protected int screenHeight = 0;
	private boolean sliding = false;
	protected Login login_fragment = null;
	protected Register register_fragment = null;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	    super.onCreate(savedInstanceState);
        MyApplication.setCurrentActivity(this);
		setContentView(R.layout.starter);
		screenWidth = Tools.screenWidth();
		screenHeight = Tools.screenHeight();
		Button btnSwReg = (Button) findViewById(R.id.starter_switch_register);
		Button btnSwLog = (Button) findViewById(R.id.starter_switch_login);
		initStarter();
		btnSwReg.setOnClickListener(animSlide);
		btnSwLog.setOnClickListener(animSlide);
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
	
    protected void initStarter(){
    	Log.v("debug", "initstarter");
    	progress = (View) findViewById(R.id.starter_slider);
		setSlider();
		setLogin();
		setRegister();
		if(Login.isLogged())
		{
			Log.v("debug", "user token defined");
			login_fragment.fetchLogin();
		}else
		{
			Log.v("debug", "user token undefined");
			showForms();
		}
    }
    
    protected CallbackContext loginCallback(){
    	return new CallbackContext(this){
			@Override
			public void complete(DataObject data, HTTPStatus err){
		    	Log.v("debug", "callback complete");
				Starter s = (Starter) origin;
				if(s == null)
					return;
				if(!err.error && Login.isLogged())
					s.openMain();
				else
					s.showForms();
			}
			
			@Override
			public void start(){
		    	Log.v("debug", "callback start");
				Starter s = (Starter) origin;
				if(s == null)
					return;
				s.hideForms();
			}
		};
    }
    
    protected CallbackContext registerCallback(){
    	return new CallbackContext(this){
			@Override
			public void complete(DataObject data, HTTPStatus err){
		    	Log.v("debug", "callback complete");
				Starter s = (Starter) origin;
				if(s == null)
					return;
				s.showForms();
				if(!err.error)
				{
					if(!Login.isLogged())
						Tools.alert("anormaly not logged");
					else
						s.openMain();
				}else
					s.showForms();
			}
			
			@Override
			public void start(){
		    	Log.v("debug", "callback start");
				Starter s = (Starter) origin;
				if(s == null)
					return;
				s.hideForms();
			}
		};
    }
    
    protected void showForms(){
    	progress.setVisibility(View.GONE);
    	slider.setVisibility(View.VISIBLE);
    }
    
    protected void hideForms(){
    	progress.setVisibility(View.VISIBLE);
    	slider.setVisibility(View.GONE);
    }
    
    protected void setSlider(){
    	slider = (View) findViewById(R.id.starter_slider);
    	LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) slider.getLayoutParams();
    	params.width = screenWidth*2;
    	params.leftMargin = 0;
    	slider.setLayoutParams(params);
    }
    
    protected void setLogin(){
    	login_fragment = new Login();
    	login_fragment.setFetchingCallback(loginCallback());
    	FragmentManager fragmentManager = getFragmentManager();
    	fragmentManager.beginTransaction().replace(R.id.starter_login_frame, login_fragment).commit();
    }
    
    protected void setRegister(){
    	Register register_fragment = new Register();
    	register_fragment.setFetchingCallback(registerCallback());
    	FragmentManager fragmentManager = getFragmentManager();
    	fragmentManager.beginTransaction().replace(R.id.starter_register_frame, register_fragment).commit();
    }
    
    protected void openMain(){
		Intent new_intent = new Intent(MyApplication.getAppContext(), MainActivity.class);
		startActivity(new_intent);
		finish();
    }
    
	OnClickListener animSlide = new OnClickListener(){
		@Override
		public void onClick(View v) {
			LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) slider.getLayoutParams();
			int target = 0;
			//ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams)menuLinearLayout.getLayoutParams();
			//boolean isUp = layoutParams.bottomMargin == dipsToPixels(BOTTOM_MARGIN_UP)
			if(params.leftMargin == 0)
				target = -screenWidth;
			else if(params.leftMargin == -screenWidth)
				target = 0;
			TranslateAnimation ta = makeAnimation(params.leftMargin, target);
			if(ta != null)
				slider.startAnimation(ta);
		}
	};
	private TranslateAnimation makeAnimation(final int fromMargin, final int toMargin)
	{
		if(sliding)
			return null;
		sliding = true;
	    TranslateAnimation animation = new TranslateAnimation(0, (toMargin - fromMargin), 0, 0);
	    animation.setDuration(500);
	    animation.setAnimationListener(new Animation.AnimationListener()
	    {
	        public void onAnimationEnd(Animation animation)
	        {
	            // Cancel the animation to stop the menu from popping back.
	            slider.clearAnimation();

	            // Set the new bottom margin.
	            setLeftMargin(slider, toMargin);
	            sliding = false;
	        }

	        public void onAnimationStart(Animation animation) {}

	        public void onAnimationRepeat(Animation animation) {}
	    });
	    return animation;
	}
	
	private void setLeftMargin(View view, int bottomMarginInDips)
	{
	    ViewGroup.MarginLayoutParams layoutParams =    
	        (ViewGroup.MarginLayoutParams)view.getLayoutParams();
	    layoutParams.leftMargin = bottomMarginInDips;
	    view.requestLayout();
	}

	private int dipsToPixels(int dips)
	{
	    final float scale = getResources().getDisplayMetrics().density;
	    return (int)(dips * scale + 0.5f);
	}

}
