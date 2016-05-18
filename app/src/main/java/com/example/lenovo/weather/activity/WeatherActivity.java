package com.example.lenovo.weather.activity;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
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
import com.example.lenovo.weather.model.BaseData;
import com.example.lenovo.weather.model.environment;
import com.example.lenovo.weather.service.AutoUpdataService;
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
    private TextView type_1;
    private TextView high_1;
    private TextView low_1;
    private TextView date_1;
    private TextView type_2;
    private TextView high_2;
    private TextView low_2;
    private TextView date_2;
    private TextView type_3;
    private TextView high_3;
    private TextView low_3;
    private TextView date_3;
    private TextView type_4;
    private TextView high_4;
    private TextView low_4;
    private TextView date_4;
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
        type_1= (TextView)findViewById(R.id.type_1);
        low_1 = (TextView)findViewById(R.id.low_1);
        high_1 = (TextView)findViewById(R.id.high_1);
        date_1 = (TextView)findViewById(R.id.data_1);
        type_2= (TextView)findViewById(R.id.type_2);
        low_2 = (TextView)findViewById(R.id.low_2);
        high_2 = (TextView)findViewById(R.id.high_2);
        date_2 = (TextView)findViewById(R.id.data_2);
        type_3 = (TextView)findViewById(R.id.type_3);
        low_3 = (TextView)findViewById(R.id.low_3);
        high_3 = (TextView)findViewById(R.id.high_3);
        date_3 = (TextView)findViewById(R.id.data_3);
        type_4 = (TextView)findViewById(R.id.type_4);
        low_4 = (TextView)findViewById(R.id.low_4);
        high_4 = (TextView)findViewById(R.id.high_4);
        date_4 = (TextView)findViewById(R.id.data_4);
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
        String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey="+weatherCode;
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
                            saveWeatherInfoWeatherCode(WeatherActivity.this,weathercode);
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
     * 保存weathercode
     */

    public static void saveWeatherInfoWeatherCode(Context context, String weratherCode){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString("weather_code", weratherCode);
        editor.commit();
    }

    /**
     * 从文件中读取天气信息，并显示在界面上
     */

    private void showWeather(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        liner = (LinearLayout)findViewById(R.id.liner);
        cityNameText.setText(prefs.getString("city_name",""));
        temp1Text.setText(prefs.getString("lowtemp_1",""));
        temp2Text.setText(prefs.getString("hightemp_1", ""));
        weatherDespText.setText(prefs.getString("type_1", ""));
        publishText.setText("今天" + prefs.getString("update_time", "") + "发布");
        currentDateText.setText(prefs.getString("data_1", ""));

        type_1.setText(prefs.getString("type_2", ""));
        String a = prefs.getString("lowtemp_2", "");
        int b = a.length();
        String c = a.substring(b-3);
        low_1 .setText(c);
        a = prefs.getString("hightemp_2", "");
        b = a.length();
        c = a.substring(b - 3);
        high_1.setText(c);
        date_1.setText(prefs.getString("data_2", ""));

        type_2.setText(prefs.getString("type_3", ""));
        a = prefs.getString("lowtemp_3", "");
        b = a.length();
        c = a.substring(b - 3);
        low_2 .setText(c);
        a = prefs.getString("hightemp_3", "");
        b = a.length();
        c = a.substring(b - 3);
        high_2.setText(c);
        date_2.setText(prefs.getString("data_3", ""));

        type_3.setText(prefs.getString("type_4", ""));
        a = prefs.getString("lowtemp_4", "");
        b = a.length();
        c = a.substring(b - 3);
        low_3 .setText(c);
        a = prefs.getString("hightemp_4", "");
        b = a.length();
        c = a.substring(b - 3);
        high_3.setText(c);
        date_3.setText(prefs.getString("data_4", ""));

        type_4.setText(prefs.getString("type_5", ""));
        a = prefs.getString("lowtemp_5", "");
        b = a.length();
        c = a.substring(b - 3);
        low_4 .setText(c);
        a = prefs.getString("hightemp_5", "");
        b = a.length();
        c = a.substring(b - 3);
        high_4.setText(c);
        date_4.setText(prefs.getString("data_5", ""));



        BaseData baseData =new BaseData();
        environment en = new environment();
        baseData.setCity(prefs.getString("city_name", ""));
        baseData.setTemp(prefs.getString("wendu", ""));
        baseData.setUpDateTime(prefs.getString("update_time", ""));
        baseData.setShidu(prefs.getString("shidu", ""));
        baseData.setFengxiang(prefs.getString("fengxiang", ""));
        baseData.setFengli(prefs.getString("fengli", ""));
        baseData.setSunRise(prefs.getString("sunrise", ""));
        baseData.setSunSet(prefs.getString("sunset", ""));
        en.setAqi(prefs.getString("aqi", ""));
        en.setPm10(prefs.getString("pm10", ""));
        en.setPm25(prefs.getString("pm25", ""));
        en.setQuality(prefs.getString("quality", ""));
        /*if ("n0.gif".equals(img)||"d0.gif".equals(img)){
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
        }*/    //设置背景
        liner.setVisibility(View.VISIBLE);
        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityNameText.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, AutoUpdataService.class);
        startService(intent);
    }
}

