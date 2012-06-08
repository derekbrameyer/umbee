package com.doomonafireball.umbee.util;

import com.doomonafireball.umbee.R;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * User: derek Date: 5/24/12 Time: 6:47 PM
 */
public class SharedPrefsManager {

    private static String UMBIE_ENABLED = "umbie_enabled";
    private static String UMBIE_LOCATION = "umbie_location";
    private static String UMBIE_UPDATE_TIME = "umbie_update_time";
    private static String UMBIE_API = "umbie_api";
    private static String UMBIE_ALERT_TYPE = "umbie_alert_type";
    private static String UMBIE_CUSTOM_THRESHOLD = "umbie_custom_threshold";
    private static String UMBIE_SINGLE_THRESHOLD = "umbie_single_threshold";
    private static String UMBIE_TRIPLE_THRESHOLD_1 = "umbie_triple_threshold_1";
    private static String UMBIE_TRIPLE_THRESHOLD_2 = "umbie_triple_threshold_2";
    private static String UMBIE_TRIPLE_THRESHOLD_3 = "umbie_triple_threshold_3";
    private static String UMBIE_ENABLE_LOCATION_UPDATE = "umbie_enable_location_update";
    private static String UMBIE_ADVANCED_OPTIONS = "umbie_advanced_options";

    private static SharedPrefsManager sharedPrefsManager;
    private SharedPreferences.Editor editor;
    private SharedPreferences settings;

    public static void initialize(Context context) {
        sharedPrefsManager = new SharedPrefsManager(context);
    }

    public static SharedPrefsManager getInstance() {
        return sharedPrefsManager;
    }

    private SharedPrefsManager(Context context) {
        String prefsFile = context.getString(R.string.prefs_file);
        settings = context.getSharedPreferences(prefsFile, Context.MODE_PRIVATE);
        editor = settings.edit();
    }

    public boolean getEnabled() {
        return settings.getBoolean(UMBIE_ENABLED, true);
    }

    public void setEnabled(boolean b) {
        editor.putBoolean(UMBIE_ENABLED, b);
        editor.commit();
    }

    public String getLocation() {
        return settings.getString(UMBIE_LOCATION, "");
    }

    public void setLocation(String s) {
        editor.putString(UMBIE_LOCATION, s);
        editor.commit();
    }

    public int getUpdateTime() {
        return settings.getInt(UMBIE_UPDATE_TIME, 300);
    }

    public void setUpdateTime(int i) {
        editor.putInt(UMBIE_UPDATE_TIME, i);
        editor.commit();
    }

    public int getAlertType() {
        return settings.getInt(UMBIE_ALERT_TYPE, Refs.ALERT_SIMPLE);
    }

    public void setAlertType(int i) {
        editor.putInt(UMBIE_ALERT_TYPE, i);
        editor.commit();
    }

    public boolean getCustomThreshold() {
        return settings.getBoolean(UMBIE_CUSTOM_THRESHOLD, false);
    }

    public void setCustomThreshold(boolean b) {
        editor.putBoolean(UMBIE_CUSTOM_THRESHOLD, b);
        editor.commit();
    }

    public boolean getAdvancedOptions() {
        return settings.getBoolean(UMBIE_ADVANCED_OPTIONS, false);
    }

    public void setAdvancedOptions(boolean b) {
        editor.putBoolean(UMBIE_ADVANCED_OPTIONS, b);
        editor.commit();
    }

    public boolean getEnableLocationUpdates() {
        return settings.getBoolean(UMBIE_ENABLE_LOCATION_UPDATE, false);
    }

    public void setEnableLocationUpdates(boolean b) {
        editor.putBoolean(UMBIE_ENABLE_LOCATION_UPDATE, b);
        editor.commit();
    }

    public int getSingleThreshold() {
        return settings.getInt(UMBIE_SINGLE_THRESHOLD, 50);
    }

    public void setSingleThreshold(int i) {
        editor.putInt(UMBIE_SINGLE_THRESHOLD, i);
        editor.commit();
    }

    public int getTripleThreshold1() {
        return settings.getInt(UMBIE_TRIPLE_THRESHOLD_1, 25);
    }

    public void setTripleThreshold1(int i) {
        editor.putInt(UMBIE_TRIPLE_THRESHOLD_1, i);
        editor.commit();
    }

    public int getTripleThreshold2() {
        return settings.getInt(UMBIE_TRIPLE_THRESHOLD_2, 50);
    }

    public void setTripleThreshold2(int i) {
        editor.putInt(UMBIE_TRIPLE_THRESHOLD_2, i);
        editor.commit();
    }

    public int getTripleThreshold3() {
        return settings.getInt(UMBIE_TRIPLE_THRESHOLD_3, 75);
    }

    public void setTripleThreshold3(int i) {
        editor.putInt(UMBIE_TRIPLE_THRESHOLD_3, i);
        editor.commit();
    }

}
