package com.dooweb.flip;

import java.io.File;

import org.json.simple.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.dooweb.flip.objects.CallbackContext;
import com.dooweb.flip.objects.DataObject;
import com.dooweb.flip.objects.HTTPStatus;
import com.loopj.android.image.SmartImageView;

public class Create extends Activity {
	//protected ScrollView layout = null;
	//protected LayoutInflater inflater = null;
	
    protected static final int REQUEST_PICK_IMAGE = 1;
    protected static final int REQUEST_PICK_CROP_IMAGE = 2;
	protected ImageView imageOnPhone = null;
	protected ImageView imageOnServer = null;
	protected TextView textOnPhone = null;
	protected TextView textOnServer = null;
	protected String idOnPhone = "";
	protected String idOnServer = "";
	protected String idImage = "";
	protected View layout = null;
	private Create myself = null;
	private boolean sending = false;
	protected View btn_launch = null;
	protected View v_launching = null;
	
	@Override
    public void onCreate(Bundle savedInstanceState){
    	myself = this;
		//Tools.alert(savedInstanceState.toString());
    	super.onCreate(savedInstanceState);
    	Bundle extras = getIntent().getExtras();
    	String newString;
    	if (savedInstanceState == null) {
    	    extras = getIntent().getExtras();
    	    if(extras == null) {
    	        newString= null;
    	    } else {
    	        newString= extras.getString("id");
    	    }
    	} else {
    	    newString= (String) savedInstanceState.getSerializable("id");
    	}
    	if(newString == null)
    		newString = "";
    	Log.v("create activity notif id",newString);
    	layout = (View) View.inflate(this, R.layout.create, null);
    	TextView text = (TextView) layout.findViewById(R.id.notif_id);
    	if(newString!="")
    	{
    		Notification.cancel(Integer.parseInt(newString));
    		text.setText("Notif id : "+newString);
    	}else
    		text.setText("");
    	setContentView(layout);

    	imageOnPhone = (ImageView) layout.findViewById(R.id.onPhone);
    	imageOnServer = (ImageView) layout.findViewById(R.id.onServer);

    	textOnPhone = (TextView) layout.findViewById(R.id.onphone_id);
    	textOnServer = (TextView) layout.findViewById(R.id.onserver_id);

    	btn_launch = (View) layout.findViewById(R.id.create_launch);
    	v_launching = (View) layout.findViewById(R.id.create_launching);
    	
    	layout.findViewById(R.id.create_launch).setOnClickListener(oclBtnCreate);
    	
    	layout.findViewById(R.id.create_add_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent externalActivityIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                externalActivityIntent.setType("image/*");
                startActivityForResult(externalActivityIntent, REQUEST_PICK_IMAGE);
            }
        });
    }
    protected void onResume() {
        super.onResume();
        MyApplication.setCurrentActivity(this);
    }
    protected void onPause() {
    	MyApplication.clearReferences(this);
        super.onPause();
    }
    protected void onDestroy() {        
    	MyApplication.clearReferences(this);
        super.onDestroy();
    }
    public String getImage(){
    	return idImage;
    }
    public void setImage(final String image){
		Log.v("setImage", image);
    	idImage = image;
		this.runOnUiThread(new Runnable() {
            public void run() {
        		myself.setOnServer("http://" + Network.host + ":" + Network.port + "/upload/"+image);
            }
        });
    }
    
    private void setSendingOn(){
    	sending = true;
    	btn_launch.setVisibility(View.GONE);
    	v_launching.setVisibility(View.VISIBLE);
    }
    
    private void setSendingOff(){
    	sending = false;
    	if(btn_launch != null)
    	{
    		btn_launch.setVisibility(View.GONE);
        	v_launching.setVisibility(View.VISIBLE);
    	}
    }
	
	OnClickListener oclBtnCreate = new OnClickListener() {
        @Override
        public void onClick(View v) {
        	if(sending)
        		return;
    		String desc = ((TextView) layout.findViewById(R.id.create_description)).getText().toString();
    		if(desc == "" || desc == null)
    		{
    			Tools.alert("DESCRIPTION");
    			return;
    		}
    		JSONObject data = new JSONObject();
    		data.put("description", desc);
    		data.put("image", getImage());
    		
    		//CallbackOneQuestion callback = new CallbackOneQuestion();
        	if(!desc.matches(""))
        	{
            	setSendingOn();
        		Network.http("/questions", "POST", data, new CallbackContext((Create) v.getContext()){
        			@Override
        			public void complete(DataObject data, HTTPStatus s){
        				setSendingOff();
        				if(s.error)
        					Log.v("Han MAN? Whata da error :", s.message);
        				else
        				{
        					Create c = (Create) origin;
        					//startActivity(new Intent(MyApplication.getAppContext(), MainActivity.class));
        					Intent new_intent = new Intent(MyApplication.getAppContext(), MainActivity.class);
        					new_intent.putExtra("page", "dashboard");
        					//Tools.reload_dashboard= true; 
        					startActivity(new_intent);
        					//Tools.main_activity.selectDashBoard();
        					if(c != null)
        					{
        						Log.v("Activity create", "destroy activity");
        						finish();
        					}
        				}
        			}
        		});
        	}
        	else
        		Tools.alert("DESCRIPTION is missing");
        }
    };
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		Log.v("test", "config changes");
	}
    
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        //ImageView imageOnPhone = null;
    	MyApplication.setCurrentActivity(this);
        Log.v("requestCode", String.valueOf(requestCode));
        Log.v("resultCode", String.valueOf(resultCode));
        Log.v("intent", String.valueOf(intent));
        switch (requestCode) {

	        case REQUEST_PICK_IMAGE:
	               if (RESULT_OK == resultCode) {
	            	   String picturePath = "";
	            	   try{
		            	   Log.v("truc", "icic");
		            	   Uri selectedImage = intent.getData();
		                   String[] filePathColumn = { MediaStore.Images.Media.DATA };
		           
		                   Cursor cursor = getContentResolver().query(selectedImage,
		                           filePathColumn, null, null, null);
		                   cursor.moveToFirst();
		           
		                   int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		                   picturePath = cursor.getString(columnIndex);
		                   cursor.close();
		                   Log.v("path", picturePath);
	            	   }catch(Exception e){
	            		   picturePath = "";
	            	   }
	            	   if(picturePath!="")
	            	   {
	            		   setOnPhone(picturePath);
	            		   startUpload();
	            	   }
	            	   else
	            		   Log.v("REQUEST_PICK_IMAGE", "no path found");
	               }
	               break;
	        case REQUEST_PICK_CROP_IMAGE:
	               Bitmap selectedImage = BitmapFactory.decodeFile(Environment
	                            .getExternalStorageDirectory() + "/temp.jpg");
	               imageOnPhone.setImageBitmap(selectedImage);
	               break;
        }
    }
    
    private void startUpload(){
    	//startActivity(new Intent(Tools.main_activity, Upload.class));
    	Upload upload = new Upload();
    	//onUploadSuccess callback = new onUploadSuccess();
    	CallbackContext callback = new CallbackContext(myself){
    		String res = "";
    		Create c = null;
    		@Override
    		public void complete(DataObject data, HTTPStatus s){
    			c = (Create) origin;
    			if(c != null)
    			{
    				res = data.getString();
    				setImage(res);
    			}
    		}
    	};
    	//callback.setIntent((Create) v.getContext());
    	if(idOnPhone!="")
    		upload.image(idOnPhone, callback);
    }
    public void setOnPhone(String path){
    	File imgFile = new  File(path);
        if(imgFile.exists()){
        	idOnPhone = path;
        	textOnPhone.setText("On phone name " + path);
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageOnPhone.setImageBitmap(myBitmap);
        }
    }
    public void setOnServer(String path){
    	/*File imgFile = new  File(path);
        if(imgFile.exists()){
        	idOnServer = path;
        	textOnServer.setText("On phone name " + path);
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageOnServer.setImageBitmap(myBitmap);
        }else
        	Log.v("c'est le bon endroit quand meme","donc ca va");*/
    	Log.v("on server path", path);
    	idOnServer = path;
    	textOnServer.setText("On phone name " + path);
    	//SmartImageView myImage = (SmartImageView) imageOnServer;

    	SmartImageView myImage = (SmartImageView) layout.findViewById(R.id.create_image_preview);
    	myImage.setVisibility(View.VISIBLE);
    	myImage.setImageUrl(path);
    }
}
