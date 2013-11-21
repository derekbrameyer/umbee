package com.doomonafireball.umbee.util;

import com.actionbarsherlock.app.ActionBar;
import com.doomonafireball.umbee.R;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;

import oak.util.FontTypefaceSpan;

/**
 * User: derek Date: 6/5/12 Time: 3:33 PM
 */
public class UmbeeTextUtils {

    private static final String zipCodePattern = "\\d{5}(-\\d{4})?";

    public static boolean isValidZipCode(String zip) {
        return zip.matches(zipCodePattern);
    }

    public static void setActionBarTitle(Context context, ActionBar actionBar, int titleResId) {
        SpannableString s = new SpannableString(context.getString(titleResId));
        s.setSpan(new FontTypefaceSpan(context, context.getString(R.string.font_light)), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        actionBar.setTitle(s);
    }
}
