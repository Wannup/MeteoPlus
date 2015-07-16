package com.example.erwan.meteoplus;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by mike on 04/02/2015.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    public static final String TABLE_METEO = "meteo";
    public static final String TABLE_METEO_COLUMN_NAME = "name";
    public static final String TABLE_METEO_COLUMN_TEMPERATURE = "temperature";
    public static final String TABLE_METEO_COLUMN_WEATHER = "weather";
    public static final String TABLE_METEO_COLUMN_HUMIDITY = "humidity";
    public static final String TABLE_METEO_COLUMN_PRESSURE = "pressure";
    public static final String TABLE_METEO_COLUMN_SPEED = "speed";
    public static final String TABLE_METEO_COLUMN_DIRECTION = "direction";
    public static final String TABLE_METEO_COLUMN_UNIT = "unit";
    public static final String TABLE_METEO_COLUMN_DAY = "day";
    public static final String TABLE_METEO_COLUMN_DAYTIME = "daytime";
    public static final String TABLE_METEO_COLUMN_DATE = "date";

    private static final String DATABASE_NAME = "meteoplus.db";
    private static final int DATABASE_VERSION = 4;

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String tableMeteo = "CREATE TABLE "+ TABLE_METEO +" (" +
                TABLE_METEO_COLUMN_NAME + " TEXT NOT NULL," +
                TABLE_METEO_COLUMN_TEMPERATURE + " TEXT NOT NULL," +
                TABLE_METEO_COLUMN_WEATHER + " TEXT NOT NULL," +
                TABLE_METEO_COLUMN_HUMIDITY + " TEXT NOT NULL," +
                TABLE_METEO_COLUMN_PRESSURE + " TEXT NOT NULL," +
                TABLE_METEO_COLUMN_SPEED + " TEXT NOT NULL," +
                TABLE_METEO_COLUMN_DIRECTION + " TEXT NOT NULL," +
                TABLE_METEO_COLUMN_UNIT + " TEXT NOT NULL," +
                TABLE_METEO_COLUMN_DAY + " TEXT," +
                TABLE_METEO_COLUMN_DAYTIME + " TEXT," +
                TABLE_METEO_COLUMN_DATE + " TEXT NOT NULL" +
                ");";
        db.execSQL(tableMeteo);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String drop_tables = "DROP TABLE IF EXISTS "+ TABLE_METEO +";";
        db.execSQL(drop_tables);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String drop_tables = "DROP TABLE IF EXISTS "+ TABLE_METEO +";";
        db.execSQL(drop_tables);
        onCreate(db);
    }
}
