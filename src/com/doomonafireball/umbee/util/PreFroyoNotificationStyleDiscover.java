package com.doomonafireball.umbee.util;

import android.app.Notification;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PreFroyoNotificationStyleDiscover {

    private Integer mNotifyTextColor = null;
    private float mNotifyTextSize = 11;
    private Integer mNotifyTitleColor = null;
    private float mNotifyTitleSize = 12;
    private final String TEXT_SEARCH_TEXT = "SearchForText";
    private final String TEXT_SEARCH_TITLE = "SearchForTitle";
    private Context mContext;

    PreFroyoNotificationStyleDiscover(Context context) {
        mContext = context;
        discoverStyle();
    }

    public int getTextColor() {
        return mNotifyTextColor.intValue();
    }

    public float getTextSize() {
        return mNotifyTextSize;
    }

    public int getTitleColor() {
        return mNotifyTitleColor;
    }

    public float getTitleSize() {
        return mNotifyTitleSize;
    }

    private boolean recurseGroup(ViewGroup group) {
        final int count = group.getChildCount();

        for (int i = 0; i < count; ++i) {
            if (group.getChildAt(i) instanceof TextView) {
                final TextView tv = (TextView) group.getChildAt(i);
                final String text = tv.getText().toString();
                if (text.startsWith("SearchFor")) {
                    DisplayMetrics metrics = new DisplayMetrics();
                    WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
                    wm.getDefaultDisplay().getMetrics(metrics);

                    if (TEXT_SEARCH_TEXT == text) {
                        mNotifyTextColor = tv.getTextColors().getDefaultColor();
                        mNotifyTextSize = tv.getTextSize();
                        mNotifyTextSize /= metrics.scaledDensity;
                    } else {
                        mNotifyTitleColor = tv.getTextColors().getDefaultColor();
                        mNotifyTitleSize = tv.getTextSize();
                        mNotifyTitleSize /= metrics.scaledDensity;
                    }

                    if (null != mNotifyTitleColor && mNotifyTextColor != null) {
                        return true;
                    }
                }
            } else if (group.getChildAt(i) instanceof ViewGroup) {
                if (recurseGroup((ViewGroup) group.getChildAt(i))) {
                    return true;
                }
            }
        }
        return false;
    }

    private void discoverStyle() {
        if (null != mNotifyTextColor) {
            return;
        }

        try {
            Notification notify = new Notification();
            notify.setLatestEventInfo(mContext, TEXT_SEARCH_TITLE, TEXT_SEARCH_TEXT, null);
            LinearLayout group = new LinearLayout(mContext);
            ViewGroup event = (ViewGroup) notify.contentView.apply(mContext, group);
            recurseGroup(event);
            group.removeAllViews();
        } catch (Exception e) {
            mNotifyTextColor = android.R.color.black;
            mNotifyTitleColor = android.R.color.black;
        }
    }
}
