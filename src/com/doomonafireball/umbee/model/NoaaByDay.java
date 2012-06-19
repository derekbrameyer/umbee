package com.doomonafireball.umbee.model;

import com.doomonafireball.umbee.util.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Pair;

import java.util.ArrayList;

/**
 * User: derek Date: 6/4/12 Time: 5:17 PM
 */
public class NoaaByDay {

    public NoaaProbabilityOfPrecipitation mPop;

    public NoaaByDay() {
        mPop = new NoaaProbabilityOfPrecipitation();
        mPop.probabilities = new ArrayList<Pair<Integer, Integer>>();
    }

    public static class NoaaProbabilityOfPrecipitation {

        public String type;
        public String units;
        public String timeLayout;
        public String name;
        public ArrayList<Pair<Integer, Integer>> probabilities;
    }

    public String toJsonString() throws JSONException {
        JSONObject json = new JSONObject();
        JSONObject mPopJson = new JSONObject();
        mPopJson.put("type", mPop.type);
        mPopJson.put("units", mPop.units);
        mPopJson.put("timeLayout", mPop.timeLayout);
        mPopJson.put("name", mPop.name);
        JSONArray mPopProbabilitiesJson = new JSONArray();
        for (int i = 0; i < mPop.probabilities.size(); i++) {
            Pair<Integer, Integer> mPopProb = mPop.probabilities.get(i);
            JSONObject mPopP = new JSONObject();
            mPopP.put("morningProbability", mPopProb.first);
            mPopP.put("eveningProbability", mPopProb.second);
            mPopProbabilitiesJson.put(mPopP);
        }
        mPopJson.put("probabilities", mPopProbabilitiesJson);
        json.put("mPop", mPopJson);
        return json.toString();
    }

}
