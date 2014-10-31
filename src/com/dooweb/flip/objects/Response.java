package com.dooweb.flip.objects;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import android.util.Log;

public class Response {
	
	private DataObject data = null;
	private HTTPStatus status = null;
	
	public Response(String res){
		Log.v("response", " "+res);
		try{
			JSONParser jsonParser = new JSONParser();
			JSONObject json = (JSONObject) jsonParser.parse(res);
			data = new DataObject(json.get("data"));
			status = new HTTPStatus(
					(Integer.parseInt(String.valueOf(json.get("code")))),
					(new DataObject(json.get("message")).toString()),
					(new DataObject(json.get("data")).toString())
					);
			Log.v("response", "5");
		}catch(Exception e){
			status = new HTTPStatus(400, "JSON Client Parse Error", null);
			data = new DataObject(null);
		}
	}
	public DataObject getData(){
		return data;
	}
	public HTTPStatus getStatus(){
		return status;
	}
}
