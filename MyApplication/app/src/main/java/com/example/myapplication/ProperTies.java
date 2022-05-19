package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.Map;
import java.util.Set;

public class ProperTies {
    private static SharedPreferences share;
    private static final String configPath = "config";
    private static final String servePath = "serve_config";
    public Context context;
    public String sfromserve;

    public ProperTies(Context c){
        this.context = c;
        try {
            share = context.getSharedPreferences(configPath, Context.MODE_PRIVATE);
            CheckUpdate();
            if(!share.contains("initialized")){
                SharedPreferences.Editor ed = share.edit();
                //假设已经初始化创建了
                ed.putBoolean("initialized", true);
                //设置初始默认值
                ed.putString("version", "1.0");
                ed.putString("baseurl", "http://8.130.50.39//");
                ed.putInt("gps_time_interval", 10*60*1000);
                ed.putInt("gps_distance_interval", 100);
                ed.apply();
                System.out.println("文件不存在");
            }else {
                System.out.println("文件已存在");
                int timeval = share.getInt("gps_time_interval", 0);
                if (timeval != 10*60*1000){//目前使用，未来删除
                    SharedPreferences.Editor ed = share.edit();
                    //假设已经初始化创建了
                    ed.putInt("gps_time_interval", 10*60*1000);
                    ed.apply();
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void CheckUpdate(){

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        GetJson getJson = new GetJson();
        String config_url = "http://8.130.50.39/xml/";
        try {
            sfromserve = getJson.doGet(config_url);
        }catch (Exception e){
            e.printStackTrace();
            return;
        }
        System.out.println(sfromserve);
        parseXMLWithPull(sfromserve);

        SharedPreferences co_share = getPropertiesShare();
        String version = co_share.getString("version","");
        SharedPreferences serve_share = context.getSharedPreferences(servePath, Context.MODE_PRIVATE);
        String v_serve = serve_share.getString("version", "");
        int gps_time_interval = serve_share.getInt("gps_time_interval", 0);
        System.out.println("gps_time_interval" + gps_time_interval);
//        if (version.equals(v_serve)){
//            SharedPreferences.Editor co_ed = share.edit();
//            co_ed.putBoolean("initialized", true);
//            //设置服务器中数值默认值
//            co_ed.putString("version", serve_share.getString("version",""));
//            co_ed.putString("baseurl", serve_share.getString("baseurl",""));
//            co_ed.putInt("gps_time_interval", serve_share.getInt("gps_time_interval", 0));
//            co_ed.putInt("gps_distance_interval", serve_share.getInt("gps_distance_interval", 0));
//            co_ed.apply();
//            Toast.makeText(context, "配置文件更新成功" , Toast.LENGTH_SHORT).show();
//        }
    }

    class run_2 implements Runnable{
        private GetJson getJson_1;
        private String config_url;
        public run_2(GetJson getJson, String config_url){
            this.getJson_1 = getJson;
            this.config_url = config_url;
        }

        public void run(){
            sfromserve = getJson_1.doGet(config_url);
        }
    }

    private void parseXMLWithPull(String xmlData){
        SharedPreferences serve_share = context.getSharedPreferences(servePath, Context.MODE_PRIVATE);
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(new StringReader(xmlData));
            int eventType = xmlPullParser.getEventType();
            SharedPreferences.Editor ed = serve_share.edit();
            while (eventType != XmlPullParser.END_DOCUMENT){
                String nodeName = xmlPullParser.getName();
                switch (eventType){
                    case XmlPullParser.START_TAG:{
                        if ("version".equals(nodeName)){
                            String ver = xmlPullParser.nextText();
                            ed.putString("version", ver);
                        }
                        else if ("gps_time_interval".equals(nodeName)){
                            int gpstime = Integer.parseInt(xmlPullParser.nextText());
                            ed.putInt("gps_time_interval", gpstime);
                        }else if ("gps_distance_interval".equals(nodeName)){
                            int gpsdis = Integer.parseInt(xmlPullParser.nextText());
                            ed.putInt("gps_distance_interval", gpsdis);
                        }else if ("baseurl".equals(nodeName)){
                            String baseu = xmlPullParser.nextText();
                            ed.putString("baseurl", baseu);
                        }
                        break;
                    }
                    case XmlPullParser.END_TAG:{
                        break;
                    }
                    default:
                        break;
                }
                eventType = xmlPullParser.next();
            }
            ed.apply();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //返回配置文件的share格式
    public SharedPreferences getPropertiesShare(){
        try {
            share = context.getSharedPreferences(configPath, Context.MODE_PRIVATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return share;
    }

    // 读取配置文件中的字符串格式信息
    public String getPropertiesString(String get_string) {
        try {
            share = context.getSharedPreferences(configPath, Context.MODE_PRIVATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return share.getString(get_string,"");
    }

    //写入配置文件
    public Boolean setPropertiesMap(Map<String,String> maps) {
        try {
            share = context.getSharedPreferences(configPath, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = share.edit();//取得编辑器
            Set<Map.Entry<String, String>> set = maps.entrySet();
            // 遍历键值对对象的集合，得到每一个键值对对象
            for (Map.Entry<String, String> me : set) {
                // 根据键值对对象获取键和值
                String key = me.getKey();
                String value = me.getValue();
                editor.putString(key, value);//存储配置 参数1 是key 参数2 是值
            }
            editor.apply();//提交刷新数据
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("setPropertiesError", e.toString());
            return false;
        }
        return true;
    }

}
