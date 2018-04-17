package com.example.y.routeplanner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;



@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {
    private static final int SHOW_TOAST=1;
    public static final int SEARCH_POINT=1;
    public static final int SEARCH_START_POINT=2;
    public static final int SEARCH_END_POINT=3;
    public static final int SEARCH_BUS_LINT=4;
    public static final int SEARCH_BUS_STEP=5;
    private RecyclerView.Adapter adapter;


    @SuppressLint("HandlerLeak")
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SHOW_TOAST:
                    String s=msg.getData().getString("toast");
                    Toast.makeText(getApplicationContext(),s, Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };


    public void initPopUpWindow(PopupWindow popupWindow){
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
    public void sendMessage(String info){
        Message message=new Message();
        message.what=1;
        Bundle bundle=new Bundle();
        bundle.putString("toast",info);
        message.setData(bundle);
        handler.sendMessage(message);
    }
    public void sendMessage(RecyclerView.Adapter adapter){
        Message message=new Message();
        message.what=2;
        this.adapter=adapter;
        handler.sendMessage(message);

    }


    public void setToolBar(String s, final Context context, final int requestCode){
        LinearLayout linearLayout = findViewById(R.id.toolbar_content);
        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.toolbar_search, null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        TextView search = view.findViewById(R.id.search_poi);
        search.setText(s);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SearchTipActivity.class);
                intent.putExtra("requestCode",requestCode);
                startActivityForResult(intent, requestCode);
            }
        });
        linearLayout.addView(view);
        showToolBar();

    }
    public void showToolBar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
    }

}
