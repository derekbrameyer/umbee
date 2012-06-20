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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * User: derek Date: 6/19/12 Time: 5:08 PM
 */
public class NoaaPagerAdapter extends PagerAdapter {

    private Context mContext;
    private NoaaByDay mNbd;
    private LayoutInflater inflater;
    private SimpleDateFormat dayFormatter;

    public NoaaPagerAdapter(Context ctx, NoaaByDay nbd) {
        mContext = ctx;
        mNbd = nbd;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        dayFormatter = new SimpleDateFormat("EEEE");
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Pair<Integer, Integer> currPair = mNbd.mPop.probabilities.get(position);

        View v = inflater.inflate(R.layout.precip_noaa, null);

        TextView precipTitleTV = (TextView) v.findViewById(R.id.TV_precip_title);
        TextView morningPrecipTV = (TextView) v.findViewById(R.id.TV_morning_precip);
        TextView eveningPrecipTV = (TextView) v.findViewById(R.id.TV_evening_precip);

        morningPrecipTV.setText(
                String.format(mContext.getResources().getString(R.string.dynamic_int_percentage),
                        currPair.first));
        eveningPrecipTV.setText(
                String.format(mContext.getResources().getString(R.string.dynamic_int_percentage),
                        currPair.second));

        String dayOfWeek = "";
        if (position == 0) {
            dayOfWeek = mContext.getResources().getString(R.string.today);
        } else if (position == 1) {
            dayOfWeek = mContext.getResources().getString(R.string.tomorrow);
        } else {
            GregorianCalendar gc = (GregorianCalendar) GregorianCalendar.getInstance();
            gc.add(Calendar.DAY_OF_WEEK, position);
            dayOfWeek = dayFormatter.format(gc.getTime()).toLowerCase();
        }
        precipTitleTV.setText(String.format(mContext.getResources().getString(R.string.dynamic_day_precipitation), dayOfWeek));

        container.addView(v);
        return v;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object view) {
        container.removeView((View) view);
    }

    @Override
    public int getCount() {
        return mNbd.mPop.probabilities.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }
}
