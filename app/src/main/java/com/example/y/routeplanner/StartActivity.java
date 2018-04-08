package com.example.y.routeplanner;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.example.y.routeplanner.gson.User;
import com.example.y.routeplanner.util.Test;
import com.example.y.routeplanner.util.Util;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;


@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class StartActivity extends BaseActivity implements View.OnClickListener, AMapLocationListener,NavigationView.OnNavigationItemSelectedListener {
    private TextView name, tel,cityShow;
    private Button load;
    private LinearLayout view;
    private DrawerLayout drawerLayout;
    Intent intent;

    private String[] permissions = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
    };
    private List<String> pList = new ArrayList<>();
    private Util util;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        intent = new Intent();

        request();          //请求权限
        initView();         //初始化视图
        util = new Util();
        SharedPreferences spf = getSharedPreferences("user", MODE_PRIVATE);
        if (spf.getInt("login", 0) == 1) {                       //获取本地用户信息,设置单例
            Test.getInstance().loginOrNot = 1;

            initUser();
        }

        initUserInfo();
        getLocation();

    }

    private void initUser() {
        Test.getInstance().user = new Gson().fromJson(util.read(getApplicationContext()), User.class);
    }

    public void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar_main);
        toolbar.setTitle("");
        cityShow=toolbar.findViewById(R.id.city_show);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        NavigationView navigationView = findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(this);
        drawerLayout = findViewById(R.id.drawer_layout);




        load = navigationView.getHeaderView(0).findViewById(R.id.user_log);
        name = navigationView.getHeaderView(0).findViewById(R.id.user_name);
        tel = navigationView.getHeaderView(0).findViewById(R.id.user_tel);
        ImageView profile = navigationView.getHeaderView(0).findViewById(R.id.user_profile);
        view = navigationView.getHeaderView(0).findViewById(R.id.user_info_view);


        LinearLayout searchLocation = findViewById(R.id.search_location);
        LinearLayout searchBusPath = findViewById(R.id.search_bus_path);
        LinearLayout collection = findViewById(R.id.collection);
        LinearLayout searchBusLine = findViewById(R.id.search_bus_line);
        LinearLayout searchBusStep = findViewById(R.id.search_bus_step);



        load.setOnClickListener(this);
        profile.setOnClickListener(this);

        searchLocation.setOnClickListener(this);
        searchBusPath.setOnClickListener(this);
        collection.setOnClickListener(this);
        searchBusLine.setOnClickListener(this);
        searchBusStep.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {

            drawerLayout.closeDrawer(GravityCompat.START);

        } else {

            super.onBackPressed();

        }
    }

    public void request() {
        pList.clear();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(StartActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
                pList.add(permission);
            }
        }
        if (!pList.isEmpty()) {
            String[] permissions = pList.toArray(new String[pList.size()]);//将List转为数组
            ActivityCompat.requestPermissions(StartActivity.this, permissions, 1);
        }
    }



    @Override

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    boolean showRequestPermission = ActivityCompat.shouldShowRequestPermissionRationale(StartActivity.this, permissions[i]);
                    if (showRequestPermission) {
                        Toast.makeText(this, "您拒绝了权限，定位功能将无法正常使用！", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initUserInfo();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.user_log:
                intent.setClass(this, LoadActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.user_profile:
                Toast.makeText(StartActivity.this, "click profile", Toast.LENGTH_SHORT).show();
                break;
            case R.id.search_bus_path:
                intent.setClass(this, SearchPathActivity.class);
                startActivity(intent);
                break;
            case R.id.search_location:
                intent.setClass(this, SearchPointActivity.class);
                startActivity(intent);
                break;
            case R.id.search_bus_line:
                intent.setClass(this, SearchBusLineActivity.class);
                startActivity(intent);
                break;
            case R.id.search_bus_step:
                intent.setClass(this, SearchBusStepActivity.class);
                startActivity(intent);
                break;
            case R.id.collection:
                intent.setClass(this, CollectionActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void initUserInfo() {
        if (Test.getInstance().loginOrNot == 1) {      //登陆
            User user = Test.getInstance().user;
            load.setVisibility(View.GONE);
            view.setVisibility(View.VISIBLE);
            name.setText(user.getUserName());
            tel.setText(user.getTelphone());

        } else {
            view.setVisibility(View.GONE);
            load.setVisibility(View.VISIBLE);
        }
    }


    public void getLocation() {

        AMapLocationClient locationClient = new AMapLocationClient(StartActivity.this);
        locationClient.setLocationListener(this);
        AMapLocationClientOption locationClientOption = new AMapLocationClientOption();
        locationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        locationClientOption.setOnceLocation(true);
        locationClient.setLocationOption(locationClientOption);
        locationClient.startLocation();

    }


    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                Test.getInstance().cityCode = aMapLocation.getCityCode();   //设置cityCode
                Test.getInstance().aMapLocation = aMapLocation;
                cityShow.setText(aMapLocation.getCity());
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.setting:
                intent.setClass(this, SetActivity.class);
                startActivity(intent);
                break;
            case R.id.exit:
                finish();
                break;
            case R.id.log_out:
                SharedPreferences.Editor editor=getSharedPreferences("user",MODE_PRIVATE).edit();
                editor.putInt("login",0);
                editor.apply();
                Test.getInstance().user=null;
                Test.getInstance().loginOrNot=0;    //设置未登陆
                finish();
                break;
            case R.id.city:
                Toast.makeText(this, "更换城市", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }
}
