package de.berlin.special.concertmap.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import de.berlin.special.concertmap.data.EventContract.FavArtistEntry;
import de.berlin.special.concertmap.data.EventContract.ArtistEntry;
import de.berlin.special.concertmap.data.EventContract.EventEntry;
import de.berlin.special.concertmap.data.EventContract.VenueEntry;
import de.berlin.special.concertmap.data.EventContract.TicketEntry;

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

        // Create a table to hold events.
        final String SQL_CREATE_EVENT_TABLE = "CREATE TABLE " + EventEntry.TABLE_NAME + " (" +
                EventEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                // the ID of the location entry associated with this event
                EventEntry.COLUMN_CON_THRILL_ID + " INTEGER NOT NULL UNIQUE, " +
                EventEntry.COLUMN_CON_NAME + " TEXT NOT NULL, " +
                EventEntry.COLUMN_CON_START_AT + " TEXT NOT NULL, " +
                EventEntry.COLUMN_CON_IMAGE + " TEXT NOT NULL, " +
                EventEntry.COLUMN_CON_ATTEND + " INTEGER NOT NULL, " +
                EventEntry.COLUMN_CON_BELONG_TO_ARTIST + " INTEGER NOT NULL " + ");";

        // Create a table to hold locations.
        final String SQL_CREATE_VENUE_TABLE = "CREATE TABLE " + VenueEntry.TABLE_NAME + " (" +
                VenueEntry._ID + " INTEGER PRIMARY KEY," +
                VenueEntry.COLUMN_VEN_CON_ID + " INTEGER NOT NULL, " +
                VenueEntry.COLUMN_VEN_THRILL_ID + " INTEGER NOT NULL, " +
                VenueEntry.COLUMN_VEN_NAME + " TEXT NOT NULL, " +
                VenueEntry.COLUMN_VEN_STREET + " TEXT NOT NULL, " +
                VenueEntry.COLUMN_VEN_CITY + " TEXT NOT NULL, " +
                VenueEntry.COLUMN_VEN_GEO_LAT + " REAL NOT NULL, " +
                VenueEntry.COLUMN_VEN_GEO_LONG + " REAL NOT NULL, " +
                VenueEntry.COLUMN_VEN_WEB+ " REAL NOT NULL, " +
                " FOREIGN KEY (" + VenueEntry.COLUMN_VEN_CON_ID + ") REFERENCES " +
                EventEntry.TABLE_NAME + " (" + EventEntry._ID + ") " +
                ");";

        // Create a table to hold artists.
        final String SQL_CREATE_ARTISTS_TABLE = "CREATE TABLE " + ArtistEntry.TABLE_NAME + " (" +
                ArtistEntry._ID + " INTEGER PRIMARY KEY," +
                ArtistEntry.COLUMN_ART_CON_ID + " INTEGER NOT NULL, " +
                ArtistEntry.COLUMN_ART_THRILL_ID + " INTEGER NOT NULL, " +
                ArtistEntry.COLUMN_ART_NAME + " TEXT NOT NULL, " +
                " FOREIGN KEY (" + ArtistEntry.COLUMN_ART_CON_ID + ") REFERENCES " +
                EventEntry.TABLE_NAME + " (" + EventEntry._ID + ") " +
                ");";

        // Create a table to hold tickets.
        final String SQL_CREATE_TICKETS_TABLE = "CREATE TABLE " + TicketEntry.TABLE_NAME + " (" +
                TicketEntry._ID + " INTEGER PRIMARY KEY," +
                TicketEntry.COLUMN_TICKET_CON_ID + " INTEGER NOT NULL, " +
                TicketEntry.COLUMN_TICKET_NAME + " TEXT NOT NULL, " +
                TicketEntry.COLUMN_TICKET_URL + " TEXT NOT NULL, " +
                " FOREIGN KEY (" + TicketEntry.COLUMN_TICKET_CON_ID + ") REFERENCES " +
                EventEntry.TABLE_NAME + " (" + EventEntry._ID + ") " +
                ");";

        // Create a table to hold favorite artists.
        final String SQL_CREATE_FAV_ARTIST_TABLE = "CREATE TABLE " + FavArtistEntry.TABLE_NAME + " (" +
                FavArtistEntry._ID + " INTEGER PRIMARY KEY," +
                FavArtistEntry.COL_FAV_ART_THRILL_ID + " INTEGER NOT NULL UNIQUE, " +
                FavArtistEntry.COL_FAV_ART_NAME + " TEXT NOT NULL, " +
                FavArtistEntry.COL_FAV_ART_OFFICIAL_URL + " TEXT , " +
                FavArtistEntry.COL_FAV_ART_IMAGE_LARGE + " TEXT NOT NULL, " +
                FavArtistEntry.COL_FAV_ART_IMAGE_MOBILE + " TEXT NOT NULL, " +
                FavArtistEntry.COL_FAV_ART_TRACKED + " INTEGER NOT NULL " + ");";

        db.execSQL(SQL_CREATE_EVENT_TABLE);
        db.execSQL(SQL_CREATE_VENUE_TABLE);
        db.execSQL(SQL_CREATE_ARTISTS_TABLE);
        db.execSQL(SQL_CREATE_TICKETS_TABLE);
        db.execSQL(SQL_CREATE_FAV_ARTIST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
