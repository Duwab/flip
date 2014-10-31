package com.dooweb.flip;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import android.util.Base64;
import android.util.Log;

public class Signature {
	
	private List<String> stringified = new ArrayList<String>();
	private String signature = "";
	private JSONParser jsonParser = new JSONParser();
	
	public Signature(JSONObject data, String path, String method, int timestamp){
		Signature2(data, path, method, timestamp);
		/*JSONObject dataClone = (JSONObject) data.clone();
		dataClone.put("path", path);
		dataClone.put("method", method);
		dataClone.put("timestamp", timestamp);
		stringifyAny(dataClone.toString(), "");
		sort();*/
	}
	
	public String getValue(){
		return signature;
	}
	
	private void stringifyAny(String json, String base){
		Log.v("any",json);
		try{
			char first = json.charAt(0);
			if(first == "[".charAt(0)){
				Log.v("any","parseArray");
				JSONArray jsonArray = (JSONArray) jsonParser.parse(json);
				Log.v("any","parseArray OK");
				stringifyArray(jsonArray, base);
			}else if(first == "{".charAt(0)){
				Log.v("any","parseObject");
				JSONObject jsonObject = (JSONObject) jsonParser.parse(json);
				Log.v("any","parseObject OK");
				stringifyObject(jsonObject, base);
			}else
				stringifyOther(json.toString(), base);		
		}catch(Exception e){
			Log.v("any","error");
			Log.v("any error message",e.getMessage());
		}
		
	}
	
	private void stringifyObject(JSONObject json, String base){
		Log.v("signature", "object"+json.toString());
		Iterator iterator = json.entrySet().iterator();
		Log.v("signature","object start while");
		while (iterator.hasNext()) {
			AbstractMap.Entry mapEntry = (AbstractMap.Entry) iterator.next();
			Log.v("signature","object next");
			//System.out.println("The key is: " + mapEntry.getKey() + ",value is :" + mapEntry.getValue());
			stringifyAny(json.get(mapEntry.getKey()).toString(), base+(base!=""?"object_":"")+mapEntry.getKey().toString()+"_");
		}
	}
	
	private void stringifyArray(JSONArray json, String base){
		Log.v("signature", "array"+json.toString());
		Iterator i = json.iterator();
		int index = 0;
		Log.v("signature", "array start while");
		while (i.hasNext()) {
			String innerObj = (String) i.next().toString();
			Log.v("signature", "array next");
			stringifyAny(innerObj, base+"array_"+index+"_");
			index++;
		}
	}
	
	private void stringifyOther(String json, String base){
		Log.v("signature", "other"+json);
		stringified.add(base+"="+json);
	}
	
	private void sort(){
		java.util.Collections.sort(stringified);
		String listString = "";
		for (String s : stringified)
		{
		    listString += s + "&";
		}
		signature = listString;
		Log.v("listString", listString);
	}
	
	
	
	
	
	
	
	
	/*
	 * 
	 * Ok this is it, let's 2
	 * 
	 */
	
	public void Signature2(JSONObject data, String path, String method, int timestamp){
		if(data == null)
			data = new JSONObject();
		JSONObject OBJ = new JSONObject();
		JSONObject dataClone = (JSONObject) data.clone();
		
		OBJ.put("uri", path.replace("lesitedesaliments.fr", "5.39.95.85:3000"));
		OBJ.put("method", method);
		//OBJ.put("timestamp", timestamp);
		OBJ.put("timestamp", timestamp);
		OBJ.put("user", Login.getUser());
		OBJ.put("payload", dataClone);

		Log.v("signature payload",dataClone.toString());
		Log.v("signature input",OBJ.toString());
		String res = stringifyAny2(OBJ.toJSONString());
		Log.v("signature ordered"," "+res);
		res = hashing(res);
		Log.v("Passwrod SHA512",Login.getPassword());
		Log.v("signature digested",res);
		
		Log.v("ecncoded value is ", Base64.encodeToString(res.getBytes(), Base64.DEFAULT));
		
		
		signature = res;
	}
	
	private String stringifyAny2(String json){
		String res = "";
		if(json != null)
			res = json;
		Log.v("stringifyAny2",json + " ");
		try{
			if(json == "")
				res = "\"\"";
			else if(json==null)
				return "{}";
			else
			{
				char first = json.charAt(0);
				if(first == "[".charAt(0)){
					JSONArray jsonArray = (JSONArray) jsonParser.parse(json);
					res = stringifyArray2(jsonArray);
				}else if(first == "{".charAt(0)){
					JSONObject jsonObject = (JSONObject) jsonParser.parse(json);
					res = stringifyObject2(jsonObject);
				}else
					res = stringifyOther2(json.toString());
			}
		}catch(Exception e){
			Log.v("any","error");
			Log.v("any error message"," "+e.getMessage());
		}
		return res;
		
	}
	
	private String stringifyObject2(JSONObject json){
		List<String> list = new ArrayList<String>();
		Iterator iterator = json.entrySet().iterator();
		while (iterator.hasNext()) {
			AbstractMap.Entry mapEntry = (AbstractMap.Entry) iterator.next();
			Object value = json.get(mapEntry.getKey());
			if(value==null)
				value = "{}";
			else
				value = value.toString();
			list.add("\""+mapEntry.getKey()+"\":"+stringifyAny2((String) value));
		}
		return sortObject(list);
	}
	
	private String sortObject(List<String> list){
		java.util.Collections.sort(list);
		String listString = "";
		Boolean first = true;
		for (String s : list)
		{
			if(first)
				first = false;
			else
				listString += ",";
			listString += s;
		}
		return "{"+listString+"}";
	}
	
	private String stringifyArray2(JSONArray json){
		List<String> list = new ArrayList<String>();
		Iterator i = json.iterator();
		while (i.hasNext()) {
			Object innerObj = (Object) i.next();
			if(innerObj == null)
				innerObj = "{}";
			else
				innerObj = innerObj.toString();
			Log.v("array", (String) innerObj);
			list.add(stringifyAny2((String) innerObj));
		}
		return sortArray(list);
	}
	
	private String sortArray(List<String> list){
		//java.util.Collections.sort(list);
		String listString = "";
		Boolean first = true;
		for (String s : list)
		{
			if(first)
				first = false;
			else
				listString += ",";
		    listString += s;
		}
		return "["+listString+"]";
	}
	
	private String stringifyOther2(String json){
		Log.v("json other", "= "+json);
		if(json==null)
			return "{}";
		else if(Tools.isNumeric(json) || json == "true" || json == "false")
			//return json;
			return "\""+json+"\"";
		else
			return "\""+json.replaceAll("\\\\", "\\\\\\\\").replaceAll("\n", "\\\\n").replaceAll("\t", "\\\\t").replaceAll("\"", "\\\\\"")+"\"";
	}
	
	public static String sha_digest (String input) {
		/*try
		{
			MessageDigest digester = MessageDigest.getInstance("SHA-512");
		    digester.update(input.getBytes("UTF-8"));
		    byte messageDigest[] = digester.digest();
		    StringBuffer hexString = new StringBuffer();
	        for (int i=0; i<messageDigest.length; i++)
	             hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
	        return hexString.toString();
		}catch(NoSuchAlgorithmException nsae)
		{
			Log.v("signature error", "nsae");
			return "";
		}catch(UnsupportedEncodingException uee)
		{
			Log.v("signature error", "uee");
			return "";
		}*/
		
		MessageDigest md;
        String message = input;
        try {
            md= MessageDigest.getInstance("SHA-512");
 
            md.update(message.getBytes());
            byte[] mb = md.digest();
            String out = "";
            for (int i = 0; i < mb.length; i++) {
                byte temp = mb[i];
                String s = Integer.toHexString(new Byte(temp));
                while (s.length() < 2) {
                    s = "0" + s;
                }
                s = s.substring(s.length() - 2);
                out += s;
            }
            return out;
 
        } catch (NoSuchAlgorithmException e) {
            System.out.println("ERROR: " + e.getMessage());
            return "";
        }
	}
	
	public static String hashing(String value){
		return hash_hmac("HmacSHA512", value, Login.getPassword());
	}
	
	public static String hash_hmac(String type, String value, String key){
		if(value == null || type == null || key == null)
			return "";
	    try {
	        javax.crypto.Mac mac = javax.crypto.Mac.getInstance(type);
	        javax.crypto.spec.SecretKeySpec secret = new javax.crypto.spec.SecretKeySpec(key.getBytes(), type);
	        mac.init(secret);
	        byte[] digest = mac.doFinal(value.getBytes());
	        StringBuilder sb = new StringBuilder(digest.length*2);
	        /*String s;
	        for (byte b : digest){
		        s = Integer.toHexString(new Byte(b));
		        if(s.length() == 1) sb.append('0');
		        sb.append(s);
	        }*/
	        String out = "";
            for (int i = 0; i < digest.length; i++) {
                byte temp = digest[i];
                String s = Integer.toHexString(new Byte(temp));
                while (s.length() < 2) {
                    s = "0" + s;
                }
                s = s.substring(s.length() - 2);
                out += s;
            }
	        return out;
	    } catch (Exception e) {
	        android.util.Log.v("TAG","Exception ["+e.getMessage()+"]", e);
	    }
        return "";
    }
}