package com.example.mkms_android;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

public class MyService extends Service {
	
	private static final int ONGOING_NOTIFICATION = 12;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		return START_STICKY;
	}
	
	@Override
	public void onCreate(){
		super.onCreate();
		startForeground(ONGOING_NOTIFICATION,createNotification());
	}
	
	private Notification createNotification(){
		Intent intent = new Intent(getApplicationContext(), MainActivity.class);
		PendingIntent pending = PendingIntent.getActivity(this, 0, intent, 0);
		Notification.Builder mBuilder = new Notification.Builder(this)
			.setSmallIcon(R.drawable.ic_launcher)
			.setContentTitle("My notification")
			.setContentText("Hello World!")
			.setContentIntent(pending);
		
		return mBuilder.build();
	}

}
