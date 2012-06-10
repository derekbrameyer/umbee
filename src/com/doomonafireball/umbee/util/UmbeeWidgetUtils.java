package com.doomonafireball.umbee.util;

import com.doomonafireball.umbee.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * User: Derek Date: 6/9/12 Time: 10:05 PM
 */
public class UmbeeWidgetUtils {

    public static LayerDrawable getActionBarDrawable(Context ctx, int bgColor) {
        // The sun
        GradientDrawable gd1 = (GradientDrawable) ctx.getResources()
                .getDrawable(R.drawable.action_bar_bg_layer_1).mutate();
        gd1.setGradientCenter(getPercentOfDay(), 0.0f);
        // The bottom yellow bar
        GradientDrawable gd2 = (GradientDrawable) ctx.getResources()
                .getDrawable(R.drawable.action_bar_bg_layer_2);
        // The changing color background
        GradientDrawable gd3 = (GradientDrawable) ctx.getResources()
                .getDrawable(R.drawable.action_bar_bg_layer_2).mutate();
        gd3.setColor(bgColor);
        Drawable[] layers = new Drawable[3];
        layers[0] = gd3;
        layers[1] = gd1;
        layers[2] = gd2;
        LayerDrawable ld = new LayerDrawable(layers);
        ld.setLayerInset(
                2,
                0,
                ctx.getResources().getDimensionPixelSize(R.dimen.action_bar_offset),
                0,
                0);
        return ld;
    }

    public static float getPercentOfDay() {
        GregorianCalendar cal = new GregorianCalendar();
        float dayMillis = 0;
        dayMillis += (cal.get(Calendar.HOUR_OF_DAY) * 60 * 60);
        dayMillis += (cal.get(Calendar.MINUTE) * 60);
        dayMillis += cal.get(Calendar.SECOND);
        float totalDayMillis = (24f * 60f * 60f);
        return (dayMillis / totalDayMillis);
    }

    public static int getBetweenColorByPercent(float percent, int color1, int color2) {
        int r = (color1 >> 16);
        int g = (color1 >> 8 & 0xFF);
        int b = (color1 & 0xFF);

        r += ((color2 >> 16) - r) * percent;
        g += ((color2 >> 8 & 0xFF) - g) * percent;
        b += ((color2 & 0xFF) - b) * percent;

        int retVal = (r << 16 | g << 8 | b);
        return retVal;
    }
}
