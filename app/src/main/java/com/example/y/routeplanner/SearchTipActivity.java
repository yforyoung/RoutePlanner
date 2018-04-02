package com.example.y.routeplanner;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.amap.api.services.busline.BusLineItem;
import com.amap.api.services.busline.BusLineQuery;
import com.amap.api.services.busline.BusLineResult;
import com.amap.api.services.busline.BusLineSearch;
import com.amap.api.services.busline.BusStationItem;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.example.y.routeplanner.adapter.LocalAdapter;
import com.example.y.routeplanner.util.Test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class SearchTipActivity extends BaseActivity implements Inputtips.InputtipsListener {
    private EditText search;
    private RecyclerView recyclerView;
    private List<Tip> tipList;
    private InputtipsQuery query;
    private Inputtips inputtips;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_search_tip);
        search = findViewById(R.id.search_edit);
        recyclerView = findViewById(R.id.search_list);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        tipList = new ArrayList<>();


        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                query = new InputtipsQuery(search.getText().toString(), Test.getInstance().cityCode);
                query.setCityLimit(true);
                inputtips = new Inputtips(getApplicationContext(), query);
                inputtips.setInputtipsListener(SearchTipActivity.this);
                inputtips.requestInputtipsAsyn();
            }
        });
    }


    @Override
    public void onGetInputtips(final List<Tip> list, int i) {       //在这里判断提示信息的类型  显示对应信息
        if (i == 1000) {
            if (list != null) {
                tipList = list;
                LocalAdapter adapter = new LocalAdapter(tipList);
                recyclerView.setAdapter(adapter);
                adapter.setOnItemClickListener(new LocalAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("tip", list.get(position));
                        intent.putExtra("tip", bundle);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                    //点击事件
                });

            }

        }

    }
}
