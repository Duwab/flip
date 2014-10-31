package com.dooweb.flip;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.dooweb.flip.objects.CallbackContext;
import com.dooweb.flip.objects.DataObject;
import com.dooweb.flip.objects.HTTPStatus;

public class Question{
	private long id = 0;
	private String original_json = "";
	private String author = "";
	private String description = "";
	private String date = "";
	private String url = null;
	public String country = "";
	public String region = "";
	public String lat = "";
	public String lon = "";
	private long count_yes = 0;
	private long count_no = 0;
	private Boolean valid = true;
	private Dashboard dashboard = null;
	private View view = null;
	private Boolean answer = null;
	private Boolean answering = false;
	protected Question myself = null; 
	//http://www.javacodegeeks.com/2013/10/android-json-tutorial-create-and-parse-json-data.html
	public Question(){
		//quand hydrater?
		myself = this;
	}
	public void setId(long _id){
		id = _id;
	}
	public long getId(){
		return id;
	}
	public void setDescription(String _description){
		description = _description;
	}
	public String getDescription(){
		return description;
	}
	public void setAuthor(String _author){
		author = _author;
	}
	public String getAuthor(){
		return author;
	}
	public void setDate(String _date){
		date = _date;
	}
	public String getDate(){
		return date;
	}
	public void setAnswer(Boolean value){
		answer = value;
		displayAnswer();
	}
	public void displayAnswer(){
		Log.v("debug", "7");
		if(view != null)
		{
			Log.v("debug", "8");
			// Answer
			View button_bar = (View) view.findViewById(R.id.buttons);
			Log.v("debug", "9");
			View answer_bar = (View) view.findViewById(R.id.answer);
			Log.v("debug", "10");
			TextView answer_text = (TextView) view.findViewById(R.id.answer_text);
			Log.v("debug", "11");
			if(answer == null)
			{
				button_bar.setVisibility(android.view.View.VISIBLE);
				answer_bar.setVisibility(android.view.View.GONE);
			}else
			{
				button_bar.setVisibility(android.view.View.GONE);
				answer_bar.setVisibility(android.view.View.VISIBLE);
				answer_text.setText("Your answer is " + (answer ? "YES" : "NO"));
			}
			Log.v("debug", "12");
			
			// Counters
			TextView cy = (TextView) view.findViewById(R.id.count_yes);
			Log.v("debug", "13");
			TextView cn = (TextView) view.findViewById(R.id.count_no);
			Log.v("debug", "14");
			cy.setText(String.valueOf(count_yes));
			cn.setText(String.valueOf(count_no));
		}
	}
	public Boolean getAnswer(){
		return answer;
	}
	public long getCountYes(){
		return count_yes;
	}
	public void setCountYes(long l){
		count_yes = l;
	}
	public long getCountNo(){
		return count_no;
	}
	public void setCountNo(long l){
		count_no = l;
	}
	public Boolean isValid(){
		return valid;
	}
	public View generateView(Context c, Dashboard d){
		Log.v("generate view for", original_json);
		view = (View) View.inflate(c, R.layout.question, null);
		dashboard = d;
    	Button yes = (Button) view.findViewById(R.id.yes);
    	yes.setOnClickListener(addItemClik);
    	Button no = (Button) view.findViewById(R.id.no);
    	no.setOnClickListener(addItemClik);
    	TextView name = (TextView) view.findViewById(R.id.name);
    	name.setText(author);
    	TextView desc = (TextView) view.findViewById(R.id.question);
    	desc.setText(description);
    	displayAnswer();
    	return view;
	}
	private OnClickListener addItemClik = new OnClickListener() {
        @Override
        public void onClick(View v) {
        	if(answering)
        		return;
        	answering = true;
        	JSONObject data = new JSONObject();
        	data.put("value", (v.getId()==R.id.yes ? "true" : "false"));
        	Network.http("/questions/"+id+"/answers", "POST", data, new CallbackContext(myself){
        		@Override
        		public void complete(DataObject data, HTTPStatus s){
        			Question question = (Question) origin;
        			if(question == null)
        				return;
        			JSONObject json = data.getJSON();
        			Log.v("question answer complete. res =>", json.toString());
        			Boolean value = null;
        			value = (Boolean) json.get("value");
        			JSONObject update = (JSONObject) json.get("question");
        			if(String.valueOf(update) != "null")
        			{
        				question.count_yes = Long.parseLong(update.get("yes").toString());
        				question.count_no = Long.parseLong(update.get("no").toString());
        			}
        			if(value != null && question != null)
        			{
        				question.setAnswer(value);    				
        			}
    				question.dashboard.addItem();
        		}
        	});
        }
	};
	public static Question parseJSON(String json){
		//Tools.alert("Parse json question = "+json);
		//Log.v("question", json);
		Question q = new Question();
		q.original_json = json;
		try{
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject) jsonParser.parse(json);
			//JSONArray data = (JSONArray) jsonObject.get("data");
			if(jsonObject.get("question") != null)
				jsonObject = (JSONObject) jsonObject.get("question");
			q.setId(Long.parseLong(jsonObject.get("id").toString()));
			q.setDescription(String.valueOf(jsonObject.get("description")));
			q.setDate(String.valueOf(jsonObject.get("date")));
			q.setAuthor(String.valueOf(jsonObject.get("author")));
			q.setCountYes(Long.parseLong(jsonObject.get("yes").toString()));
			q.setCountNo(Long.parseLong(jsonObject.get("no").toString()));
			q.setAnswer((Boolean) jsonObject.get("answer"));
		}catch(Exception e)
		{
			q.valid = false;
		}
		
		return q;
	}
	public static List<Question> parseJSONList(JSONArray json){
		List<Question> list = new ArrayList<Question>();
		if(json == null)
			return list;
		Iterator i = json.iterator();
		while (i.hasNext()) {
			list.add(Question.parseJSON((String) i.next().toString()));
		}
		return list;
	}
}