package com.example.myapplication;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;

public class Gps_Activity{
    private String locationProvider;
    public LocationManager locationManager;
    public Context main_activity;
    public TextView postionView;
    public String restring;
    public int time_interval;

    public Gps_Activity(LocationManager locationManager, Context main_activity, TextView postionView, int time_inter){
        this.locationManager = locationManager;
        this.main_activity = main_activity;
        this.postionView = postionView;
        this.time_interval = time_inter;
    }

    public String getLocation() {
        llistener locationListener = new llistener(postionView, this.main_activity, this.time_interval);
        //获取所有可用的位置提供器
        List<String> providers = locationManager.getProviders(true);
        if (providers.contains(LocationManager.GPS_PROVIDER)) {
            //如果是GPS
            locationProvider = LocationManager.GPS_PROVIDER;
            System.out.println("gps");
        } else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            //如果是Network
            locationProvider = LocationManager.NETWORK_PROVIDER;
            System.out.println("network");
        } else {
            System.out.println("没有可用的位置提供器");
        }
        //设置精度，低功耗
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);

        //获取Location
        if (ActivityCompat.checkSelfPermission(main_activity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                        main_activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return " ";
        }
        Location location = locationManager.getLastKnownLocation(locationProvider);
        if(location!=null){
            //不为空,显示地理位置经纬度
            this.restring = showLocation_string(location, postionView);
        }
        //监视地理位置变化
        locationManager.requestLocationUpdates(locationProvider, 3000, 1,
                locationListener);
        return this.restring;
    }



    private String showLocation_string(Location location, TextView postionView){

//         * 显示地理位置经度和纬度信息
//         * @param location

        String locationStr = "纬度：" + location.getLatitude() +"\n"
                + "经度：" + location.getLongitude();
        postionView.setText(locationStr);
        return locationStr;
    }


    public void check(){
        int count=0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        String date = sdf.format(new Date());
        String cutoutdate=date.substring(0,8);
        //System.out.println(cutoutdate);
        String cutouttime = date.substring(9,13);
        FileUtils fileUtils = new FileUtils(this.main_activity, cutoutdate);
        String str= fileUtils.ReadFromFile();
        //System.out.println(str);
        while(cutouttime=="23:59") {
            String arr[]=str.split(" ");
            for(int i=0;i<arr.length;i=3*i){
                if(arr[i].charAt(2)==':')
                    count++;}
            for(int i=1;i<arr.length;i=i+3){
                if(arr[i]!=null) {
                    if ((Math.abs(Double.parseDouble(arr[i + 3]) - Double.parseDouble(arr[i]))) > 20000 / 6371.393e3) {
                        System.out.println("第" + i + "行" + "纬度数据错误");
                    }
                }
            }
            for(int i=2;i<arr.length;i=i+3){
                if(arr[i]!=null) {
                    if ((Math.abs(Double.parseDouble(arr[i + 3]) - Double.parseDouble(arr[i]))) > 20000 / (6371.393e3 * Math.cos(Double.parseDouble(arr[i])))) {
                        System.out.println("第" + i + "行" + "经度数据错误");
                    }
                }
            }
        }
        if(count==24*6)
        {
            System.out.println("success");
        }
    }


    private Date showLocation(Location location, TextView postionView, Date last_in, int time_interval){
        /**
         * 显示地理位置经度和纬度信息
         * @param location
         */
        DecimalFormat df = new DecimalFormat("#.000000");
        String Latitude = df.format(location.getLatitude());
        String Longitude = df.format(location.getLongitude());
        String locationStr = "纬度：" + Latitude +"\n" + "经度：" + Longitude;
        postionView.setText(locationStr);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        String date = sdf.format(new Date());
        String cutout=date.substring(0,8);
        String loc =  Latitude + " " + Longitude+ "\n";

        Date now = new Date();
        double interval = now.getTime() - last_in.getTime();
        System.out.println("interval:"+interval);

        if(interval > time_interval){
            Calendar calendar = Calendar.getInstance();
            System.out.println(calendar);
            calendar.set(Calendar.MINUTE, (calendar.get(Calendar.MINUTE))/10*10);
            calendar.set(Calendar.SECOND,0);
            Date early = calendar.getTime();
            System.out.println(early);
            Date start = last_in;
            last_in = early;

            double write_times = Math.floor(interval / (time_interval));
            System.out.println("write_times" + write_times);
            DeleteFileDate();
            for (int i = 1; i <= write_times; i++) {
                start = new Date(start.getTime() +  time_interval);
                Date start_10 = new Date(start.getTime() +  time_interval);
                SimpleDateFormat sdf_in = new SimpleDateFormat("HH:mm");
                String date_in = sdf_in.format(start);
                String date_in_10 = sdf_in.format(start_10);
                String locin = date_in + " " + date_in_10 +" " + loc;
                System.out.println("文件名："+cutout);
                FileUtils fileUtils_1 = new FileUtils(this.main_activity, cutout, locin, true);
                fileUtils_1.WriteToFile();
                System.out.println("locin:"+locin);
            }
        }
        else {
            return last_in;
        }
        return last_in;
    }


    public static void DeleteFileDate(){
        Date newdate=new Date();
        Date olddate;
        Calendar now = Calendar.getInstance();
        now.setTime(newdate);
        now.set(Calendar.DATE, now.get(Calendar.DATE) - 1);	//测试用4天，实际写成14天 第三组同学可以根据测试进行修改  注意如果每隔的时间小于一分钟 这里不能写1天
        olddate=now.getTime();//得到四天前的时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String oldstr = sdf.format(olddate);
        File directory = new File("data/data/com.example.myapplication/files");
        // 获取该目录下的所有文件
        String[]  tempList  =  directory.list();

        File  temp  =  null;
        for  (int  i  =  0;  i  <  tempList.length;  i++)  {
            String path="data/data/com.example.myapplication/files/"+tempList[i];
            temp  =  new  File(path);
            System.out.println(temp.getName());
            if(temp.getName().startsWith(oldstr)){
                //System.out.println(temp.getName());
                //如果存在这个文件//创建一个新的文件的时候就删除一次
                temp.delete();
            }

        }
    }


    /**
     * LocationListern监听器
     * 参数：地理位置提供器、监听位置变化的时间间隔、位置变化的距离间隔、LocationListener监听器
     */
    class llistener implements LocationListener {
        public TextView postionView;
        public Date last_in;
        public Context main_activity;
        public int time_interval;

        public llistener(TextView postionView, Context main_act, int interval_time) {
            this.postionView = postionView;
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
            Date new_in = showLocation(location, this.postionView, this.last_in, this.time_interval);
            this.last_in = new_in;
        }
    }

}
