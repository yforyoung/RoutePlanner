package com.example.y.routeplanner;

import android.content.Intent;
import android.os.Bundle;
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

import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;

import static android.content.ContentValues.TAG;

public class ProvingActivity extends BaseActivity{          //用于验证码  修改密码、注册、找回密码
    private String sCode="";
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proving);
        final EditText code=findViewById(R.id.register_prove_code);
        final Button ok=findViewById(R.id.register_ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 intent=getIntent();
                 sCode=code.getText().toString().trim();
                //判断code是否符合
                int type=intent.getIntExtra("type",0);
                if (type==0){       //注册
                    register();
                }else if (type==1){
                    changePasswd();
                }

            }
        });
    }

    private void changePasswd() {
        if (!sCode.equals("")&&isRight()){
            String  tel=intent.getStringExtra("tel");
            String passwd=intent.getStringExtra("passwd");
        }
    }

    private void register() {
        if (!sCode.equals("")&&isRight()){
            String sName=intent.getStringExtra("name");
            String sTel=intent.getStringExtra("tel");
            String sPasswd=intent.getStringExtra("passwd");
            RequestBody requestBody=new FormBody.Builder()
                    .add("username",sName)
                    .add("telphone",sTel)
                    .add("password",sPasswd)
                    .build();
            Request request=new Request.Builder()
                    .url("http://120.77.170.124:8080/busis/user/register.do")
                    .post(requestBody)
                    .build();
            Util util=new Util();
            util.setHandleResponse(new Util.handleResponse() {
                @Override
                public void handleResponses(String response) {
                    ResponseData responseData=new Gson().fromJson(response,new TypeToken<ResponseData<User>>(){}.getType());
                    Log.i(TAG, "login: "+response);
                    if (responseData.getCode()==1) {
                        User user = (User) responseData.getData();
                        new Util().login(ProvingActivity.this,user);
                    }else {
                        sendMessage(responseData.getMessage());
                    }
                }
            });

            util.doPost(ProvingActivity.this,request);
        }else{
            Toast.makeText(ProvingActivity.this, "验证码错误！", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isRight() {      //判断验证码
        return true;
    }
}
