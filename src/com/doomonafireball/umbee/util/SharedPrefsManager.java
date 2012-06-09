package com.doomonafireball.umbee.util;

import com.doomonafireball.umbee.R;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * User: derek Date: 5/24/12 Time: 6:47 PM
 */
public class SharedPrefsManager {

    private static String UMBEE_ENABLED = "umbee_enabled";
    private static String UMBEE_LOCATION = "umbee_location";
    private static String UMBEE_UPDATE_TIME = "umbee_update_time";
    private static String UMBEE_API = "umbee_api";
    private static String UMBEE_ALERT_TYPE = "umbee_alert_type";
    private static String UMBEE_CUSTOM_THRESHOLD = "umbee_custom_threshold";
    private static String UMBEE_SINGLE_THRESHOLD = "umbee_single_threshold";
    private static String UMBEE_TRIPLE_THRESHOLD_1 = "umbee_triple_threshold_1";
    private static String UMBEE_TRIPLE_THRESHOLD_2 = "umbee_triple_threshold_2";
    private static String UMBEE_TRIPLE_THRESHOLD_3 = "umbee_triple_threshold_3";
    private static String UMBEE_ENABLE_LOCATION_UPDATE = "umbee_enable_location_update";
    private static String UMBEE_ADVANCED_OPTIONS = "umbee_advanced_options";
    private static String UMBEE_NOAA_MORNING = "umbee_noaa_morning";
    private static String UMBEE_NOAA_EVENING = "umbee_noaa_evening";

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
        return settings.getBoolean(UMBEE_ENABLED, true);
    }

    public void setEnabled(boolean b) {
        editor.putBoolean(UMBEE_ENABLED, b);
        editor.commit();
    }

    public String getLocation() {
        return settings.getString(UMBEE_LOCATION, "");
    }

    public void setLocation(String s) {
        editor.putString(UMBEE_LOCATION, s);
        editor.commit();
    }

    public int getUpdateTime() {
        return settings.getInt(UMBEE_UPDATE_TIME, 300);
    }

    public void setUpdateTime(int i) {
        editor.putInt(UMBEE_UPDATE_TIME, i);
        editor.commit();
    }

    public int getAlertType() {
        return settings.getInt(UMBEE_ALERT_TYPE, Refs.ALERT_SIMPLE);
    }

    public void setAlertType(int i) {
        editor.putInt(UMBEE_ALERT_TYPE, i);
        editor.commit();
    }

    public boolean getCustomThreshold() {
        return settings.getBoolean(UMBEE_CUSTOM_THRESHOLD, false);
    }

    public void setCustomThreshold(boolean b) {
        editor.putBoolean(UMBEE_CUSTOM_THRESHOLD, b);
        editor.commit();
    }

    public boolean getAdvancedOptions() {
        return settings.getBoolean(UMBEE_ADVANCED_OPTIONS, false);
    }

    public void setAdvancedOptions(boolean b) {
        editor.putBoolean(UMBEE_ADVANCED_OPTIONS, b);
        editor.commit();
    }

    public boolean getEnableLocationUpdates() {
        return settings.getBoolean(UMBEE_ENABLE_LOCATION_UPDATE, false);
    }

    public void setEnableLocationUpdates(boolean b) {
        editor.putBoolean(UMBEE_ENABLE_LOCATION_UPDATE, b);
        editor.commit();
    }

    public int getSingleThreshold() {
        return settings.getInt(UMBEE_SINGLE_THRESHOLD, 50);
    }

    public void setSingleThreshold(int i) {
        editor.putInt(UMBEE_SINGLE_THRESHOLD, i);
        editor.commit();
    }

    public int getTripleThreshold1() {
        return settings.getInt(UMBEE_TRIPLE_THRESHOLD_1, 25);
    }

    public void setTripleThreshold1(int i) {
        editor.putInt(UMBEE_TRIPLE_THRESHOLD_1, i);
        editor.commit();
    }

    public int getTripleThreshold2() {
        return settings.getInt(UMBEE_TRIPLE_THRESHOLD_2, 50);
    }

    public void setTripleThreshold2(int i) {
        editor.putInt(UMBEE_TRIPLE_THRESHOLD_2, i);
        editor.commit();
    }

    public int getTripleThreshold3() {
        return settings.getInt(UMBEE_TRIPLE_THRESHOLD_3, 75);
    }

    public void setTripleThreshold3(int i) {
        editor.putInt(UMBEE_TRIPLE_THRESHOLD_3, i);
        editor.commit();
    }

    public int getNoaaMorningPrecip() {
        return settings.getInt(UMBEE_NOAA_MORNING, -1);
    }

    public void setNoaaMorningPrecip(int i) {
        editor.putInt(UMBEE_NOAA_MORNING, i);
        editor.commit();
    }

    public int getNoaaEveningPrecip() {
        return settings.getInt(UMBEE_NOAA_EVENING, -1);
    }

    public void setNoaaEveningPrecip(int i) {
        editor.putInt(UMBEE_NOAA_EVENING, i);
        editor.commit();
    }

}
