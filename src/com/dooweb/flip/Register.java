package com.dooweb.flip;

import org.json.simple.JSONObject;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.dooweb.flip.objects.CallbackContext;
import com.dooweb.flip.objects.CallbackInterface;
import com.dooweb.flip.objects.DataObject;
import com.dooweb.flip.objects.HTTPStatus;

public class Register extends Fragment {
	
	protected ScrollView layout = null;
	protected LayoutInflater inflater = null;
	protected EditText layout_name = null;
	protected EditText layout_password = null;
	protected EditText layout_confirm = null;
	protected Button layout_create = null;
	protected TextView layout_error = null;
	protected CallbackInterface fetching_callback = null;
	protected boolean fetching = false;
	protected String user = "";
	protected String password = "";
	
	public Register(){
		
	}
	
	@Override
    public ScrollView onCreateView(LayoutInflater main_inflater, ViewGroup main_container, Bundle savedInstanceState) {
        //View rootView = inflater.inflate(R.layout.ask, container, false);
        super.onCreate(savedInstanceState);
        inflater = main_inflater;
        layout = (ScrollView) inflater.inflate(R.layout.register, null);
        initLayout();
        return layout;
	}
	
	public void initLayout(){
		layout_name = (EditText) layout.findViewById(R.id.register_name);
		layout_password = (EditText) layout.findViewById(R.id.register_password);
		layout_confirm = (EditText) layout.findViewById(R.id.register_confirm);
		layout_create = (Button) layout.findViewById(R.id.register_create);
		layout_error = (TextView) layout.findViewById(R.id.register_error_message);
		
		layout_create.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){

				if(fetching || layout_name.getText() == null || layout_name.getText().length() == 0)
					return;
				fetching = true;

				if(fetching_callback != null)
					fetching_callback.start();

				user = layout_name.getText().toString();
				password = Signature.sha_digest(layout_password.getText().toString());
				
				JSONObject data = new JSONObject();
				data.put("fullname", user);
				data.put("tag", user);
				data.put("hash", password);
				data.put("image", "random.jpg");
				data.put("description", "I am beautiful");
				
				Log.v("Register POST", "data = " + data.toString());
				
				Network.http("/profiles", "POST", data, onCreate());
			}
		});
	}
	
	public CallbackContext onCreate(){
		return new CallbackContext(this){
			public void complete(DataObject data, HTTPStatus s){
				fetching = false;
				Log.v("data", " "+ data.toString());
				Log.v("status", s.code + " - " + s.message);
				layout_error.setText(s.message);
				if(!s.error)
				{
					Login.setUser(user);
					Login.setValidPassword(password);
					Profile.setProfileFromJSON(data.getJSON());
				}
				if(fetching_callback != null)
					fetching_callback.complete(data, s);
			}
		};
	}
    
    public void setFetchingCallback(CallbackContext cc){
    	fetching_callback = cc;
    }
}
