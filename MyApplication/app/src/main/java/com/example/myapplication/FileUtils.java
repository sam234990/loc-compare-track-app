package com.example.myapplication;

import android.content.Context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class FileUtils{
    //两个成员变量：
    public String filename;
    public String data_to_file;
    public boolean aflag;
    public Context c = null;

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
}