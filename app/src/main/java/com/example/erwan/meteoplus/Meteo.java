package com.example.erwan.meteoplus;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by mike on 04/02/2015.
 */
public class Meteo implements Serializable {

    private final String name;
    private String temperature;
    private String min;
    private String max;
    private String units;
    private String weather;
    private String humidity;
    private String pressure;
    private String speed;
    private String direction;
    private Date date;

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
        DatabaseHandler.TABLE_METEO_COLUMN_DAY,
        DatabaseHandler.TABLE_METEO_COLUMN_DAYTIME,
        DatabaseHandler.TABLE_METEO_COLUMN_DATE
    };

    public Meteo(String name, Context context) {
        this.name = name;
        this.databaseHandler = new DatabaseHandler(context);
    }

    public String getName () {
        return this.name;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getDate() {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(this.date);
    }

    public void setDate(Date date) {
        this.date = date;
    }

    private void open () {
        this.database = this.databaseHandler.getWritableDatabase();
    }

    private void close () {
        this.databaseHandler.close();
    }

    public void save () {
        this.save(null, null);
    }

    public void save (Date day, DayTime dayTime) {
        this.open();
        this.date = new Date();
        ContentValues values = new ContentValues();
        values.put(DatabaseHandler.TABLE_METEO_COLUMN_NAME, this.name);
        values.put(DatabaseHandler.TABLE_METEO_COLUMN_TEMPERATURE, this.temperature);
        values.put(DatabaseHandler.TABLE_METEO_COLUMN_WEATHER, this.weather);
        values.put(DatabaseHandler.TABLE_METEO_COLUMN_HUMIDITY, this.humidity);
        values.put(DatabaseHandler.TABLE_METEO_COLUMN_PRESSURE, this.pressure);
        values.put(DatabaseHandler.TABLE_METEO_COLUMN_SPEED, this.speed);
        values.put(DatabaseHandler.TABLE_METEO_COLUMN_DIRECTION, this.direction);
        values.put(DatabaseHandler.TABLE_METEO_COLUMN_UNIT, this.units);
        if (day == null) {
            values.putNull(DatabaseHandler.TABLE_METEO_COLUMN_DAY);
        } else {
            values.put(DatabaseHandler.TABLE_METEO_COLUMN_DAY, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(day));
        }
        if (dayTime == null) {
            values.putNull(DatabaseHandler.TABLE_METEO_COLUMN_DAYTIME);
        } else {
            values.put(DatabaseHandler.TABLE_METEO_COLUMN_DAYTIME, dayTime.name());
        }
        values.put(DatabaseHandler.TABLE_METEO_COLUMN_DATE, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(this.date));
        this.database.insert(DatabaseHandler.TABLE_METEO, null, values);
        this.close();
    }

    public void delete () {
        this.open();
        this.database.delete(DatabaseHandler.TABLE_METEO,
            DatabaseHandler.TABLE_METEO_COLUMN_NAME + " = '" + this.name + "'" +
            " AND "+DatabaseHandler.TABLE_METEO_COLUMN_DAY + " IS NULL", null);
        this.close();
    }

    public void load () {
        if (exist()) {
            this.open();
            Cursor cursor = database.query(DatabaseHandler.TABLE_METEO, allColumns,
                    DatabaseHandler.TABLE_METEO_COLUMN_NAME + " = '" + this.name +"'" +
                    " AND "+DatabaseHandler.TABLE_METEO_COLUMN_DAY+" IS NULL", null, null, null, null);
            cursor.moveToFirst();
            this.temperature = cursor.getString(cursor.getColumnIndex(DatabaseHandler.TABLE_METEO_COLUMN_TEMPERATURE));
            this.weather = cursor.getString(cursor.getColumnIndex(DatabaseHandler.TABLE_METEO_COLUMN_WEATHER));
            this.humidity = cursor.getString(cursor.getColumnIndex(DatabaseHandler.TABLE_METEO_COLUMN_HUMIDITY));
            this.pressure = cursor.getString(cursor.getColumnIndex(DatabaseHandler.TABLE_METEO_COLUMN_PRESSURE));
            this.speed = cursor.getString(cursor.getColumnIndex(DatabaseHandler.TABLE_METEO_COLUMN_SPEED));
            this.direction = cursor.getString(cursor.getColumnIndex(DatabaseHandler.TABLE_METEO_COLUMN_DIRECTION));
            this.units = cursor.getString(cursor.getColumnIndex(DatabaseHandler.TABLE_METEO_COLUMN_UNIT));
            String dateString = cursor.getString(cursor.getColumnIndex(DatabaseHandler.TABLE_METEO_COLUMN_DATE));
            this.date = null;
            try {
                this.date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateString);
            } catch (ParseException ex) {
                Logger.getLogger(Meteo.class.getName()).log(Level.SEVERE, null, ex);
            }
            cursor.close();
            this.close();
        }
    }

    public boolean exist () {
        this.open();
        Cursor cursor = database.query(DatabaseHandler.TABLE_METEO, allColumns,
            DatabaseHandler.TABLE_METEO_COLUMN_NAME + " = '" + this.name +"'" +
            " AND "+DatabaseHandler.TABLE_METEO_COLUMN_DAY+" IS NULL", null, null, null, null);
        boolean exist = cursor.getCount() == 1;
        cursor.close();
        this.close();
        return exist;
    }

    public boolean isValid (int minutes) {
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(this.date);
        calendar.add(Calendar.HOUR_OF_DAY, 1);
        Date datePlus1h = calendar.getTime();
        return datePlus1h.after(now);
    }

}
