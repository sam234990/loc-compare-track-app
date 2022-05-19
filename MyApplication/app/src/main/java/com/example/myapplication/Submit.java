package com.example.myapplication;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;

public class Submit {
    private JSONObject jsonOBJ1;
    private String url;

    public Submit(JSONObject jsonOBJ,String url){
        this.jsonOBJ1 = jsonOBJ;
        this.url = url;
    }

    public void submit() throws JSONException, IOException {    //上传文件涉及的参数
        try {
            //创建连接
            URL realUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setConnectTimeout(5000);
            connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            //connection.setRequestProperty("Content-Type", "data/data/com.example.myapplication/files/1.json charset=UTF-8");

            connection.connect();

//               POST请求
            System.out.println("1");
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            //out.writeBytes(obj.toString());//这个中文会乱码
            //out.write(jsonOBJ1.toString().getBytes("UTF-8"));//这样可以处理中文乱码问题
            String content = JSONObject.toJSONString(jsonOBJ1, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
                    SerializerFeature.WriteDateUseDateFormat);
            out.write(content.getBytes("UTF-8"));
            out.flush();
            out.close();
            //读取响应
            System.out.println("2");
            System.out.println(connection.getResponseCode());
            if (connection.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                System.out.println("3");
                String lines;
                StringBuffer sb = new StringBuffer("");
                while ((lines = reader.readLine()) != null) {
                    lines = new String(lines.getBytes(), "utf-8");
                    sb.append(lines);
                }
                System.out.println("4");
                System.out.println(sb);
                reader.close();
                // 断开连接
                connection.disconnect();
                System.out.println(sb.toString());
            }
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("url解析异常！");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("不支持的编码格式！");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("服务端异常！");
        }
    }
}
