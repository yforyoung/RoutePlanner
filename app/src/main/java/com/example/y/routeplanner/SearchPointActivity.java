package com.example.y.routeplanner;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;


import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.EditText;

import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.help.Tip;
import com.example.y.routeplanner.gson.ResponseData;
import com.example.y.routeplanner.gson.Result;
import com.example.y.routeplanner.util.Test;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;


public class SearchPointActivity extends BaseActivity implements LocationSource, AMapLocationListener {

    private MapView mapView;
    private LocationSource.OnLocationChangedListener mListener;
    private AMapLocationClient mapLocationClient;
    private AMap aMap = null;
    private LatLng latLng, myLatLng;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_map);
        mapView = findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);

        setToolBar("搜索地点", SearchPointActivity.this, SEARCH_POINT);


        if (aMap == null) {
            aMap = mapView.getMap();
        }
        aMap.moveCamera(CameraUpdateFactory.zoomTo((float) 15));

        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);
        aMap.setMyLocationStyle(myLocationStyle);//定位蓝点
        aMap.setLocationSource(this);
        aMap.setMyLocationEnabled(true);

        showPoint();        //显示收藏点

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.point_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.coll_point_map:
                if (Test.getInstance().loginOrNot == 1) {         //是否登陆
                    collectionPoint();
                } else {
                    Toast.makeText(SearchPointActivity.this, "请先登陆再进行操作！", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.my_loc_map:
                aMap.setMyLocationEnabled(true);
                move(myLatLng);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void collectionPoint() {

        final EditText et = new EditText(SearchPointActivity.this);
        new AlertDialog.Builder(SearchPointActivity.this).setTitle("请输入收藏点描述")
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        final String input = et.getText().toString();
                        if (input.equals("")) {
                            Toast.makeText(SearchPointActivity.this, "描述不能为空！" + input, Toast.LENGTH_LONG).show();
                        } else {
                            final LatLng l;
                            if (latLng == null)
                                l = myLatLng;
                            else
                                l = latLng;
                            final String longitude = String.valueOf(l.longitude).trim();
                            final String latitude = String.valueOf(l.latitude).trim();
                            Log.i(TAG, "onClick: " + longitude);
                            Log.i(TAG, "onClick: " + latitude);

                            SearchPointActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    OkHttpClient client = new OkHttpClient();
                                    RequestBody requestBody = new FormBody.Builder()
                                            .add("location_name", input)
                                            .add("location_longitude", longitude)
                                            .add("location_latitude", latitude)
                                            .add("user_id", Test.getInstance().user.getUserId())
                                            .build();
                                    Request request = new Request.Builder()
                                            .url("http://120.77.170.124:8080/busis/location/add.do")
                                            .post(requestBody)
                                            .build();
                                    client.newCall(request).enqueue(new Callback() {
                                        @Override
                                        public void onFailure(Call call, IOException e) {

                                        }

                                        @Override
                                        public void onResponse(Call call, Response response) throws IOException {
                                            String s = response.body().string();
                                            Log.i(TAG, "onResponse:   " + s);
                                            ResponseData responseData = new Gson().fromJson(s, new TypeToken<ResponseData<Result>>() {
                                            }.getType());
                                            sendMessage(responseData.getMessage());
                                        }
                                    });

                                }
                            });
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .show();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if (null != mapLocationClient)
            mapLocationClient.onDestroy();
    }

    @Override
    public void activate(LocationSource.OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mapLocationClient == null) {
            mapLocationClient = new AMapLocationClient(getApplicationContext());
            AMapLocationClientOption mapLocationClientOption = new AMapLocationClientOption();
            mapLocationClient.setLocationListener(this);
            mapLocationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            mapLocationClient.setLocationOption(mapLocationClientOption);
            mapLocationClient.startLocation();
        }
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mapLocationClient != null) {
            mapLocationClient.stopLocation();
            mapLocationClient.onDestroy();
        }
        mapLocationClient = null;

    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mListener != null && aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                //可获取定位数据
                myLatLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                mListener.onLocationChanged(aMapLocation);

            } else {
                Toast.makeText(this, "定位失败", Toast.LENGTH_SHORT).show();

            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {//获取查询点
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SEARCH_POINT:
                if (resultCode == Activity.RESULT_OK) {
                    //显示地图点
                    Tip tip = data.getBundleExtra("tip").getParcelable("tip");
                    if (tip.getPoiID() != null && tip.getPoint() != null) {
                        latLng = new LatLng(tip.getPoint().getLatitude(), tip.getPoint().getLongitude());
                        aMap.addMarker(new MarkerOptions().position(latLng).title(tip.getName()));
                        move(latLng);
                    }
                }
                break;
            default:
        }
    }


    private void showPoint() {
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("latlong");
        if (bundle != null) {
            LatLng latLng = bundle.getParcelable("latlong");
            aMap.addMarker(new MarkerOptions().position(latLng).title(""));
            move(latLng);
            aMap.setMyLocationEnabled(false);
        }

    }

    private void move(LatLng latLng) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng, 15, 0, 30));
        aMap.moveCamera(cameraUpdate);
    }
}
