package com.dooweb.flip;

import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Point;
import android.net.ParseException;
import android.view.Display;

public class Tools {
	
	//public static Activity main_activity = null;
	public static byte[] salt;
	public static Boolean reload_dashboard = false;
	
	public static void alert(String msg){
		if(MyApplication.getCurrentActivity() == null)
			return;
		AlertDialog.Builder truc = new AlertDialog.Builder(MyApplication.getCurrentActivity());
    	truc.setTitle("Delete entry")
        .setMessage(msg)
        .setPositiveButton("oui", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) { 
                // continue with delete
            }
         })
        .setNegativeButton("non", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) { 
                // do nothing
            }
         })
        .setIcon(android.R.drawable.ic_dialog_alert);
    	truc.show();
	}
	
	public static int screenWidth(){
		Display display = MyApplication.getCurrentActivity().getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		return size.x;
	}
	
	public static int screenHeight(){
		Display display = MyApplication.getCurrentActivity().getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		return size.y;
	}
	
	public static int timestamp(){
		long tsLong = System.currentTimeMillis()/1000;
		int tsInt = (int) tsLong;
		return tsInt;
	}
	
	public static int random(int max){
		// returns an integer from [0, max]
		Random rn = new Random();
		return rn.nextInt(max + 1);
	}
	
	public static boolean isNumeric(String str)
	{
		if(str == "")
			return false;
		try{
			return str.matches("-?\\d+(\\.\\d+)?");
		}catch(Exception e){
			NumberFormat formatter = NumberFormat.getInstance();
			ParsePosition pos = new ParsePosition(0);
			formatter.parse(str, pos);
			return str.length() == pos.getIndex();
		}
	}
	
	public static String date_format(String s_date, String format){
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Date date = new Date();
		try {
			date = dateFormat.parse(s_date);
		} catch (ParseException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		} catch(Exception e){
			
		}
		System.out.println(date);
		String res = date.getHours() + ":" + date.getMinutes() + "  " + date.getMonth() + "/" + date.getDay() + "/" + date.getYear();
		return res;
	}
	/*
	 * 
	 * 	static public String join(List<String> list, String conjunction)
		{
		   StringBuilder sb = new StringBuilder();
		   boolean first = true;
		   for (String item : list)
		   {
		      if (first)
		         first = false;
		      else
		         sb.append(conjunction);
		      sb.append(item);
		   }
		   return sb.toString();
		}
	 */
}