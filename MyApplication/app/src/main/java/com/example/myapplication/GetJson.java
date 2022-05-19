package com.example.myapplication;

import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class GetJson {

    public run_1 run1;

    public GetJson(Context main_activity, TextView textDate, String runurl){
        this.run1 = new run_1(main_activity, textDate, runurl);
    }

    public GetJson(){

    }

    class run_1 implements Runnable {
        public TextView textDate;
        public Context main_activity;
        public String baseurl;

        public run_1(Context main_activity, TextView textDate, String runurl){
            this.main_activity = main_activity;
            this.textDate = textDate;
            this.baseurl = runurl;
        }

        final String[] s = {""};
        @Override
        public void run(){
            //URL格式设计过程
            //真实使用时的日期获取
            String[] dates = getdatas();
            //此处定义仅供实现指定如期测试
            //String[] dates = {"2022-03-01","2022-03-02","2022-03-03","2022-03-04","2022-03-05","2022-03-06"};
            //进行A，B文件分类
            String[] query = {"A","B"};
            String ip_id = this.baseurl;//从配置文件读取当下的IP地址
            //String ip_id = "8.130.50.39";
            //两次循环分别读取A，B类确诊患者信息
            for (int j = 0;j<2;j++){
                //此处用于测试，实际获取当前日期的前十四天信息
                for(int i = 0;i < 14;i++) {
                    //设计新的url格式进行json文件访问
                    //String url = "http://"+ip_id+"/query"+query[j]+"/?date=" + dates[i];
                    String url = ip_id+"/gettraces_date/"+"?type="+query[j]+"&date=" + dates[i];
                    //String url = "http://"+ ip_id+"/test/"+"?type="+query[j]+"&date=" + dates[i];

                    System.out.println(url);
                    s[0] = doGet(url);
                    try {
                        JSONObject content = new JSONObject(s[0]);
                        //新建变量，判断文件得否为空
                        pretreatment(content);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //获取JSON文件内容，并将结果存入文件内
                    String final_S = getS();//获取整个json文件的信息
                    String name ="trace" + dates[i]+ "-"+ query[j]+".txt";
                    FileUtils fileUtils2 = new FileUtils(this.main_activity, name, final_S);
                    fileUtils2.WriteToFile();
                    clearS();//对全局变量进行初始化，防止每次都是追加写入

                    //获取错误信息
                    int Error_log = getError_log();
                    if(Error_log != 100){
                        String error_typy = getError_type(Error_log);
                    }
                }
            }
            System.out.println("即将进行文件删除");
            //DeleteFileDate();
            System.out.println("已经完成文件删除");

            //获取错误信息
            int Error_log = getError_log();
            String error_typy = getError_type(Error_log);
            //System.out.println(error_typy);
            if(Error_log != 100){
                Looper.prepare();
                Toast.makeText(main_activity, error_typy, Toast.LENGTH_SHORT).show();
                System.out.println("界面显示完成");
                Looper.loop();
            }

            Log.e("Thread", "stop");
        }
    }


    public String S = "";
    //选择一次行写入文件，该S为全局变量，此处为获取内容
    public String getS(){
        return this.S;
    }
    //由于需要循环写入，故在该部分选择将全局变量S置空，方便下次写入
    public void clearS(){
        this.S = "";
        error_log = 100;
    }
    //该变量为记录错误类型的标识符
    public int error_log = 0;
    //在mainactivity函数，获取到错误类型的编码
    public int getError_log(){
        return this.error_log;
    }
    //由于错误类型较多，故需要每次重置，从而能够即时更新错误信息
    public void clearError_log(){
        this.error_log = 0;
    }
    //获取网络请求，以字符串形式返回JSON文件
    public String doGet(String url){
        String result = "";
        BufferedReader read = null;
        String bookJsonString = null;
        try{
            HttpURLConnection httpURLConnection = null;
            URL resquestUrl = new URL(url);
            httpURLConnection = (HttpURLConnection)resquestUrl.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setConnectTimeout(5000);
            try{
                httpURLConnection.connect();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader reader = new BufferedReader((new InputStreamReader(inputStream)));
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null){
                    builder.append(line);
                    builder.append("\n");
                }
                if(builder.length() == 0){
                    error_log =21;//当前页面为空，请联系该服务器进行信息更新
                    return null;
                }
                result = builder.toString();
            } catch (IOException e) {
                error_log = 12;//网络连接超时，请检查配置文件
                e.printStackTrace();
            }
        }catch (MalformedURLException | ProtocolException e){
            e.printStackTrace();
            error_log = 11;//网络不稳定，需要用户自行设置网络
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(result);
        return result;
    }
    //JSON文件解析
    public void pretreatment(JSONObject content){
        String date = null;
        JSONArray trace = new JSONArray();
        date = content.optString("date");
        String name = "trace" + "_" + date + ".txt";
        S = S+date+"\n";
        try{
            trace = content.optJSONArray("trace");
            //System.out.println(trace);
            try {
                for (int i = 0; i < trace.length(); i++) {
                    JSONObject jsonObj = trace.optJSONObject(i);
                    JSONArray traces = new JSONArray();
                    traces = jsonObj.optJSONArray("traces");
                    int id = jsonObj.optInt("user_id");
                    String title ="id： " + id+ '\n';

                    //WriteDate(title,name);
                    S = S+title;
                    for (int j = 0; j < traces.length(); j++) {
                        JSONObject single_trace = traces.optJSONObject(j);

                        JSONArray pos = new JSONArray();
                        pos = single_trace.optJSONArray("pos");

                        double x = pos.optDouble(0);
                        double y = pos.optDouble(1);
                        //System.out.println(x);

                        String start_time = single_trace.optString("start_time");
                        String end_time = single_trace.optString("end_time");

                        String s = start_time + ' ' + end_time + ' ' + x + ' ' + y + '\n';
                        depart_time(start_time,end_time,x,y,name);
                        System.out.println(s);
                    }
                }
            }catch(Exception e){
                error_log = 23;//该页面信息不全，请您谨慎参考
            }
        }catch(Exception e){
            error_log = 22;//该页面格式有误，无法解析
        }

    }
    //分割时间段
    public void depart_time(String start_time,String end_time,Double x,Double y,String name){
        String[] time1 = start_time.split(":");
        String[] time2 = end_time.split(":");
        int inum1 = Integer.parseInt(time1[0]);
        int inum2 = Integer.parseInt(time1[1]);
        int inum3 = Integer.parseInt(time2[0]);
        int inum4 = Integer.parseInt(time2[1]);
        String new_start_time;
        String new_end_time;

        inum2 = inum2/10*10;
        inum4 = inum2/10*10;
        if(inum3 == 0)inum3 = 24;

        if(inum1 < inum3) {
            while(inum1 < inum3 && inum1 != 24) {
                for(int temp = 0;temp < 6; temp++) {
                    new_start_time = String.format("%2d", inum1).replace(" ", "0")+":" + String.format("%2d", inum2).replace(" ", "0");
                    inum2 = inum2 + 10;
                    if(inum2 == 60) {
                        inum2 = 0;
                        inum1 = inum1 +1;
                    }
                    new_end_time = String.format("%2d", inum1).replace(" ", "0")+":" + String.format("%2d", inum2).replace(" ", "0");
                    if(new_start_time != new_end_time) {
                        String s = new_start_time + ' ' + new_end_time + ' ' + x + ' ' + y + '\n';
//                        //WriteDate(s,name);
                        S = S+s;
                        System.out.println(s);
                    }
                }
                //inum1 = inum1 +1;
                while(inum2 < inum4 && inum4 != 0) {
                    new_start_time = String.format("%2d", inum1).replace(" ", "0")+":" + String.format("%2d", inum2).replace(" ", "0");
                    inum2 = inum2 + 10;
                    new_end_time = String.format("%2d", inum1).replace(" ", "0")+":" + String.format("%2d", inum2).replace(" ", "0");
                    if(new_start_time != new_end_time) {
                        String s = new_start_time + ' ' + new_end_time + ' ' + x + ' ' + y + '\n';
//                        //WriteDate(s,name);
                        S = S+s;
                        System.out.println(s);
                    }
                }
            }
        }
    }
    //计算当前日期以及前十四天日期的数组
    public String[] getdatas(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String[] dates = new String[15];
        Calendar cal = Calendar.getInstance();
        //获取前面的时间用-负号
        cal.setTime(date);
        int i = 0;
        for(i = 0;i < 14;i++){
            cal.setTime(date);
            cal.add(Calendar.DAY_OF_MONTH, -i);
            dates[i] = formatter.format(cal.getTime());
            System.out.println(i+"天前的时间为：" + dates[i]);
         }
        return dates;
    }
    //判断错误类型
    public String getError_type(int Error_log){
        String Error_type = null;
        if(Error_log == 0){
            Error_type = "该程序存在未知错误";
        }
        if(Error_log == 11){
            Error_type = "网络不稳定，需要用户自行设置网络";
        }
        if(Error_log == 12){
            Error_type = "网络连接超时，请检查配置文件";
        }
        if(Error_log == 21){
            Error_type = "当前页面为空，请联系该服务器进行信息更新";
        }
        if(Error_log == 22){
            Error_type = "该页面格式有误，无法解析";
        }
        if(Error_log == 23){
            Error_type = "该页面信息不全，请您谨慎参考";
        }
        if(Error_log == 100){
            Error_type = "正常执行";
        }
        return Error_type;
    }

    //删除文件功能
    public void DeleteFileDate(){
        String[] keep_dates = getdatas();
        //System.out.println(keep_dates);

        //此处定义的文件名为需要保留的A，B类型文件
        String[] olddate1 = new String[15];
        String[] olddate2 = new String[15];

        String temp1 = null;
        for(int j = 0;j<14;j++){
            System.out.println(keep_dates[j]);
            temp1 = "trace"+keep_dates[j]+"-"+"A"+".txt";
            System.out.println(temp1);
            olddate1[j] = temp1;
            System.out.println(olddate1[j]);
        }
        for(int j = 0;j<14;j++){
            olddate2[j] = "trace"+keep_dates[j]+"-"+"B"+".txt";
        }

        File directory = new File("data/data/com.example.myapplication/files");
        // 获取该目录下的所有文件
        File  temp  =  null;
        String[]  tempList  =  directory.list();
        for (int s = 0;s < tempList.length;s++){
            String path="data/data/com.example.myapplication/files/"+tempList[s];
            temp  =  new  File(path);
            if(temp.getName().startsWith("t")){
                int sign = 10;
                for(int t = 0;t<14;t++){
                    if(temp.getName().startsWith(olddate1[t])||temp.getName().startsWith(olddate2[t])){
                        System.out.println(temp.getName());
                        //如果存在这个文件//创建一个新的文件的时候就删除一次
                        //temp.delete();
                        sign = 101;
                    }
                }
                if(sign != 101) {
                    //如果文件不符合要求，就将文件删除
                    temp.delete();
                }
            }

        }
        //if(error_log == 0){
            error_log = 100;
        //}
    }


}

