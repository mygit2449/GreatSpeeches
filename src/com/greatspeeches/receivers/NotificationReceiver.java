package com.greatspeeches.receivers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.greatspeeches.MainActivity;
import com.greatspeeches.R;
import com.greatspeeches.helper.GreateSpeechesUtil;
import com.greatspeeches.helper.ObjectSerializer;
import com.greatspeeches.models.HomeDataModel;

public class NotificationReceiver extends BroadcastReceiver{
 	
	private ArrayList<HomeDataModel> homeDataarr = null;
	private static final int NOTIFICATION_ID = 1;
	private NotificationManager mNotificationManager;
	private SharedPreferences.Editor preferencesEdit;
	
	 @Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		SharedPreferences prefs = context.getSharedPreferences("gl", Context.MODE_PRIVATE);
		boolean isFirstTrack = prefs.getBoolean("firstTrack", true);
		int prevNum = prefs.getInt("prevNum", 0);
		preferencesEdit = prefs.edit();
		if(isFirstTrack){
			preferencesEdit.putBoolean("firstTrack", false);
			preferencesEdit.commit();
		}else {
			try { 
				homeDataarr = (ArrayList<HomeDataModel>) ObjectSerializer.deserialize(prefs.getString("popularItems", ObjectSerializer.serialize(new ArrayList<HomeDataModel>())));
			} catch (IOException e) {
				e.printStackTrace();
			} 
			HomeDataModel objHome = null;
			if (null != homeDataarr && homeDataarr.size() > 0) {
				Random rand = new Random();  
				int pickedNumber = rand.nextInt(homeDataarr.size());
				
				if(prevNum != pickedNumber){
					objHome = homeDataarr.get(pickedNumber);
					preferencesEdit.putInt("prevNum", pickedNumber);
					preferencesEdit.commit();
				}else{
					pickedNumber = rand.nextInt(homeDataarr.size());
					objHome = homeDataarr.get(pickedNumber);
					preferencesEdit.putInt("prevNum", pickedNumber);
					preferencesEdit.commit();
				}
				sendNotification(context, intent, objHome);
			}
		}
		
	}
	
	private void sendNotification(Context context,  Intent check_intent, HomeDataModel objHome) {
		
		
		try {
			Intent notiintent = new Intent(context, MainActivity.class);
			notiintent.setAction("fromNotification");
			notiintent.putExtra("notiObject", (Parcelable)objHome);
			PendingIntent contentIntent = PendingIntent.getActivity(context, 0,notiintent, PendingIntent.FLAG_UPDATE_CURRENT);
			
			android.support.v4.app.NotificationCompat.Builder builder = new android.support.v4.app.NotificationCompat.Builder(context.getApplicationContext());
	 		RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.notification_layout);
	 	    contentView.setImageViewResource(R.id.popular_img, GreateSpeechesUtil.getResId(objHome.getImageId()+"_l", R.drawable.class));
	 	    contentView.setTextViewText(R.id.person_name, objHome.getName());
	 	    contentView.setTextViewText(R.id.person_quote, objHome.getQuote());
	 	    builder.setAutoCancel(true)
		     .setDefaults(Notification.DEFAULT_ALL)
		     .setWhen(System.currentTimeMillis())         
		     .setSmallIcon(R.drawable.app_icon144_5)
		     .setContentTitle("Great Lives")
		     .setContent(contentView)
		     .setDefaults(Notification.DEFAULT_LIGHTS| Notification.DEFAULT_VIBRATE| Notification.DEFAULT_SOUND)
		     .setContentIntent(contentIntent);
			
			mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//			Notification notification = new Notification.BigTextStyle(builder).bigText(""+objHome.getQuote()).build();
//			Notification notification = new NotificationCompat.Builder(context);
//			notification.bigContentView = contentView;
			mNotificationManager.notify(NOTIFICATION_ID, builder.getNotification());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
}
