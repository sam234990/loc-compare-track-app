package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    private int interval;
    private LocationManager locationManager ;
    private TextView postionView;
    public Map<String,String> inmaps = new HashMap<String, String>();
    private String baseurl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        postionView = findViewById(R.id.postionView);
        locationManager= (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        TextView textDate = findViewById(R.id.textDate);

        Button verify_button = findViewById(R.id.verify_button);
        Button Gps_button = findViewById(R.id.Gps_button);
        //设置按钮点击监听器
        verify_button.setOnClickListener(new verifyClickListener());
        Gps_button.setOnClickListener(new GpsClickListener());

        init(textDate);

    }

    protected void init(TextView textDate){
        //设置上次对比时间
        utils read = new utils(getApplicationContext(), textDate, "Datefile");
        read.Read_file();

        //读取配置文件
        this.inmaps.put("version","2.0");
        ProperTies config = new ProperTies(getApplicationContext());
        if (config.setPropertiesMap(inmaps)){
            String Vsrsion_String = config.getPropertiesString("version");
            SharedPreferences share = config.getPropertiesShare();
            this.baseurl = config.getPropertiesString("baseurl");
            System.out.println("配置文件中的baseurl为"+baseurl);
            this.interval = share.getInt("gps_time_interval",0);
            System.out.println("配置文件中获取的时间间隔为"+this.interval);
//            Toast.makeText(MainActivity.this, "当前版本：" + Vsrsion_String, Toast.LENGTH_SHORT).show();
            System.out.println(Vsrsion_String);
        }
    }


    //定义确认对比按钮点击监听器
    class verifyClickListener implements View.OnClickListener {
        TextView textDate = findViewById(R.id.textDate);
        TextView result_text = findViewById(R.id.result_text);
        //tView = new TextView[100];

        TextView ch_gps_view_1 = findViewById(R.id.ch_gps_view_1);
        TextView ch_gps_view_2 = findViewById(R.id.ch_gps_view_2);
        TextView ch_gps_view_3 = findViewById(R.id.ch_gps_view_3);
        TextView ch_gps_view_4 = findViewById(R.id.ch_gps_view_4);
        TextView ch_gps_view_5 = findViewById(R.id.ch_gps_view_5);
        TextView ch_gps_view_6 = findViewById(R.id.ch_gps_view_6);
        TextView ch_gps_view_7 = findViewById(R.id.ch_gps_view_7);
        TextView ch_gps_view_8 = findViewById(R.id.ch_gps_view_8);
        TextView ch_gps_view_9 = findViewById(R.id.ch_gps_view_9);
        TextView ch_gps_view_10 = findViewById(R.id.ch_gps_view_10);
        TextView ch_gps_view_11 = findViewById(R.id.ch_gps_view_11);
        TextView ch_gps_view_12 = findViewById(R.id.ch_gps_view_12);
        TextView ch_gps_view_13 = findViewById(R.id.ch_gps_view_13);
        TextView ch_gps_view_14 = findViewById(R.id.ch_gps_view_14);

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.verify_button) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String check_date = sdf.format(new Date());
                utils write = new utils(getApplicationContext(), check_date, "Datefile");
                write.writeToApp();
                //在文本框显示输出
                textDate.setText(check_date);

                TextView textDate = findViewById(R.id.textDate);
                GetJson getJson = new GetJson(getApplicationContext(), textDate, baseurl);
                Thread t1 = new Thread(getJson.run1);
                t1.start();

                //获取当前日期以及前14天日期，存入dates数组
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date();
                String[] dates = new String[15];
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                int m = 0;
                for(m = 0;m < 14;m++) {
                    cal.setTime(date);
                    cal.add(Calendar.DAY_OF_MONTH, -m);
                    dates[m] = formatter.format(cal.getTime());
                }


                //ReadFromFile: 传入十四天的数据（自己造）：
                int number = 0;
                for (int i = 0; i < 14; i++) {

                    String doc = "trace";

                    doc = doc + dates[i];

                    String nameA = doc+"-A.txt";
                    String nameB = doc+"-B.txt";

                    //每次new一个工具类对象并读出其中的string作为构造基础
                    FileUtils fileobject = new FileUtils(getApplicationContext(),dates[0]);


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
                    CheckGPSList test = new CheckGPSList(message1,message2,Date);
                    CheckGPSList test2 = new CheckGPSList(message1,message3,Date);
                    //比对过程：调main就行

                    try {
                        test.main();
                        String result1 = test.result1;
                        //比对结果直接调类里面的resultstring
                        //根据比对结果调整颜色, 示例如下，记得调增灰度，不然会把字盖上
                        GradientDrawable background1 = (GradientDrawable) result_text.getBackground();
                        background1 = (GradientDrawable) background1.mutate();
                        background1.setColor(Color.BLUE);

                        TextView textview_result_text;
                        textview_result_text=(TextView)findViewById(R.id.result_text);
                        if (result1.equals("B"))
                        {

                            textview_result_text.setText("您疑似密接");
                            background1 = (GradientDrawable) background1.mutate();
                            background1.setColor(Color.RED);
                        }
                        else if(result1.equals("C"))
                        {
                            textview_result_text.setText("您不是密接");
                            background1 = (GradientDrawable) background1.mutate();
                            background1.setColor(Color.YELLOW);
                            //上传数据：但是需要区分B,C上传的数据位置

                        }
                        else{
                            //但是需要比对test2：
                            test2.main();
                            String result2 = test2.result2;

                            if (result2.equals("C")){
                                //上传数据：但是需要区分B,C上传的数据位置
                                textview_result_text.setText("您疑似密接");
                                background1 = (GradientDrawable) background1.mutate();
                                background1.setColor(Color.YELLOW);
                            }
                            else{
                                //没有密接的情况：
                                textview_result_text.setText("您不是密接");
                                background1 = (GradientDrawable) background1.mutate();
                                background1.setColor(Color.GREEN);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }


    }

    //定义获取GPS信息按钮
    class GpsClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.Gps_button) {
                //检查是否获取定位权限
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    //申请授权
                    if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        Toast.makeText(MainActivity.this, "请授权！", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    } else
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                } else {
                    Gps_Activity gps_activity = new Gps_Activity(locationManager,
                            MainActivity.this, postionView, interval);
                    String gpsStr = gps_activity.getLocation();
                    System.out.println(gpsStr);

                    if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        String TAG = "Checking";
                        Intent intent = new Intent(MainActivity.this, MyService.class);
                        Log.i(TAG, "service started");
                        startForegroundService(intent);
                    }
                }
            }
        }
    }
}