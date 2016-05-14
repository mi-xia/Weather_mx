package com.example.lenovo.weather.util;

import android.text.TextUtils;
import android.util.Log;

import com.example.lenovo.weather.db.WeatherDB;
import com.example.lenovo.weather.model.City;
import com.example.lenovo.weather.model.County;
import com.example.lenovo.weather.model.Province;

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

}

