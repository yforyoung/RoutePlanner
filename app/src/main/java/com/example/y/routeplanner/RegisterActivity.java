package com.example.y.routeplanner;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


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
                    RequestBody requestBody=new FormBody.Builder()
                            .add("telphone",sTel)
                            .build();
                    Request request=new Request.Builder()
                            .url("http://120.77.170.124:8080/busis/user/sms.do")
                            .post(requestBody)
                            .build();
                    new OkHttpClient().newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {

                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            Headers header=response.headers();
                            String s=response.body().string();
                            String session=header.values("Set-Cookie").get(0);
                            String sessionId=session.substring(0,session.indexOf(";"));
                            @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor=getSharedPreferences("user",MODE_PRIVATE).edit();
                            editor.putString("session_id",sessionId);
                            editor.apply();

                            Log.i("code", "handleResponses: "+sessionId);
                            sendMessage("验证码已发送");
                        }
                    });



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
