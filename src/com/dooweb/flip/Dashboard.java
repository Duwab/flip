package com.dooweb.flip;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.GridLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.dooweb.flip.objects.CallbackContext;
import com.dooweb.flip.objects.DataObject;
import com.dooweb.flip.objects.HTTPStatus;

public class Dashboard extends Fragment {
	protected ScrollView layout = null;
	protected LinearLayout scroll = null;
	protected LayoutInflater inflater = null;
	protected ViewGroup container = null;
	protected ProgressBar msg_loading = null;
	protected TextView msg_no_more = null;
	protected TextView msg_no_connection = null;
	protected TextView msg_default = null;
	//protected static boolean cache = false;
	protected boolean reload = false;
	protected List<Question> q_queue = new ArrayList<Question>();
	protected List<Question> q_done = new ArrayList<Question>();
	private Boolean fetching_new  = false;
	
	public Dashboard() {
        // Empty constructor required for fragment subclasses
		//https://developer.android.com/training/implementing-navigation/nav-drawer.html#DrawerLayout
		//https://developer.android.com/design/patterns/navigation-drawer.html
		//http://developer.android.com/reference/android/app/Fragment.html
    }

    @Override
    public ScrollView onCreateView(LayoutInflater main_inflater, ViewGroup main_container, Bundle savedInstanceState) {
        setRetainInstance(true);
        if(layout != null && !reload)
        {
        	Log.v("debug Dashboard", "no reload");
        	return layout;
        }
    	Log.v("debug Dashboard", "reload");
        super.onCreate(savedInstanceState);
        inflater = main_inflater;
        container = main_container;
        layout = (ScrollView) inflater.inflate(R.layout.dashboard, null);
        scroll = (LinearLayout) layout.findViewById(R.id.scroll);
        initMessages();
        addItem();
        getActivity().setTitle("Dashboard");
        return layout;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putString("is_set", String.valueOf(true));
        //cache = true;
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        //super.onConfigurationChanged(newConfig);
    }
    private void getNewItems(){
    	Log.v("debug Dashboard", "getNewItems");
    	if(fetching_new)
    		return;
    	this.setFetchingNew(true);
    	Network.http("/questions", "GET", null, new CallbackContext(this){
    		@Override
    		public void complete(DataObject data, HTTPStatus s){
    			super.complete(data, s);
    			Log.v("new question", data.toString());
    			Dashboard d = (Dashboard) origin;
    			if(d != null)
    			{
    				setFetchingNew(false);
        			if(!s.error)
        			{
        				List<Question> newquestions = Question.parseJSONList(data.getArray());
        				newquestions.addAll(d.q_queue);
            			q_queue = newquestions;
            			syncQueueDone();
            			if(q_queue.size() > 0)
            				addItem();
            			else
            				showMessage(msg_no_more);
        			}else
        			{
        				setDefaultMessage(s.message);
        				//Tools.alert("it says:\n\"GO FUCK YOURSELF : " + s.message + "\"");
        			}
    			}
    			
    		}
    	});
    }
    
    public void syncQueueDone(){
    	int i = 0;
    	List<Question> temp = new ArrayList<Question>();
    	while(i < q_queue.size())
    	{
    		if(!isQuestionDone(q_queue.get(i)))
    			temp.add(q_queue.get(i));
    		i++;
    	}
    	q_queue = temp;
    }
    
    public boolean isQuestionDone(Question q){
    	int i = 0;
    	while(i < q_done.size())
    	{
    		if(q_done.get(i).getId() == q.getId()){
    			return true;
    		}
    		i++;
    	}
    	return false;
    }
    
    public void addItem(){
    	if(q_queue.size() == 0)
    		getNewItems();
    	else
    	{
    		Question q = q_queue.get(0);
    		q_queue.remove(0);
            if(q.isValid())
            {
        		q_done.add(q);
	    		View v = q.generateView(MyApplication.getCurrentActivity(), this);
	            scroll.addView(v, 0);
	            expand(v);
	            if(q.getAnswer() != null)
	            	addItem();
            }else
            	addItem();
    	}
    }
    
    public static void expand(final View v) {
    	//thanks to http://stackoverflow.com/questions/4946295/android-expand-collapse-animation
        v.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        final int targtetHeight = v.getMeasuredHeight();

        v.getLayoutParams().height = 0;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? LayoutParams.WRAP_CONTENT
                        : (int)(targtetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        //a.setDuration((int)(targtetHeight / v.getContext().getResources().getDisplayMetrics().density));
        a.setDuration(750);
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
    	final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }
    
    public void initMessages(){
        msg_loading = (ProgressBar) layout.findViewById(R.id.dashboard_loading_question);
        msg_no_more = (TextView) layout.findViewById(R.id.dashboard_message_no_more);
        msg_no_connection = (TextView) layout.findViewById(R.id.dashboard_message_no_connection);
        msg_default = (TextView) layout.findViewById(R.id.dashboard_message_default);
        
        msg_no_more.setOnClickListener(new OnClickListener(){
        	@Override
        	public void onClick(View v){
        		getNewItems();
        	}
        });
        
        msg_no_connection.setOnClickListener(new OnClickListener(){
        	@Override
        	public void onClick(View v){
        		getNewItems();
        	}
        });
        
        msg_default.setOnClickListener(new OnClickListener(){
        	@Override
        	public void onClick(View v){
        		getNewItems();
        	}
        });
        
        hideMessages();
    }
    
    public void setFetchingNew(Boolean b){
    	fetching_new = b;
    	if(b)
    		showMessage(msg_loading);
    	else
    		hideMessages();
    }
    
    public void setDefaultMessage(String msg){
    	hideMessages();
    	msg_default.setText(msg);
    	if(msg != "" && msg != null)
    		showMessage(msg_default);
    }
    
    public void hideMessages(){
    	msg_loading.setVisibility(View.GONE);
    	msg_no_connection.setVisibility(View.GONE);
    	msg_no_more.setVisibility(View.GONE);
    	msg_default.setVisibility(View.GONE);
    }
    
    public void showMessage(View msg_v){
    	hideMessages();
    	msg_v.setVisibility(View.VISIBLE);
    }
}