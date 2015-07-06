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

    public static int getImageByWeather (String weather) {
        switch (weather) {
            case "01d":
                return R.drawable.sund;
            case "01n":
                return R.drawable.moonn;
            case "02d":
                return R.drawable.suncloud;
            case "02n":
                return R.drawable.mooncloud;
            case "03d":
                return R.drawable.cloud;
            case "03n":
                return R.drawable.cloud;
            case "04d":
                return R.drawable.darkcloud;
            case "04n":
                return R.drawable.darkcloud;
            case "09d":
                return R.drawable.rain;
            case "09n":
                return R.drawable.rain;
            case "10d":
                return R.drawable.suncloudrain;
            case "10n":
                return R.drawable.mooncloudrain;
            case "11d":
                return R.drawable.lightning;
            case "11n":
                return R.drawable.lightning;
            case "13d":
                return R.drawable.snow;
            case "13n":
                return R.drawable.snow;
            case "50d":
                return R.drawable.fog;
            case "50n":
                return R.drawable.fog;
            default:
                return 0;
        }
    }
}
