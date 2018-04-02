package com.example.y.routeplanner.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.y.routeplanner.R;
import com.example.y.routeplanner.adapter.CollectionRouteAdapter;
import com.example.y.routeplanner.gson.CollectionRoute;
import com.example.y.routeplanner.gson.MyPath;
import com.example.y.routeplanner.gson.MyRoute;
import com.example.y.routeplanner.gson.ResponseData;
import com.example.y.routeplanner.gson.Result;
import com.example.y.routeplanner.gson.User;
import com.example.y.routeplanner.util.Test;
import com.example.y.routeplanner.util.Util;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;


public class CollectionRouteFragment extends Fragment implements CollectionRouteAdapter.OnItemClickListener,CollectionRouteAdapter.OnItemLongClickListener{
    private List<MyPath> myPathList;
    private User user;
    private PopupWindow popupWindow;
    private LinearLayout popLinearLayout;
    private  View popContentView;
    private CollectionRouteAdapter adapter;
    private List<CollectionRoute> list;
    @SuppressLint("HandlerLeak")
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    break;
                case 2:
                    adapter.notifyDataSetChanged();
                    break;

            }
        }
    };
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_collection_path,container,false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        RecyclerView listView = getActivity().findViewById(R.id.co_path);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        listView.setLayoutManager(manager);

        myPathList=new ArrayList<>();
        adapter= new CollectionRouteAdapter(myPathList);
        listView.setAdapter(adapter);
        listView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        adapter.setOnItemClickListener(this);
        adapter.setOnItemLongClickListener(this);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (Test.getInstance().loginOrNot==1){      //登陆
            user=Test.getInstance().user;
            refresh();
        }
    }

    private void refresh() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                RequestBody requestBody = new FormBody.Builder()
                        .add("user_id", user.getUserId()+"")
                        .build();
                Request request = new Request.Builder()
                        .url("http://120.77.170.124:8080/busis/collection/query.do")
                        .post(requestBody)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String s=response.body().string();
                        Log.i(TAG, "onResponse: "+s);
                        ResponseData responseData=new Gson().fromJson(s,new TypeToken<ResponseData<List<CollectionRoute>>>(){}.getType());

                        Message message=new Message();
                        if (responseData.getCode()==1){
                            myPathList.clear();
                            list= (List<CollectionRoute>) responseData.getData();
                            for (CollectionRoute cr : list){
                                myPathList.add(cr.getRoute_information());
                            }
                            message.what=2;
                            handler.sendMessage(message);
                        }else{
                            message.what=1;
                            handler.sendMessage(message);
                        }

                    }
                });
            }
        });
    }

    @Override
    public void onItemClick(View v, int position) {
        initPopUpWindow();
        popLinearLayout.removeAllViews();
        List<MyRoute> myRoutes=myPathList.get(position).getRoutes();
        for (int i=0;i<myRoutes.size();i++){
            MyRoute myRoute=myRoutes.get(i);
            @SuppressLint("InflateParams") View v1 = getLayoutInflater().inflate(R.layout.pop_item, null);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            v1.setLayoutParams(lp);
            TextView t1, t2, t3, t4, t6;
            t1 = v1.findViewById(R.id.from_loc);
            t2 = v1.findViewById(R.id.walk);
            t3 = v1.findViewById(R.id.bus_begin);
            t4 = v1.findViewById(R.id.bus_name);
            t6 = v1.findViewById(R.id.bus_end);
            t1.setText(myRoute.getStart());
            t2.setText(myRoute.getWalk());
            t4.setText(myRoute.getBus());
            t3.setText(myRoute.getEntrance());
            t6.setText(myRoute.getOut());

            if (t1.getText().toString().trim().equals("")) t1.setVisibility(View.GONE);
            if (t2.getText().toString().trim().equals("")) t2.setVisibility(View.GONE);
            if (t3.getText().toString().trim().equals("")) t3.setVisibility(View.GONE);
            if (t4.getText().toString().trim().equals("")) t4.setVisibility(View.GONE);
            if (t6.getText().toString().trim().equals("")) t6.setVisibility(View.GONE);

            if (t6.getText().toString().contains("终点"))
                t6.setTextColor(getResources().getColor(R.color.beginEnd));

            popLinearLayout.addView(v1);
        }
        popupWindow.setContentView(popContentView);
        @SuppressLint("InflateParams") View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_collection, null);
        popupWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
    }

    @SuppressLint("InflateParams")
    private void initPopUpWindow(){
        popContentView = LayoutInflater.from(getActivity()).inflate(R.layout.pop_window_bus_path, null);
        popLinearLayout = popContentView.findViewById(R.id.pop_linear_layout);
        popupWindow = new PopupWindow(getActivity());
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0));
        popupWindow.setTouchable(true);
        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return false;
            }
        });
        popupWindow.setAnimationStyle(R.style.PopupAnimation);
        popupWindow.setClippingEnabled(true);
        popupWindow.setFocusable(true);

    }

    @Override
    public void onItemLongClick(View v, final int position) {
        Log.i(TAG, "onItemLongClick: ");
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setItems(new String[]{"删除"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteRoute(position);
            }
        }).show();
    }

    public void deleteRoute(final int position){
        final int collectionID = list.get(position).getCollectionID();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                RequestBody requestBody = new FormBody.Builder()
                        .add("collection_id", String.valueOf(collectionID))
                        .build();
                Request request = new Request.Builder()
                        .url("http://120.77.170.124:8080/busis/collection/delete.do")
                        .post(requestBody)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String s = response.body().string();
                        Log.i(TAG, "onResponse:删除路线 " + s);
                        Message message = new Message();
                        ResponseData responseData = new Gson().fromJson(s, new TypeToken<ResponseData<Result>>() {
                        }.getType());
                        if (responseData.getCode() == 1) {
                            myPathList.remove(position);
                            message.what = 2;
                            handler.sendMessage(message);
                        } else {
                            message.what = 1;
                            handler.sendMessage(message);
                        }
                    }
                });

            }
        });
    }
}
