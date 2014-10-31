package com.dooweb.flip;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;

import com.dooweb.flip.objects.CallbackContext;
import com.dooweb.flip.objects.DataObject;
import com.dooweb.flip.objects.HTTPStatus;

public class TestSignature extends Fragment{
	
	protected ScrollView layout = null;
	protected LayoutInflater inflater = null;
	protected ViewGroup container = null;
	int count = 0;
	
	public TestSignature() {
    }

    @Override
    public ScrollView onCreateView(LayoutInflater main_inflater, ViewGroup main_container, Bundle savedInstanceState) {
        //View rootView = inflater.inflate(R.layout.ask, container, false);
        super.onCreate(savedInstanceState);
        inflater = main_inflater;
        container = main_container;
        layout = (ScrollView) inflater.inflate(R.layout.test_signature, null);
        Button button = (Button) layout.findViewById(R.id.button_test);
        button.setOnClickListener(oclBtnTesty);

        getActivity().setTitle("Signature");
        return layout;
    }
    
    OnClickListener oclBtnTesty = new OnClickListener(){

        @Override
        public void onClick(View v) {
        	JSONObject data = new JSONObject();
    		
    		JSONObject object2 = new JSONObject();
    		object2.put("Lapin", "Carotte");
    		object2.put("Singe", "Banane");
    		
    		JSONArray subdata = new JSONArray();
    		subdata.add("");
    		subdata.add("tester");
    		subdata.add("je cite \"être ou ne pas être, là est la question\"");
    		subdata.add("chelous %EA % ù *$£¤~ñ");
    		subdata.add(1);
    		subdata.add(2);
    		subdata.add(true);
    		subdata.add(object2);
    		subdata.add(null);
    		data.put("subdata_array", subdata);
    		
    		data.put("other", 12);
    		data.put("mode", "show");
    		
    		JSONObject object1 = new JSONObject();
    		object1.put("truc", "bidule");
    		object1.put("bidule", "chouette");
    		object1.put("utile", true);
    		object1.put("int", 12);
    		object1.put("double", 12.5);
    		object1.put("null", null);

    		object1.put("object", object2);
    		data.put("object1", object1);
    		data.put("object2", object2);
    		
    		JSONObject good = new JSONObject();
    		good.put("bidule", "chouette");

    		JSONObject subgood = new JSONObject();
    		subgood.put("nombre", 12314);

    		JSONArray euraie = new JSONArray();
    		euraie.add(1);
    		euraie.add(3);
    		euraie.add(4);
    		euraie.add(2);
    		euraie.add("12");
    		euraie.add(null);
    		euraie.add(true);
    		euraie.add("true");

    		good.put("subgood", subgood);
    		subgood.put("euraie", euraie);
    		
    		
    		Network.http("/questions", "POST", good, new CallbackContext(this){
    	    	@Override
    	        public void complete(DataObject data, HTTPStatus s){
    	    		Tools.alert("callback overridden " + data.getString());
    	    	}
    		});
        }
    };
	
}