package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class LoginActivity<activity_login> extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //获取按钮
        Button log_button = findViewById(R.id.login_button);
        Button reg_button = findViewById(R.id.register_button);

        //设置按钮点击监听器
        log_button.setOnClickListener((View.OnClickListener) new LoginClickListener());
        reg_button.setOnClickListener((View.OnClickListener) new RegisterClickListener());
        //获取文本输入框
//        int localversion = getLocalVersion(this);
        String localVersionname = getLocalVersionName(this);
        System.out.println(localVersionname);
//        Toast.makeText(LoginActivity.this, localVersionname, Toast.LENGTH_SHORT).show();
        checkVersion();
    }

    /**
     * 获取本地软件版本号
     */
    public static int getLocalVersion(Context ctx) {
        int localVersion = 0;
        try {
            PackageInfo packageInfo = ctx.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(ctx.getPackageName(), 0);
            localVersion = packageInfo.versionCode;
            Log.d("TAG", "当前版本号：" + localVersion);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return localVersion;
    }

    /**
     * 获取本地软件版本号名称
     */
    public static String getLocalVersionName(Context ctx) {
        String localVersionname = "";
        try {
            PackageInfo packageInfo = ctx.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(ctx.getPackageName(), 0);
            localVersionname = packageInfo.versionName;
            Log.d("TAG", "当前版本名称：" + localVersionname);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return localVersionname;
    }

    /**
     * 对比目前版本信息是否需要更新
     */
    public void checkVersion() {
        //如果检测本程序的版本号小于服务器的版本号，那么提示用户更新
        int localversion = getLocalVersion(this);
//        if (localversion <= 2) {
//        //弹出提示版本更新的对话框
//            Toast.makeText(LoginActivity.this, "请前往更新", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(LoginActivity.this, "已经是最新版本", Toast.LENGTH_SHORT).show();
//        }
    }





    //定义登录按钮点击监听器
    class LoginClickListener implements View.OnClickListener {

        EditText accountNumber = findViewById(R.id.TextNumber);
        EditText accountPassword = findViewById(R.id.TextPassword);

        //按钮点击
        public void onClick(View view) {

            if (view.getId() == R.id.login_button && !TextUtils.isEmpty(accountNumber.getText()) && !TextUtils.isEmpty(accountPassword.getText())) {
                //被点击的是登录按钮且账号密码均不为空
                //显示提示框“登录成功！”
                Toast.makeText(LoginActivity.this, "登陆成功！", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, MainActivity.class);//this前面为当前activty名称，class前面为要跳转到得activity名称
                startActivity(intent);
            } else if (view.getId() == R.id.login_button && TextUtils.isEmpty(accountNumber.getText())) {
                //若账号为空，显示提示框“账号不能为空！”
                Toast.makeText(LoginActivity.this, "账号不能为空！", Toast.LENGTH_SHORT).show();
            } else if (view.getId() == R.id.login_button && TextUtils.isEmpty(accountPassword.getText())){
                //若密码为空，显示提示框“密码不能为空！”
                Toast.makeText(LoginActivity.this, "密码不能为空！", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //定义注册按钮点击监听器
    class RegisterClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            if(view.getId() == R.id.register_button){//被点击的是注册按钮
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, RegisterActivity.class);//this前面为当前activty名称，class前面为要跳转到得activity名称
                startActivity(intent);
            }
        }
    }
}