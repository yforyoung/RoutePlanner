package com.example.y.routeplanner.util;

import com.amap.api.location.AMapLocation;
import com.example.y.routeplanner.gson.User;



public class Test {
    private Test(){}

    private static Test test;
    public User user;
    public int loginOrNot=0;
    public String cityCode="";
    public int isSetLocation=0;
    public AMapLocation aMapLocation=null;

    public static Test getInstance(){
        if (test==null)
            test=new Test();
        return test;
    }
}
