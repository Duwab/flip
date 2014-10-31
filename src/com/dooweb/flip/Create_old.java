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

public class Create_old extends Activity {
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
	protected View layout = null;
	private Create_old myself = null;
	
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
    	
    	layout.findViewById(R.id.create_launch).setOnClickListener(oclBtnCreate);
    	
    	layout.findViewById(R.id.upload).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	//startActivity(new Intent(Tools.main_activity, Upload.class));
            	Upload upload = new Upload();
            	//onUploadSuccess callback = new onUploadSuccess();
            	CallbackContext callback = new CallbackContext((Create) v.getContext()){
            		String res = "";
            		Create c = null;
            		@Override
            		public void complete(DataObject data, HTTPStatus s){
            			c = (Create) origin;
            			if(c != null)
            			{
            				res = data.getString();
                    		c.runOnUiThread(new Runnable() {
                                public void run() {
                            		Log.v("callback result", res);
                            		c.setOnServer("http://" + Network.host + ":" + Network.port + "/upload/"+res);
                                }
                            });
            			}
            		}
            	};
            	//callback.setIntent((Create) v.getContext());
            	if(idOnPhone!="")
            		upload.image(idOnPhone, callback);
            	else
            		upload.image("/mnt/sdcard/DCIM/itw19.jpg", callback);
            }
        });
    	
    	
    	layout.findViewById(R.id.upload_file).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an intent that asks the user to pick a photo, but using
                // FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET, ensures that relaunching
                // the application from the device home screen does not return
                // to the external activity.
                Intent externalActivityIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //externalActivityIntent.setType("image/*");
                //externalActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                //startActivity(externalActivityIntent);
                externalActivityIntent.setType("image/*");
                //File tempFile = new File(Environment.getExternalStorageDirectory() + "/temp.jpg");
                //Uri tempUri = Uri.fromFile(tempFile);
                //externalActivityIntent.putExtra(MediaStore.EXTRA_OUTPUT,tempUri);
                //externalActivityIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
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
	
	OnClickListener oclBtnCreate = new OnClickListener() {
        @Override
        public void onClick(View v) {
    		String desc = ((TextView) layout.findViewById(R.id.create_description)).getText().toString();
    		if(desc == "" || desc == null)
    		{
    			Tools.alert("DESCRIPTION");
    			return;
    		}
    		JSONObject data = new JSONObject();
    		data.put("description", desc);
    		
    		//CallbackOneQuestion callback = new CallbackOneQuestion();
        	if(!desc.matches(""))
        		Network.http("/questions", "POST", data, new CallbackContext((Create) v.getContext()){
        			@Override
        			public void complete(DataObject data, HTTPStatus s){
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
        	else
        		Tools.alert("DESCRIPTION");
        }
    };
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		Log.v("test", "config changes");
	}
    
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        //ImageView imageOnPhone = null;
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
	            		   setOnPhone(picturePath);
	            	   else
	            		   Log.v("REQUEST_PICK_IMAGE", "no path found");
	            	   /*
	            	   Uri imageUri = intent.getData();
	            	   Log.v("truc", "1");
	            	   Bitmap bitmap;
	            	   try {
	            		   Log.v("truc", "2");
	            		   bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
	            		   //Bitmap selectedImage = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/temp.jpg");
	            		   Log.v("truc", "3 ");
	            		   //imageOnPhone.setImageBitmap(selectedImage);
	            		   imageOnPhone.setImageBitmap(bitmap);
	            		   Log.v("truc", "4");
	            	   } catch(Exception e){
	            		   Log.v("any error message","truc");
	            	   }
	            		   /*catch (FileNotFoundException e) {
	            		   Log.v("truc", "4");
	            	   } catch (IOException e) {
	            		   Log.v("truc", "5");
	            	   }
	                     /*Uri imageUri = intent.getData();
	                     Bitmap bitmap;
	                     try {
	                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
	                            imageOnPhone.setImageBitmap(bitmap);
	                            //Tools.alert(imageUri.toString());
	                     } catch (FileNotFoundException e) {
	                            // TODO Auto-generated catch block
	                            e.printStackTrace();
	                     } catch (IOException e) {
	                            // TODO Auto-generated catch block
	                            e.printStackTrace();
	                     }*/
	
	               }
	               break;
	        case REQUEST_PICK_CROP_IMAGE:
	               Bitmap selectedImage = BitmapFactory.decodeFile(Environment
	                            .getExternalStorageDirectory() + "/temp.jpg");
	               imageOnPhone.setImageBitmap(selectedImage);
	               break;
        }
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

    	SmartImageView myImage = (SmartImageView) layout.findViewById(R.id.onServer);
    	myImage.setImageUrl(path);
    }
    
    /*public class onUploadSuccess implements CallbackInterface{
    	private Create intent;
    	private String res = "";
    	@Override
        public void complete(String result){
    		res = result;
    		intent.runOnUiThread(new Runnable() {
                public void run() {
            		Log.v("callback result", res);
            		intent.setOnServer("http://5.39.95.85:3000/upload/"+res);
                }
            });
    	}
    	public void setIntent(Create activity){
    		intent = activity;
    	}
    	public Create getIntent(){
    		return intent;
    	}
    }*/
    /*public ScrollView onCreateView(LayoutInflater main_inflater, ViewGroup main_container, Bundle savedInstanceState) {
        //View rootView = inflater.inflate(R.layout.ask, container, false);
        super.onCreate(savedInstanceState);
        inflater = main_inflater;
        container = main_container;
        layout = (ScrollView) inflater.inflate(R.layout.create, null);
        getActivity().setTitle("Dashboard");
        return layout;
    }*/
}
