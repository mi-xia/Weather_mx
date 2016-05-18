package com.example.lenovo.weather.model;

/**
 * Created by lenovo on 2016/5/18.
 */
public class environment {
    private String aqi;
    private String pm25;
    private String quality;
    private String pm10;

    public environment(){
        aqi = "暂无";
        pm25 = "暂无";
        quality = "暂无";
        pm10 = "暂无";
    }

    public String getAqi() {
        return aqi;
    }

    public void setAqi(String aqi) {
        this.aqi = aqi;
    }

    public String getPm25() {
        return pm25;
    }

    public void setPm25(String pm25) {
        this.pm25 = pm25;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getPm10() {
        return pm10;
    }

    public void setPm10(String pm10) {
        this.pm10 = pm10;
    }
}
