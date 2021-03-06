package com.example.y.routeplanner;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;

import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.help.Tip;
import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.BusStep;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteBusLineItem;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.example.y.routeplanner.adapter.RouteAdapter;
import com.example.y.routeplanner.gson.ResponseData;
import com.example.y.routeplanner.gson.Result;
import com.example.y.routeplanner.util.Test;
import com.example.y.routeplanner.util.Util;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;


//查询换乘路线
public class SearchPathActivity extends BaseActivity implements View.OnClickListener, RouteSearch.OnRouteSearchListener, RouteAdapter.OnItemClickListener {
    private TextView begin, end;
    private ImageView repeat, searchButon;
    private Intent intent;
    private LatLonPoint from, to;
    private AMapLocation aMLocation;
    private String cityCode = "";
    private RecyclerView routeListView;
    private RouteAdapter adapter;
    private List<BusPath> busPaths = new ArrayList<>();
    private PopupWindow popupWindow;
    private View popContentView;
    private LinearLayout popLinearLayout;
    private Tip fromTip, endTip;
    private ArrayList<String> busLineNameSave = new ArrayList<>();

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_path);
        initView();
        aMLocation = Test.getInstance().aMapLocation;
        if (aMLocation != null) {
            from = new LatLonPoint(aMLocation.getLatitude(), aMLocation.getLongitude());
            cityCode = Test.getInstance().cityCode;

        } else {
            Toast.makeText(this, "定位失败！请检查相关设置或网络状态", Toast.LENGTH_SHORT).show();
            begin.setText("请输入起点");
        }


        LinearLayoutManager manager = new LinearLayoutManager(SearchPathActivity.this);
        routeListView.setLayoutManager(manager);
        adapter = new RouteAdapter(busPaths);
        adapter.setOnItemClickListener(this);

        routeListView.setAdapter(adapter);
        routeListView.addItemDecoration(new DividerItemDecoration(SearchPathActivity.this, DividerItemDecoration.VERTICAL));


        begin.setOnClickListener(this);
        end.setOnClickListener(this);
        repeat.setOnClickListener(this);
        searchButon.setOnClickListener(this);


        intent = new Intent(SearchPathActivity.this, SearchTipActivity.class);


        popContentView = LayoutInflater.from(SearchPathActivity.this).inflate(R.layout.pop_window_bus_path, null);
        popLinearLayout = popContentView.findViewById(R.id.pop_linear_layout);
        LinearLayout button = popContentView.findViewById(R.id.coll_button);
        button.setOnClickListener(this);

        popupWindow = new PopupWindow(SearchPathActivity.this);
        initPopUpWindow(popupWindow);


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 2:
                if (resultCode == Activity.RESULT_OK) {
                    fromTip = data.getBundleExtra("tip").getParcelable("tip");
                    assert fromTip != null;
                    from = fromTip.getPoint();
                    begin.setText(fromTip.getName());

                }
                break;
            case 3:
                if (resultCode == Activity.RESULT_OK) {
                    endTip = data.getBundleExtra("tip").getParcelable("tip");
                    assert endTip != null;
                    to = endTip.getPoint();
                    end.setText(endTip.getName());

                }
                break;
            default:
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.begin:
                startActivityForResult(intent, SEARCH_START_POINT);
                break;
            case R.id.end:
                startActivityForResult(intent, SEARCH_END_POINT);
                break;
            case R.id.repeat:
                String s = begin.getText().toString();
                begin.setText(end.getText().toString());
                end.setText(s);
                if (from != null) {
                    LatLonPoint p = from;
                    from = to;
                    to = p;
                } else {
                    Toast.makeText(this, "请选择一个起点！", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.search_button:
                //搜索
                RouteSearch routeSearch = new RouteSearch(SearchPathActivity.this);
                RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(from, to);
                RouteSearch.BusRouteQuery query = new RouteSearch.BusRouteQuery(fromAndTo, RouteSearch.BUS_LEASE_WALK, cityCode, 0);
                routeSearch.setRouteSearchListener(this);
                routeSearch.calculateBusRouteAsyn(query);
                break;
            case R.id.coll_button:
                if (Test.getInstance().loginOrNot == 1) {          //登陆
                    collectionRoute();
                } else {
                    Toast.makeText(SearchPathActivity.this, "请先登陆再进行操作！", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    private void collectionRoute() {


        new AlertDialog.Builder(SearchPathActivity.this).setTitle("确定收藏本路线？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String fromName;
                        if (fromTip==null){
                            fromName=aMLocation.getPoiName();
                        }else {
                            fromName=fromTip.getName();
                        }
                        Gson gson = new Gson();
                        final String data = gson.toJson(busLineNameSave);
                        RequestBody requestBody = new FormBody.Builder()
                                .add("start_longitude", from.getLongitude() + "")
                                .add("start_latitude", from.getLatitude() + "")
                                .add("end_longitude", to.getLongitude() + "")
                                .add("end_latitude", to.getLatitude() + "")
                                .add("start_point",fromName)
                                .add("area",aMLocation.getCity())
                                .add("end_point", endTip.getName())
                                .add("user_id", Test.getInstance().user.getUserId() + "")
                                .add("route_information", data)
                                .build();
                        Request request = new Request.Builder()
                                .url("http://120.77.170.124:8080/busis/collection/add.do")
                                .post(requestBody)
                                .build();
                        Util util = new Util();
                        util.setHandleResponse(new Util.handleResponse() {
                            @Override
                            public void handleResponses(String response) {

                                ResponseData responseData = new Gson().fromJson(response, new TypeToken<ResponseData<Result>>() {
                                }.getType());
                                sendMessage(responseData.getMessage());
                            }
                        });
                        util.doPost(SearchPathActivity.this, request);

                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }   //收藏路线


    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {
        busPaths.clear();
        busPaths.addAll(busRouteResult.getPaths());
        adapter.notifyDataSetChanged();
        //解析
    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {

    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onItemClick(View v, int position) {

        BusPath busPath = busPaths.get(position);
        List<BusStep> stepList = busPath.getSteps();
        popLinearLayout.removeAllViews();

        busLineNameSave.clear();

        for (int i = 0; i < stepList.size(); i++) {

            BusStep busStep = stepList.get(i);
            @SuppressLint("InflateParams") View v1 = getLayoutInflater().inflate(R.layout.pop_item, null);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            v1.setLayoutParams(lp);

            TextView t1, t2, t3, t4, t5;
            t1 = v1.findViewById(R.id.from_loc);
            t2 = v1.findViewById(R.id.walk);
            t3 = v1.findViewById(R.id.bus_begin);
            t4 = v1.findViewById(R.id.bus_name);
            t5 = v1.findViewById(R.id.bus_end);
            if (i == 0) {
                t1.setTextColor(getResources().getColor(R.color.beginEnd));
                if (fromTip != null)
                    t1.setText(fromTip.getName() + "（起点）");
                else
                    t1.setText(aMLocation.getPoiName() + "（起点）");

            } else
                t1.setVisibility(View.GONE);

            if (busStep.getWalk() != null) {
                t2.setText("步行" + busStep.getWalk().getDistance() + "米");
            } else
                t2.setText("步行0米");

            List<RouteBusLineItem> routeBusLineItems = busStep.getBusLines();    //为空

            if (routeBusLineItems.size() > 0) {//多个

                t3.setText(routeBusLineItems.get(0).getDepartureBusStation().getBusStationName() + "上车");//出发站

                t4.setText(routeBusLineItems.get(0).getBusLineName().substring(0, routeBusLineItems.get(0).getBusLineName().indexOf("(") - 1) + "路");//线路名
                busLineNameSave.add(t4.getText().toString());

                t5.setText(routeBusLineItems.get(0).getArrivalBusStation().getBusStationName() + "下车");//到达点

            } else {
                t3.setVisibility(View.GONE);
                t3.setVisibility(View.GONE);
                t4.setVisibility(View.GONE);
                t5.setTextColor(getResources().getColor(R.color.beginEnd));
                t5.setText(endTip.getName() + "（终点）");
            }

            popLinearLayout.addView(v1);

        }
        popupWindow.setContentView(popContentView);
        @SuppressLint("InflateParams") View rootView = LayoutInflater.from(SearchPathActivity.this).inflate(R.layout.activity_search_path, null);
        popupWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);

    }


    private void initView() {
        begin = findViewById(R.id.begin);
        end = findViewById(R.id.end);
        repeat = findViewById(R.id.repeat);
        searchButon = findViewById(R.id.search_button);
        routeListView = findViewById(R.id.route_list);
    }
}
