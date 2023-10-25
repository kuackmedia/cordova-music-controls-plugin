package com.homerours.musiccontrols;

import android.app.Notification;
import android.app.Service;
import android.os.Build;
import android.os.IBinder;
import android.os.Binder;
import android.app.NotificationManager;
import android.content.Intent;
import android.util.Log;

public class MusicControlsNotificationKiller extends Service {

	private static int NOTIFICATION_ID;
	private NotificationManager mNM;
	private final IBinder mBinder = new KillBinder(this);

	@Override
	public IBinder onBind(Intent intent) {
		this.NOTIFICATION_ID=intent.getIntExtra("notificationID",1);
		return mBinder;
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return Service.START_STICKY;
	}

	@Override
	public void onCreate() {
		this.removeNotification();
	}

	@Override
	public void onDestroy() {
		this.removeNotification();
	}

	public void setForeground(Notification notification) {
	    Log.v("MusicControls", "setForeground " + NOTIFICATION_ID);
		this.startForeground(NOTIFICATION_ID, notification);
	}

	public void clearForeground() {
	 Log.v("MusicControls", "stopForeground");
		if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
			return;
		}

		this.stopForeground(STOP_FOREGROUND_DETACH);
	}

	@Override
	public void onTaskRemoved(Intent rootIntent) {
		this.removeNotification();
		this.stopSelf();
	}

	private void removeNotification() {
		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		 Log.v("MusicControls", "removeNotification " + NOTIFICATION_ID);
		mNM.cancel(NOTIFICATION_ID);
	}
}
