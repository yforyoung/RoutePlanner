package com.example.y.routeplanner.gson;

import java.util.List;

/**
 * Created by yforyoung on 2018/3/23.
 */

public class ResponseData <T>{
    private int code;
    private String message;
    private T data;



    public ResponseData() {
    }

    public ResponseData(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
