package de.berlin.special.concertmap.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Hashtable;

import de.berlin.special.concertmap.Utility;
import de.berlin.special.concertmap.data.EventContract.ArtistEntry;
import de.berlin.special.concertmap.data.EventContract.EventEntry;
import de.berlin.special.concertmap.data.EventContract.VenueEntry;
import de.berlin.special.concertmap.data.EventContract.FavArtistEntry;
import de.berlin.special.concertmap.data.EventDbHelper;

/**
 * Created by Saeed on 27-Jul-15.
 */
public class ParseJSONtoDatabase {

    private final String LOG_TAG = ParseJSONtoDatabase.class.getSimpleName();
    private EventDbHelper mDbHelper;
    private String concertJsonStr;

    public ParseJSONtoDatabase(Context mContext, String json){
        // Deleting the concert.db databases
        // mContext.deleteDatabase(EventDbHelper.DATABASE_NAME);

        // If Database exist?
        File dbFile = mContext.getDatabasePath(EventDbHelper.DATABASE_NAME);
        if(dbFile.exists())
            Utility.db = mContext.openOrCreateDatabase(EventDbHelper.DATABASE_NAME, Context.MODE_PRIVATE, null);
        else {
            // Creating the concert.db databases
            mDbHelper = new EventDbHelper(mContext);
            // Gets the data repository in write mode
            Utility.db = mDbHelper.getWritableDatabase();
        }
        // Deleting old records from Database
        deleteOldDataFromDatabase(Utility.db);
        concertJsonStr = json;
    }

    public void deleteOldDataFromDatabase(SQLiteDatabase db){

        db.execSQL("DELETE FROM " + EventEntry.TABLE_NAME
                + " WHERE " + EventEntry.COLUMN_CON_ATTEND + " = " + Utility.EVENT_ATTEND_NO + ";");

        db.execSQL("DELETE FROM " + FavArtistEntry.TABLE_NAME
                + " WHERE " + FavArtistEntry.COL_FAV_ART_TRACKED + " = " + Utility.ARTIST_TRACKED_NO + ";");

        db.execSQL("DELETE FROM " + VenueEntry.TABLE_NAME
                + " WHERE " + VenueEntry.COLUMN_VEN_CON_ID + " NOT IN "
                + "( SELECT " + EventEntry._ID + " FROM " + EventEntry.TABLE_NAME + " );");

        db.execSQL("DELETE FROM " + ArtistEntry.TABLE_NAME
                + " WHERE " + ArtistEntry.COLUMN_ART_CON_ID + " NOT IN "
                + "( SELECT " + EventEntry._ID + " FROM " + EventEntry.TABLE_NAME + " );");
    }

    public void parseData() {

        // These are the names of the JSON objects that need to be extracted.
        final String CON_Thrill_ID = "id";
        final String CON_NAME = "name";
        final String CON_START_AT = "starts_at";
        final String CON_IMAGE_JSON_KEY = "photos";
        final String CON_IMAGE = "mobile";


        final String ART_JSON_KEY = "artists";
        final String ART_Thrill_ID = "id";
        final String ART_NAME = "name";

        final String VEN_JSON_KEY = "venue";
        final String VEN_Thrill_ID = "id";
        final String VEN_NAME = "name";
        final String VEN_STREET = "address1";
        final String VEN_CITY = "city";
        final String VEN_GEO_LAT = "latitude";
        final String VEN_GEO_LONG = "longitude";
        final String VEN_WEB = "official_url";

        try {

            JSONArray eventArray = new JSONArray(concertJsonStr);

            for(int i = 0; i < eventArray.length(); i++) {

                int conThrillID;
                String conName;
                String conStartAt;
                String conImage;

                Hashtable<Integer, String> artList = new Hashtable<Integer, String>();

                int venThrillID;
                String venName;
                String venStreet;
                String venCity;
                double venGeoLat;
                double venGeoLong;
                String venWeb;

                // Get the JSON object representing the event
                JSONObject event = eventArray.getJSONObject(i);

                conThrillID = event.getInt(CON_Thrill_ID);
                conName = event.getString(CON_NAME);
                conStartAt = event.getString(CON_START_AT);
                JSONObject photosJSONObject = event.getJSONObject(CON_IMAGE_JSON_KEY);
                conImage = photosJSONObject.getString(CON_IMAGE);

                JSONArray artistsJSONArray = event.getJSONArray(ART_JSON_KEY);
                for (int j = 0; j < artistsJSONArray.length(); j++) {
                    JSONObject artistObject = artistsJSONArray.getJSONObject(j);
                    artList.put(artistObject.getInt(ART_Thrill_ID), artistObject.getString(ART_NAME));
                }

                JSONObject venueJSON = event.getJSONObject(VEN_JSON_KEY);
                venThrillID = venueJSON.getInt(VEN_Thrill_ID);
                venName = venueJSON.getString(VEN_NAME);
                venStreet = venueJSON.getString(VEN_STREET);
                venCity = venueJSON.getString(VEN_CITY);
                venGeoLat = venueJSON.getDouble(VEN_GEO_LAT);
                venGeoLong = venueJSON.getDouble(VEN_GEO_LONG);
                venWeb = venueJSON.getString(VEN_WEB);

                ContentValues eventValues = new ContentValues();
                // Create a new map of values for event, where column names are the keys
                eventValues.put(EventEntry.COLUMN_CON_THRILL_ID, conThrillID);
                eventValues.put(EventEntry.COLUMN_CON_NAME, conName);
                eventValues.put(EventEntry.COLUMN_CON_START_AT, conStartAt);
                eventValues.put(EventEntry.COLUMN_CON_IMAGE, conImage);
                eventValues.put(EventEntry.COLUMN_CON_ATTEND, Utility.EVENT_ATTEND_NO);

                // Insert the new event row
                long newRowIdEvent;
                newRowIdEvent = Utility.db.insert(
                        EventEntry.TABLE_NAME,
                        null,
                        eventValues);
                Log.d(LOG_TAG, "Event id"+String.valueOf(newRowIdEvent));

                ContentValues locationValues = new ContentValues();
                // Create a new map of values for location, where column names are the keys
                locationValues.put(VenueEntry.COLUMN_VEN_CON_ID, newRowIdEvent);
                locationValues.put(VenueEntry.COLUMN_VEN_THRILL_ID, venThrillID);
                locationValues.put(VenueEntry.COLUMN_VEN_NAME, venName);
                locationValues.put(VenueEntry.COLUMN_VEN_STREET, venStreet);
                locationValues.put(VenueEntry.COLUMN_VEN_CITY, venCity);
                locationValues.put(VenueEntry.COLUMN_VEN_GEO_LAT, venGeoLat);
                locationValues.put(VenueEntry.COLUMN_VEN_GEO_LONG, venGeoLong);
                locationValues.put(VenueEntry.COLUMN_VEN_WEB, venWeb);

                // Insert the new venue row
                long newRowIdVenue;
                newRowIdVenue = Utility.db.insert(
                        VenueEntry.TABLE_NAME,
                        null,
                        locationValues);
                Log.d(LOG_TAG, "Location id"+String.valueOf(newRowIdVenue));

                for (int key : artList.keySet()) {
                    ContentValues artistValues = new ContentValues();
                    // Create a new map of values for artist, where column names are the keys
                    artistValues.put(ArtistEntry.COLUMN_ART_CON_ID, newRowIdEvent);
                    artistValues.put(ArtistEntry.COLUMN_ART_THRILL_ID, key);
                    artistValues.put(ArtistEntry.COLUMN_ART_NAME, artList.get(key));
                    // Insert the new venue row
                    long newRowIdArtist;
                    newRowIdArtist = Utility.db.insert(
                            ArtistEntry.TABLE_NAME,
                            null,
                            artistValues);
                    Log.d(LOG_TAG, "Artist id" + String.valueOf(newRowIdArtist));
                }

                artList.clear();
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }
}
