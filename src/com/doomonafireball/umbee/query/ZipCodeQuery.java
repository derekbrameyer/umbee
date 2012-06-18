package com.doomonafireball.umbee.query;

import com.doomonafireball.umbee.MainApp;
import com.doomonafireball.umbee.R;
import com.doomonafireball.umbee.model.GeoNamesPostalCode;
import com.doomonafireball.umbee.util.JsonParser;
import com.doomonafireball.umbee.util.RestClient;
import com.doomonafireball.umbee.util.SharedPrefsManager;

import org.json.JSONException;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;

/**
 * User: Derek Date: 6/9/12 Time: 9:21 PM
 */
public class ZipCodeQuery extends AsyncTask<Void, Void, Void> {

    private static final String GEONAMES_URL = "http://ws.geonames.org/findNearbyPostalCodesJSON";

    private Context mContext;
    private ProgressDialog mDialog;
    private float mLat;
    private float mLng;
    private boolean override;
    private boolean mDisplayProgress;
    private SharedPrefsManager mSPM;
    private ArrayList<GeoNamesPostalCode> mPostalCodes;
    private Handler mHandler;
    private String response;
    private String errorMessage;
    private int responseCode;

    public ZipCodeQuery(Context ctx, float lat, float lng, boolean displayProgress, boolean override, Handler handler) {
        SharedPrefsManager.initialize(ctx);
        mSPM = SharedPrefsManager.getInstance();
        this.mContext = ctx;
        this.mLat = lat;
        this.mLng = lng;
        this.override = override;
        this.mDisplayProgress = displayProgress;
        this.mHandler = handler;
    }

    @Override
    protected void onPreExecute() {
        if (mDisplayProgress) {
            mDialog = new ProgressDialog(mContext);
            mDialog.setIndeterminate(true);
            mDialog.setMessage(mContext.getResources().getString(R.string.zip_code_fetching));
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.setCancelable(true);
            mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    if (mDialog.isShowing()) {
                        mDialog.dismiss();
                    }
                }
            });
            mDialog.show();
        }
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if (mSPM.getEnableLocationUpdates() || this.override) {
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
            response = geoNamesClient.getResponse();
            errorMessage = geoNamesClient.getErrorMessage();
            responseCode = geoNamesClient.getResponseCode();
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

    protected void onCancelled() {
        if (mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    @Override
    protected void onPostExecute(Void v) {
        if (mDisplayProgress) {
            mDialog.dismiss();
        }
        if (mPostalCodes.size() > 0) {
            mSPM.setLocation(mPostalCodes.get(0).postalCode);
        }
        mHandler.sendMessage(new Message());
    }
}
