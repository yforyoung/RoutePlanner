package com.example.y.routeplanner.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.BusStep;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteBusLineItem;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.example.y.routeplanner.BaseActivity;
import com.example.y.routeplanner.R;
import com.example.y.routeplanner.adapter.CollectionRouteAdapter;
import com.example.y.routeplanner.gson.CollectionRoute;
import com.example.y.routeplanner.gson.ResponseData;
import com.example.y.routeplanner.gson.User;
import com.example.y.routeplanner.util.Test;
import com.example.y.routeplanner.util.Util;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;

import static android.content.ContentValues.TAG;


public class CollectionRouteFragment extends Fragment implements CollectionRouteAdapter.OnItemClickListener, CollectionRouteAdapter.OnItemLongClickListener {
    private User user;
    private CollectionRouteAdapter adapter;
    private List<CollectionRoute> list = new ArrayList<>();
    private Util util = new Util();
    private CollectionRoute collectionRoute;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_collection_path, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        RecyclerView listView = getActivity().findViewById(R.id.co_path);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        listView.setLayoutManager(manager);

        adapter = new CollectionRouteAdapter(list);
        listView.setAdapter(adapter);
        listView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        adapter.setOnItemClickListener(this);
        adapter.setOnItemLongClickListener(this);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (Test.getInstance().loginOrNot == 1) {      //登陆
            user = Test.getInstance().user;
            refresh();
        }
    }


    private void refresh() {
        RequestBody requestBody = new FormBody.Builder()
                .add("user_id", user.getUserId() + "")
                .build();
        Request request = new Request.Builder()
                .url("http://120.77.170.124:8080/busis/collection/query.do")
                .post(requestBody)
                .build();

        util.setHandleResponse(new Util.handleResponse() {
            @Override
            public void handleResponses(String response) {
                Log.i(TAG, "handleResponses: " + response);
                ResponseData responseData = new Gson().fromJson(response, new TypeToken<ResponseData<List<CollectionRoute>>>() {
                }.getType());

                if (responseData.getCode() == 1) {
                    list.clear();
                    list.addAll((List<CollectionRoute>) responseData.getData());
                    ((BaseActivity) getActivity()).sendMessage(adapter);
                }
            }
        });
        util.doPost((AppCompatActivity) getActivity(), request);
    }

    @SuppressLint("InflateParams")
    @Override
    public void onItemClick(View v, int position) {
        Log.i(TAG, "onItemClick: " + list.get(position).getRoute_information());
        collectionRoute=list.get(position);
        searchRout(collectionRoute);
    }

    private void searchRout(final CollectionRoute collectionRoute) {
        RouteSearch routeSearch = new RouteSearch(getActivity());
        LatLonPoint from = new LatLonPoint(Double.parseDouble(collectionRoute.getStart_latitude())
                , Double.parseDouble(collectionRoute.getStart_longitude()));
        LatLonPoint end = new LatLonPoint(Double.parseDouble(collectionRoute.getEnd_latitude())
                , Double.parseDouble(collectionRoute.getEnd_longitude()));
        RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
                from,
                end);
        RouteSearch.BusRouteQuery query = new RouteSearch.BusRouteQuery(fromAndTo, RouteSearch.BUS_LEASE_WALK, collectionRoute.getArea(), 0);
        routeSearch.setRouteSearchListener(new RouteSearch.OnRouteSearchListener() {
            @Override
            public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {
                List<BusPath> busPaths = busRouteResult.getPaths();
                for (BusPath b : busPaths) {
                    List<BusStep> steps = b.getSteps();
                    for (int j = 0; j < steps.size(); j++) {
                        if (!steps.get(j).getBusLines().get(0).getBusLineName().contains(collectionRoute.getRoute_information().get(j)))
                            return;
                        else
                            j++;
                        showCollectionRoute(b);
                    }
                }

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
        });
        routeSearch.calculateBusRouteAsyn(query);
    }

    @SuppressLint("SetTextI18n")
    private void showCollectionRoute(BusPath b) {
        @SuppressLint("InflateParams") View popContentView = LayoutInflater.from(getActivity()).inflate(R.layout.pop_window_bus_path, null);
        LinearLayout popLinearLayout = popContentView.findViewById(R.id.pop_linear_layout);
        LinearLayout co = popContentView.findViewById(R.id.coll_button);
        co.setVisibility(View.INVISIBLE);

        PopupWindow popupWindow = new PopupWindow(getActivity());
        ((BaseActivity) getActivity()).initPopUpWindow(popupWindow);
        popLinearLayout.removeAllViews();
        List<BusStep> stepList = b.getSteps();

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

                t1.setText(collectionRoute.getStart_point()+ "（起点）");

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

                t5.setText(routeBusLineItems.get(0).getArrivalBusStation().getBusStationName() + "下车");//到达点

            } else {
                t3.setVisibility(View.GONE);
                t3.setVisibility(View.GONE);
                t4.setVisibility(View.GONE);
                t5.setTextColor(getResources().getColor(R.color.beginEnd));
                t5.setText(collectionRoute.getEnd_point()+ "（终点）");
            }

            popLinearLayout.addView(v1);

        }
        popupWindow.setContentView(popContentView);
        @SuppressLint("InflateParams") View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.activity_collection, null);
        popupWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);

    }

    @Override
    public void onItemLongClick(View v, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(new String[]{"删除"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteRoute(position);
            }
        }).show();
    }

    public void deleteRoute(final int position) {
        final int collectionID = list.get(position).getCollectionID();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                RequestBody requestBody = new FormBody.Builder()
                        .add("collection_id", String.valueOf(collectionID))
                        .build();
                Request request = new Request.Builder()
                        .url("http://120.77.170.124:8080/busis/collection/delete.do")
                        .post(requestBody)
                        .build();
                util.setHandleResponse(new Util.handleResponse() {
                    @Override
                    public void handleResponses(String response) {
                        Log.i(TAG, "handleResponses: " + response);

                        ResponseData responseData = new Gson().fromJson(response, new TypeToken<ResponseData<String>>() {
                        }.getType());
                        if (responseData.getCode() == 1) {
                            list.remove(position);
                            ((BaseActivity) getActivity()).sendMessage(adapter);
                        }
                    }
                });

                util.doPost((AppCompatActivity) getActivity(), request);

            }
        });
    }
}
