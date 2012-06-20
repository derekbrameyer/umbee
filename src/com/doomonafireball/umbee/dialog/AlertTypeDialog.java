package com.doomonafireball.umbee.dialog;

import com.actionbarsherlock.view.Window;
import com.doomonafireball.umbee.R;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
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
public class AlertTypeDialog extends Dialog {


    String[] alertTypeOptions;
    AlertTypeAdapter alertTypeAdapter;

    private ListView itemsLV;
    private Context mContext;
    private Bundle mExtras;
    private Handler mHandler;

    public static final int START_ALERT_TYPE_ACTIVITY_FOR_RESULT = 31337;
    public static final String ALERT_TYPE_OPTIONS = "alert_type_options";
    public static final String SELECTED_ALERT = "selected_alert";
    public static final String SELECTED_ALERT_TEXT = "selected_alert_text";

    public AlertTypeDialog(Context context) {
        super(context);
        mContext = context;
    }

    public AlertTypeDialog(Context context, int theme) {
        super(context, theme);
        mContext = context;
    }

    protected AlertTypeDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        mContext = context;
    }

    public AlertTypeDialog(Context context, Bundle bundle, Handler handler) {
        super(context);
        mContext = context;
        mExtras = bundle;
        mHandler = handler;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature((int) Window.FEATURE_NO_TITLE);

        setContentView(R.layout.alert_type);
        setCancelable(true);
        setCanceledOnTouchOutside(true);

        if (mExtras != null && mExtras.containsKey(ALERT_TYPE_OPTIONS)) {
            alertTypeOptions = mExtras.getStringArray(ALERT_TYPE_OPTIONS);
        } else {
            mHandler.sendMessage(new Message());
        }

        alertTypeAdapter = new AlertTypeAdapter(mContext, alertTypeOptions);
        getWindow().setBackgroundDrawableResource(R.drawable.abs__dialog_full_holo_light);

        itemsLV = (ListView) findViewById(R.id.LV_items);

        itemsLV.setAdapter(alertTypeAdapter);
        itemsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Bundle extras = new Bundle();
                extras.putInt(SELECTED_ALERT, position);
                extras.putString(SELECTED_ALERT_TEXT, alertTypeOptions[position]);
                Message returnMsg = new Message();
                returnMsg.setData(extras);
                mHandler.sendMessage(returnMsg);
                dismiss();
            }
        });
    }

    private class AlertTypeAdapter extends BaseAdapter {

        Context mContext;
        String[] mItems;
        private final LayoutInflater inflator;

        private class ViewHolder {

            public TextView itemTextView;
        }

        AlertTypeAdapter(Context context, String[] items) {
            super();
            this.mContext = context;
            this.mItems = items;
            inflator = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return mItems.length;
        }

        @Override
        public Object getItem(int position) {
            return mItems[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = inflator.inflate(R.layout.list_view_item, null);

                TextView itemTV = (TextView) convertView.findViewById(R.id.TV_list_item);
                holder = new ViewHolder();
                holder.itemTextView = itemTV;
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.itemTextView.setText(mItems[position]);

            return convertView;
        }
    }
}
