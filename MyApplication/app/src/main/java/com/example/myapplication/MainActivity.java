package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import com.alibaba.fastjson.JSONException;


public class MainActivity extends AppCompatActivity {
    private int interval;
    private LocationManager locationManager ;
    private TextView postionView;
    public Map<String,String> inmaps = new HashMap<String, String>();
    private String baseurl = "";
    public ArrayList<TextView> gps_1 = new ArrayList<TextView>(14);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化
        initview();
        //获取用户权限
        getgpsauthority();
        //启动后台服务
        startmyservice();

    }

    protected void initview(){
        TextView textDate = findViewById(R.id.textDate);
        postionView = findViewById(R.id.postionView);
        locationManager= (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Button verify_button = findViewById(R.id.verify_button);
        Button Gps_button = findViewById(R.id.Gps_button);
        Button Person_cen = findViewById(R.id.Person_cen);
        Button enquire = findViewById(R.id.enquiry);
        enquire.setVisibility(View.INVISIBLE);

        init(textDate);

        enquire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 轮询函数
                enquire_state();
                // 提交函数
//                enquire_submit();
            }
        });

        verify_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String check_date = sdf.format(new Date());
                utils write = new utils(getApplicationContext(), check_date, "Datefile");
                write.writeToApp();
                //在文本框显示输出
                textDate.setText(check_date);
                //获取大数据组轨迹
                getdatetrace(textDate);
                //比对，如果结果异常更改界面、提交
                comparetrace();

            }
        });

        Person_cen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, Person_setActivity.class);//this前面为当前activty名称，class前面为要跳转到得activity名称
                startActivity(intent);
            }
        });

        Gps_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //先判断是否有权限
                getgpsauthority();
                //获取GPS并修改界面。
                Gps_Activity gps_activity = new Gps_Activity(locationManager, MainActivity.this, postionView, interval);
                gps_activity.forwardgetLocation();

            }
        });

    }

    public void init(TextView textDate){
        //设置上次对比时间
        utils read = new utils(getApplicationContext(), textDate, "Datefile");
        read.Read_file();

        gps_1.add((TextView)findViewById(R.id.ch_gps_view_1));
        gps_1.add((TextView)findViewById(R.id.ch_gps_view_2));
        gps_1.add((TextView)findViewById(R.id.ch_gps_view_3));
        gps_1.add((TextView)findViewById(R.id.ch_gps_view_4));
        gps_1.add((TextView)findViewById(R.id.ch_gps_view_5));
        gps_1.add((TextView)findViewById(R.id.ch_gps_view_6));
        gps_1.add((TextView)findViewById(R.id.ch_gps_view_7));
        gps_1.add((TextView)findViewById(R.id.ch_gps_view_8));
        gps_1.add((TextView)findViewById(R.id.ch_gps_view_9));
        gps_1.add((TextView)findViewById(R.id.ch_gps_view_10));
        gps_1.add((TextView)findViewById(R.id.ch_gps_view_11));
        gps_1.add((TextView)findViewById(R.id.ch_gps_view_12));
        gps_1.add((TextView)findViewById(R.id.ch_gps_view_13));
        gps_1.add((TextView)findViewById(R.id.ch_gps_view_14));

        //读取配置文件
        this.inmaps.put("version","2.0");
        ProperTies config = new ProperTies(getApplicationContext());
        if (config.setPropertiesMap(inmaps)){
            SharedPreferences share = config.getPropertiesShare();
            this.baseurl = config.getPropertiesString("baseurl");
            System.out.println("配置文件中的baseurl为"+baseurl);
            this.interval = share.getInt("gps_time_interval",0);
            System.out.println("配置文件中获取的时间间隔为"+this.interval);
        }
    }

    public void getgpsauthority(){
        int Code_PERMISSION = 0 ;
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {  //1. 检查是否已经有该权限
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // 用户拒绝过这个权限了，应该提示用户，为什么需要这个权限
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("友情提醒")
                        .setMessage("没有定位权限您将无法正常使用，请把定位权限赐给我吧！")
                        .setPositiveButton("开启", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                // 用户同意 ，再次申请
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, Code_PERMISSION);
                            }
                        })
                        .setNegativeButton("禁止", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                // 用户拒绝 ，如果APP必须有权限否则崩溃，那就继续重复询问弹框~~
                            }
                        }).show();}
            else{
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, Code_PERMISSION);
            }
        }

    }

    public void startmyservice(){
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String TAG = "Checking";
            Intent intent = new Intent(MainActivity.this, MyService.class);
            Log.i(TAG, "service started");
            startForegroundService(intent);
            bindService(intent, serviceConnection, BIND_IMPORTANT);
        }
    }

    public void enquire_state(){
        TextView result_text;
        result_text = (TextView) findViewById(R.id.result_text);
        GradientDrawable background1 = (GradientDrawable) result_text.getBackground();

        GetType getType = new GetType(80);
        int type = getType.get_type();
        if(type == 1){
            //需要转换按钮：
            //提示健康：
            result_text.setText("您的状态为健康");
            background1 = (GradientDrawable) background1.mutate();
            background1.setColor(Color.GREEN);
            change_button2();
        }
        else if(type == 0){
            //提示异常：
            Toast toast = Toast.makeText(getApplicationContext(), "网络异常，请稍后再试", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,-20);
            int tvToastId = Resources.getSystem().getIdentifier("message", "id", "android");
            TextView tvToast = ((TextView) toast.getView().findViewById(tvToastId));
            if(tvToast != null){
                tvToast.setGravity(Gravity.CENTER);
                tvToast.setTextSize(20);
            }
            toast.show();
        }
        else if(type == 2){
            //提示疑似密接+提示性话语：
            result_text.setText("您的状态为疑似密接");
            background1 = (GradientDrawable) background1.mutate();
            background1.setColor(Color.BLUE);

            Toast toast = Toast.makeText(getApplicationContext(), "您的状态为疑似密接\n请您居家隔离，及时查看自己状态", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,-20);
            int tvToastId = Resources.getSystem().getIdentifier("message", "id", "android");
            TextView tvToast = ((TextView) toast.getView().findViewById(tvToastId));
            if(tvToast != null){
                tvToast.setGravity(Gravity.CENTER);
                tvToast.setTextSize(20);
            }
            toast.show();
        }
        else if(type == 3){
            //提示A类：
            result_text.setText("您的状态为患者");
            background1 = (GradientDrawable) background1.mutate();
            background1.setColor(Color.RED);

            Toast toast = Toast.makeText(getApplicationContext(), "您的状态为患者\n请您不要外出，等待电话通知", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,-20);
            int tvToastId = Resources.getSystem().getIdentifier("message", "id", "android");
            TextView tvToast = ((TextView) toast.getView().findViewById(tvToastId));
            if(tvToast != null){
                tvToast.setGravity(Gravity.CENTER);
                tvToast.setTextSize(20);
            }
            toast.show();
        }
        else if(type == 4){
            //提示B类：
            result_text.setText("您的状态为B类密接者");
            background1 = (GradientDrawable) background1.mutate();
            background1.setColor(Color.YELLOW);

            Toast toast = Toast.makeText(getApplicationContext(), "您的状态为B类密接\n请您不要外出，等到电话通知", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,-20);
            int tvToastId = Resources.getSystem().getIdentifier("message", "id", "android");
            TextView tvToast = ((TextView) toast.getView().findViewById(tvToastId));
            if(tvToast != null){
                tvToast.setGravity(Gravity.CENTER);
                tvToast.setTextSize(20);
            }
            toast.show();
        }
        else if(type == 5){
            //提示C类：
            result_text.setText("您的状态为C类密接者");
            background1 = (GradientDrawable) background1.mutate();
            background1.setColor(Color.GRAY);

            Toast toast = Toast.makeText(getApplicationContext(), "您的状态为C类密接\n请您居家隔离", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,-20);
            int tvToastId = Resources.getSystem().getIdentifier("message", "id", "android");
            TextView tvToast = ((TextView) toast.getView().findViewById(tvToastId));
            if(tvToast != null){
                tvToast.setGravity(Gravity.CENTER);
                tvToast.setTextSize(20);
            }
            toast.show();
        }
    }


    public void getdatetrace(TextView textDate){
        GetJson getJson = new GetJson(getApplicationContext(), textDate, baseurl);
        Thread t1 = new Thread(getJson.run1);
        t1.start();
    }

    public void comparetrace(){
        TextView textDate = findViewById(R.id.textDate);
        TextView result_text = findViewById(R.id.result_text);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String check_date = sdf.format(new Date());
        utils write = new utils(getApplicationContext(), check_date, "Datefile");
        write.writeToApp();
        //在文本框显示输出
        textDate.setText(check_date);

        GetJson getJson = new GetJson(getApplicationContext(), textDate, baseurl);
        Thread t1 = new Thread(getJson.run1);
        t1.start();

        //获取当前日期以及前14天日期，存入dates数组
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String[] dates = new String[15];
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int m = 0;
        for (m = 0; m < 14; m++) {
            cal.setTime(date);
            cal.add(Calendar.DAY_OF_MONTH, -m);
            dates[m] = formatter.format(cal.getTime());
        }


        //ReadFromFile: 传入十四天的数据（自己造）：
        int number = 0;
        for (int i = 0; i < 14; i++) {

            String doc = "trace";

            doc = doc + dates[i];

            String nameA = doc + "-A.txt";
            String nameB = doc + "-B.txt";

            //每次new一个工具类对象并读出其中的string作为构造基础
            FileUtils fileobject = new FileUtils(getApplicationContext(), dates[0]);


            //message1保存的是compare的轨迹信息
            String message1 = fileobject.ReadFromFile();


            //读入A类患者数据,message2保存的是A类密接的轨迹信息
            FileUtils d = new FileUtils(getApplicationContext(), nameA);
            String message2 = d.ReadFromFile();

            //读入B类密接数据，message3保存的是B类密接的轨迹信息
            FileUtils d2 = new FileUtils(getApplicationContext(), nameB);
            String message3 = d2.ReadFromFile();


            String[] information = message2.split("\n");
            String Date = information[0];

            //读出文件传入new checkgpslist中
            CheckGPSList test1 = new CheckGPSList(message1, message2, Date, 1);
            CheckGPSList test2 = new CheckGPSList(message1, message3, Date, 0);
            //比对过程：调main就行

            try {
                test1.main(1,"m","s","123");
                Boolean result_b1 = test1.result;
                //比对结果直接调类里面的resultstring
                //根据比对结果调整颜色, 示例如下，记得调增灰度，不然会把字盖上
                GradientDrawable background1 = (GradientDrawable) result_text.getBackground();
                background1 = (GradientDrawable) background1.mutate();
                background1.setColor(Color.BLUE);

                TextView textview_result_text;
                textview_result_text = (TextView) findViewById(R.id.result_text);

                if (result_b1) {
                    textview_result_text.setText("您疑似密接");
                    background1 = (GradientDrawable) background1.mutate();
                    background1.setColor(Color.RED);
                    change_button1();
                } else {
                    test2.main(2,"m","s","123");
                    Boolean result_b2 = test2.result;
                    if (result_b2) {
                        textview_result_text.setText("您疑似密接");
                        background1 = (GradientDrawable) background1.mutate();
                        background1.setColor(Color.RED);
                        change_button1();

                    } else {
                        textview_result_text.setText("您不是密接");
                        background1 = (GradientDrawable) background1.mutate();
                        background1.setColor(Color.GREEN);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                String error = String.valueOf(e);
                Toast.makeText(MainActivity.this, error, Toast.LENGTH_LONG).show();
            }
        }
    }




    MyService.ServiceBinder binder = null;

    private ServiceConnection serviceConnection  = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            binder = (MyService.ServiceBinder) iBinder;

            binder.getService().OnSetLister(new GPSChangeListener() {
                @Override
                public void OnGPSChanged(String result) {
                    // service gps  callback
                    runOnUiThread(() -> {
                        postionView.setText(result);
                        System.out.println("传回成功！！！！！！！！！！！！");
                    });
                }

                @Override
                public void OnFileChanged(ArrayList<Integer> checklist) {
                    runOnUiThread(() -> {
                        for(int i = 0; i < 14; i++){
                            if (checklist.get(i) == 1) {
                                GradientDrawable background = (GradientDrawable) gps_1.get(i).getBackground();
                                background = (GradientDrawable) background.mutate();
                                background.setColor(Color.GREEN);
                            } else {
                                GradientDrawable background = (GradientDrawable) gps_1.get(i).getBackground();
                                background = (GradientDrawable) background.mutate();
                                background.setColor(Color.RED);
                            }
                        }
                    });

                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {


        }
    };



    public void change_button1(){
        Button Check_result;
        Check_result = (Button) findViewById(R.id.verify_button);
        Check_result.setVisibility(View.INVISIBLE);
        System.out.println();
        Button show_enquiry;
        show_enquiry = (Button) findViewById(R.id.enquiry);
        show_enquiry.setVisibility(View.VISIBLE);
    }

    public void change_button2(){
        Button Check_result;
        Check_result = (Button) findViewById(R.id.verify_button);
        Check_result.setVisibility(View.VISIBLE);
        System.out.println();
        Button show_enquiry;
        show_enquiry = (Button) findViewById(R.id.enquiry);
        show_enquiry.setVisibility(View.INVISIBLE);
    }

}