package com.doomonafireball.umbee.receiver;

import com.doomonafireball.umbee.service.NotificationService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * User: derek Date: 6/5/12 Time: 4:53 PM
 */
public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent dailyNotif = new Intent(context, NotificationService.class);
        context.startService(dailyNotif);
    }
}
