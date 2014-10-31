package com.dooweb.flip.objects;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class DataObject {
	private Object json = null;
	public DataObject(Object jsonData){
		json = jsonData;
	}
	@Override
	public String toString(){
		return getString();
	}
	public String getString(){
		if(json == null)
			return "";
		if(json instanceof String)
			return (String) json;
		try{
			return json.toString();
		}catch(Exception e){
			return String.valueOf(json);
		}
	}
	public JSONArray getArray(){
		if(json instanceof JSONArray)
			return (JSONArray) json;
		return new JSONArray();
	}
	public JSONObject getJSON(){
		if(json instanceof JSONObject)
			return (JSONObject) json;
		return new JSONObject();
	}
	public Integer getInteger(){
		try{
			return Integer.parseInt(String.valueOf(json));
		}catch(Exception e){
			return 0;
		}
	}
}
