package com.dooweb.flip;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.dooweb.flip.objects.CallbackContext;
import com.dooweb.flip.objects.DataObject;
import com.dooweb.flip.objects.HTTPStatus;

public class Profile extends Fragment {
	protected ScrollView layout = null;
	protected LayoutInflater inflater = null;
	protected ViewGroup container = null;
	protected static String fullname = null;
	protected static String tag = null;
	protected List<Question> q_previous = new ArrayList<Question>();
	protected TableLayout table_previous = null;
	protected ProgressBar msg_loading = null;
	protected TextView msg_default = null;
	protected View page_content = null;
	protected boolean fetching = false; 
	
	public Profile() {
    }

    @Override
    public ScrollView onCreateView(LayoutInflater main_inflater, ViewGroup main_container, Bundle savedInstanceState) {
        //View rootView = inflater.inflate(R.layout.ask, container, false);
        super.onCreate(savedInstanceState);
        inflater = main_inflater;
        container = main_container;
        layout = (ScrollView) inflater.inflate(R.layout.profile, null);
        table_previous = (TableLayout) layout.findViewById(R.id.profile_previous_answered);
        Button see_slides = (Button) layout.findViewById(R.id.btn_see_slides);
        see_slides.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	startActivity(new Intent(MyApplication.getCurrentActivity(), TestProfile.class));
            }
        });
        getActivity().setTitle("Profile");
        initParts();
        fetchPage();
        return layout;
    }
    
    public static void initCurrent(CallbackContext cc){
    	if(Login.isLogged())
    	{
    		Network.http("/profiles/"+Login.getUser(), "GET", null, new CallbackContext(cc){
            	@Override
            	public void complete(DataObject data, HTTPStatus err){
            		Profile.setProfileFromJSON(data.getJSON());
            		CallbackContext new_cc = (CallbackContext) origin;
            		if(new_cc != null)
            			new_cc.complete(data, err);
            	}
            });
    	}else
    	{
    		flush();
    		cc.complete(new DataObject(null), new HTTPStatus(400, "Not connected", null));
    		Log.v("initCurrent", "not togged");
    	}
    }
    
    public static void setProfileFromJSON(JSONObject json){
		fullname = String.valueOf(json.get("fullname"));
		tag = String.valueOf(json.get("tag"));
    }
    
    public void fetchPage(){
    	if(fetching)
    		return;
    	fetching = true;
    	showLoader();
        Network.http("/profiles/"+Login.getUser(), "GET", null, new CallbackContext(this){
        	@Override
        	public void complete(DataObject data, HTTPStatus s){
            	fetching = false;
        		if(s.error)
        			showMessage(s.message);
        		else
        		{
            		JSONObject json = data.getJSON();
            		Profile.setProfileFromJSON(json);
            		initPrevious(json);
            		//Tools.alert(json.toString());
            		Log.v("profile response", data.getString());
            		Profile profile = (Profile) origin;
            		((TextView) profile.layout.findViewById(R.id.fullname)).setText(fullname);
            		((TextView) profile.layout.findViewById(R.id.tag)).setText(tag);
            		showContent();
        		}
        	}
        });
    }
    
    public void initPrevious(JSONObject json){
    	JSONArray l = (JSONArray) (new DataObject(json.get("answers"))).getJSON().get("latest");
    	Log.v("initprevious", l.toString());
    	q_previous = Question.parseJSONList(l);
    	for(Question q : q_previous){
    		table_previous.addView(generateQuestion(q));
    	}
    }
    
    public View generateQuestion(Question q){
    	TableRow tr = (TableRow) TableRow.inflate(MyApplication.getAppContext(), R.layout.profile_question, null);
    	Log.v("generateQuestion",q.getDescription() + " : " + q.getDate());
    	((TextView) tr.findViewById(R.id.profile_question_question)).setText(q.getDescription());
    	((TextView) tr.findViewById(R.id.profile_question_date)).setText(Tools.date_format(q.getDate(), null));
    	return tr;
    }
    
    public static void flush(){
    	fullname = null;
    	tag = null;
    }
    
    public void initParts(){
        msg_loading = (ProgressBar) layout.findViewById(R.id.profile_loader);
        msg_default = (TextView) layout.findViewById(R.id.profile_message_default);
        page_content = (View) layout.findViewById(R.id.profile_content);
        
        msg_default.setOnClickListener(new OnClickListener(){
        	@Override
        	public void onClick(View v){
        		fetchPage();
        	}
        });
    }
    
    public void showLoader(){
    	hideParts();
    	msg_loading.setVisibility(View.VISIBLE);
    }
    
    public void showMessage(String msg){
    	hideParts();
    	msg_default.setText(msg);
    	msg_default.setVisibility(View.VISIBLE);
    }
    
    public void showContent(){
    	hideParts();
    	page_content.setVisibility(View.VISIBLE);
    }
    
    public void hideParts(){
    	msg_default.setVisibility(View.GONE);
    	page_content.setVisibility(View.GONE);
    	msg_loading.setVisibility(View.GONE);
    }
    
}
