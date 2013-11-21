package com.doomonafireball.umbee;

import com.google.inject.Inject;
import com.google.inject.Injector;

import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibrary;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;
import android.util.Log;

import roboguice.RoboGuice;

@ReportsCrashes(formKey = "dEhNYllsNE9NbnNnY1lJeVlFcVhzVkE6MQ")
public class MainApp extends Application {

    public static String TAG = "umbee";

    @Inject Datastore mDataStore;

    @Override
    public void onCreate() {
        ACRA.init(this);
        super.onCreate();
        Log.i(TAG, "onCreate");
        LocationLibrary.initialiseLibrary(getBaseContext(), "com.doomonafireball.umbee");
        Injector i = RoboGuice.getBaseApplicationInjector(this);
        mDataStore = i.getInstance(Datastore.class);
    }
}

