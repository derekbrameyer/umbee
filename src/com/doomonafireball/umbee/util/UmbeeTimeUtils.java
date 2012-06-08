package com.doomonafireball.umbee.util;

import java.util.Calendar;

/**
 * User: derek Date: 5/24/12 Time: 7:24 PM
 */
public class UmbeeTimeUtils {

    public static String timeOfDayFromInt(int i) {
        int hourOfDay = (int) (i / 60);
        int minute = (int) (i % 60);
        if (hourOfDay == 0) {
            return "12:" + String.format("%02d", minute) + " AM";
        } else if (hourOfDay == 12) {
            return "12:" + String.format("%02d", minute) + " PM";
        } else if (hourOfDay > 12) {
            return "" + (hourOfDay - 12) + ":" + String.format("%02d", minute) + " PM";
        } else {
            return "" + hourOfDay + ":" + String.format("%02d", minute) + " AM";
        }
    }

    public static int hourOfDayFromInt(int i) {
        return ((int) (i / 60));
    }

    public static int minuteFromInt(int i) {
        return ((int) (i % 60));
    }

    public static int timeOfDayFromTimePicker(int hourOfDay, int minute) {
        return ((hourOfDay * 60) + (minute));
    }

    public static String formatNoaaForCalendar(Calendar c) {
        return "" +
                c.get(Calendar.YEAR) +
                "-" +
                String.format("%02d", (c.get(Calendar.MONTH) + 1)) +
                "-" +
                String.format("%02d", c.get(Calendar.DATE));
    }

}
