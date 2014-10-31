package com.dooweb.flip;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;

import com.dooweb.flip.objects.CallbackInterface;
import com.dooweb.flip.objects.HTTPStatus;
import com.dooweb.flip.objects.Response;

public class Network extends BroadcastReceiver {
	
	public static String host = "lesitedesaliments.fr";//"5.39.95.85";//"91.121.66.94";//"lesitedesaliments.fr";
	public static Integer port = 80;//3000;
	
	public void onReceive(Context context, Intent intent) {
	    //super.onReceive(context, intent);
	    Log.d("app","Network connectivity change");
	    if(intent.getExtras()!=null) {
	        NetworkInfo ni=(NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
	        if(ni!=null && ni.getState()==NetworkInfo.State.CONNECTED) {
	            Log.i("app","Network "+ni.getTypeName()+" connected");
	        } else if(intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY,Boolean.FALSE)) {
	            Log.d("app","There's no network connectivity");
	        }
	    }
	}
	
	private static class AsyncHTTPRequest extends AsyncTask<Object, String, String>{
		
		private CallbackInterface listener;

	    public void setCallback(CallbackInterface listener){
	        this.listener=listener;
	    }
		
		protected void onPreExecute(){
			//Tools.alert("preExec");
		}
		
		protected void onProgressUpdate(Integer... progress) {
	        //setProgressPercent(progress[0]);
	    }

	    protected void onPostExecute(String result) {
	        //showDialog("Downloaded " + result + " bytes");
	    	//Tools.alert("Async request over " + result);
	    	Response res = new Response(result);
	    	listener.complete(res.getData(), res.getStatus());
	    }

		@Override
		protected String doInBackground(Object... parameters) {
			//publishProgress(50);
			try{
				Looper.prepare();
			}catch(Exception e){
				Log.v("async http task error","Looper prepare failed");
			}
			String path = parameters[0].toString();
			String method = parameters[1].toString();
			JSONObject data = (JSONObject) parameters[2];
			if(data == null)
				data = new JSONObject();
        	int timestamp = Tools.timestamp();
        	String signature = new Signature(data, path, method, timestamp).getValue();

        	//Log.v("Final async http signature", signature);
			String json = Network.fetch(path, method, data, timestamp, signature);
			//Log.v("async",json);
			//Looper.loop();
			return json;
		}
	}
	
	public static void http(String path, String method, JSONObject data, CallbackInterface callback){
		Log.v("Network", "launching async http request");
		HashMap params = new HashMap();
		params.put("path", path);
		params.put("method", method);
		params.put("data", data);
		
		AsyncHTTPRequest httpreq = new AsyncHTTPRequest();
		httpreq.setCallback(callback);
		httpreq.execute(path, method, data, callback);
	}
	
	public static String fetch (String path, String method, JSONObject data, int timestamp, String signature) {
		//if data is JSONObject : req.setEntity(new ByteArrayEntity(data.toString().getBytes("UTF8")));
		ConnectivityManager connectivityManager = (ConnectivityManager) MyApplication.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if(data==null){
			data = new JSONObject();
		}

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		String header_auth = "flip " + Login.getUser() + ":" + signature;
		String header_date = new Date((long) timestamp * 1000).toGMTString();
		Log.v("date", header_date);
		params.add(new BasicNameValuePair("payload", data.toString()));
		try{
			if(networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected()) {
				boolean wifi = networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
			    Log.v("NetworkState", "L'interface de connexion active est du Wifi : " + wifi);
			    
			    //StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			    //StrictMode.setThreadPolicy(policy);
			    
			    HttpResponse response;
		        if(method == "POST"){
		        	URI urlsda = new URI("http://"+ Network.host + ":" + Network.port + path);
				    HttpClient httpclient = new DefaultHttpClient();
				    HttpPost req = new HttpPost(urlsda);
				    try {
				    	req.setEntity(new UrlEncodedFormEntity(params, "utf-8"));
				    	//req.setEntity(new ByteArrayEntity(data.toString().getBytes("UTF8")));
				    	//req.setEntity(new ByteArrayEntity(data.toString().getBytes("US-ASCII")));
			        } catch (UnsupportedEncodingException e) {
			            e.printStackTrace();
			        }
				    req.setHeader("authorization", header_auth);
				    req.setHeader("date", header_date);
				    response = httpclient.execute(req);
		        }else if(method == "PUT"){
		        	URI urlsda = new URI("http://"+ Network.host + ":" + Network.port + path);
				    HttpClient httpclient = new DefaultHttpClient();
				    HttpPut req = new HttpPut(urlsda);
				    try {
				    	req.setEntity(new UrlEncodedFormEntity(params, "utf-8"));
				    	//req.setEntity(new ByteArrayEntity(data.toString().getBytes("UTF8")));
				    	//req.setEntity(new ByteArrayEntity(data.toString().getBytes("US-ASCII")));
			        } catch (UnsupportedEncodingException e) {
			            e.printStackTrace();
			        }
				    req.setHeader("authorization", header_auth);
				    req.setHeader("date", header_date);
				    response = httpclient.execute(req);
		        }else if(method == "DELETE"){
		        	String query = URLEncodedUtils.format(params, "utf-8");
		        	URI urlsda = URIUtils.createURI("http", Network.host, Network.port, path, query, null);
				    HttpClient httpclient = new DefaultHttpClient();
				    HttpDelete req = new HttpDelete(urlsda);
				    req.setHeader("authorization", header_auth);
				    req.setHeader("date", header_date);
				    response = httpclient.execute(req);
		        }else{
		        	String query = URLEncodedUtils.format(params, "utf-8");
		        	URI urlsda = URIUtils.createURI("http", Network.host, Network.port, path, query, null);
				    HttpClient httpclient = new DefaultHttpClient();
				    HttpGet req = new HttpGet(urlsda);
				    req.setHeader("Content-Type", "application/json; charset=UTF-8");
				    req.setHeader("authorization", header_auth);
				    req.setHeader("date", header_date);
				    response = httpclient.execute(req);
		        }
			    
			    
			    StatusLine statusLine = response.getStatusLine();
			    if(statusLine.getStatusCode() == HttpStatus.SC_OK){
			        ByteArrayOutputStream out = new ByteArrayOutputStream();
			        response.getEntity().writeTo(out);
			        out.close();
			        String responseString = out.toString();
			        Log.v("Network", "response OK");
			        return responseString;
			    } else{
			        ByteArrayOutputStream out = new ByteArrayOutputStream();
			        response.getEntity().writeTo(out);
			        out.close();
			        String responseString = out.toString();
			        Log.v("Network", "response KO");
			        return responseString;
			    }
			}else{

				JSONObject error_json = new JSONObject();
				error_json.put("code", HTTPStatus.NO_CONNECTION.getValue());
				error_json.put("message", HTTPStatus.NO_CONNECTION.getName());
				return error_json.toJSONString();
			}
		}catch(Exception e){
			JSONObject error_json = new JSONObject();
			error_json.put("code", HTTPStatus.REQUEST_ERROR.getValue());
			error_json.put("message", HTTPStatus.REQUEST_ERROR.getName());
			return error_json.toJSONString();
		}
	}
}