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

public class ProvingActivity extends BaseActivity {          //用于验证码  修改密码、注册、找回密码
    private String sCode = "";
    private Intent intent;
    private EditText code;
    private Util util = new Util();
    private String sessionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proving);
        code = findViewById(R.id.register_prove_code);
        final Button ok = findViewById(R.id.register_ok);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = getIntent();
                sCode = code.getText().toString().trim();
                sessionId = getSharedPreferences("user", MODE_PRIVATE).getString("session_id", "");
                if (!sessionId.equals("")) {
                    //判断code是否符合
                    if (!sCode.equals("")) {
                        int type = intent.getIntExtra("type", 0);
                        if (type == 0) {       //注册
                            register();
                        } else if (type == 1) {
                            changePasswd();
                        }
                    } else {
                        Toast.makeText(ProvingActivity.this, "请输入验证码！", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void changePasswd() {
        //String tel = intent.getStringExtra("tel");
        String passwd = intent.getStringExtra("passwd");
        RequestBody requestBody = new FormBody.Builder()
                .add("code", sCode)
                .add("password", passwd)
                .build();
        Request request = new Request.Builder()
                .addHeader("cookie",sessionId)
                .url("http://120.77.170.124:8080/busis/user/recover.do")
                .post(requestBody)
                .build();
        util.setHandleResponse(new Util.handleResponse() {
            @Override
            public void handleResponses(String response) {
                Log.i(TAG, "handleResponses: " + response);
                sendMessage("密码已修改，请重新登陆");
                Intent intent = new Intent(ProvingActivity.this, LoadActivity.class);
                startActivity(intent);
                finish();
            }
        });
        util.doPost(ProvingActivity.this, request);


    }

    private void register() {

        String sName = intent.getStringExtra("name");
        String sTel = intent.getStringExtra("tel");
        String sPasswd = intent.getStringExtra("passwd");
        String sCode = code.getText().toString().trim();
        RequestBody requestBody = new FormBody.Builder()

                .add("username", sName)
                .add("telphone", sTel)
                .add("password", sPasswd)
                .add("code", sCode)
                .build();
        Request request = new Request.Builder()
                .addHeader("cookie",sessionId)
                .url("http://120.77.170.124:8080/busis/user/register.do")
                .post(requestBody)
                .build();

        util.setHandleResponse(new Util.handleResponse() {
            @Override
            public void handleResponses(String response) {
                Log.i(TAG, "login: " + response);
                int code = Integer.parseInt(response.substring(8, 9));
                if (code == 1) {
                    ResponseData responseData = new Gson().fromJson(response, new TypeToken<ResponseData<User>>() {
                    }.getType());
                    Log.i(TAG, "login: " + response);

                    User user = (User) responseData.getData();
                    new Util().login(ProvingActivity.this, user);
                } else {
                    ResponseData m = new Gson().fromJson(response, new TypeToken<ResponseData<String>>() {
                    }.getType());
                    sendMessage(m.getMessage());
                }
            }
        });

        util.doPost(ProvingActivity.this, request);
    }
}
