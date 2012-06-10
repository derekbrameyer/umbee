package com.doomonafireball.umbee.query;

import com.doomonafireball.umbee.MainApp;
import com.doomonafireball.umbee.model.GeoNamesPostalCode;
import com.doomonafireball.umbee.util.JsonParser;
import com.doomonafireball.umbee.util.RestClient;
import com.doomonafireball.umbee.util.SharedPrefsManager;

import org.json.JSONException;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;

/**
 * User: Derek Date: 6/9/12 Time: 9:21 PM
 */
public class ZipCodeQuery extends AsyncTask<Void, Void, Void> {

    private static final String GEONAMES_URL = "http://ws.geonames.org/findNearbyPostalCodesJSON";

    private Context mContext;
    private float mLat;
    private float mLng;
    private SharedPrefsManager mSPM;
    private ArrayList<GeoNamesPostalCode> mPostalCodes;

    public ZipCodeQuery(Context ctx, float lat, float lng) {
        SharedPrefsManager.initialize(ctx);
        mSPM = SharedPrefsManager.getInstance();
        this.mContext = ctx;
        this.mLat = lat;
        this.mLng = lng;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected Void doInBackground(Void... voids) {
        if (mSPM.getEnableLocationUpdates()) {
            // Get Geonames results
            RestClient geoNamesClient = new RestClient(GEONAMES_URL);
            geoNamesClient.AddParam("formatted", "true");
            geoNamesClient.AddParam("lat", Float.toString(mLat));
            geoNamesClient.AddParam("lng", Float.toString(mLng));
            try {
                geoNamesClient.Execute(RestClient.RequestMethod.GET);
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            String response = geoNamesClient.getResponse();
            String errorMessage = geoNamesClient.getErrorMessage();
            int responseCode = geoNamesClient.getResponseCode();
            if (response != null) {
                Log.d(MainApp.TAG, "GeoNames response: " + response);
                // Parse the response
                try {
                    mPostalCodes = JsonParser.parseGeoNamesPostalCodes(response);
                } catch (JSONException e) {
                    mPostalCodes = new ArrayList<GeoNamesPostalCode>();
                }
            }
        } else {
            mPostalCodes = new ArrayList<GeoNamesPostalCode>();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void v) {
        if (mPostalCodes.size() > 0) {
            mSPM.setLocation(mPostalCodes.get(0).postalCode);
        }
    }
}
