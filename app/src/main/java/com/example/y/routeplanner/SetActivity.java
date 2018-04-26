package com.example.y.routeplanner;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import com.example.y.routeplanner.gson.ResponseData;
import com.example.y.routeplanner.gson.User;
import com.example.y.routeplanner.util.Test;
import com.example.y.routeplanner.util.Util;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.util.Calendar;


import okhttp3.FormBody;

import okhttp3.Request;
import okhttp3.RequestBody;

//设置页面
public class SetActivity extends BaseActivity implements View.OnClickListener, View.OnFocusChangeListener {
    private EditText username, telephone, introduce;
    private TextView gender, birthday;
    private User user;
    private String textUserName, textGender, textBirthday, textTel, textIntro;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);
        user = Test.getInstance().user;

        Toolbar toolbar = findViewById(R.id.toolbar);

        username = findViewById(R.id.username_change);
        gender = findViewById(R.id.gender_change);
        birthday = findViewById(R.id.birthday_change);
        telephone = findViewById(R.id.telephone_change);
        introduce = findViewById(R.id.introduce_change);

        username.setText(user.getUserName());
        gender.setText(user.getGender());
        birthday.setText(user.getBirthday());
        telephone.setText(user.getTelphone());
        introduce.setText(user.getIntroduce());
        gender.setFocusable(false);
        birthday.setFocusable(false);
        gender.setOnClickListener(this);
        birthday.setOnClickListener(this);


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
        getMenuInflater().inflate(R.menu.set_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        changeInfo(user.getUserId());

        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.birthday_change:
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        birthday.setText(i + "-" + (++i1) + "-" + i2);
                    }
                };
                DatePickerDialog dialog = new DatePickerDialog(SetActivity.this, 0, listener, year, month, day);
                dialog.show();
                break;
            case R.id.gender_change:
                AlertDialog.Builder builder = new AlertDialog.Builder(SetActivity.this);
                final String s[] = new String[]{"男", "女"};
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

    private void changeInfo(final String userId) {

        final String gen = gender.getText().toString();
        final String a = username.getText().toString();
        final int b;
        if (gen.equals("男")) {
            b = 1;
        } else {
            b = 0;
        }
        final String c = birthday.getText().toString();
        final String d = introduce.getText().toString();
        final String e = telephone.getText().toString();

        textUserName = a;
        textTel = e;
        textBirthday = c;
        textGender = gen;
        textIntro = d;

        RequestBody requestBody = new FormBody.Builder()
                .add("user_id", userId)
                .add("username", a)
                .add("gender", String.valueOf(b))
                .add("birthday", c)
                .add("introduce", d)
                .add("telphone", e)
                .build();
        Request request = new Request.Builder()
                .url("http://120.77.170.124:8080/busis/user/modify.do")
                .post(requestBody)
                .build();
        final Util util = new Util();
        util.setHandleResponse(new Util.handleResponse() {
            @Override
            public void handleResponses(String response) {
                ResponseData responseData = new Gson().fromJson(response, new TypeToken<ResponseData<User>>() {
                }.getType());
                if (responseData.getCode() == 1) {
                    user.setUserName(textUserName);
                    user.setGender(textGender);
                    user.setBirthday(textBirthday);
                    user.setIntroduce(textIntro);
                    user.setTelphone(textTel);
                    Test.getInstance().user = user;       //修改用户信息
                    util.save(new Gson().toJson(user), getApplicationContext());        //覆盖本地用户信息
                    sendMessage("修改成功！");
                } else {
                    sendMessage(responseData.getMessage());
                }
                finish();
            }
        });

        util.doPost(SetActivity.this, request);
    }


    @Override
    public void onFocusChange(View view, boolean b) {
        hideInput();
        switch (view.getId()) {
            case R.id.birthday_change:
                if (b) {

                    Calendar calendar = Calendar.getInstance();
                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH);
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                            birthday.setText(i + "-" + (++i1) + "-" + i2);
                        }
                    };
                    DatePickerDialog dialog = new DatePickerDialog(SetActivity.this, 0, listener, year, month, day);
                    dialog.show();
                }
                break;
            case R.id.gender_change:
                if (b) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(SetActivity.this);
                    final String s[] = new String[]{"男", "女"};
                    builder.setItems(s, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            gender.setText(s[i]);
                        }
                    });
                    builder.show();
                }
                break;
        }
    }

    public void hideInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
