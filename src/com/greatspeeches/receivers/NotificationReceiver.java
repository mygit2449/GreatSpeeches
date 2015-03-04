package com.greatspeeches.receivers;

import java.util.Random;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.greatspeeches.MainActivity;
import com.greatspeeches.R;
import com.greatspeeches.database.GreatLivesDataBaseHelper;
import com.greatspeeches.helper.GreateSpeechesUtil;
/**
 * 
 * @author Satish
 *
 */
public class NotificationReceiver extends BroadcastReceiver{
 	
	private static final int NOTIFICATION_ID = 1;
	private NotificationManager mNotificationManager;
	private SharedPreferences.Editor preferencesEdit;
	private GreatLivesDataBaseHelper mDataBaseHelper = null;
	 @Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		SharedPreferences prefs = context.getSharedPreferences("gl", Context.MODE_PRIVATE);
		boolean isFirstTrack = prefs.getBoolean("firstTrack", true);
		int prevNum = prefs.getInt("prevNum", 0);
		mDataBaseHelper = new GreatLivesDataBaseHelper(context);
		mDataBaseHelper.openDataBase();
		preferencesEdit = prefs.edit();
		if(isFirstTrack){
			preferencesEdit.putBoolean("firstTrack", false);
			preferencesEdit.commit();
		}else {
			int totCount = mDataBaseHelper.getTotaRecCount();
			Cursor notiCursor = null;
			if (totCount > 0) {
				Random rand = new Random();  
				int pickedNumber = rand.nextInt(totCount);
				if(prevNum != pickedNumber){
					preferencesEdit.putInt("prevNum", pickedNumber);
					preferencesEdit.commit();
				}else{
					pickedNumber = rand.nextInt(totCount);
					preferencesEdit.putInt("prevNum", pickedNumber);
					preferencesEdit.commit();
				}
				notiCursor = mDataBaseHelper.queryDataBase(String.format(GreatLivesDataBaseHelper.NOTI_SELECT_QUERY, pickedNumber));
				sendNotification(context, intent, notiCursor, pickedNumber);
			}
		}
		
	}
	
	private void sendNotification(Context context,  Intent check_intent, Cursor objHome,  int selectedPos) {
		
		
		try {
			Intent notiintent = new Intent(context, MainActivity.class);
			notiintent.setAction("fromNotification");
			notiintent.putExtra("selectedPos", selectedPos);
			PendingIntent contentIntent = PendingIntent.getActivity(context, 0,notiintent, PendingIntent.FLAG_UPDATE_CURRENT);
			
			android.support.v4.app.NotificationCompat.Builder builder = new android.support.v4.app.NotificationCompat.Builder(context.getApplicationContext());
	 		RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.notification_layout);
	 	    
	 		if (objHome.getCount() > 0 && objHome.moveToFirst()) {
	 			contentView.setImageViewResource(R.id.popular_img, GreateSpeechesUtil.getResId(objHome.getString(0)+"_l", R.drawable.class));
	 			contentView.setTextViewText(R.id.person_name, objHome.getString(1));
	 			contentView.setTextViewText(R.id.person_quote, objHome.getString(2));
			}
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
