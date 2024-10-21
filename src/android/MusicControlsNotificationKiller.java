package com.homerours.musiccontrols;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.os.Build;
import android.os.IBinder;
import android.app.NotificationManager;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import android.util.Log;
import android.content.pm.ServiceInfo;

public class MusicControlsNotificationKiller extends Service {

    private static int NOTIFICATION_ID;
    private NotificationManager mNM;
    private final IBinder mBinder = new KillBinder(this);
    private static final String CHANNEL_ID = "music_controls_channel";

    @Override
    public IBinder onBind(Intent intent) {
        this.NOTIFICATION_ID = intent.getIntExtra("notificationID", 1);
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.removeNotification();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.removeNotification();
    }

    public void setForeground(Notification notification) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
           // createNotificationChannel();
        }

        try {
            Log.v("MusicControlsService", "Poniendo en primer plano el servicio " + notification.toString());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
               // ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
                this.startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK);
            } else {
                this.startForeground(NOTIFICATION_ID, notification);
            }
        }  catch (Exception e) {
            // Manejar cualquier otra excepción que pueda ocurrir
            Log.v("MusicControlsService", "Error al intentar poner en primer plano el servicio: " + e.getMessage());
        }
    }

    public void clearForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            this.stopForeground(STOP_FOREGROUND_DETACH);
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        this.removeNotification();
        this.stopSelf();
    }

    private void removeNotification() {
        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (mNM != null) {
            mNM.cancel(NOTIFICATION_ID);
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Music Controls Service Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (mNM != null) {
                mNM.createNotificationChannel(serviceChannel);
            }
        }
    }
}
