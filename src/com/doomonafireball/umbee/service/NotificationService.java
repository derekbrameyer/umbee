package com.doomonafireball.umbee.service;

import com.doomonafireball.umbee.query.WeatherQuery;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;

public class NotificationService extends IntentService {

    public NotificationService() {
        super("UmbieNotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        WeatherQuery wq = new WeatherQuery(this, false);
        wq.execute();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
