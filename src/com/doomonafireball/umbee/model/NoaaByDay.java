package com.doomonafireball.umbee.model;

/**
 * User: derek Date: 6/4/12 Time: 5:17 PM
 */
public class NoaaByDay {

    public NoaaProbabilityOfPrecipitation mPop;

    public static class NoaaProbabilityOfPrecipitation {
        public String type;
        public String units;
        public String timeLayout;
        public String name;
        public int morningProbability;
        public int eveningProbability;
    }

}
