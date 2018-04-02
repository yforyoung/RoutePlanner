package com.example.y.routeplanner;

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
import com.amap.api.services.busline.BusStationItem;
import com.amap.api.services.busline.BusStationQuery;
import com.amap.api.services.busline.BusStationResult;
import com.amap.api.services.busline.BusStationSearch;
import com.amap.api.services.help.Tip;
import com.example.y.routeplanner.adapter.BusLineAdapter;
import com.example.y.routeplanner.adapter.BusStepAdapter;
import com.example.y.routeplanner.util.Test;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class SearchBusStepActivity extends BaseActivity implements BusStationSearch.OnBusStationSearchListener{
    private TextView stationName;
    private BusStepAdapter adapter;

    private List<BusLineItem> list;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_search_bus_step);
        setToolBar("搜索站台",SearchBusStepActivity.this,SEARCH_BUS_STEP);

        stationName=findViewById(R.id.station_name);
        stationName.setVisibility(View.INVISIBLE);

        //初始化recyclerView
        RecyclerView listView = findViewById(R.id.bus_step_list);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        listView.setLayoutManager(manager);
        list=new ArrayList<>();
        adapter=new BusStepAdapter(list);
        listView.setAdapter(adapter);
        listView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapter.setOnItemClickListener(new BusStepAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
               showLine(position);
            }
        });
    }

    private void showLine(int position) {

    }


    @Override
    public void onBusStationSearched(BusStationResult busStationResult, int i) {
        stationName.setVisibility(View.VISIBLE);
        list.clear();
        BusStationItem stationItem=busStationResult.getBusStations().get(0);
        list.addAll(stationItem.getBusLineItems());
        adapter.notifyDataSetChanged();
        stationName.setText(stationItem.getBusStationName());

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==SEARCH_BUS_STEP&&resultCode==RESULT_OK){
            searchBusStep((Tip) data.getBundleExtra("tip").getParcelable("tip"));
        }
    }
    private void searchBusStep(Tip tip){
        BusStationQuery query=new BusStationQuery(tip.getName(), Test.getInstance().cityCode);
        BusStationSearch search=new BusStationSearch(this,query);
        search.setOnBusStationSearchListener(this);
        search.searchBusStationAsyn();
    }
}