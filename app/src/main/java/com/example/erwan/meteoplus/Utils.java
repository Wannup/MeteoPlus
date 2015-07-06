package com.example.erwan.meteoplus;

import java.util.Calendar;

/**
 * Created by mike on 06/07/2015.
 */
public class Utils {

    public static String getStringDay (int day) {
        switch (day) {
            case Calendar.MONDAY :
                return "Lundi";
            case Calendar.TUESDAY :
                return "Mardi";
            case Calendar.WEDNESDAY :
                return "Mercredi";
            case Calendar.THURSDAY :
                return "Jeudi";
            case Calendar.FRIDAY :
                return "Vendredi";
            case Calendar.SATURDAY :
                return "Samedi";
            case Calendar.SUNDAY :
                return "Dimanche";
            default:
                return "";
        }
    }
}
