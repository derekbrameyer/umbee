package com.doomonafireball.umbee.service;

import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;

import com.doomonafireball.umbee.R;
import com.doomonafireball.umbee.activity.StartupActivity;
import com.doomonafireball.umbee.model.NoaaByDay;
import com.doomonafireball.umbee.util.JsonParser;
import com.doomonafireball.umbee.util.Refs;
import com.doomonafireball.umbee.util.SharedPrefsManager;

import org.json.JSONException;

import android.content.Context;
import android.content.Intent;

/**
 * User: derek Date: 2/25/13 Time: 9:24 PM
 */
public class UmbeeDashClockService extends DashClockExtension {

    @Override
    protected void onUpdateData(int i) {
        Context ctx = getApplicationContext();
        SharedPrefsManager.initialize(ctx);
        SharedPrefsManager sharedPrefsManager = SharedPrefsManager.getInstance();
        try {
            NoaaByDay nbd = JsonParser.parseNoaaByDay(sharedPrefsManager.getNoaaByDayString());

            int type = sharedPrefsManager.getAlertType();
            int morPrecip = 0;
            int evePrecip = 0;
            try {
                if (sharedPrefsManager.getNotifyTomorrow()) {
                    morPrecip = nbd.mPop.probabilities.get(1).first;
                    evePrecip = nbd.mPop.probabilities.get(1).second;
                } else {
                    morPrecip = nbd.mPop.probabilities.get(0).first;
                    evePrecip = nbd.mPop.probabilities.get(0).second;
                }
            } catch (IndexOutOfBoundsException e) {
                // User has not gotten any data yet!
                morPrecip = -1;
                evePrecip = -1;
            }
            int highestPercentage = Math.max(morPrecip, evePrecip);
            String status = "", expandedTitle = "", expandedBody = "";
            if (highestPercentage == -1) {
                status = ctx.getString(R.string.not_configured);
                expandedTitle = ctx.getString(R.string.not_configured_expanded_title);
                expandedBody = ctx.getString(R.string.not_configured_expanded_body);
            } else {
                switch (type) {
                    case Refs.ALERT_SIMPLE:
                        int threshold = 50;
                        if (sharedPrefsManager.getCustomThreshold()) {
                            threshold = sharedPrefsManager.getSingleThreshold();
                        }
                        if (highestPercentage > threshold) {
                            status = ctx.getString(R.string.need_it_c);
                            expandedTitle = ctx.getString(R.string.umbee_thinks_yes_short);
                        } else {
                            status = ctx.getString(R.string.dont_need_it_c);
                            expandedTitle = ctx.getString(R.string.umbee_thinks_no_short);
                        }
                        expandedBody = getSimpleMorningText(ctx, morPrecip)
                                + "\n"
                                + getSimpleEveningText(ctx, evePrecip);
                        break;
                    case Refs.ALERT_COMPLEX:
                        int t1 = 25;
                        int t2 = 50;
                        int t3 = 75;
                        if (sharedPrefsManager.getCustomThreshold()) {
                            t1 = sharedPrefsManager.getTripleThreshold1();
                            t2 = sharedPrefsManager.getTripleThreshold2();
                            t3 = sharedPrefsManager.getTripleThreshold3();
                        }
                        if (highestPercentage > t3) {
                            // Definitely
                            status = ctx.getString(R.string.need_it_c);
                            expandedTitle = ctx.getString(R.string.umbee_thinks_yes_short);
                        } else if (highestPercentage > t2) {
                            // Probably
                            status = ctx.getString(R.string.probs_need_it_c);
                            expandedTitle = ctx.getString(R.string.umbee_thinks_probs_short);
                        } else if (highestPercentage > t1) {
                            // Maybe
                            status = ctx.getString(R.string.maybs_need_it_c);
                            expandedTitle = ctx.getString(R.string.umbee_thinks_probs_short);
                        } else {
                            // Nope
                            status = ctx.getString(R.string.dont_need_it_c);
                            expandedTitle = ctx.getString(R.string.umbee_thinks_no_short);
                        }
                        expandedBody = getSimpleMorningText(ctx, morPrecip)
                                + "\n"
                                + getSimpleEveningText(ctx, evePrecip);
                        break;
                    case Refs.ALERT_PERCENT:
                        // Not in use
                        break;
                    default:
                        int threshold2 = sharedPrefsManager.getSingleThreshold();
                        if (highestPercentage > threshold2) {
                            status = ctx.getString(R.string.need_it_c);
                            expandedTitle = ctx.getString(R.string.umbee_thinks_yes_short);
                        } else {
                            status = ctx.getString(R.string.dont_need_it_c);
                            expandedTitle = ctx.getString(R.string.umbee_thinks_no_short);
                        }
                        expandedBody = getSimpleMorningText(ctx, morPrecip)
                                + "\n"
                                + getSimpleEveningText(ctx, evePrecip);
                        break;
                }
                if (sharedPrefsManager.getNotifyTomorrow()) {
                    expandedTitle = ctx.getString(R.string.tomorrow_c) + " " + expandedTitle;
                } else {
                    expandedTitle = ctx.getString(R.string.today_c) + " " + expandedTitle;
                }
            }

            // Publish the extension data update.
            publishUpdate(new ExtensionData()
                    .visible(true)
                    .icon(R.drawable.icon_notif)
                    .status(status)
                    .expandedTitle(expandedTitle)
                    .expandedBody(expandedBody)
                    .clickIntent(new Intent(this, StartupActivity.class)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static String getSimpleMorningText(Context ctx, int percentage) {
        return String.format(ctx.getString(R.string.dynamic_simple_morning_short), percentage);
    }

    public static String getSimpleEveningText(Context ctx, int percentage) {
        return String.format(ctx.getString(R.string.dynamic_simple_evening_short), percentage);
    }
}
