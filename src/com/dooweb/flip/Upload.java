package com.dooweb.flip;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;
import java.util.regex.Pattern;

import android.app.ProgressDialog;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dooweb.flip.objects.CallbackInterface;
import com.dooweb.flip.objects.Response;
  
public class Upload {
     
    TextView messageText;
    Button uploadButton;
    int serverResponseCode = 0;
    ProgressDialog dialog = null;
    final String uploadPath = "/upload";
    final String uploadMethod = "POST";
    public static CallbackInterface final_callback = null;
    private String initialFileName = "itw19.jpg";
    
    public Upload(){
    	
    }
    
    public String image(String file, CallbackInterface callback){
    	
    	//uploadPath = "/upload";
    	
    	final_callback = callback;
    	
    	dialog = ProgressDialog.show(MyApplication.getCurrentActivity(), "", "Uploading file...", true);
        
    	initialFileName = file;
    	
        new Thread(new Runnable() {
            public void run() {
            	MyApplication.getCurrentActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            //messageText.setText("uploading started.....");
                        	Log.v("upload info", "uploading started");
                        }
                    });                      
               
                 uploadFile(initialFileName, final_callback);
                                          
            }
          }).start();
    	return "ok";
    }
      
    public int uploadFile(String sourceFileUri, CallbackInterface callback) {
           
           
          String fileName = sourceFileUri;
          Log.v("fileName", fileName);
          String[] parts = fileName.split("\\.");
          Log.v("parts.length", String.valueOf(parts.length));
          fileName = "file";
          int i = 0;
          while(i<parts.length-2)
          {
        	  fileName += parts[i].replaceAll("(\\W|_)", "");
              Log.v("parts[i]", parts[i]);
        	  i++;
          }
          if(parts.length > 0)
          {
              Log.v("parts[parts.length-1]", parts[parts.length-1]);
        	  fileName += ".";
        	  fileName += parts[parts.length-1];
          }
          //fileName = fileName.replaceAll("(\\W|_)", "");
  
          HttpURLConnection conn = null;
          DataOutputStream dos = null;  
          String lineEnd = "\r\n";
          String twoHyphens = "--";
          String boundary = "*****";
          int bytesRead, bytesAvailable, bufferSize;
          byte[] buffer;
          int maxBufferSize = 1 * 1024 * 1024; 
          File sourceFile = new File(sourceFileUri); 
           
          if (!sourceFile.isFile()) {
               
               dialog.dismiss(); 
                
               Log.e("uploadFile", "Source File not exist :"
                                   +fileName);
                
               MyApplication.getCurrentActivity().runOnUiThread(new Runnable() {
                   public void run() {
                       //messageText.setText("Source File not exist :"+uploadFilePath + "" + uploadFileName);
                       Log.v("upload info", "Source File not exist :");
                   }
               }); 
                
               return 0;
            
          }
          else
          {
               try { 
                    
                   // Auth and timestamp
            	   int timestamp = Tools.timestamp();
            	   String signature = new Signature(null, uploadPath, uploadMethod, timestamp).getValue();
            	   String header_auth = "flip " + Login.getUser() + ":" + signature;
           		   String header_date = new Date((long) timestamp * 1000).toGMTString();
            	   
            	   // open a URL connection to the Servlet
                   FileInputStream fileInputStream = new FileInputStream(sourceFile);
                   URL url = new URL("http://" + Network.host + ":" + Network.port + uploadPath);
                   //URL url = new URL("http://5.39.95.85:3000" + uploadPath);
                   
                   // Open a HTTP  connection to  the URL
                   conn = (HttpURLConnection) url.openConnection();
                   conn.setDoInput(true); // Allow Inputs
                   conn.setDoOutput(true); // Allow Outputs
                   conn.setUseCaches(false); // Don't use a Cached Copy
                   conn.setRequestMethod(uploadMethod);
                   conn.setRequestProperty("Connection", "Keep-Alive");
                   conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                   conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                   conn.setRequestProperty("uploaded_file", fileName);
                   conn.setRequestProperty("authorization", header_auth);
                   conn.setRequestProperty("date", header_date); 
                   
                   dos = new DataOutputStream(conn.getOutputStream());
          
                   dos.writeBytes(twoHyphens + boundary + lineEnd); 
                   dos.writeBytes("Content-Disposition: form-data; name="+"file"+";filename=\""
                                             + fileName + "\"" + lineEnd);
                    
                   dos.writeBytes(lineEnd);
          
                   // create a buffer of  maximum size
                   bytesAvailable = fileInputStream.available(); 
          
                   bufferSize = Math.min(bytesAvailable, maxBufferSize);
                   buffer = new byte[bufferSize];
          
                   // read file and write it into form...
                   bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                   //InputStream in = new BufferedInputStream(conn.getInputStream());
                   //try {
                	     //readStream(in);
                	   /*StringWriter writer = new StringWriter();
                	   IOUtils.copy(in, writer, "UTF-8");
                	   String theString = writer.toString();
                	   Log.v("theString", theString);*/
                   //}finally {
                	     //in.close();
                   //}
                   while (bytesRead > 0) {
                        
                     dos.write(buffer, 0, bufferSize);
                     bytesAvailable = fileInputStream.available();
                     bufferSize = Math.min(bytesAvailable, maxBufferSize);
                     bytesRead = fileInputStream.read(buffer, 0, bufferSize);   
                      
                    }
                   
                   
                   
                   // send multipart form data necesssary after file data...
                   dos.writeBytes(lineEnd);
                   dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
          
                   // Responses from the server (code and message)
                   serverResponseCode = conn.getResponseCode();
                   String serverResponseMessage = conn.getResponseMessage();
                   //Log.v("content", conn.getContent().toString());
                     
                   Log.v("uploadFile", "HTTP Response is : "
                           + serverResponseMessage + " : " + serverResponseCode);
                    
                   if(serverResponseCode == 200){
                        
                	   MyApplication.getCurrentActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                 
                                String msg = "File Upload Completed.\n\n See uploaded file here : \n\n"
                                              +" http://www.androidexample.com/media/uploads/";
                                 
                                //messageText.setText(msg);
                                Log.v("upload info", msg);
                                Toast.makeText(MyApplication.getCurrentActivity(), "File Upload Complete.", 
                                             Toast.LENGTH_SHORT).show();
                            }
                        });                
                   }    
                    
                   //close the streams //
                   fileInputStream.close();
                   dos.flush();
                   dos.close();

                   BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                   String inputLine;
                   String res = "";
                   while ((inputLine = in.readLine()) != null) 
                	   res += inputLine;
                   in.close();
                   Response res_clean = new Response(res);
                   Log.v("res", res_clean.getData().toString());
                   callback.complete(res_clean.getData(), res_clean.getStatus());
                     
              } catch (MalformedURLException ex) {
                   
                  dialog.dismiss();  
                  ex.printStackTrace();
                   
                  MyApplication.getCurrentActivity().runOnUiThread(new Runnable() {
                      public void run() {
                          //messageText.setText("MalformedURLException Exception : check script url.");
                    	  Log.v("upload info", "MalformedURLException Exception : check script url.");
                          Toast.makeText(MyApplication.getCurrentActivity(), "MalformedURLException", 
                                                              Toast.LENGTH_SHORT).show();
                      }
                  });
                   
                  Log.e("Upload file to server", "error: " + ex.getMessage(), ex);  
              } catch (Exception e) {
                   
                  dialog.dismiss();  
                  e.printStackTrace();
                   
                  MyApplication.getCurrentActivity().runOnUiThread(new Runnable() {
                      public void run() {
                          //messageText.setText("Got Exception : see logcat ");
                    	  Log.v("upload info", "Got Exception : see logcat ");
                          Toast.makeText(MyApplication.getCurrentActivity(), "Got Exception : see logcat ", 
                                  Toast.LENGTH_SHORT).show();
                      }
                  });
                  Log.e("Upload file to server Exception", "Exception : "
                                                   + e.getMessage(), e);  
              }
              dialog.dismiss();       
              return serverResponseCode; 
               
           } // End else block 
    }
}