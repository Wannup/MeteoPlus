package com.example.erwan.meteoplus;

import android.content.Context;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by mike on 03/07/2015.
 */
public enum DayTime {

    MATIN, APRES_MIDI, SOIR, NUIT;

    public String toString(Context context) {
        switch (this) {
            case MATIN:
                return context.getResources().getString(R.string.morning);
            case APRES_MIDI:
                return context.getResources().getString(R.string.afternoon);
            case SOIR:
                context.getResources().getString(R.string.evening);
            case NUIT:
                return context.getResources().getString(R.string.night);
            default:
                return "";
        }
    }

    public static DayTime getDayTime (Date from, Date to) {
        Calendar calendarFrom = Calendar.getInstance();
        calendarFrom.setTime(from);
        Calendar calendarTo = Calendar.getInstance();
        calendarTo.setTime(to);
        int hour = calendarFrom.get(Calendar.HOUR_OF_DAY);
        if (hour < 6) {
            return NUIT;
        } else if (hour < 12) {
            return MATIN;
        } else if (hour < 18) {
            return APRES_MIDI;
        } else {
            return SOIR;
        }
    }

}
