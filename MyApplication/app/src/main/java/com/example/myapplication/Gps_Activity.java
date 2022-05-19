package com.example.myapplication;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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

import org.json.JSONObject;

public class Gps_Activity{
    private String locationProvider;
    public LocationManager locationManager;
    public Context context_activity;
    public TextView postionView;
    public String restring;
    public int time_interval;

    public Gps_Activity(LocationManager locationManager, Context context_activity, TextView postionView, int time_inter){
        this.locationManager = locationManager;
        this.context_activity = context_activity;
        this.postionView = postionView;
        this.time_interval = time_inter;
    }

    public Gps_Activity(LocationManager locationManager, Context context_activity, int time_inter){
        this.locationManager = locationManager;
        this.context_activity = context_activity;
        this.time_interval = time_inter;
    }

    public Gps_Activity(Context context_activity, int time_interval){
        this.context_activity = context_activity;
        this.time_interval = time_interval;
    }

    public void forwardgetLocation() //前台
    {
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

        if (ActivityCompat.checkSelfPermission(context_activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context_activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            return;
        }

        Location location = locationManager.getLastKnownLocation(locationProvider);
        if(location != null){
            //不为空,显示地理位置经纬度
            DecimalFormat df = new DecimalFormat("#.000000");
            String Latitude = df.format(location.getLatitude());
            String Longitude = df.format(location.getLongitude());
            String locationStr = "纬度：" + location.getLatitude() +"\n"
                    + "经度：" + location.getLongitude();
            String output = null;
            try {
                output = getAMapByLngAndLat(Longitude, Latitude, "477ce9837df8f02e9bc65007eb7b37d2");//由于在虚拟机上使用的是虚拟的负数的经纬度，所以输出不出来，不是代码的问题
            } catch (Exception e) {
                e.printStackTrace();
            }
            String Str=locationStr+"\n"+output;
            System.out.println("weizhi");
            postionView.setText(Str);
        }
        forwardlistener locationListener = new forwardlistener(postionView);
        locationManager.requestLocationUpdates(locationProvider, 3000, 1, locationListener);

    }


    public void backgetLocation(LocationListener locationListener) {
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
        if (ActivityCompat.checkSelfPermission(context_activity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                context_activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        //监视地理位置变化
        locationManager.requestLocationUpdates(locationProvider, 3000, 1,
                locationListener);
        return;
    }


    private static double EARTH_RADIUS = 6371000;
    private static double rad(double d){return d * Math.PI/180.0;}
    public int check(String datetime)
    {
        int f = 1;
        String path = "data/data/com.example.myapplication/files/" + datetime ;
        File file;
        file = new File(path);
        System.out.println(datetime+file.exists());
        if(file.exists())
        {
            //  if (temp.getName().startsWith(datetime)) {
            FileUtils fileUtils = new FileUtils(this.context_activity, datetime);
            String str = fileUtils.ReadFromFile();
            if (str != null)
            {
                String[] arr0 = str.split("\n");
                //for(int i=0;i<arr0.length;i++){
                //  System.out.println("第"+i+"行"+arr0[i]);
                //}
                String[][] arr = new String[144][4];
                if (arr0.length == 24 * 60 * 1000 * 60 / time_interval)
                {
                    //f=1;
                        /*for (int j = 0; j < arr0.length; j++)
                        {
                            if (arr0[j].charAt(2) == ':')
                                count++;
                            //System.out.println(arr[i]);
                        }*/
                    //System.out.println(datetime+' '+"count: "+count);
                    for (int a = 0; a < arr0.length; a++) {
                        String[] str1 = arr0[a].split(" ");
                        for (int b = 0; b < 4; b++) {
                            arr[a][b] = str1[b];
                        }
                    }
                    flag:
                    for (int t = 0; t < arr.length - 1; t++) {
                        if (arr[t][2] != null && arr[t][3] != null && arr[t + 1][2] != null && arr[t + 1][3] != null) {
                            Double lat1 = rad(Double.parseDouble(arr[t][2]));
                            Double lat2 = rad(Double.parseDouble(arr[t + 1][2]));
                            Double lon1 = rad(Double.parseDouble(arr[t][3]));
                            Double lon2 = rad(Double.parseDouble(arr[t + 1][3]));
                            Double d1 = lat2 - lat1;
                            Double d2 = lon2 - lon1;
                            Double distance = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(d1 / 2), 2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(d2 / 2), 2)));
                            distance = distance * EARTH_RADIUS;
                            distance = Double.valueOf(Math.round(distance * 10000) / 10000);
                            if (Math.abs(distance) > 140000) {
                                System.out.println("第" + "t" + "行" + "数据错误");
                                f = 0;
                                break flag;
                            }
                            //else flag = 1;
                        } else {
                            f = 0;
                            System.out.println("第" + "t" + "行" + "数据获取失败");
                        }
                        //}else f = 0;
                    }
                } else f = 0;
            } else f = 0;
        }
        else
        {
            f = 0;
            System.out.println(datetime + "文件不存在");
        }

        return f;
    }


    private static String getResponse(String serverUrl) {

        // 用JAVA发起http请求，并返回json格式的结果
        // 用JAVA发起http请求，并返回json格式的结果
        StringBuffer result = new StringBuffer();
        try {
            URL url = new URL(serverUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
            in.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }



    public static String getAMapByLngAndLat(String getLng, String getLat, String key) throws Exception {
        String url;
        System.out.println(getLng);
        System.out.println(getLat);
        try {
            url = "http://restapi.amap.com/v3/geocode/regeo?output=JSON&location=" + getLng + ","
                    + getLat + "&key=" + key + "&radius=0&extensions=base";
            String queryResult = getResponse(url); // 高德返回的是JSON格式的字符串
            // 将获取结果转为json数据
            JSONObject obj = new JSONObject(queryResult);
            // JSONObject obj = JSONObject.parseObject(queryResult);
            System.out.println("obj为：" + obj);
            if (obj.get("status").toString().equals("1")) {
                // 如果没有返回-1
                JSONObject regeocode = obj.getJSONObject("regeocode");
                if (regeocode.length()> 0) {
                    // 在regeocode中拿到 formatted_address 具体位置
//                    String formatted_address= regeocode.get("formatted_address").toString();
                    JSONObject addressComponent = regeocode.getJSONObject("addressComponent");
                    String city= addressComponent.get("city").toString();
                    String province= addressComponent.get("province").toString();
                    String output= province+" "+city;
                    return output;
                } else {
                    throw new RuntimeException("未找到相匹配的地址！");
                }
            } else {
                throw new RuntimeException("请求错误！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "-1";
    }


    public void DeleteFileDate(){
        Date newdate=new Date();
        Date olddate;
        Calendar now = Calendar.getInstance();
        now.setTime(newdate);
        now.set(Calendar.DATE, now.get(Calendar.DATE) - 14);	//14天以前时间
        olddate=now.getTime();//得到14天前的时间
        System.out.println("olddate:"+olddate);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String oldstr = sdf.format(olddate);
        System.out.println("oldstr:"+oldstr);
        File directory = new File("data/data/com.example.myapplication/files");
        // 获取该目录下的所有文件
        String[]  tempList  =  directory.list();
        File  temp  =  null;
        for  (int  i  =  0;  i  <  tempList.length;  i++) {
            String path = "data/data/com.example.myapplication/files/" + tempList[i];
            temp = new File(path);
            System.out.println(temp.getName());
            //if (!temp.getName().startsWith("t") ) {
            Calendar cal1;
            try {
                Date date1 = sdf.parse(temp.getName());
                cal1 = Calendar.getInstance();
                cal1.setTime(date1);
                if (cal1.before(now) || temp.getName().startsWith(oldstr)) {
                    //System.out.println(temp.getName());
                    //如果存在这个文件//创建一个新的文件的时候就删除一次
                    temp.delete();
                    System.out.println("删除文件: " + temp.getName());
                }
                System.out.println(cal1.before(now));
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * LocationListern监听器
     * 参数：地理位置提供器、监听位置变化的时间间隔、位置变化的距离间隔、LocationListener监听器
     */
    class forwardlistener implements LocationListener {
        public TextView postionView;
        public Date last_in;
        public Context main_activity;
        public int time_interval;

        public forwardlistener(TextView postionView) {
            this.postionView = postionView;
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

            //
        }
    }



}
