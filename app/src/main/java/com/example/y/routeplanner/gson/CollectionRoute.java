package com.example.y.routeplanner.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class CollectionRoute {
    @SerializedName("collection_id")
    private int collectionID;
    private String start_point;
    private String end_point;
    private String start_longitude ;
    private String start_latitude;
    private String  end_longitude ;
    private String  end_latitude;
    private String  area ;
    private List<String> route_information ;
    private int  user_id;

    public CollectionRoute() {
    }

    public CollectionRoute(int collectionID, String start_point, String end_point, String start_longitude, String start_latitude, String end_longitude, String end_latitude, String area, List<String> route_information, int user_id) {
        this.collectionID = collectionID;
        this.start_point = start_point;
        this.end_point = end_point;
        this.start_longitude = start_longitude;
        this.start_latitude = start_latitude;
        this.end_longitude = end_longitude;
        this.end_latitude = end_latitude;
        this.area = area;
        this.route_information = route_information;
        this.user_id = user_id;
    }

    public int getCollectionID() {
        return collectionID;
    }

    public void setCollectionID(int collectionID) {
        this.collectionID = collectionID;
    }

    public String getStart_point() {
        return start_point;
    }

    public void setStart_point(String start_point) {
        this.start_point = start_point;
    }

    public String getEnd_point() {
        return end_point;
    }

    public void setEnd_point(String end_point) {
        this.end_point = end_point;
    }

    public String getStart_longitude() {
        return start_longitude;
    }

    public void setStart_longitude(String start_longitude) {
        this.start_longitude = start_longitude;
    }

    public String getStart_latitude() {
        return start_latitude;
    }

    public void setStart_latitude(String start_latitude) {
        this.start_latitude = start_latitude;
    }

    public String getEnd_longitude() {
        return end_longitude;
    }

    public void setEnd_longitude(String end_longitude) {
        this.end_longitude = end_longitude;
    }

    public String getEnd_latitude() {
        return end_latitude;
    }

    public void setEnd_latitude(String end_latitude) {
        this.end_latitude = end_latitude;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public List<String> getRoute_information() {
        return route_information;
    }

    public void setRoute_information(List<String> route_information) {
        this.route_information = route_information;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
}
