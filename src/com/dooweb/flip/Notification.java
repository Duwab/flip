package com.dooweb.flip;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

public class Notification {
	public static int id = 0;
	public Notification(String text){
		//id = Tools.random(2);
		id++;
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MyApplication.getCurrentActivity())
		        //.setSmallIcon(R.drawable.notification_icon)
		        .setSmallIcon(R.drawable.ic_launcher)
		        .setContentTitle("My notification" + id)
		        .setContentText(text);
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(MyApplication.getAppContext(), Create.class);
		resultIntent.putExtra("id", String.valueOf(id));
		// this line is usefull for multiple notification management. It makes the intent unique (otherwise, it is cached and overwritten by next ones)
		resultIntent.setData(Uri.parse("custom://"+System.currentTimeMillis()));
		// The stack builder object will contain an artificial back stack for the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(MyApplication.getCurrentActivity());
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(MainActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent =
		        stackBuilder.getPendingIntent(
		            0,
		            PendingIntent.FLAG_UPDATE_CURRENT
		        );
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager =
		    (NotificationManager) MyApplication.getCurrentActivity().getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(id, mBuilder.build());
	}
	public static void cancel(int id){
		NotificationManager mNotificationManager =
			    (NotificationManager) MyApplication.getAppContext().getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(id);
	}
}
