package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import java.util.Map;
import java.util.Set;

public class ProperTies {
    private static SharedPreferences share;
    private static final String configPath = "config";
    public Context context;

    public ProperTies(Context c){
        this.context = c;
        try {
            share = context.getSharedPreferences(configPath, Context.MODE_PRIVATE);

            if(!share.contains("initialized")){
                SharedPreferences.Editor ed = share.edit();
                //假设已经初始化创建了
                ed.putBoolean("initialized", true);
                //设置初始默认值
                ed.putString("version", "1.0");
                ed.putString("baseurl", "http://data.antivirus.net.cn/");
                ed.putInt("gps_time_interval", 10*60*1000);
                ed.putInt("gps_distance_interval", 100);


                ed.commit();
                System.out.println("文件不存在");
                Toast.makeText(c,"新建文件成功",  Toast.LENGTH_SHORT).show();
            }else {
                System.out.println("文件已存在");
                int timeval = share.getInt("gps_time_interval", 0);
                if (timeval != 10*60*1000){
                    SharedPreferences.Editor ed = share.edit();
                    //假设已经初始化创建了
                    ed.putInt("gps_time_interval", 10*60*1000);
                    ed.commit();
                }
            }
        }
        catch (Exception e) {
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
            editor.commit();//提交刷新数据
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("setPropertiesError", e.toString());
            return false;
        }
        return true;
    }

}
