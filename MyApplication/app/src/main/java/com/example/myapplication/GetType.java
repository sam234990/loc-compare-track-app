package com.example.myapplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class GetType {
    //成员变量：用户id，返回轮询类型
    private int id;
    private String type;

    //构造方法：
    public GetType(int id) {
        this.id = id;
    }

    /*
    //Get_type的使用
            System.out.println("开始进行类型获取");
            int id = 0;//id是传的参数
            Get_type gettype = new Get_type();
            String type = gettype.get_type(id);
            System.out.println(type);
            System.out.println("类型获取完成");
     */
    public int get_type() {
        //http://8.130.50.39/get_type/?user_id=1
        //数据接口位置
        String url = "http://" + "8.130.50.39" + "/get_type/?user_id=" + this.id;
        System.out.println(url);
        //获取JSON的content
        String s = doGet(url);
        System.out.println(s);
        JSONObject content = null;
        try {
            content = new JSONObject(s);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //解析json文件，返回类型
        String type = pretreatment(content);
        //type赋值：
        this.type = type;
        int i = GetTypeFromURL();
        return i;
    }

    public String doGet(String url) {
        String result = "";
        BufferedReader read = null;
        String bookJsonString = null;
        try {
            HttpURLConnection httpURLConnection = null;
            URL resquestUrl = new URL(url);
            httpURLConnection = (HttpURLConnection) resquestUrl.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setConnectTimeout(5000);
            try {
                httpURLConnection.connect();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader reader = new BufferedReader((new InputStreamReader(inputStream)));
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                    builder.append("\n");
                }
                if (builder.length() == 0) {
                    return null;
                }
                result = builder.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException | ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(result);
        return result;
    }

    public String pretreatment(JSONObject content) {
        String type = "";
        type = content.optString("type");
        return type;
    }

    public int GetTypeFromURL(){
        //留一个id的接口，此处直接写为1
        GetType getType = new GetType(1);
        if(this.type.equals("健康")){
            return 1;
            //1代表见健康，也就是将绿按钮转换为比对按钮
        }
        else if(this.type.equals("D")||this.type.equals("E")){
            return 2;
            //2代表疑似密接，不转换按钮，出现提示疑似密接+化语xxx
        }
        else if(this.type.equals("A")){
            return 3;
            //3表示患者，不转换按钮并提示隔离
        }
        else if(this.type.equals("B")){
            return 4;
            //4表示B类密接，不转换按钮+话xxx
        }
        else if(this.type.equals("C")){
            return 5;
            //5表示C类密接，不转换按钮+话xxx
        }
        //0表示异常，不换按钮，提示请刷新重试
        else return 0;
    }
}
