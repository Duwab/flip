package com.dooweb.flip;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import android.app.Fragment;
import android.content.res.Configuration;
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

public class Login extends Fragment {
	protected ScrollView layout = null;
	protected EditText login = null;
	protected EditText password = null;
	protected TextView status = null;
	protected TextView warning = null;
	protected LayoutInflater inflater = null;
	protected ViewGroup container = null;
	protected Login myself = null;
	protected Button button_login = null;
	protected Button button_disconnect = null;
	protected boolean fetching = false;
	protected CallbackInterface fetching_callback = null;
	protected static String user_token = "";
	protected static String password_SHA = "";
	protected static boolean valid_login = false;
	private static String CREDENTIALS_FILE = "credentials";
	int count = 0;
	
	public Login() {
    }
	
    public static void init(){
    	Map<String, String> previous = getStoredCredentials();
    	if(previous != null)
    	{
    		Log.v("previous map", previous.get("user") + " : " + previous.get("password"));
    		Login.setUser(previous.get("user"));
    		Login.setPassword(previous.get("password"));
    	}
    }

    @Override
    public ScrollView onCreateView(LayoutInflater main_inflater, ViewGroup main_container, Bundle savedInstanceState) {
        //View rootView = inflater.inflate(R.layout.ask, container, false);
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        inflater = main_inflater;
        container = main_container;
        layout = (ScrollView) inflater.inflate(R.layout.login, null);
        login = (EditText) layout.findViewById(R.id.input_login);
        password = (EditText) layout.findViewById(R.id.input_password);
        status = (TextView) layout.findViewById(R.id.login_status);
        warning = (TextView) layout.findViewById(R.id.login_warning_info);
        myself = this;
        button_login = (Button) layout.findViewById(R.id.button_login);
        button_login.setOnClickListener(oclBtnOk);
        button_disconnect = (Button) layout.findViewById(R.id.button_disconnect);
        button_disconnect.setOnClickListener(new OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	Tools.alert("disconnect");
	        	disconnect();
	        	uiFillPage(null);
	        }
        });

        getActivity().setTitle("Login");
        uiFillPage(null);
        return layout;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        //mDrawerToggle.onConfigurationChanged(newConfig);
    }
    
    public void uiFillPage(String warning_info){
    	Log.v("debug", "uifillpage");
    	if(Login.isValidLogin())
    	{
    		status.setText("You are actually logged under '" + Login.getUser() + "' user");
    		button_disconnect.setVisibility(View.VISIBLE);
    	}
    	else
    	{
    		status.setText("Enter your credentials");
    		button_disconnect.setVisibility(View.GONE);
    	}
    	if(Login.isLogged())
    		login.setText(Login.getUser());
    	else
    		password.setText("");
    	if(warning == null)
    		warning.setText("logged");
    	else
    		warning.setText(warning_info);
    }
    
    public static boolean isLogged(){
    	//is logged means that user entered credentials, but there may not have been any verification
    	if(user_token.length() != 0)
    		return true;
    	else
    		return false;
    }
    
    public static boolean isValidLogin(){
    	//is valid login means that the credentials have been verified
    	return valid_login;
    }
    
    public static void setValidLogin(boolean b){
    	valid_login = b;
    }
    
    public static String getUser(){
    	return user_token;
    }
    
    public static void setUser(String new_user){
    	Log.v("setUser","'"+new_user+"'");
    	if(new_user == null)
    		Log.v("setUser","'null'");
    	else
    		Log.v("setUser","'"+new_user+"' with length " + new_user.length());
    	if(new_user == null)
    		user_token = "";
    	else
    		user_token = new_user;
    }
    
    public static String getPassword(){
    	return password_SHA;
    }
    
    public static void setPassword(String new_password){
    	if(new_password == null)
    		password_SHA = "";
    	else
    		password_SHA = new_password;
    }
    
    public static void setValidPassword(String new_password){
    	setPassword(new_password);
    	setValidLogin(true);
    	changePassword();
    }
    
    public static void changePassword(){
    	String string;
    	valid_login = true;
    	Log.v("file directory",MyApplication.getAppContext().getFilesDir().toString());
    	File f = new File(MyApplication.getAppContext().getFilesDir(), CREDENTIALS_FILE);
    	if(!Login.isLogged())
    		return;
    	string = Login.getUser() + "," + Login.getPassword();
    	try{
    		FileOutputStream fos = new FileOutputStream(f);
        	fos.write(string.getBytes());
        	fos.close();
        	Log.v("changePassword", "no error");
    	}catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
    	Log.v("changePassword", "done");
    	getStoredCredentials();
    }
    
    public static void disconnect(){
    	Log.v("disconnect","true");
    	valid_login = false;
    	Login.setUser(null);
    	Login.setPassword(null);
    	File f = new File(MyApplication.getAppContext().getFilesDir(), CREDENTIALS_FILE);
        f.delete();
        Profile.flush();
    }
    
    public static boolean isFileExists() {
    	File f = new File(MyApplication.getAppContext().getFilesDir(), CREDENTIALS_FILE);
    	return f.exists();
    }

    public static Map<String, String> getStoredCredentials(){
    	if(!isFileExists())
    		return null;
    	Map<String, String> map = null;
    	File f = new File(MyApplication.getAppContext().getFilesDir(), CREDENTIALS_FILE);
		try{
        	BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
    		String previous = br.readLine();
    		br.close();
    		Log.v("login read previous files", previous);
    		if(previous != null)
    		{
    			map = new HashMap<String, String>();
    			String[] parts = previous.split(",");
    			if(parts.length == 2)
    			{
    				map.put("user", parts[0]);
        			map.put("password", parts[1]);
    			}
    		}
    	}catch (FileNotFoundException e) {
        	Log.v("erro","error");
            e.printStackTrace();
        }catch(IOException e){
        	Log.v("erro","error");
            e.printStackTrace();
        }catch(Exception e){
        	Log.v("erro","error");
        }
    	return map;
    }
    
    public void fetchLogin(){
    	Log.v("debug", "fetchLogin");
    	if(fetching)
    		return;
    	Log.v("debug", "start fetching");
    	fetching = true;
    	if(fetching_callback == null)
        	Log.v("debug", "callback IS null");
    	else
        	Log.v("debug", "callback not null");
		if(fetching_callback != null)
			fetching_callback.start();
		//Network.http("/profiles/"+Tools.user_token, "GET", null, new CallbackContext(myself){
		Profile.initCurrent(new CallbackContext(myself){
			@Override
			public void complete(DataObject data, HTTPStatus s){
	        	Log.v("debug", "complete");
				fetching = false;
				Login l = (Login) origin;
				Log.v("int "+String.valueOf(s.code), "msg "+s.message);
				String warning = null;
				if(s.error)
				{
					if(s.code != Integer.valueOf(HTTPStatus.NO_CONNECTION.getValue()))
						Login.disconnect();
					warning = s.message;
				}
    			else
    				Login.changePassword();
				if(l != null)
					l.uiFillPage(warning);
				if(fetching_callback != null)
					fetching_callback.complete(data, s);
			}
		});
    }
    
    public void setFetchingCallback(CallbackContext cc){
    	fetching_callback = cc;
    }
    
    OnClickListener oclBtnOk = new OnClickListener() {
        @Override
        public void onClick(View v) {
        	String u = login.getText().toString();
        	if(u == null || u.length() == 0)
        	{
        		Tools.alert("Fill user");
        		return;
        	}
        	Login.setUser(u);
    		Login.setPassword(Signature.sha_digest(password.getText().toString()));
        	fetchLogin();
        }
      };
}