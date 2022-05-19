package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Person_setActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_set);
        Button modify_button = findViewById(R.id.modify_button);
        Button exit_button = findViewById(R.id.exit_button);
        modify_button.setOnClickListener((View.OnClickListener) new modifyClickListener());
        exit_button.setOnClickListener((View.OnClickListener) new exitClickListener());
    }
    class exitClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.exit_button){
                Intent intent = new Intent();
                intent.setClass(Person_setActivity.this, LoginActivity.class);//this前面为当前activty名称，class前面为要跳转到得activity名称
                startActivity(intent);
            }
        }
    }
    class modifyClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.modify_button){
                Intent intent = new Intent();
                intent.setClass(Person_setActivity.this, ModifyActivity.class);//this前面为当前activty名称，class前面为要跳转到得activity名称
                startActivity(intent);
            }
        }
    }
}