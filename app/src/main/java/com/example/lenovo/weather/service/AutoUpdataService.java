package com.example.lenovo.weather.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.lenovo.weather.R;
import com.example.lenovo.weather.activity.WeatherActivity;
import com.example.lenovo.weather.receiver.AutoUpdataReceiver;
import com.example.lenovo.weather.util.HttpUtil;
import com.example.lenovo.weather.util.HtttCallbackListener;
import com.example.lenovo.weather.util.Utility;

/**
 * Created by lenovo on 2016/5/15.
 */
public class AutoUpdataService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Intent notificationIntent = new Intent(this,WeatherActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,0);
        Notification.Builder mBuilder = new Notification.Builder(AutoUpdataService.this)
                .setTicker("今日天气")
                .setSmallIcon(R.drawable.logo_16x)
                .setContentTitle(prefs.getString("city_name",""))
                .setContentText(prefs.getString("type_1", "")+"  "+prefs.getString("lowtemp_1","")+"~"+prefs.getString("hightemp_1", ""))
                .setContentIntent(pendingIntent);
        Notification notification = mBuilder.build();
        startForeground(1,notification);

        new Thread(new Runnable() {
            @Override
            public void run() {
                updateWeather();
            }
        }).start();
        AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
        int anHour = 60*60*1000;
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent(this,AutoUpdataReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this,0,i,0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        return super.onStartCommand(intent, flags, startId);
    }

    private void updateWeather(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherCode = prefs.getString("weather_code", "");
        String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey="+weatherCode;
        HttpUtil.sendHttpRequest(address, new HtttCallbackListener() {
            @Override
            public void onFinish(String response) {
                Utility.handleWeatherResponse(AutoUpdataService.this,response);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }
}
