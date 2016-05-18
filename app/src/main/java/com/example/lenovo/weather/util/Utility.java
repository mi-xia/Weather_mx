package com.example.lenovo.weather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.example.lenovo.weather.db.WeatherDB;
import com.example.lenovo.weather.model.BaseData;
import com.example.lenovo.weather.model.City;
import com.example.lenovo.weather.model.County;
import com.example.lenovo.weather.model.ForeCast;
import com.example.lenovo.weather.model.Province;
import com.example.lenovo.weather.model.environment;
import com.example.lenovo.weather.model.zhishu;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
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

        int count = 1;
        int count2 = 1;
        Log.d("Untility handle", response);
        try{
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(new StringReader(response));
            BaseData baseData = new BaseData();
            environment en = new environment();
            ForeCast foreCast = new ForeCast();
            zhishu zs = new zhishu();

            int eventType = xmlPullParser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT){
                String nodeName = xmlPullParser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG :{
                        if ("resp".equals(nodeName)) {
                        } else if ("city".equals(nodeName)) {
                            baseData.setCity(xmlPullParser.nextText());
                        } else if ("updatetime".equals(nodeName)) {
                            baseData.setUpDateTime(xmlPullParser.nextText());
                        } else if ("wendu".equals(nodeName)){
                            baseData.setTemp(xmlPullParser.nextText());
                        } else if ("fengli".equals(nodeName)){
                            baseData.setFengli(xmlPullParser.nextText());
                        } else if ("shidu".equals(nodeName)){
                            baseData.setShidu(xmlPullParser.nextText());
                        } else if ("fengxiang".equals(nodeName)){
                            baseData.setFengxiang(xmlPullParser.nextText());
                        }else if ("sunrise_1".equals(nodeName)){
                            baseData.setSunRise(xmlPullParser.nextText());
                        }else if ("sunset_1".equals(nodeName)) {
                            baseData.setSunSet(xmlPullParser.nextText());
                        }else if ("aqi".equals(nodeName)) {
                            en.setAqi(xmlPullParser.nextText());
                        }else if ("pm25".equals(nodeName)) {
                            en.setPm25(xmlPullParser.nextText());
                        }else if ("quality".equals(nodeName)) {
                            en.setQuality(xmlPullParser.nextText());
                        }else if ("pm10".equals(nodeName)) {
                            en.setPm10(xmlPullParser.nextText());
                        }else if ("date".equals(nodeName)){
                            foreCast.setData(xmlPullParser.nextText());
                        }else if ("high".equals(nodeName)){
                            foreCast.setHighTemp(xmlPullParser.nextText());
                        }else if ("low".equals(nodeName)){
                            foreCast.setLowTemp(xmlPullParser.nextText());
                        }else if ("day".equals(nodeName)) {
                            eventType = xmlPullParser.next();
                            if(eventType != XmlPullParser.END_DOCUMENT) {
                                nodeName = xmlPullParser.getName();
                            }
                            if ("type".equals(nodeName)) {
                                foreCast.setType(xmlPullParser.nextText());
                            }
                            eventType = xmlPullParser.next();
                            if(eventType != XmlPullParser.END_DOCUMENT) {
                                nodeName = xmlPullParser.getName();
                            }
                            if ("fengxiang".equals(nodeName)) {
                                foreCast.setFengxiang(xmlPullParser.nextText());
                            }
                            eventType = xmlPullParser.next();
                            if(eventType != XmlPullParser.END_DOCUMENT) {
                                nodeName = xmlPullParser.getName();
                            }
                            if ("fengli".equals(nodeName)) {
                                foreCast.setFengli(xmlPullParser.nextText());
                            }
                        }else if ("name".equals(nodeName)) {
                            zs.setName(xmlPullParser.nextText());
                        }else if ("value".equals(nodeName)) {
                            zs.setValue(xmlPullParser.nextText());
                        }else if ("detail".equals(nodeName)) {
                            zs.setDetail(xmlPullParser.nextText());
                        }
                        break;
                    }
                    case XmlPullParser.END_TAG: {
                        if ("sunrise_2".equals(nodeName)) {
                            saveWeatherInfoBaseData(context,baseData);
                            Log.d("mainactivity", baseData.getCity());
                            Log.d("mainactivity", baseData.getUpDateTime());
                            Log.d("mainactivity", baseData.getTemp());
                            Log.d("mainactivity", baseData.getFengli());
                            Log.d("mainactivity", baseData.getShidu());
                            Log.d("mainactivity", baseData.getFengxiang());
                            Log.d("mainactivity", baseData.getSunRise());
                            Log.d("mainactivity", baseData.getSunSet());
                        }else if("environment".equals(nodeName)){
                            saveWeatherInfoEn(context,en);
                            Log.d("mainactivity",en.getAqi());
                            Log.d("mainactivity",en.getPm25());
                            Log.d("mainactivity",en.getQuality());
                            Log.d("mainactivity",en.getPm10());
                        }else if("weather".equals(nodeName)){
                            if ("暂无".equals(en.getAqi())){
                                saveWeatherInfoEn(context,en);
                            }
                            saveWeatherInfo(context,foreCast,count);
                            count++;
                            if (count>11){count = 1;}
                            Log.d("mainactivity", foreCast.getData());
                            Log.d("mainactivity", foreCast.getFengxiang());
                            Log.d("mainactivity", foreCast.getFengli());
                            Log.d("mainactivity", foreCast.getType());
                            Log.d("mainactivity", foreCast.getHighTemp());
                            Log.d("mainactivity", foreCast.getLowTemp());
                        }else if("zhishu".equals(nodeName)) {
                            saveWeatherInfoZs(context,zs,count2);
                            count2++;
                            if (count2>11){count2 = 1;}
                            Log.d("mainactivity", zs.getName());
                            Log.d("mainactivity", zs.getValue());
                            Log.d("mainactivity", zs.getDetail());

                        }
                        break;
                    }
                    default:
                        break;
                }
                eventType = xmlPullParser.next();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void saveWeatherInfoBaseData(Context context,BaseData baseData){
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected",true);
        editor.putString("city_name", baseData.getCity());
        editor.putString("update_time",baseData.getUpDateTime());
        editor.putString("wendu",baseData.getTemp());
        editor.putString("fengli",baseData.getFengli());
        editor.putString("shidu",baseData.getShidu());
        editor.putString("fengxiang",baseData.getFengxiang());
        editor.putString("sunrise",baseData.getSunRise());
        editor.putString("sunset",baseData.getSunSet());
        editor.commit();
    }
    public static void saveWeatherInfoEn(Context context,environment en){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString("aqi", en.getAqi());
        editor.putString("pm25", en.getPm25());
        editor.putString("pm10",en.getPm10());
        editor.putString("quality", en.getQuality());
        editor.commit();
    }

    public static void saveWeatherInfo(Context context,ForeCast foreCast, int count){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString("data_"+count, foreCast.getData());
        editor.putString("hightemp_"+count, foreCast.getHighTemp() );
        editor.putString("lowtemp_"+count, foreCast.getLowTemp());
        editor.putString("type_"+count, foreCast.getType());
        editor.putString("fengxiang_1_"+count, foreCast.getFengxiang());
        editor.putString("fengli_1_"+count, foreCast.getFengli());
        editor.commit();
    }

    public static void saveWeatherInfoZs(Context context, zhishu zs, int count2){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString("name_"+count2, zs.getName());
        editor.putString("vlaue_"+count2, zs.getValue());
        editor.putString("datail_"+count2, zs.getDetail());
        editor.commit();
    }
}

