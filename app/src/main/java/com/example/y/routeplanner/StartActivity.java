package com.example.y.routeplanner;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.bumptech.glide.Glide;
import com.example.y.routeplanner.adapter.RollPagerAdapter;
import com.example.y.routeplanner.gson.ResponseData;
import com.example.y.routeplanner.gson.User;
import com.example.y.routeplanner.util.Test;
import com.example.y.routeplanner.util.Util;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.hintview.ColorPointHintView;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;

import okhttp3.Request;
import okhttp3.RequestBody;


@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class StartActivity extends BaseActivity implements View.OnClickListener, AMapLocationListener, NavigationView.OnNavigationItemSelectedListener {
    private static final int OPEN_ALBUM = 6;
    private TextView name, tel, cityShow;
    private Button load;
    private LinearLayout view;
    private DrawerLayout drawerLayout;
    private CircleImageView profile;
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
        setContentView(R.layout.activity_main);
        intent = new Intent();

        request();          //请求权限
        initView();         //初始化视图
        util = new Util();
        getLocation();
    }


    public void initView() {        //初始化视图
        Toolbar toolbar = findViewById(R.id.toolbar_main);
        toolbar.setTitle("");
        cityShow = toolbar.findViewById(R.id.city_show);
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

        RollPagerView rollPagerView = findViewById(R.id.roll_view_pager);       //滚动效果 使用rollviewpager框架
        rollPagerView.setPlayDelay(5000);
        rollPagerView.setHintView(new ColorPointHintView(this, Color.parseColor("#ff228c8a"), Color.WHITE));
        rollPagerView.setAdapter(new RollPagerAdapter());


        load = navigationView.getHeaderView(0).findViewById(R.id.user_log);
        name = navigationView.getHeaderView(0).findViewById(R.id.user_name);
        tel = navigationView.getHeaderView(0).findViewById(R.id.user_tel);
        profile = navigationView.getHeaderView(0).findViewById(R.id.user_profile);
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

    public void request() {         //请求权限
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
        initUserInfo();     //初始化用户信息
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.user_log:
                intent.setClass(this, LoadActivity.class);
                startActivity(intent);
                break;
            case R.id.user_profile:
                openAlbum();
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

        SharedPreferences spf = getSharedPreferences("user", MODE_PRIVATE);
        if (spf.getInt("login", 0) == 1) {                       //获取本地用户信息,设置单例
            Test.getInstance().loginOrNot = 1;
            Test.getInstance().user = new Gson().fromJson(util.read(getApplicationContext()), User.class);
            User user = Test.getInstance().user;
            load.setVisibility(View.GONE);
            view.setVisibility(View.VISIBLE);
            name.setText(user.getUserName());
            tel.setText(user.getTelphone());

            if (!readPicPathFromLocal().equals("")) {       //设置头像 本地有则读取本地图片 无则加载网络图片
                File file = new File(readPicPathFromLocal());
                if (file.exists())
                    setProfile(readPicPathFromLocal());
                else
                    loadProfileFromServer();
            } else {
                loadProfileFromServer();
            }
        } else {
            view.setVisibility(View.GONE);
            load.setVisibility(View.VISIBLE);
        }

    }

    private void setProfile(final String imagePath) {       //设置头像
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            final BitmapDrawable bd = new BitmapDrawable(bitmap);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                profile.setImageDrawable(bd);
            }
        } else {
            Toast.makeText(this, "获取图片失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadProfileFromServer() {            //加载网络头像
        String profilePath = Test.getInstance().user.getHeadPortrail();
        if (!profilePath.equals("")) {
            Glide.with(this).load(profilePath)
                    .error(R.drawable.load_error)
                    .into(profile);
        }
    }


    public void getLocation() {     //获取定位信息

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
        switch (item.getItemId()) {
            case R.id.setting:
                if (Test.getInstance().loginOrNot == 0) {
                    Toast.makeText(this, "请先登陆!", Toast.LENGTH_SHORT).show();
                } else {
                    intent.setClass(this, SetActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.exit:
                finish();
                break;
            case R.id.log_out:
                if (Test.getInstance().loginOrNot == 0) {
                    Toast.makeText(this, "请先登陆!", Toast.LENGTH_SHORT).show();
                } else {
                    SharedPreferences.Editor editor = getSharedPreferences("user", MODE_PRIVATE).edit();
                    editor.putInt("login", 0);
                    editor.apply();
                    Test.getInstance().user = null;
                    Test.getInstance().loginOrNot = 0;    //设置未登陆
                    intent.setClass(this, LoadActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.change_passwd:        //dialog
                if (Test.getInstance().loginOrNot == 0) {
                    Toast.makeText(this, "请先登陆!", Toast.LENGTH_SHORT).show();
                } else {
                    changePasswd();
                }
                break;
            case R.id.notify:
                intent.setClass(this, NotifyActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    private void changePasswd() {       //修改密码

        @SuppressLint("InflateParams") View view = LayoutInflater.from(this).inflate(R.layout.dialog_chang_passwd, null);
        final EditText op = view.findViewById(R.id.old_passwd);
        final EditText np = view.findViewById(R.id.new_passwd);

        new AlertDialog.Builder(this).setTitle("修改密码")
                .setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String sOp = op.getText().toString().trim();
                        String sNp = np.getText().toString().trim();
                        RequestBody requestBody = new FormBody.Builder()
                                .add("user_id", Test.getInstance().user.getUserId())
                                .add("old_password", sOp)
                                .add("new_password", sNp)
                                .build();
                        Request request = new Request.Builder()
                                .url("http://120.77.170.124:8080/busis/user/modify/password.do")
                                .post(requestBody)
                                .build();
                        Util util = new Util();
                        util.setHandleResponse(new Util.handleResponse() {
                            @Override
                            public void handleResponses(String response) {

                                ResponseData responseData = new Gson().fromJson(response, new TypeToken<ResponseData<String>>() {
                                }.getType());
                                sendMessage(responseData.getMessage());
                            }
                        });
                        util.doPost(StartActivity.this, request);


                    }
                })
                .setNegativeButton("取消", null)
                .show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case OPEN_ALBUM:
                if (resultCode == RESULT_OK) {
                    if (Build.VERSION.SDK_INT >= 19) {
                        handleImageOnKitKat(data);
                    }
                }
        }
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {     //头像处理 4.4之后可用
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            assert uri != null;
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android..providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads//public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else {
            assert uri != null;
            if ("content".equalsIgnoreCase(uri.getScheme())) {
                imagePath = getImagePath(uri, null);
            } else if ("file".equalsIgnoreCase(uri.getScheme())) {
                imagePath = uri.getPath();
            }
        }                       //读取图片路径

        //保存图片到本地
        savePicPathToLocal(imagePath);

        //设置头像
        setProfile(imagePath);

        //保存图片到服务器
        savePicPathToServer(imagePath);




    }

    public void savePicPathToServer(String imagePath) {

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", imagePath, RequestBody.create(MediaType.parse("image/*"), new File(imagePath)))
                .addFormDataPart("user_id", Test.getInstance().user.getUserId())
                .build();

        Request request = new Request.Builder()
                .url("http://120.77.170.124:8080/busis/user/modify/head_portrail/file.do")
                .post(requestBody)
                .build();

        util.setHandleResponse(new Util.handleResponse() {
            @Override
            public void handleResponses(String response) {

                Log.i("head", "handleResponses: " + response);
                //保存到userFile
                User user = Test.getInstance().user;
                user.setHeadPortrail(response);
                Gson gson = new Gson();
                String data = gson.toJson(user);
                util.save(data, getApplicationContext());
            }
        });
        util.doPost(StartActivity.this, request);

    }

    public void savePicPathToLocal(final String picPath) {
        SharedPreferences.Editor editor = getSharedPreferences("sher" + Test.getInstance().user.getUserId(), MODE_PRIVATE).edit();
        editor.putString("pic_path", picPath);
        editor.apply();     //存本地
    }

    public String readPicPathFromLocal() {
        SharedPreferences preferences = getSharedPreferences("sher" + Test.getInstance().user.getUserId(), MODE_PRIVATE);
        return preferences.getString("pic_path", "");
    }


    private String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, OPEN_ALBUM);
    }
}
