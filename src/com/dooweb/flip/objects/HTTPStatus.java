package com.dooweb.flip.objects;

import org.apache.http.message.BasicNameValuePair;

import android.util.Log;

public class HTTPStatus {

	public static BasicNameValuePair NO_CONNECTION = new BasicNameValuePair("No Connection", "700");
	public static BasicNameValuePair REQUEST_ERROR = new BasicNameValuePair("Network Request Error", "701"); 
	
	public int code;
	public String message = "";
	public Boolean error = false;
	
	public HTTPStatus(int c, String msg, String data){
		Log.v("HTTPStatus inputs", msg + "   " + data);
		code = c;
		if(msg != null && msg != "")
			message = msg;
		else if(data != null && data != "")
			message = data;
		else
			message = "";
		if(c >=300)
			error = true;
	}
	@Override
	public String toString(){
		return "HTTPStatus : code = " + code + " | message = " + message;
	}
}
