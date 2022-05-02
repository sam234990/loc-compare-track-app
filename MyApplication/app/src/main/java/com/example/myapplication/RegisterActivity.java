package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //获取按钮
        Button inner_reg = findViewById(R.id.inner_reg_button);
        //获取按钮监听器
        inner_reg.setOnClickListener((View.OnClickListener) new RegisterClickListener());
    }

    //定义注册界面注册按钮点击监听器
    class RegisterClickListener implements View.OnClickListener {


//        EditText editTextNumber = findViewById(R.id.editTextNumber);
//        EditText editTextTextPassword = findViewById(R.id.editTextTextPassword);
//        EditText editTextTextPassword2 = findViewById(R.id.editTextTextPassword2);
//        将获取的内容转为字符串
//        String string_password = editTextTextPassword.getText().toString();
//        String string_password2 = editTextTextPassword2.getText().toString();

        public void onClick(View view) {
            EditText editTextNumber = findViewById(R.id.editTextNumber);
            EditText editTextTextPassword = findViewById(R.id.editTextTextPassword);
            EditText editTextTextPassword2 = findViewById(R.id.editTextTextPassword2);
            //将获取的内容转为字符串
            String string_password = editTextTextPassword.getText().toString();
            String string_password2 = editTextTextPassword2.getText().toString();

            System.out.println(string_password);
            System.out.println(string_password2);
            System.out.println(string_password.equals(string_password2));

            if (view.getId() == R.id.inner_reg_button &&
                    !TextUtils.isEmpty(editTextNumber.getText()) &&
                    !TextUtils.isEmpty(editTextTextPassword.getText()) &&
                    !TextUtils.isEmpty(editTextTextPassword2.getText()) &&
                    string_password.equals(string_password2)){
                //被点击的是注册按钮且账号密码均不为空，两次密码相同
                //显示提示框“注册成功！”
                Toast.makeText(RegisterActivity.this, "注册成功！", Toast.LENGTH_SHORT).show();
                //跳转
                Intent intent = new Intent();
                intent.setClass(RegisterActivity.this, Register_jmpActivity.class);//this前面为当前activty名称，class前面为要跳转到得activity名称
                startActivity(intent);
            }
            else if (view.getId() == R.id.inner_reg_button && !TextUtils.isEmpty(editTextNumber.getText()) && !TextUtils.isEmpty(editTextTextPassword.getText()) && !TextUtils.isEmpty(editTextTextPassword2.getText()) &&
                    !(string_password.equals(string_password2))) {
                //被点击的是注册按钮且账号密码均不为空，两次密码不同
                //显示提示框“注册失败！两次密码不同，请重新注册”
                Toast.makeText(RegisterActivity.this, "注册失败！两次密码不同，请重新注册", Toast.LENGTH_SHORT).show();
            }
            else if (view.getId() == R.id.inner_reg_button && TextUtils.isEmpty(editTextNumber.getText())){
                //若账号为空，显示提示框“账号不能为空！”
                Toast.makeText(RegisterActivity.this, "账号不能为空！", Toast.LENGTH_SHORT).show();
            }
            else if (view.getId() == R.id.inner_reg_button &&  TextUtils.isEmpty(editTextTextPassword.getText())){
                //若密码为空，显示提示框“密码不能为空！”
                Toast.makeText(RegisterActivity.this, "密码不能为空！", Toast.LENGTH_SHORT).show();
            }
            else if(view.getId() == R.id.inner_reg_button &&  TextUtils.isEmpty(editTextTextPassword2.getText())){
                //若密码未再次输入，显示提示框“请确认密码！”
                Toast.makeText(RegisterActivity.this, "请确认密码！", Toast.LENGTH_SHORT).show();
            }
        }
    }
}