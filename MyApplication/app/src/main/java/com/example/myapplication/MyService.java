package com.example.myapplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;


import java.util.List;

public class MyService extends Service {
    String TAG = "Checking";
    private Gps_Activity Gps_activity;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "on create started");

    }
    public void setForegroundService(){
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(MyService.this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setTicker("Foreground Service Start");
        builder.setContentTitle("Foreground Service");
        builder.setContentText("Make this service run in the foreground.");
        builder.setOngoing(true);
        Notification notification = builder.build();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("001","my_channel",NotificationManager.IMPORTANCE_LOW);
            manager.createNotificationChannel(channel);
            builder.setChannelId("001");
        }

        Notification n = builder.build();

        //manager.notify(1,n);
        startForeground(1,n);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Log.i(TAG, "onstartcommand started");
            setForegroundService();
            Log.i(TAG, "foreground started");
        }
        //String gpsStr = Gps_activity.getLocation();
        //System.out.println(gpsStr);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
    }

}


