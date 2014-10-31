package com.dooweb.flip;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;

public class TestNotification extends Fragment {

	protected ScrollView layout = null;
	protected LayoutInflater inflater = null;
	
	public TestNotification(){
		
	}
	
	@Override
    public ScrollView onCreateView(LayoutInflater main_inflater, ViewGroup main_container, Bundle savedInstanceState) {
        //View rootView = inflater.inflate(R.layout.ask, container, false);
        super.onCreate(savedInstanceState);
        inflater = main_inflater;
        layout = (ScrollView) inflater.inflate(R.layout.test_notification, null);
        Button buttonNotif = (Button) layout.findViewById(R.id.button_create_notification);
        buttonNotif.setOnClickListener(oclBtnNotif);

        getActivity().setTitle("Test Notification");
        return layout;
    }

    OnClickListener oclBtnNotif = new OnClickListener() {
        @Override
        public void onClick(View v) {
        	new Notification("Hello world!");
        }
    };
}
