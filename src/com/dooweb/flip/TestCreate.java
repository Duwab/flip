package com.dooweb.flip;

import java.util.List;

import org.json.simple.JSONObject;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;

import com.dooweb.flip.objects.CallbackContext;
import com.dooweb.flip.objects.CallbackInterface;
import com.dooweb.flip.objects.DataObject;
import com.dooweb.flip.objects.HTTPStatus;

public class TestCreate extends Fragment {
	protected ScrollView layout = null;
	protected EditText description = null;
	protected EditText input_id = null;
	protected LayoutInflater inflater = null;
	protected ViewGroup container = null;
	int count = 0;
	
	public TestCreate() {
    }

    @Override
    public ScrollView onCreateView(LayoutInflater main_inflater, ViewGroup main_container, Bundle savedInstanceState) {
        //View rootView = inflater.inflate(R.layout.ask, container, false);
        super.onCreate(savedInstanceState);
        inflater = main_inflater;
        container = main_container;
        layout = (ScrollView) inflater.inflate(R.layout.test_create, null);
        description = (EditText) layout.findViewById(R.id.input_description);
        input_id = (EditText) layout.findViewById(R.id.input_questionid);
        Button buttonNotif = (Button) layout.findViewById(R.id.button_create_notification);
        Button button = (Button) layout.findViewById(R.id.button_create);
        Button button3 = (Button) layout.findViewById(R.id.button_getquestion);
        Button buttonTes2 = (Button) layout.findViewById(R.id.button_testarrayquestion);
        buttonNotif.setOnClickListener(oclBtnNotif);
        button.setOnClickListener(oclBtnCreate);
        button3.setOnClickListener(oclBtnId);
        buttonTes2.setOnClickListener(oclBtnLast);

        getActivity().setTitle("Dashboard");
        return layout;
    }
    OnClickListener oclBtnNotif = new OnClickListener() {
        @Override
        public void onClick(View v) {
        	new Notification("Hello world!");
        }
    };
    OnClickListener oclBtnCreate = new OnClickListener() {
        @Override
        public void onClick(View v) {
          //Tools.alert("click " + login.getText().toString() + " " + password.getText().toString());
        	//List<NameValuePair> data = new ArrayList<NameValuePair>(3);
    		//data.add(new BasicNameValuePair("description", description.getText().toString()));
    		String desc = description.getText().toString();
    		
    		JSONObject data = new JSONObject();
    		data.put("description", desc);
    		
    		CallbackOneQuestion callback = new CallbackOneQuestion();
        	if(!desc.matches(""))
        		Network.http("/questions", "POST", data, callback);
        	else
        		Tools.alert("DESCRIPTION");
        }
    };
    
    OnClickListener oclBtnId = new OnClickListener() {
        @Override
        public void onClick(View v) {
        	String id = input_id.getText().toString();
        	CallbackOneQuestion callback = new CallbackOneQuestion();
        	if(Tools.isNumeric(id))
        		Network.http("/questions/"+id, "GET", null, callback);
        	else if(id=="")
        		Tools.alert("FILL THE ID!");
        	else
        		Tools.alert("I NEED A NUMBER");
        }
    };

    OnClickListener oclBtnLast = new OnClickListener() {
        @Override
        public void onClick(View v) {
        	CallbackListQuestion callback = new CallbackListQuestion(this);
    		Network.http("/questions", "GET", null, callback);
        }
    };
    
    public class CallbackOneQuestion implements CallbackInterface{
    	@Override
        public void complete(DataObject data, HTTPStatus s){
    		Tools.alert(data.getString());
    	}

		@Override
		public void progress(double percent) {
		}

		@Override
		public void start() {
		}
    }
    
    public class CallbackListQuestion extends CallbackContext{
    	public CallbackListQuestion(Object ctx) {
			super(ctx);
		}

		@Override
        public void complete(DataObject data, HTTPStatus s){
    		Tools.alert(data.getString());
    		List<Question> qList = Question.parseJSONList(data.getArray());
			Tools.alert("There is " + qList.size() + " questions");
    	}
    }
}