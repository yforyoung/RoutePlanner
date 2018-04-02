package com.example.y.routeplanner;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;


import com.example.y.routeplanner.gson.ResponseData;
import com.example.y.routeplanner.gson.Result;
import com.example.y.routeplanner.gson.User;
import com.example.y.routeplanner.util.Test;
import com.example.y.routeplanner.util.Util;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.Calendar;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SetActivity extends BaseActivity implements View.OnClickListener{
    private static final String TAG ="setting" ;
    private EditText username,telephone,introduce;
    private TextView gender,birthday;
    private User user;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_set);
        user=Test.getInstance().user;

        Toolbar toolbar = findViewById(R.id.toolbar);
        Button logout = findViewById(R.id.logout);
        username=findViewById(R.id.username_change);
        gender=findViewById(R.id.gender_change);
        birthday=findViewById(R.id.birthday_change);
        telephone=findViewById(R.id.telephone_change);
        introduce=findViewById(R.id.introduce_change);

        username.setText(user.getUserName());
        gender.setText(user.getGender());
        birthday.setText(user.getBirthday());
        telephone.setText(user.getTelphone());
        introduce.setText(user.getIntroduce());
        gender.setFocusable(false);
        birthday.setFocusable(false);

        logout.setOnClickListener(this);
        toolbar.setNavigationIcon(R.drawable.back);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.set_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        changeInfo(user.getUserId());

        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.logout:
                SharedPreferences.Editor editor=getSharedPreferences("user",MODE_PRIVATE).edit();
                editor.putInt("login",0);
                editor.apply();
                Test.getInstance().user=null;
                Test.getInstance().loginOrNot=0;    //设置未登陆
                finish();
                break;
            case R.id.birthday_change:
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog.OnDateSetListener listener=new DatePickerDialog.OnDateSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        birthday.setText(i+"-"+(++i1)+"-"+i2);
                    }
                };
                DatePickerDialog dialog=new DatePickerDialog(SetActivity.this, 0,listener, year, month, day);
                dialog.show();
                break;
            case R.id.gender_change:
                AlertDialog.Builder builder=new AlertDialog.Builder(SetActivity.this);
                final String s[]=new String[]{"男", "女"};
                builder.setItems(s, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        gender.setText(s[i]);
                    }
                });
                builder.show();
                break;
        }
    }
    private void changeInfo(final String userId){
        Log.i(TAG, "changeInfo: change");
        final String a=username.getText().toString();
        final String b=gender.getText().toString();
        final  String c=birthday.getText().toString();
        final  String d=introduce.getText().toString();
        final String e=telephone.getText().toString();


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                RequestBody requestBody = new FormBody.Builder()
                        .add("user_id", userId)
                        .add("username", a)
                        .add("gender",b)
                        .add("birthday",c)
                        .add("introduce",d)
                        .add("telphone",e)
                        .build();
                Request request = new Request.Builder()
                        .url("http://120.77.170.124:8080/busis/user/modify.do")
                        .post(requestBody)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG, "onFailure: ", e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String userJson = response.body().string();
                        Util util=new Util();
                        Log.i(TAG, "onResponse: "+userJson);
                        ResponseData responseData=new Gson().fromJson(userJson,new TypeToken<ResponseData<Result>>(){}.getType());
                        if (responseData.getCode()==1){
                            user.setUserName(a);
                            user.setGender(b);
                            user.setBirthday(c);
                            user.setIntroduce(d);
                            user.setTelphone(e);
                            Test.getInstance().user=user;       //修改用户信息
                            util.save(new Gson().toJson(user),getApplicationContext());        //覆盖本地用户信息
                        }
                        sendMessage(responseData.getMessage());

                        finish();

                    }
                });

            }
        });

    }

}
