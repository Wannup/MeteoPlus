package com.example.erwan.meteoplus;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by mike on 03/07/2015.
 */
public enum DayTime {

    MATIN, APRES_MIDI, SOIR, NUIT;

    @Override
    public String toString() {
        switch (this) {
            case MATIN:
                return "Matin";
            case APRES_MIDI:
                return "Après-midi";
            case SOIR:
                return "Soir";
            case NUIT:
                return "Nuit";
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
