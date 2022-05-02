package com.example.myapplication;

import android.content.Context;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class utils {
    public TextView textDate = null;
    public String date = null;
    public Context maincontext;
    public String filename;

    public utils(Context context, TextView textDate, String filename){
        this.textDate = textDate;
        this.filename = filename;
        this.maincontext = context;
    }

    public utils(Context context, String date, String filename){
        this.maincontext = context;
        this.date = date;
        this.filename = filename;
    }

    public void Read_file() {
        try {
            FileInputStream fileIn = this.maincontext.openFileInput(this.filename);
            InputStreamReader InputRead = new InputStreamReader(fileIn);
            char[] inputBuffer = new char[256];
            String s = "";
            int charRead;
            while ((charRead = InputRead.read(inputBuffer)) > 0) {
                // char to string conversion
                String readstring = String.copyValueOf(inputBuffer, 0, charRead);
                s += readstring;
                System.out.println(s);
            }
            InputRead.close();
            //Toast.makeText(getBaseContext(), s,Toast.LENGTH_SHORT).show();
            this.textDate.setText(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeToApp() {
        try {
            FileOutputStream out = this.maincontext.openFileOutput(this.filename, maincontext.MODE_PRIVATE);
            out.write(date.getBytes());
            out.flush();// 清理缓冲区的数据流
            out.close();// 关闭输出流
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
