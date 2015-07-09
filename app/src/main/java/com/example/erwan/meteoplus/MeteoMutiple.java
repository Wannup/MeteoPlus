package com.example.erwan.meteoplus;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by mike on 03/07/2015.
 */
public class MeteoMutiple {

    Map<Date, Map<DayTime, Meteo>> map;

    private SQLiteDatabase database;
    private DatabaseHandler databaseHandler;
    private String[] allColumns = {
            DatabaseHandler.TABLE_METEO_COLUMN_NAME,
            DatabaseHandler.TABLE_METEO_COLUMN_TEMPERATURE,
            DatabaseHandler.TABLE_METEO_COLUMN_WEATHER,
            DatabaseHandler.TABLE_METEO_COLUMN_HUMIDITY,
            DatabaseHandler.TABLE_METEO_COLUMN_PRESSURE,
            DatabaseHandler.TABLE_METEO_COLUMN_SPEED,
            DatabaseHandler.TABLE_METEO_COLUMN_DIRECTION,
            DatabaseHandler.TABLE_METEO_COLUMN_DATE
    };

    public MeteoMutiple(Context context) {
        this.databaseHandler = new DatabaseHandler(context);
        this.map = new HashMap<>();
    }

    public DayTime getDayTime (Date date, int i) {
        int value = 4 + i - this.size(date);
        System.out.println("i = " + i);
        System.out.println("this.size(date) = " + this.size(date));
        System.out.println("value = " + value);
        switch (value) {
            case 0:
                return DayTime.NUIT;
            case 1:
                return DayTime.MATIN;
            case 2:
                return DayTime.APRES_MIDI;
            case 3:
                return DayTime.SOIR;
        }
        return null;
    }

    public int size (Date date) {
        return this.map.get(date).size();
    }

    public Meteo getMeteo (Date date, DayTime dayTime) {
        if (!this.map.containsKey(date)) {
            return null;
        }
        if (!this.map.get(date).containsKey(dayTime)) {
            return null;
        }
        return this.map.get(date).get(dayTime);
    }

    public void setMeteo (Date date, DayTime dayTime, Meteo meteo) {
        if (!this.map.containsKey(date)) {
            this.map.put(date, new HashMap<DayTime, Meteo>());
        }
        this.map.get(date).put(dayTime, meteo);
    }

    private void open () {
        this.database = this.databaseHandler.getWritableDatabase();
    }

    private void close () {
        this.databaseHandler.close();
    }

    public void save () {
        this.open();

        this.close();
    }

    public void save (Date date, DayTime dayTime) {
        /*this.open();
        Meteo meteo = this.getMeteo(date, dayTime);
        if (meteo != null) {
            meteo.setDate(new Date());
            ContentValues values = new ContentValues();
            values.put(DatabaseHandler.TABLE_METEO_COLUMN_NAME, meteo.getName());
            values.put(DatabaseHandler.TABLE_METEO_COLUMN_DAY, new SimpleDateFormat("yyyy-MM-dd").format(date));
            values.put(DatabaseHandler.TABLE_METEO_COLUMN_DAYTIME, dayTime.name());
            values.put(DatabaseHandler.TABLE_METEO_COLUMN_TEMPERATURE, meteo.getTemperature());
            values.put(DatabaseHandler.TABLE_METEO_COLUMN_WEATHER, meteo.getWeather());
            values.put(DatabaseHandler.TABLE_METEO_COLUMN_HUMIDITY, meteo.getHumidity());
            values.put(DatabaseHandler.TABLE_METEO_COLUMN_PRESSURE, meteo.getPressure());
            values.put(DatabaseHandler.TABLE_METEO_COLUMN_SPEED, meteo.getSpeed());
            values.put(DatabaseHandler.TABLE_METEO_COLUMN_DIRECTION, meteo.getDirection());
            values.put(DatabaseHandler.TABLE_METEO_COLUMN_DATE, meteo.getDate());
            this.database.insert(DatabaseHandler.TABLE_METEO, null, values);
        }
        this.close();*/
    }

    public void delete () {
        this.open();
        this.close();
    }

    public void delete (Date date, DayTime dayTime) {
        /*this.open();
        Meteo meteo = this.getMeteo(date, dayTime);
        if (meteo != null) {
            this.database.delete(DatabaseHandler.TABLE_METEO,
            DatabaseHandler.TABLE_METEO_COLUMN_NAME + " = '" + meteo.getName() + "'"
            + " AND " + DatabaseHandler.TABLE_METEO_COLUMN_DAY + " = '" + new SimpleDateFormat("yyyy-MM-dd").format(date) + "'"
            + " AND " + DatabaseHandler.TABLE_METEO_COLUMN_DAYTIME + " = '" + dayTime.name() + "'", null);
        }
        this.close();*/
    }

    public void load () {
        if (exist()) {
            this.open();

            this.close();
        }
    }

    public boolean exist () {
        this.open();

        this.close();
        return false;
    }
}
