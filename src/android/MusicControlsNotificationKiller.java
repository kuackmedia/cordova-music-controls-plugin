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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityManager.RunningAppProcessInfo appProcessInfo = new ActivityManager.RunningAppProcessInfo();
            ActivityManager.getMyMemoryState(appProcessInfo);

            // Solo proceder si la aplicación está en primer plano
            if (appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                createNotificationChannel();

                this.startForeground(NOTIFICATION_ID, notification);
            } else {
                // Opcional: manejar la situación si no se puede poner en primer plano
                Log.w("MusicControlsService", "No se puede iniciar el servicio en primer plano porque la aplicación está en segundo plano.");
                // Podrías lanzar una notificación que no requiera estar en primer plano aquí, si es necesario.
            }
        } else {
            // Para versiones anteriores a Android 10 (Q), sigue la lógica existente
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel();
            }
            this.startForeground(NOTIFICATION_ID, notification);
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
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (mNM != null) {
                mNM.createNotificationChannel(serviceChannel);
            }
        }
    }
}
