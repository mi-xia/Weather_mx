package com.example.lenovo.weather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.example.lenovo.weather.db.WeatherDB;
import com.example.lenovo.weather.model.City;
import com.example.lenovo.weather.model.County;
import com.example.lenovo.weather.model.Province;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by lenovo on 2016/5/14.
 *
 * 解析服务器返回的数据
 */
public class Utility {

    /**
     * 解析 省
     * @param weatherDB
     * @param response
     * @return
     */

    public synchronized static boolean handleProvincesResponse(WeatherDB weatherDB, String response){
        if (!TextUtils.isEmpty(response)){
            String [] allProvince = response.split(",");
            if (allProvince != null && allProvince.length > 0){
                for (String p : allProvince) {
                    String [] array = p.split("\\|");
                    Province province = new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
                    weatherDB.saveProvince(province);
                }

                return true;
            }
        }
        return false;
    }

    /**
     * 解析 市
     */

    public static boolean handleCityResponse (WeatherDB waetherDB, String response, int provindeId){
        if (!TextUtils.isEmpty(response)){
            String [] allcity = response.split(",");
            if (allcity != null && allcity.length  > 0){
                for (String c : allcity) {
                    String [] array = c.split("\\|");
                    City city = new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provindeId);
                    waetherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 解析 县
     */

    public static boolean handleCountyResponse(WeatherDB weatherDB, String response, int cityId){
        if(!TextUtils.isEmpty(response)){
            String [] allconuty = response.split(",");
            if (allconuty != null && allconuty.length > 0){
                for (String c : allconuty) {
                    String [] array = c.split("\\|");
                    County county = new County();
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);
                    weatherDB.saveCounty(county);
                }
                return true;
            }
        }

        return false;
    }


    /**
     * 解析服务器返回的数据
     */

    public static void handleWeatherResponse(Context context,String response){
        try{
            JSONObject jsonObject = new JSONObject(response);
            JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
            String cityName = weatherInfo.getString("city");
            String weatherCode = weatherInfo.getString("cityid");
            String temp1 = weatherInfo.getString("temp1");
            String temp2 = weatherInfo.getString("temp2");
            String weatherDesp = weatherInfo.getString("weather");
            String img1 = weatherInfo.getString("img1");
            String publishTime = weatherInfo.getString("ptime");
            saveWeatherInfo(context,cityName,weatherCode,temp1,temp2,weatherDesp,img1,publishTime);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public static void saveWeatherInfo(Context context,String cityName,String weatherCode,String temp1,
                                       String temp2,String weatherDesp,String img1,String publishTime){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected",true);
        editor.putString("city_name", cityName);
        editor.putString("weather_code", weatherCode);
        editor.putString("temp1",temp1);
        editor.putString("temp2",temp2);
        editor.putString("weather_desp",weatherDesp);
        editor.putString("img1",img1);
        editor.putString("publish_time",publishTime);
        editor.putString("current_date",sdf.format(new Date()));
        editor.commit();
    }
}

