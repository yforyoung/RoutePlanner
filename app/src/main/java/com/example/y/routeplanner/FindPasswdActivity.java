package com.example.y.routeplanner;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FindPasswdActivity extends BaseActivity {      //找回密码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_passwd);
        final EditText tel=findViewById(R.id.find_passwd_tel);
        final EditText passwd=findViewById(R.id.find_passwd_passwd);
        Button ok=findViewById(R.id.find_passwd_ok);


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sTel=tel.getText().toString().trim();
                if (!sTel.equals("")){
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


                    Intent intent=new Intent(FindPasswdActivity.this,ProvingActivity.class);
                    intent.putExtra("type",1);
                    intent.putExtra("tel",tel.getText().toString().trim());
                    intent.putExtra("passwd",passwd.getText().toString().trim());
                    startActivity(intent);
                }

            }
        });
    }
}
