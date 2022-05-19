package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ModifyActivity extends AppCompatActivity {
    private EditText account_text;
    private EditText password_old;
    private EditText new_password;
    private EditText re_confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);

        account_text = findViewById(R.id.account_text);
        password_old = findViewById(R.id.password_old);
        new_password = findViewById(R.id.new_password);
        re_confirm = findViewById(R.id.re_confirm);
        Button button_con = findViewById(R.id.button_con);

        button_con.setOnClickListener((View.OnClickListener) new confirmClickListener());
    }

    class confirmClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            String string_password1 = password_old.getText().toString();
            String string_password2 = new_password.getText().toString();
            String string_password3 = re_confirm.getText().toString();
            if (view.getId() == R.id.button_con){
                if(!TextUtils.isEmpty(password_old.getText()) &&
                        !TextUtils.isEmpty(new_password.getText()) &&
                        !TextUtils.isEmpty(re_confirm.getText()) &&
                        string_password2.equals(string_password3)){
                    Toast.makeText(ModifyActivity.this, "修改成功！", Toast.LENGTH_SHORT).show();
                    //跳转
                    Intent intent = new Intent();
                    intent.setClass(ModifyActivity.this, LoginActivity.class);//this前面为当前activty名称，class前面为要跳转到得activity名称
                    startActivity(intent);
                }
                if(!TextUtils.isEmpty(password_old.getText()) &&
                        !TextUtils.isEmpty(new_password.getText()) &&
                        !TextUtils.isEmpty(re_confirm.getText()) &&
                        !string_password2.equals(string_password3)){
                    Toast.makeText(ModifyActivity.this, "两次输入不一致，请重新输入", Toast.LENGTH_SHORT).show();
                }
                if(TextUtils.isEmpty(account_text.getText())||TextUtils.isEmpty(password_old.getText()) ||
                        !TextUtils.isEmpty(new_password.getText()) ||
                        !TextUtils.isEmpty(re_confirm.getText())){
                    Toast.makeText(ModifyActivity.this, "输入不能为空，请重新输入", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}