package com.example.y.routeplanner;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import android.widget.LinearLayout;

import android.widget.TextView;

import com.amap.api.services.busline.BusLineItem;
import com.amap.api.services.busline.BusLineQuery;
import com.amap.api.services.busline.BusLineResult;
import com.amap.api.services.busline.BusLineSearch;
import com.amap.api.services.busline.BusStationItem;
import com.amap.api.services.help.Tip;
import com.example.y.routeplanner.adapter.BusLineAdapter;
import com.example.y.routeplanner.util.Test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


@SuppressLint("Registered")
public class SearchBusLineActivity extends BaseActivity implements BusLineSearch.OnBusLineSearchListener{
    private TextView name, first, last, distance, price;
    private LinearLayout lineHead;
    private BusLineAdapter adapter;
    private List<BusStationItem> list;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_search_bus_line);
        setToolBar("搜索公交线路",SearchBusLineActivity.this,SEARCH_BUS_LINT);

        lineHead=findViewById(R.id.line_head);
        lineHead.setVisibility(View.INVISIBLE);

        //初始化recyclerView
        RecyclerView listView = findViewById(R.id.bus_line_list);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        listView.setLayoutManager(manager);
        list=new ArrayList<>();
        adapter=new BusLineAdapter(list);
        listView.setAdapter(adapter);
        listView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapter.setOnItemClickListener(new BusLineAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                showStep(position);
            }
        });

        name = findViewById(R.id.bus_line_name);
        first = findViewById(R.id.first_bus);
        last = findViewById(R.id.last_bus);
        distance = findViewById(R.id.bus_line_distance);
        price = findViewById(R.id.bus_price);

        String busId=getIntent().getStringExtra("busId");
        if (busId!=null){
            searchBusLine(busId);
        }

    }

    private void showStep(int position) {
        Intent intent=new Intent(this,SearchBusStepActivity.class);
        intent.putExtra("stationName",list.get(position).getBusStationName());
        startActivity(intent);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBusLineSearched(BusLineResult busLineResult, int i) {
        lineHead.setVisibility(View.VISIBLE);
        list.clear();
        List<BusLineItem> busLineItems = busLineResult.getBusLines();
        BusLineItem busLineItem = busLineItems.get(0);

        List<BusStationItem> busStationItems=busLineItem.getBusStations();

        list.addAll(busStationItems);
        adapter.notifyDataSetChanged();

        name.setText(busLineItem.getBusLineName());
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        first.setText(sdf.format(busLineItem.getFirstBusTime()));
        last.setText(sdf.format(busLineItem.getLastBusTime()));
        distance.setText("全程：" + (int) busLineItem.getDistance() + "公里");
        price.setText("票价：" + busLineItem.getTotalPrice() + "元");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==SEARCH_BUS_LINT){
            if (resultCode==RESULT_OK){
                Tip tip=data.getBundleExtra("tip").getParcelable("tip");
                assert tip != null;
                searchBusLine(tip.getPoiID());
            }
        }
    }


    private void searchBusLine(String poiId) {
        //路线查询
        BusLineQuery busLineQuery = new BusLineQuery(poiId, BusLineQuery.SearchType.BY_LINE_ID, Test.getInstance().cityCode);
        BusLineSearch busLineSearch = new BusLineSearch(this, busLineQuery);
        busLineSearch.setOnBusLineSearchListener(this);
        busLineSearch.searchBusLineAsyn();

    }
}
