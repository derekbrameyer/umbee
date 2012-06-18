package com.doomonafireball.umbee.receiver;

import com.doomonafireball.umbee.query.ZipCodeQuery;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationInfo;
import com.littlefluffytoys.littlefluffylocationlibrary.LocationLibraryConstants;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

/**
 * User: Derek Date: 6/9/12 Time: 9:15 PM
 */
public class LocationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("LocationBroadcastReceiver", "onReceive: received location update");

        final LocationInfo locationInfo = (LocationInfo) intent.getSerializableExtra(
                LocationLibraryConstants.LOCATION_BROADCAST_EXTRA_LOCATIONINFO);

        // Translate the LocationInfo to zip code and persist
        ZipCodeQuery zcq = new ZipCodeQuery(context, locationInfo.lastLat, locationInfo.lastLong, false, false, new Handler());
        zcq.execute();
    }
}
