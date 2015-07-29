package de.berlin.special.concertmap.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import de.berlin.special.concertmap.data.EventContract.EventEntry;
import de.berlin.special.concertmap.data.EventContract.LocationEntry;

/**
 * Created by Saeed on 18-Apr-15.
 */
public class EventDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "concert.db";
    private static final int DATABASE_VERSION = 1;

    public EventDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create a table to hold locations.  A location consists of the string supplied in the
        // location setting, the city name, and the latitude and longitude
        final String SQL_CREATE_LOCATION_TABLE = "CREATE TABLE " + LocationEntry.TABLE_NAME + " (" +
                LocationEntry._ID + " INTEGER PRIMARY KEY," +
                LocationEntry.COLUMN_LOC_SETTING + " TEXT, " +
                LocationEntry.COLUMN_LOC_NAME + " TEXT, " +
                LocationEntry.COLUMN_LOC_STREET + " TEXT, " +
                LocationEntry.COLUMN_LOC_POSTAL_CODE + " INTEGER, " +
                LocationEntry.COLUMN_LOC_CITY + " TEXT, " +
                LocationEntry.COLUMN_LOC_COUNTRY + " TEXT, " +
                LocationEntry.COLUMN_LOC_WEB + " TEXT, " +
                LocationEntry.COLUMN_LOC_GEO_LAT+ " REAL NOT NULL, " +
                LocationEntry.COLUMN_LOC_GEO_LONG + " REAL NOT NULL " + " );";

        // Create a table to hold events.
        final String SQL_CREATE_EVENT_TABLE = "CREATE TABLE " + EventEntry.TABLE_NAME + " (" +

                EventEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                // the ID of the location entry associated with this event
                EventEntry.COLUMN_LOC_KEY + " INTEGER NOT NULL, " +
                EventEntry.COLUMN_EVENT_TITLE + " TEXT NOT NULL, " +
                EventEntry.COLUMN_EVENT_ARTIST + " TEXT NOT NULL, " +
                EventEntry.COLUMN_EVENT_ARTIST_WEB + " TEXT, " +
                EventEntry.COLUMN_EVENT_DESC + " TEXT, " +
                EventEntry.COLUMN_EVENT_IMAGE + " TEXT, " +
                EventEntry.COLUMN_EVENT_START_DATE + " TEXT, " +

                // Set up the location column as a foreign key to location table.
                " FOREIGN KEY (" + EventEntry.COLUMN_LOC_KEY + ") REFERENCES " +
                LocationEntry.TABLE_NAME + " (" + LocationEntry._ID + ") " +
                ");";

        db.execSQL(SQL_CREATE_LOCATION_TABLE);
        db.execSQL(SQL_CREATE_EVENT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
