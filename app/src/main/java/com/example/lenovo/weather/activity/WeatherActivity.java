package com.example.lenovo.weather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.lenovo.weather.R;
import com.example.lenovo.weather.util.HttpUtil;
import com.example.lenovo.weather.util.HtttCallbackListener;
import com.example.lenovo.weather.util.Utility;

import java.io.BufferedReader;
import java.util.BitSet;

/**
 * Created by lenovo on 2016/5/15.
 */
public class WeatherActivity extends Activity implements View.OnClickListener{
    private LinearLayout weatherInfoLayout;
    private LinearLayout liner;
    private TextView cityNameText;//城市名
    private TextView publishText;//发布时间
    private TextView weatherDespText; //天气描述
    private TextView temp1Text;//温度1
    private TextView temp2Text;//温度2
    private TextView currentDateText;//日期
    private Button switchCity;
    private Button refreshWeather;

    private String img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_layout);
        weatherInfoLayout = (LinearLayout)findViewById(R.id.weather_info_layout);
        cityNameText = (TextView)findViewById(R.id.city_name);
        publishText = (TextView)findViewById(R.id.publish_text);
        weatherDespText = (TextView)findViewById(R.id.weather_desp);
        temp1Text = (TextView)findViewById(R.id.temp1);
        temp2Text = (TextView)findViewById(R.id.temp2);
        currentDateText = (TextView)findViewById(R.id.current_date);
        switchCity = (Button)findViewById(R.id.switch_city);
        refreshWeather = (Button)findViewById(R.id.refresh_weather);
        String countycode = getIntent().getStringExtra("county_code");
        if (!TextUtils.isEmpty(countycode)){
            publishText.setText("同步中...");
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            cityNameText.setVisibility(View.INVISIBLE);
            queryWeatherCode(countycode);
        }else {
            showWeather();
        }
        switchCity.setOnClickListener(this);
        refreshWeather.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.switch_city:
                Intent intent = new Intent(this,ChooseAreaActivity.class);
                intent.putExtra("from_weather_activity", true);
                startActivity(intent);
                finish();
                break;
            case R.id.refresh_weather:
                publishText.setText("同步中...");
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                String weatherCode = prefs.getString("weather_code","");
                if (!TextUtils.isEmpty(weatherCode)){
                    queryWeatherInfo(weatherCode);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 查询县级代码对应的天气
     */

    private void queryWeatherCode(String countyCode){
        String address = "http://www.weather.com.cn/data/list3/city"+countyCode+".xml";
        queryFromServer(address, "countyCode");
    }

    /**
     * 查询天气代号对应的天气
     */

    private void queryWeatherInfo(String weatherCode){
        String address = "http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
        queryFromServer(address,"weatherCode");
    }

    private void queryFromServer(final String address, final String type){
        HttpUtil.sendHttpRequest(address, new HtttCallbackListener() {
            @Override
            public void onFinish(String response) {
                if ("countyCode".equals(type)) {
                    if (!TextUtils.isEmpty(response)) {
                        String[] array = response.split("\\|");
                        if (array != null && array.length == 2) {
                            String weathercode = array[1];
                            queryWeatherInfo(weathercode);
                        }
                    }
                } else if ("weatherCode".equals(type)) {
                    Utility.handleWeatherResponse(WeatherActivity.this, response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        publishText.setText("同步失败");
                    }
                });
            }
        });

    }

    /**
     * 从文件中读取天气信息，并显示在界面上
     */

    private void showWeather(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        cityNameText.setText(prefs.getString("city_name",""));
        temp1Text.setText(prefs.getString("temp1",""));
        temp2Text.setText(prefs.getString("temp2", ""));
        weatherDespText.setText(prefs.getString("weather_desp", ""));
        img = prefs.getString("img1","");
        Log.d("WeatherActivity", img);
        publishText.setText("今天" + prefs.getString("publish_time", "") + "发布");
        currentDateText.setText(prefs.getString("current_date", ""));
        liner = (LinearLayout)findViewById(R.id.liner);
        if ("n0.gif".equals(img)||"d0.gif".equals(img)){
            liner.setBackgroundResource(R.drawable.bg_sunny);
        }else if ("n1.gif".equals(img)||"d1.gif".equals(img)){
            liner.setBackgroundResource(R.drawable.bg_cloudy);
        }else if ("n2.gif".equals(img)||"d2.gif".equals(img)){
            liner.setBackgroundResource(R.drawable.bg_mostly_cloudy);
        }else if ("n6.gif".equals(img)||"d6.gif".equals(img)||"n7.gif".equals(img)||"d7.gif".equals(img)||"n8.gif".equals(img)||"d8.gif".equals(img)){
            liner.setBackgroundResource(R.drawable.bg_rain);
        }else if ("n13.gif".equals(img)||"d13.gif".equals(img)||"n14.gif".equals(img)||"d14.gif".equals(img)) {
            liner.setBackgroundResource(R.drawable.bg_snow);
        }else {
            liner.setBackgroundResource(R.drawable.bg_windy);
        }
        liner.setVisibility(View.VISIBLE);
        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityNameText.setVisibility(View.VISIBLE);
    }
}

