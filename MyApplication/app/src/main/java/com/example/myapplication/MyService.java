package com.example.myapplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;


import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MyService extends Service {
    String TAG = "Checking";
    private Gps_Activity Gps_activity;
    public GPSChangeListener listener;
    private LocationManager locationManager ;
    public llistener locationListener;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "on create started");

        locationManager= (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        ProperTies config = new ProperTies(getApplicationContext());
        SharedPreferences share = config.getPropertiesShare();
        int interval = share.getInt("gps_time_interval",0);
        locationListener = new llistener(MyService.this, interval);
        Gps_activity = new Gps_Activity(locationManager ,MyService.this, interval);
        // 获取GPS并写入。
        Gps_activity.backgetLocation(locationListener);


        // 起线程每隔一段时间对比
        Thread mthread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        Thread.sleep(10000);//用sleep每隔一段时间执行一次
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //获取患者轨迹 TODO

                    //对比 TODO

                    //判断文件是否完整
                    adjustFile();

                    try {
                        Thread.sleep(600000);//用sleep每隔一段时间执行一次
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        mthread.start();

    }


    public void adjustFile(){
        ProperTies config = new ProperTies(getApplicationContext());
        SharedPreferences share = config.getPropertiesShare();
        int interval = share.getInt("gps_time_interval",0);
        Gps_Activity gps_activity = new Gps_Activity(MyService.this, interval);

        Date nowdate=new Date();
        Calendar now = Calendar.getInstance();
        now.setTime(nowdate);
        now.set(Calendar.DATE, now.get(Calendar.DATE) - 14);//14天前时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

        ArrayList<Integer> check = new ArrayList<Integer>(14);
        for(int i = 0; i < 14; i++) {
            now.add(now.DATE, 1);
            Date nowdate2 = now.getTime();
            String nde1 = sdf.format(nowdate2);
            System.out.println(nde1);
            int res = gps_activity.check(nde1);
            check.add(res);
            System.out.println(res);
        }
        this.listener.OnFileChanged(check);
        //删除冗余文件
        gps_activity.DeleteFileDate();
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
        return new ServiceBinder();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
    }


    class ServiceBinder extends Binder {

        public MyService getService() {
            return MyService.this;
        }
    }

    public void OnSetLister(GPSChangeListener listener) {
        this.listener = listener;
    }



    private Date showLocation(Location location, Date last_in, int time_interval){
        /**
         * 显示地理位置经度和纬度信息
         * @param location
         */
        DecimalFormat df = new DecimalFormat("#.000000");
        String Latitude = df.format(location.getLatitude());
        String Longitude = df.format(location.getLongitude());
        String locationStr = "纬度：" + Latitude +"\n" + "经度：" + Longitude;

        String output = null;
        // String output2 = null;
        try {

            output = Gps_Activity.getAMapByLngAndLat(Longitude, Latitude, "477ce9837df8f02e9bc65007eb7b37d2");//由于在虚拟机上使用的是虚拟的负数的经纬度，所以输出不出来，不是代码的问题

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("转换后地址为output：" + output);
        String Str=locationStr+"\n"+output;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        String date = sdf.format(new Date());
        String cutout=date.substring(0,8);
        String loc =  Latitude + " " + Longitude+ "\n";

        listener.OnGPSChanged(Str);


        Date now = new Date();
        double interval = now.getTime() - last_in.getTime();
        System.out.println("interval:"+interval);

        if(interval > time_interval){
            Calendar calendar = Calendar.getInstance();
            System.out.println(calendar);
            calendar.set(Calendar.MINUTE, (calendar.get(Calendar.MINUTE))/10*10);
            calendar.set(Calendar.SECOND,0);
            Date early = calendar.getTime();
            Date start = last_in;
            last_in = early;

            double write_times = Math.floor(interval / (time_interval));
            System.out.println("write_times" + write_times);
            for (int i = 1; i <= write_times; i++) {
                start = new Date(start.getTime() +  time_interval);
                Date start_10 = new Date(start.getTime() +  time_interval);
                SimpleDateFormat sdf_in = new SimpleDateFormat("HH:mm");
                String date_in = sdf_in.format(start);
                String date_in_10 = sdf_in.format(start_10);
                String locin = date_in + " " + date_in_10 +" " + loc;
                System.out.println("文件名："+cutout);
                FileUtils fileUtils_1 = new FileUtils(MyService.this, cutout, locin, true);
                fileUtils_1.WriteToFile();
                System.out.println("locin:"+locin);
            }
        }
        else {
            return last_in;
        }
        return last_in;
    }

    /**
     * LocationListern监听器
     * 参数：地理位置提供器、监听位置变化的时间间隔、位置变化的距离间隔、LocationListener监听器
     */
    class llistener implements LocationListener {
        public Date last_in;
        public Context main_activity;
        public int time_interval;

        public llistener(Context main_act, int interval_time) {
            this.main_activity = main_act;
            this.time_interval = interval_time;
            Date now_time = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
            String date = sdf.format(now_time.getTime());
            String cutout=date.substring(0,8);

            try {
                //采用文件中上一次读入的时间
                FileUtils fileUtils_1 = new FileUtils(this.main_activity, cutout);
                String read = fileUtils_1.ReadFromFile_End();
                read = read.split(" ")[0];
                read = cutout + " " + read + ":00";
                System.out.println("read"+ read);
                Date read_in = sdf.parse(read);
                double interval_read_now = Math.abs(read_in.getTime() - now_time.getTime());
                if (interval_read_now > 2 * 10 * 60 * 1000){//如果上一次读入时间过长，则不采用这种方式。
                    //设置过长时间为20min，
                    throw new Exception();//抛出异常，使用catch中的方式。
                }else {
                    this.last_in = read_in;
                    System.out.println("使用文件中的时间");
                }
            } catch (Exception e) {
                //采用当前时间向下取整后减去10min
                e.printStackTrace();
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.MINUTE, (calendar.get(Calendar.MINUTE))/10*10);
                calendar.set(Calendar.SECOND,0);
                Date early = calendar.getTime();
                early = new Date(early.getTime() - 10 * 60 * 1000 );
                this.last_in = early;
                System.out.println("使用当前时间减去10min");
            }finally {
                System.out.println(this.last_in);
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle arg2) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onLocationChanged(Location location) {
            //如果位置发生变化,重新显示
            String locationStr = "维度：" + location.getLatitude() + "\n" + "经度：" + location.getLongitude();
            Date new_in = showLocation(location, this.last_in, this.time_interval);
            this.last_in = new_in;

            //
        }
    }

}


