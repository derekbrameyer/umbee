package com.doomonafireball.umbee.util;

import com.doomonafireball.umbee.model.GeoNamesPostalCode;
import com.doomonafireball.umbee.model.NoaaByDay;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Pair;

import java.util.ArrayList;

/**
 * User: Derek Date: 6/9/12 Time: 9:36 PM
 */
public class JsonParser {

    public static ArrayList<GeoNamesPostalCode> parseGeoNamesPostalCodes(String json) throws
            JSONException {
        ArrayList<GeoNamesPostalCode> postalCodes = new ArrayList<GeoNamesPostalCode>();
        JSONObject o = new JSONObject(json);
        JSONArray codes = o.optJSONArray("postalCodes");
        for (int i = 0; i < codes.length(); i++) {
            postalCodes.add(parseGnpc((JSONObject) codes.get(i)));
        }
        return postalCodes;
    }

    private static GeoNamesPostalCode parseGnpc(JSONObject o) {
        GeoNamesPostalCode gnpc = new GeoNamesPostalCode();
        gnpc.adminCode1 = o.optString("adminCode1");
        gnpc.adminCode2 = o.optString("adminCode2");
        gnpc.adminName1 = o.optString("adminName1");
        gnpc.adminName2 = o.optString("adminName2");
        gnpc.postalCode = o.optString("postalCode");
        gnpc.countryCode = o.optString("countryCode");
        gnpc.distance = o.optString("distance");
        gnpc.placeName = o.optString("placeName");
        gnpc.lat = o.optString("lat");
        gnpc.lng = o.optString("lng");
        return gnpc;
    }

    public static NoaaByDay parseNoaaByDay(String json) throws JSONException {
        NoaaByDay nbd = new NoaaByDay();
        nbd.mPop = new NoaaByDay.NoaaProbabilityOfPrecipitation();
        nbd.mPop.probabilities = new ArrayList<Pair<Integer, Integer>>();
        JSONObject o = new JSONObject(json);
        JSONObject mPop = o.optJSONObject("mPop");
        nbd.mPop.type = o.optString("type");
        nbd.mPop.units = o.optString("units");
        nbd.mPop.timeLayout = o.optString("timeLayout");
        nbd.mPop.name = o.optString("name");
        JSONArray mPopProbabilitiesJson = mPop.optJSONArray("probabilities");
        for (int i = 0; i < mPopProbabilitiesJson.length(); i++) {
            nbd.mPop.probabilities.add(parseNpop((JSONObject) mPopProbabilitiesJson.get(i)));
        }
        return nbd;
    }

    private static Pair<Integer, Integer> parseNpop(JSONObject o) {
        int first = o.optInt("morningProbability");
        int second = o.optInt("eveningProbability");
        Pair<Integer, Integer> pair = new Pair<Integer, Integer>(first, second);
        return pair;
    }
}
