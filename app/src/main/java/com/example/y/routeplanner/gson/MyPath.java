package com.example.y.routeplanner.gson;

import java.util.List;

/**
 * Created by yforyoung on 2018/3/21.
 */

public class MyPath {
    private String collection_name;
    private List<MyRoute> routes;

    public String getCollection_name() {
        return collection_name;
    }

    public void setCollection_name(String collection_name) {
        this.collection_name = collection_name;
    }

    public List<MyRoute> getRoutes() {
        return routes;
    }

    public void setRoutes(List<MyRoute> routes) {
        this.routes = routes;
    }

    public MyPath(String collection_name, List<MyRoute> routes) {
        this.collection_name = collection_name;
        this.routes = routes;
    }

    public MyPath() {
    }
}
