package com.example.lenovo.weather.model;

import java.io.Serializable;

/**
 * Created by lenovo on 2016/5/18.
 */
public class zhishu implements Serializable{
    private String name;
    private String value;
    private String detail;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
