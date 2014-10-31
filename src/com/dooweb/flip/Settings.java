package com.dooweb.flip;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;

public class Settings extends Fragment {

	protected ScrollView layout = null;
	protected LayoutInflater inflater = null;
	
	public Settings(){
		
	}
	
	@Override
    public ScrollView onCreateView(LayoutInflater main_inflater, ViewGroup main_container, Bundle savedInstanceState) {
        //View rootView = inflater.inflate(R.layout.ask, container, false);
        super.onCreate(savedInstanceState);
        inflater = main_inflater;
        layout = (ScrollView) inflater.inflate(R.layout.settings, null);
        getActivity().setTitle("Settings");
        return layout;
    }

}
