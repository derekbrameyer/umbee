package com.doomonafireball.umbee.util;

import com.doomonafireball.umbee.R;
import com.doomonafireball.umbee.model.NoaaByDay;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

/**
 * User: derek Date: 6/5/12 Time: 12:16 PM
 */
public class UmbeeNotifUtils {

    public static void createNotification(Context ctx, NoaaByDay nbd) {
        NotificationManager mNM = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        SharedPrefsManager mSPM = SharedPrefsManager.getInstance();

        int icon = R.drawable.icon;
        long when = System.currentTimeMillis();
        int type = mSPM.getAlertType();
        int morPrecip = 0;
        int evePrecip = 0;
        if (mSPM.getNotifyTomorrow()) {
            morPrecip = nbd.mPop.probabilities.get(1).second;
            evePrecip = nbd.mPop.probabilities.get(1).first;
        } else {
            morPrecip = nbd.mPop.probabilities.get(0).second;
            evePrecip = nbd.mPop.probabilities.get(0).first;
        }
        int highestPercentage = Math.max(morPrecip, evePrecip);
        CharSequence ticker = "", title = "", text = "";
        switch (type) {
            case Refs.ALERT_SIMPLE:
                int threshold = 50;
                if (mSPM.getCustomThreshold()) {
                    threshold = mSPM.getSingleThreshold();
                }
                if (highestPercentage > threshold) {
                    ticker = ctx.getString(R.string.umbee_thinks_yes);
                    title = ctx.getString(R.string.umbee_thinks_yes);
                } else {
                    ticker = ctx.getString(R.string.umbee_thinks_no);
                    title = ctx.getString(R.string.umbee_thinks_no);
                }
                text = getSimpleMorningText(ctx, morPrecip)
                        + "\n"
                        + getSimpleEveningText(ctx, evePrecip);
                break;
            case Refs.ALERT_COMPLEX:
                int t1 = 25;
                int t2 = 50;
                int t3 = 75;
                if (mSPM.getCustomThreshold()) {
                    t1 = mSPM.getTripleThreshold1();
                    t2 = mSPM.getTripleThreshold2();
                    t3 = mSPM.getTripleThreshold3();
                }
                if (highestPercentage > t3) {
                    // Definitely
                    ticker = ctx.getString(R.string.umbee_thinks_yes);
                    title = ctx.getString(R.string.umbee_thinks_yes);
                } else if (highestPercentage > t2) {
                    // Probably
                    ticker = ctx.getString(R.string.umbee_thinks_probs);
                    title = ctx.getString(R.string.umbee_thinks_probs);
                } else if (highestPercentage > t1) {
                    // Maybe
                    ticker = ctx.getString(R.string.umbee_thinks_maybe);
                    title = ctx.getString(R.string.umbee_thinks_maybe);
                } else {
                    // Nope
                    ticker = ctx.getString(R.string.umbee_thinks_no);
                    title = ctx.getString(R.string.umbee_thinks_no);
                }
                text = getSimpleMorningText(ctx, morPrecip)
                        + "\n"
                        + getSimpleEveningText(ctx, evePrecip);
                break;
            case Refs.ALERT_PERCENT:
                // Not in use
                break;
            default:
                int threshold2 = mSPM.getSingleThreshold();
                if (highestPercentage > threshold2) {
                    ticker = ctx.getString(R.string.umbee_thinks_yes);
                    title = ctx.getString(R.string.umbee_thinks_yes);
                } else {
                    ticker = ctx.getString(R.string.umbee_thinks_no);
                    title = ctx.getString(R.string.umbee_thinks_no);
                }
                text = getSimpleMorningText(ctx, morPrecip)
                        + "\n"
                        + getSimpleEveningText(ctx, evePrecip);
                break;
        }

        PendingIntent pi = PendingIntent.getActivity(ctx, 0, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteViews contentView = new RemoteViews(ctx.getPackageName(), R.layout.two_line_notif);
        contentView.setImageViewResource(R.id.image, R.drawable.icon);
        contentView.setTextViewText(R.id.title, title);
        contentView.setTextViewText(R.id.text, text);

        Notification notif = new Notification(icon, ticker, when);
        notif.contentView = contentView;
        notif.contentIntent = pi;
        notif.flags |= Notification.FLAG_AUTO_CANCEL;
        mNM.notify(1, notif);
    }

    public static String getSimpleMorningText(Context ctx, int percentage) {
        return String.format(ctx.getString(R.string.dynamic_simple_morning), percentage);
    }

    public static String getSimpleEveningText(Context ctx, int percentage) {
        return String.format(ctx.getString(R.string.dynamic_simple_evening), percentage);
    }

}
