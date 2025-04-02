package com.homerours.musiccontrols;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.Context;
import android.os.IBinder;
import android.util.Log;

public class MusicControlsServiceConnection implements ServiceConnection {
    protected MusicControlsNotificationKiller service;
    protected Activity activity;
    protected boolean isForeground = false;

    MusicControlsServiceConnection(Activity activity) {
        this.activity = activity;
    }

    public void onServiceConnected(ComponentName className, IBinder binder) {
        Log.v("MusicControlsServiceConnection", "Connected to service 1");
        service = ((KillBinder) binder).service;
        Log.v("MusicControlsServiceConnection", "Connected to service 2");
        //service.startService(new Intent(activity, MusicControlsNotificationKiller.class));
        Log.v("MusicControlsServiceConnection 2", "Connected to service");
    }

    public void onServiceDisconnected(ComponentName className) {
    }

    void setNotification(Notification notification, boolean isPlaying) {
        if (this.service == null) {
            return;
        }
        Log.v("MusicControlsServiceConnection setNotification", isPlaying ? "true" : "false");

        // Verificar si la app está en primer plano o en segundo plano
        boolean appInForeground = isAppInForeground();
        
        if (isPlaying) {
            // Solo intentar poner en primer plano si la app también está en primer plano
            if (appInForeground && !this.isForeground) {
                Log.v("MusicControlsServiceConnection", "App y reproducción en primer plano, iniciando servicio foreground");
                this.service.setForeground(notification);
                this.isForeground = true;
            } else if (!appInForeground) {
                // Si la app está en segundo plano pero reproduciendo, intentamos mantener el servicio foreground
                // aunque con un log para informar de la situación
                Log.v("MusicControlsServiceConnection", "App en segundo plano pero reproduciendo, intentando mantener foreground");
                if (!this.isForeground) {
                    this.service.setForeground(notification);
                    this.isForeground = true;
                } else {
                    // Si ya estamos en foreground, solo actualizar la notificación
                    NotificationManager notificationManager = 
                        (NotificationManager) this.activity.getSystemService(Context.NOTIFICATION_SERVICE);
                    if (notificationManager != null && notification != null) {
                        notificationManager.notify(7824, notification);
                    }
                }
            }
        } else if (!isPlaying && this.isForeground) {
            // Si no está reproduciendo, quitar el foreground
            Log.v("MusicControlsServiceConnection", "Deteniendo reproducción, quitando servicio foreground");
            this.service.clearForeground();
            this.isForeground = false;
        }
    }
    
    // Método para verificar si la app está en primer plano
    private boolean isAppInForeground() {
        android.app.ActivityManager activityManager = 
            (android.app.ActivityManager) this.activity.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager == null) return false;
        
        java.util.List<android.app.ActivityManager.RunningAppProcessInfo> appProcesses = 
            activityManager.getRunningAppProcesses();
        if (appProcesses == null) return false;
        
        String packageName = this.activity.getPackageName();
        for (android.app.ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                    && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }
}
