package com.example.y.routeplanner.fragment;



import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.amap.api.maps.model.LatLng;
import com.example.y.routeplanner.BaseActivity;
import com.example.y.routeplanner.SearchPointActivity;
import com.example.y.routeplanner.R;
import com.example.y.routeplanner.adapter.CollectionPointAdapter;
import com.example.y.routeplanner.gson.CollectionPoint;
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


public class CollectionPointFragment extends Fragment implements CollectionPointAdapter.OnItemClickListener, CollectionPointAdapter.OnItemLongClickListener {
    private List<CollectionPoint> list;
    private User user;
    private CollectionPointAdapter adapter;
    private Util util=new Util();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_collection_point, container, false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        RecyclerView listView = getActivity().findViewById(R.id.co_point);
        list = new ArrayList<>();
        adapter = new CollectionPointAdapter(list);

        LinearLayoutManager manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        listView.setLayoutManager(manager);

        if (Test.getInstance().loginOrNot == 1) {      //登陆
            user = Test.getInstance().user;
            refresh();
        }

        listView.setAdapter(adapter);
        listView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        adapter.setOnItemClickListener(this);
        adapter.setOnItemLongClickListener(this);
    }

    private void refresh() {


        RequestBody requestBody = new FormBody.Builder()
                .add("user_id", user.getUserId() + "")
                .build();
        Request request = new Request.Builder()
                .url("http://120.77.170.124:8080/busis/location/query.do")
                .post(requestBody)
                .build();


        util.setHandleResponse(new Util.handleResponse() {
            @Override
            public void handleResponses(String response) {
                ResponseData responseData = new Gson().fromJson(response, new TypeToken<ResponseData<List<CollectionPoint>>>() {
                }.getType());
                if (responseData.getCode() == 1) {
                    list.clear();
                    List<CollectionPoint> collectionPoints = (List<CollectionPoint>) responseData.getData();
                    list.addAll(collectionPoints);
                    ((BaseActivity)getActivity()).sendMessage(adapter);
                }
            }
        });

        util.doPost((AppCompatActivity)getActivity(),request);
    }

    @Override
    public void onItemClick(View v, int position) {
        Double lat = Double.parseDouble(list.get(position).getLocation_latitude());
        Double longitude = Double.parseDouble(list.get(position).getLocation_longitude());
        LatLng latLng = new LatLng(lat, longitude);
        Intent intent = new Intent(getActivity(), SearchPointActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("latlong", latLng);
        intent.putExtra("latlong", bundle);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(View v, final int position) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setItems(new String[]{"删除"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deletePoint(position);
            }
        }).show();


    }


    public void deletePoint(final int position){
        final int locationId = list.get(position).getLocation_id();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RequestBody requestBody = new FormBody.Builder()
                        .add("location_id", String.valueOf(locationId))
                        .build();
                Request request = new Request.Builder()
                        .url("http://120.77.170.124:8080/busis/location/delete.do")
                        .post(requestBody)
                        .build();

                util.setHandleResponse(new Util.handleResponse() {
                    @Override
                    public void handleResponses(String response) {
                        ResponseData responseData = new Gson().fromJson(response, new TypeToken<ResponseData<String>>() {
                        }.getType());
                        if (responseData.getCode() == 1) {
                            list.remove(position);
                            ((BaseActivity)getActivity()).sendMessage(adapter);
                        }
                    }
                });
                util.doPost((AppCompatActivity) getActivity(),request);

            }
        });
    }
}
