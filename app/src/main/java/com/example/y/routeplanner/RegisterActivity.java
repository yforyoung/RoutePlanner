package com.example.y.routeplanner;


import android.os.Bundle;
import android.support.annotation.Nullable;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.y.routeplanner.gson.ResponseData;
import com.example.y.routeplanner.gson.User;
import com.example.y.routeplanner.util.Util;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;


public class RegisterActivity extends BaseActivity {
    private EditText tel,passwd,confirm,name;
    private String sTel="",sPasswd="",sConfirm="",sName="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_register);
        tel=findViewById(R.id.register_tel);
        passwd=findViewById(R.id.register_passwd);
        confirm=findViewById(R.id.confirm_passwd);
        Button register = findViewById(R.id.register_button);
        name=findViewById(R.id.register_name);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sTel=tel.getText().toString();
                sPasswd=passwd.getText().toString();
                sConfirm=confirm.getText().toString();
                sName=name.getText().toString();
                if (sTel.equals("")||sPasswd.equals("")||sConfirm.equals("")||sName.equals("")){
                    Toast.makeText(RegisterActivity.this, "请输入完整的用户信息！", Toast.LENGTH_SHORT).show();
                }else if (!sPasswd.equals(sConfirm)){
                    Toast.makeText(RegisterActivity.this, "密码不一致！请确认密码", Toast.LENGTH_SHORT).show();
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            OkHttpClient client=new OkHttpClient();
                            RequestBody requestBody=new FormBody.Builder()
                                    .add("username",sName)
                                    .add("telphone",sTel)
                                    .add("password",sPasswd)
                                    .build();
                            Request request=new Request.Builder()
                                    .url("http://120.77.170.124:8080/busis/user/register.do")
                                    .post(requestBody)
                                    .build();
                            client.newCall(request).enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {

                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    String json = response.body().string();
                                    ResponseData responseData=new Gson().fromJson(json,new TypeToken<ResponseData<User>>(){}.getType());
                                    Log.i(TAG, "login: "+json);
                                    if (responseData.getCode()==1) {
                                        User user = (User) responseData.getData();
                                        new Util().login(RegisterActivity.this,user);
                                    }else {
                                        sendMessage(responseData.getMessage());
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });

    }


}
