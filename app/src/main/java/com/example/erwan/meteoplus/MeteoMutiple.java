package com.example.erwan.meteoplus;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by mike on 03/07/2015.
 */
public class MeteoMutiple {

    private final Map<Date, Map<DayTime, Meteo>> map;
    private final String name;
    private final Context context;
    private List<Date> dates;

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
            DatabaseHandler.TABLE_METEO_COLUMN_UNIT,
            DatabaseHandler.TABLE_METEO_COLUMN_DATE,
            DatabaseHandler.TABLE_METEO_COLUMN_DAY,
            DatabaseHandler.TABLE_METEO_COLUMN_DAYTIME
    };

    public MeteoMutiple(Context context, String name) {
        this.context = context;
        this.databaseHandler = new DatabaseHandler(context);
        this.map = new HashMap<>();
        this.name = name;
        this.dates = new ArrayList<>();
    }

    public List<Date> getDates() {
        return dates;
    }

    public DayTime getDayTime (Date date, int i) {
        int value = 4 + i - this.size(date);
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
        for(Map.Entry<Date, Map<DayTime, Meteo>> entry : map.entrySet()) {
            for(Map.Entry<DayTime, Meteo> meteos : entry.getValue().entrySet()) {
                meteos.getValue().save(entry.getKey(), meteos.getKey());
            }
        }
        this.close();
    }

    public void delete () {
        this.open();
        for(Map.Entry<Date, Map<DayTime, Meteo>> entry : map.entrySet()) {
            for(Map.Entry<DayTime, Meteo> meteos : entry.getValue().entrySet()) {
                meteos.getValue().delete(entry.getKey(), meteos.getKey());
            }
        }
        this.close();
    }

    public void load () {
        if (exist()) {
            this.open();
            Cursor cursor = database.query(DatabaseHandler.TABLE_METEO, allColumns,
                DatabaseHandler.TABLE_METEO_COLUMN_NAME + " = '" + this.name +"'" +
                " AND "+DatabaseHandler.TABLE_METEO_COLUMN_DAY+" IS NOT NULL", null, null, null, null);
            while (cursor.moveToNext()) {
                String dateString = cursor.getString(cursor.getColumnIndex(DatabaseHandler.TABLE_METEO_COLUMN_DAY));
                Date date = null;
                try {
                    date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateString);
                } catch (ParseException ex) {
                    continue;
                }
                if (!this.map.containsKey(date)) {
                    this.map.put(date, new HashMap<DayTime, Meteo>());
                    this.dates.add(date);
                }
                Meteo meteo = new Meteo(this.name, this.context);

                String temperature = cursor.getString(cursor.getColumnIndex(DatabaseHandler.TABLE_METEO_COLUMN_TEMPERATURE));
                String weather = cursor.getString(cursor.getColumnIndex(DatabaseHandler.TABLE_METEO_COLUMN_WEATHER));
                String humidity = cursor.getString(cursor.getColumnIndex(DatabaseHandler.TABLE_METEO_COLUMN_HUMIDITY));
                String pressure = cursor.getString(cursor.getColumnIndex(DatabaseHandler.TABLE_METEO_COLUMN_PRESSURE));
                String speed = cursor.getString(cursor.getColumnIndex(DatabaseHandler.TABLE_METEO_COLUMN_SPEED));
                String direction = cursor.getString(cursor.getColumnIndex(DatabaseHandler.TABLE_METEO_COLUMN_DIRECTION));
                String units = cursor.getString(cursor.getColumnIndex(DatabaseHandler.TABLE_METEO_COLUMN_UNIT));
                dateString = cursor.getString(cursor.getColumnIndex(DatabaseHandler.TABLE_METEO_COLUMN_DATE));
                Date dateLoad = null;
                try {
                    dateLoad = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateString);
                } catch (ParseException ex) {
                    continue;
                }
                String daytime = cursor.getString(cursor.getColumnIndex(DatabaseHandler.TABLE_METEO_COLUMN_DAYTIME));
                meteo.setDirection(direction);
                meteo.setUnits(units);
                meteo.setMin(temperature);
                meteo.setWeather(weather);
                meteo.setDate(dateLoad);
                meteo.setHumidity(humidity);
                meteo.setSpeed(speed);
                meteo.setTemperature(temperature);
                meteo.setMax(temperature);
                meteo.setPressure(pressure);
                DayTime dayTime = DayTime.valueOf(daytime);
                this.map.get(date).put(dayTime, meteo);
            }
            this.close();
        }
    }

    public boolean exist () {
        this.open();
        Cursor cursor = database.query(DatabaseHandler.TABLE_METEO, allColumns,
            DatabaseHandler.TABLE_METEO_COLUMN_NAME + " = '" + this.name +"'" +
            " AND "+DatabaseHandler.TABLE_METEO_COLUMN_DAY+" IS NOT NULL", null, null, null, null);
        boolean exist = cursor.getCount() > 0;
        cursor.close();
        this.close();
        return exist;
    }

    public boolean isValid (int minutes) {
        for(Map.Entry<Date, Map<DayTime, Meteo>> entry : map.entrySet()) {
            for(Map.Entry<DayTime, Meteo> meteos : entry.getValue().entrySet()) {
                return meteos.getValue().isValid(minutes);
            }
        }
        return false;
    }
}
