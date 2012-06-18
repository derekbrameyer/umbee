package com.doomonafireball.umbee.query;

import com.doomonafireball.umbee.MainApp;
import com.doomonafireball.umbee.R;
import com.doomonafireball.umbee.model.NoaaByDay;
import com.doomonafireball.umbee.util.RestClient;
import com.doomonafireball.umbee.util.SharedPrefsManager;
import com.doomonafireball.umbee.util.UmbeeNotifUtils;
import com.doomonafireball.umbee.util.UmbeeTimeUtils;
import com.doomonafireball.umbee.util.XmlParser;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.GregorianCalendar;

/**
 * User: derek Date: 5/26/12 Time: 6:33 PM
 */
public class WeatherQuery extends AsyncTask<Void, Void, Void> {

    private static final String NOAA_URL
            = "http://graphical.weather.gov/xml/sample_products/browser_interface/ndfdBrowserClientByDay.php";

    private boolean mDisplayProgress;
    private boolean mCreateNotif;
    private Context mContext;
    private ProgressDialog mDialog;
    private NoaaByDay mNbd = new NoaaByDay();
    private SharedPrefsManager mSPM;
    private Handler mHandler;
    private String response;
    private String errorMessage;
    private int responseCode;

    public WeatherQuery(Context ctx, boolean displayProgress, boolean createNotif, Handler handler) {
        SharedPrefsManager.initialize(ctx);
        mSPM = SharedPrefsManager.getInstance();
        this.mContext = ctx;
        this.mDisplayProgress = displayProgress;
        this.mCreateNotif = createNotif;
        this.mHandler = handler;
    }

    @Override
    protected void onPreExecute() {
        if (mDisplayProgress) {
            mDialog = new ProgressDialog(mContext);
            mDialog.setIndeterminate(true);
            mDialog.setMessage(mContext.getResources().getString(R.string.test_notif_fetching));
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
        // Get NOAA results
        RestClient noaaClient = new RestClient(NOAA_URL);
        noaaClient.AddParam("format", "24 hourly");
        noaaClient.AddParam("numDays", "1");
        noaaClient.AddParam("zipCodeList", mSPM.getLocation());
        noaaClient.AddParam("startDate",
                UmbeeTimeUtils.formatNoaaForCalendar(new GregorianCalendar()));
        try {
            noaaClient.Execute(RestClient.RequestMethod.GET);
        } catch (Exception e) {
            e.printStackTrace();
        }
        response = noaaClient.getResponse();
        errorMessage = noaaClient.getErrorMessage();
        responseCode = noaaClient.getResponseCode();
        if (response != null) {
            Log.d(MainApp.TAG, "NOAA response: " + response);
            // Parse the response
            mNbd = XmlParser.parseNoaaByDay(response);
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
        // Persist data to SharedPrefs
        mSPM.setNoaaMorningPrecip(mNbd.mPop.morningProbability);
        mSPM.setNoaaEveningPrecip(mNbd.mPop.eveningProbability);
        if (mCreateNotif) {
            // Create notification
            UmbeeNotifUtils.createNotification(mContext, mNbd);
        }
        mHandler.sendMessage(new Message());
    }
}
