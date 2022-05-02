package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Register_jmpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_jmp);

        //获取按钮
        Button reg_jmp_button = findViewById(R.id.reg_jmp_button);

        //设置按钮点击监听器
        reg_jmp_button.setOnClickListener((View.OnClickListener) new Register_jmpClickListener());
    }

    //定义跳转登录按钮点击监听器
    class Register_jmpClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            {
                if(view.getId() == R.id.reg_jmp_button){//被点击的是跳转登录按钮
                    Intent intent = new Intent();
                    intent.setClass(Register_jmpActivity.this, LoginActivity.class);//this前面为当前activty名称，class前面为要跳转到得activity名称
                    startActivity(intent);
                }
            }
        }
    }
}