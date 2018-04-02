package com.example.y.routeplanner;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.services.busline.BusLineItem;
import com.amap.api.services.busline.BusLineQuery;
import com.amap.api.services.busline.BusLineResult;
import com.amap.api.services.busline.BusLineSearch;
import com.amap.api.services.busline.BusStationItem;
import com.amap.api.services.help.Tip;
import com.example.y.routeplanner.util.Test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


@SuppressLint("Registered")
public class SearchBusLineActivity extends BaseActivity implements BusLineSearch.OnBusLineSearchListener{
    private TextView name, first, last, distance, price;
    private LinearLayout lineHead;
    private ArrayAdapter<String> busLineItemArrayAdapter;
    private List<String> busStationNameList = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_search_bus_line);
        setToolBar("搜索公交线路",SearchBusLineActivity.this,4);

        lineHead=findViewById(R.id.line_head);
        lineHead.setVisibility(View.INVISIBLE);

        ListView listView = findViewById(R.id.bus_line_list);

        busLineItemArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, busStationNameList);
        listView.setAdapter(busLineItemArrayAdapter);

        name = findViewById(R.id.bus_line_name);
        first = findViewById(R.id.first_bus);
        last = findViewById(R.id.last_bus);
        distance = findViewById(R.id.bus_line_distance);
        price = findViewById(R.id.bus_price);


    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBusLineSearched(BusLineResult busLineResult, int i) {
        lineHead.setVisibility(View.VISIBLE);
        busStationNameList.clear();
        List<BusLineItem> busLineItems = busLineResult.getBusLines();
        BusLineItem busLineItem = busLineItems.get(0);
        name.setText(busLineItem.getBusLineName());
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        first.setText(sdf.format(busLineItem.getFirstBusTime()));
        last.setText(sdf.format(busLineItem.getLastBusTime()));
        distance.setText("全程：" + (int) busLineItem.getDistance() + "公里");
        price.setText("票价：" + busLineItem.getTotalPrice() + "元");

        for (BusStationItem bs : busLineItem.getBusStations()) {
            busStationNameList.add(bs.getBusStationName());
        }
        busLineItemArrayAdapter.notifyDataSetChanged();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==SEARCH_BUS_LINT){
            if (resultCode==RESULT_OK){
                searchBusLine((Tip) data.getBundleExtra("tip").getParcelable("tip"));
            }
        }
    }


    private void searchBusLine(Tip tip) {
        //路线查询
        BusLineQuery busLineQuery = new BusLineQuery(tip.getPoiID(), BusLineQuery.SearchType.BY_LINE_ID, Test.getInstance().cityCode);
        BusLineSearch busLineSearch = new BusLineSearch(this, busLineQuery);
        busLineSearch.setOnBusLineSearchListener(this);
        busLineSearch.searchBusLineAsyn();

    }
}
