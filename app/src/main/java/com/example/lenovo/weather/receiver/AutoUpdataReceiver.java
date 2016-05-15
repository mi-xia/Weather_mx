package com.example.lenovo.weather.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.lenovo.weather.service.AutoUpdataService;

/**
 * Created by lenovo on 2016/5/15.
 */
public class AutoUpdataReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, AutoUpdataService.class);
        context.startService(i);
    }
}
