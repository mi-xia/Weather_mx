package com.example.lenovo.weather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lenovo.weather.R;
import com.example.lenovo.weather.db.WeatherDB;
import com.example.lenovo.weather.model.City;
import com.example.lenovo.weather.model.County;
import com.example.lenovo.weather.model.Province;
import com.example.lenovo.weather.util.HttpUtil;
import com.example.lenovo.weather.util.HtttCallbackListener;
import com.example.lenovo.weather.util.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2016/5/14.
 */
public class ChooseAreaActivity extends Activity{
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    private boolean ifFromWeatherActivity;

    private ProgressDialog progressDialog;
    private TextView titleText;
    private ListView listView;
    private ArrayAdapter<String> adapter;  //适配器
    private WeatherDB weatherDB;
    private List<String> dataList = new ArrayList<String>();

    private List<Province> provinceList; //省列表
    private List<City> cityList;         //市列表
    private List<County> countyList;     //县列表

    private Province selectedprovince;
    private City selectedcity;

    private int currentlevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ifFromWeatherActivity = getIntent().getBooleanExtra("from_weather_activity",false);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean("city_selected",false) && !ifFromWeatherActivity){
            Intent intent = new Intent(this,WeatherActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        setContentView(R.layout.choose_area);
        listView = (ListView)findViewById(R.id.list_view);
        titleText = (TextView)findViewById(R.id.title_text);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        weatherDB = WeatherDB.getInstance(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentlevel == LEVEL_PROVINCE){
                    selectedprovince = provinceList.get(position);
                    queryCity();
                }else if (currentlevel == LEVEL_CITY){
                    selectedcity = cityList.get(position);
                    queryCounty();
                }else if(currentlevel == LEVEL_COUNTY){
                    String countyCode = countyList.get(position).getCountyCode();
                    Intent intent = new Intent(ChooseAreaActivity.this,WeatherActivity.class);
                    intent.putExtra("county_code",countyCode);
                    startActivity(intent);
                    finish();
                }
            }
        });
        queryProvince();
    }

    /**
     * 查询所有省
     */

    private void queryProvince(){
        provinceList = weatherDB.loadProvince();
        if (provinceList.size() > 0){
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }

            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("中国");
            currentlevel = LEVEL_PROVINCE;
        }else {
            queryFormServer(null,"province");
        }
    }

    /**
     * 查询相对应的市
     */

    private void queryCity(){
        cityList = weatherDB.loadcity(selectedprovince.getId());
        if(cityList.size() > 0){
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedprovince.getProvinceName());
            currentlevel = LEVEL_CITY;
        }else {
            queryFormServer(selectedprovince.getProvinceCode(),"city");
        }
    }

    private void queryCounty(){
        countyList = weatherDB.loadCounty(selectedcity.getId());
        if (countyList.size() > 0){
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }


            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedcity.getCityName());
            currentlevel = LEVEL_COUNTY;
        }else {
            queryFormServer(selectedcity.getCityCode(),"county");
        }
    }

    private void queryFormServer(final String code, final String type){
        String address;
        if(!TextUtils.isEmpty(code)){
            address = "http://www.weather.com.cn/data/list3/city"+code+".xml";
        }else{
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }
        showProgressDialog();

        HttpUtil.sendHttpRequest(address, new HtttCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result = false;
                if ("province".equals(type)){
                    result = Utility.handleProvincesResponse(weatherDB,response);
                }else if ("city".equals(type)){
                    result = Utility.handleCityResponse(weatherDB,response,selectedprovince.getId());
                }else if ("county".equals(type)){
                    result = Utility.handleCountyResponse(weatherDB,response,selectedcity.getId());
                }

                if (result){
                    //通过runOnUIThread回到主线程
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)){
                                queryProvince();
                            }else if ("city".equals(type)){
                                queryCity();
                            }else if ("county".equals(type)){
                               queryCounty();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this,"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * 显示进度对话框
     */

    private void showProgressDialog(){
        if (progressDialog == null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 关闭进度
     */

    private void closeProgressDialog(){
        if (progressDialog != null){
            progressDialog.dismiss();
        }
    }

    /**
     * 设计返回
     */

    @Override
    public void onBackPressed() {
        if (currentlevel == LEVEL_COUNTY) {
            queryCity();
        }else if (currentlevel == LEVEL_CITY){
            queryProvince();
        }else {
            if (ifFromWeatherActivity){
                Intent intent = new Intent(this,WeatherActivity.class);
                startActivity(intent);
            }
            finish();
        }
    }
}
