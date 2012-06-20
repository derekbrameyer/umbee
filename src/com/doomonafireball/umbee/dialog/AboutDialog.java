package com.doomonafireball.umbee.dialog;

import com.actionbarsherlock.view.Window;
import com.doomonafireball.umbee.R;

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

    /*public AboutDialog(Context context, int theme) {
        super(context, theme);
        mContext = context;
    }

    protected AboutDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        mContext = context;
    }

    public AboutDialog(Context context, Bundle bundle, Handler handler) {
        super(context);
        mContext = context;
        mExtras = bundle;
        mHandler = handler;
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature((int) Window.FEATURE_NO_TITLE);

        setContentView(R.layout.about);
        setCancelable(true);
        setCanceledOnTouchOutside(true);

        getWindow().setBackgroundDrawableResource(R.drawable.abs__dialog_full_holo_light);
    }
}
