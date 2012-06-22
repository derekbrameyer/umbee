package com.doomonafireball.umbee.dialog;

import com.actionbarsherlock.view.Window;
import com.doomonafireball.umbee.R;
import com.flurry.android.FlurryAgent;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * User: derek Date: 6/14/12 Time: 5:35 PM
 */
public class AboutDialog extends Dialog {

    private Context mContext;

    public AboutDialog(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature((int) Window.FEATURE_NO_TITLE);

        setContentView(R.layout.about);
        setCancelable(true);
        setCanceledOnTouchOutside(true);

        getWindow().setBackgroundDrawableResource(R.drawable.abs__dialog_full_holo_light);
    }

    @Override
    public void onStart() {
        super.onStart();
        FlurryAgent.onStartSession(mContext, mContext.getString(R.string.flurry_key));
        FlurryAgent.logEvent("about_dialog_on_start");
    }

    @Override
    public void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(mContext);
    }
}
