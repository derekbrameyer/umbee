package com.doomonafireball.umbee.util;

/**
 * User: derek Date: 6/5/12 Time: 3:33 PM
 */
public class UmbeeTextUtils {

    private static final String zipCodePattern = "\\d{5}(-\\d{4})?";

    public static boolean isValidZipCode(String zip) {
        return zip.matches(zipCodePattern);
    }

}
