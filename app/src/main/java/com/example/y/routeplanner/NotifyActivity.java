package com.example.y.routeplanner;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import com.example.y.routeplanner.adapter.NotifyAdapter;
import com.example.y.routeplanner.gson.Notify;
import com.example.y.routeplanner.gson.ResponseData;
import com.example.y.routeplanner.util.Util;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Request;


public class NotifyActivity extends BaseActivity {      //通知信息
    private List<Notify> notifies;
    private NotifyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify);
        Toolbar toolbar=findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setTitle("通知公告");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        initData();
        notifies=new ArrayList<>();
        RecyclerView recyclerView=findViewById(R.id.notify_recycler_view);
        adapter=new NotifyAdapter(notifies);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(NotifyActivity.this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);

    }

    private void initData() {
        Request request=new Request.Builder()
                .url("http://120.77.170.124:8080/busis/user/announce.do")
                .build();
        Util util=new Util();
        util.setHandleResponse(new Util.handleResponse() {
            @Override
            public void handleResponses(String response) {
                Log.i("info", "handleResponses: "+response);
                ResponseData responseData=new Gson().fromJson(response,new TypeToken<ResponseData<List<Notify>>>(){}.getType());
                List<Notify> list= (List<Notify>) responseData.getData();
                notifies.clear();
                notifies.addAll(list);
                sendMessage(adapter);

            }
        });
        util.doPost(NotifyActivity.this,request);

    }
}
