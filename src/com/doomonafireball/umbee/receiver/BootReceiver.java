package com.doomonafireball.umbee.receiver;

import com.doomonafireball.umbee.MainApp;
import com.doomonafireball.umbee.util.SharedPrefsManager;
import com.doomonafireball.umbee.util.UmbeeTimeUtils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPrefsManager spm = SharedPrefsManager.getInstance();

        Calendar currCal = new GregorianCalendar();
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.HOUR_OF_DAY, UmbeeTimeUtils.hourOfDayFromInt(spm.getUpdateTime()));
        cal.set(Calendar.MINUTE, UmbeeTimeUtils.minuteFromInt(spm.getUpdateTime()));
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        if (currCal.after(cal)) {
            // Increment the day
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }

        Intent notif = new Intent(context, NotificationReceiver.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pi = PendingIntent.getBroadcast(context, 0, notif, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);
        Log.d(MainApp.TAG, "Set alarmManager.setRepeating to: " + cal.getTime().toLocaleString());
    }

}
