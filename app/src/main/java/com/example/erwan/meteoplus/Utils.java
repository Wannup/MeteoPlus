package com.example.erwan.meteoplus;

import android.content.Context;

import java.util.Calendar;

/**
 * Created by mike on 06/07/2015.
 */
public class Utils {

    public static String getStringDay (int day, Context context) {
        switch (day) {
            case Calendar.MONDAY :
                return context.getResources().getString(R.string.monday);
            case Calendar.TUESDAY :
                return context.getResources().getString(R.string.tuesday);
            case Calendar.WEDNESDAY :
                return context.getResources().getString(R.string.wednesdayY);
            case Calendar.THURSDAY :
                return context.getResources().getString(R.string.thursday);
            case Calendar.FRIDAY :
                return context.getResources().getString(R.string.friday);
            case Calendar.SATURDAY :
                return context.getResources().getString(R.string.saturday);
            case Calendar.SUNDAY :
                return context.getResources().getString(R.string.sunday);
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
