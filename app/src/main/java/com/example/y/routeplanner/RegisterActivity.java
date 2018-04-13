package com.example.y.routeplanner;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class RegisterActivity extends BaseActivity {
    private EditText tel,passwd,name;
    private String sTel="",sPasswd="",sName="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        tel=findViewById(R.id.register_tel);
        passwd=findViewById(R.id.register_passwd);
        Button register = findViewById(R.id.register_button);
        name=findViewById(R.id.register_name);
        showToolBar();


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sTel=tel.getText().toString();
                sPasswd=passwd.getText().toString();
                sName=name.getText().toString();
                if (sTel.equals("")||sPasswd.equals("")||sName.equals("")){
                    Toast.makeText(RegisterActivity.this, "请输入完整的用户信息！", Toast.LENGTH_SHORT).show();
                }else{

                    Intent intent=new Intent(RegisterActivity.this,ProvingActivity.class);
                    intent.putExtra("type",0);
                    intent.putExtra("tel",sTel);
                    intent.putExtra("passwd",sPasswd);
                    intent.putExtra("name",sName);
                    startActivity(intent);

                }
            }
        });

    }

}
