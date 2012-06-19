package com.doomonafireball.umbee.adapter;

import com.doomonafireball.umbee.R;
import com.doomonafireball.umbee.model.NoaaByDay;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * User: derek Date: 6/19/12 Time: 5:08 PM
 */
public class NoaaPagerAdapter extends PagerAdapter {

    private Context mContext;
    private NoaaByDay mNbd;
    private LayoutInflater inflater;

    public NoaaPagerAdapter(Context ctx, NoaaByDay nbd) {
        super();
        mContext = ctx;
        mNbd = nbd;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Pair<Integer, Integer> currPair = mNbd.mPop.probabilities.get(position);

        View layout = inflater.inflate(R.layout.precip_noaa, null);

        TextView precipTitleTV = (TextView) layout.findViewById(R.id.TV_precip_title);
        TextView morningPrecipTV = (TextView) layout.findViewById(R.id.TV_morning_precip);
        TextView eveningPrecipTV = (TextView) layout.findViewById(R.id.TV_evening_precip);
        morningPrecipTV.setText(
                String.format(mContext.getResources().getString(R.string.dynamic_int_percentage),
                        currPair.first));
        eveningPrecipTV.setText(
                String.format(mContext.getResources().getString(R.string.dynamic_int_percentage),
                        currPair.second));

        container.addView(layout);
        return layout;
    }

    @Override
    public int getCount() {
        return mNbd.mPop.probabilities.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return false;
    }
}
