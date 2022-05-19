package com.example.myapplication;

import android.content.Context;

import com.alibaba.fastjson.serializer.SerializerFeature;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class FileUtils{
    private static JSONObject object = null;
    //两个成员变量：
    public String filename;
    public String data_to_file;
    public boolean aflag;
    public Context c = null;
    public Object jsonData;

    //定义构造放方法：两个都输入或只输入filename
    public FileUtils(Context c, String filename, String data_to_file, boolean aflag) {
        this.c = c;
        this.filename = filename;
        this.data_to_file = data_to_file;
        this.aflag = aflag;
    }
    public FileUtils(Context c, String filename, String data_to_file) {
        this.c = c;
        this.filename = filename;
        this.data_to_file = data_to_file;
        this.aflag = false;
    }
    public FileUtils(Context c, String filename) {
        this.c = c;
        this.filename = filename;
    }
    public FileUtils(Object jsonData, String filename) {
        this.jsonData = jsonData;
        this.filename = filename;
    }

    //方法1：读文件：
    public String ReadFromFile(){
        FileInputStream in = null;
        BufferedReader reader = null;
        StringBuilder context = new StringBuilder();
        try{
            in = c.openFileInput(this.filename);
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while((line = reader.readLine()) != null){
                context.append(line);
                context.append("\n");
            }
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if(reader!=null){
                try{
                    reader.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
        return context.toString();
    }
    public String ReadFromFile_End(){
        FileInputStream in = null;
        BufferedReader reader = null;
//        StringBuilder context = new StringBuilder();
        String end = " ";
        try{
            in = c.openFileInput(this.filename);
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while((line = reader.readLine()) != null){
                end = line;
            }

        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if(reader!=null){
                try{
                    reader.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
        return end;
    }

    //方法2：写文件：
    public void WriteToFile(){
        FileOutputStream out = null;
        BufferedWriter writer = null;
        try{
            if (this.aflag){
                out = c.openFileOutput(this.filename, Context.MODE_APPEND);
            }
            else {
                out = c.openFileOutput(this.filename, Context.MODE_PRIVATE);
            }
            writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(this.data_to_file);
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try{
                if(writer != null){
                    writer.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public boolean createJsonFile() {
        System.out.println(this.jsonData);
        String content = com.alibaba.fastjson.JSONObject.toJSONString(this.jsonData, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteDateUseDateFormat);
        // 标记文件生成是否成功
        boolean flag = true;
        // 生成json格式文件
        try {
//            // 保证创建一个新文件
//            File file = new File(filePath);
//            if (!file.getParentFile().exists()) { // 如果父目录不存在，创建父目录
//                file.getParentFile().mkdirs();
//            }
//            if (file.exists()) { // 如果已存在,删除旧文件
//                file.delete();
//            }
//            file.createNewFile();
//            // 将格式化后的字符串写入文件
            System.out.println(content.toString());
            Writer write = new OutputStreamWriter(new FileOutputStream(this.filename), "UTF-8");
            write.write(content);
            write.flush();
            write.close();
        } catch (Exception e) {
            flag = false;
            e.printStackTrace();
        }
        return flag;
    }
}