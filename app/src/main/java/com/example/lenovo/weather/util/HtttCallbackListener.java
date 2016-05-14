package com.example.lenovo.weather.util;

/**
 * Created by lenovo on 2016/5/14.
 */
public interface HtttCallbackListener {

    void onFinish(String response);
    void onError(Exception e);
}
