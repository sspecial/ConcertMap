package de.berlin.special.concertmap.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.berlin.special.concertmap.data.EventContract;
import de.berlin.special.concertmap.data.EventDbHelper;

/**
 * Created by Saeed on 27-Jul-15.
 */
public class ParseJSONtoDatabase {

    private final String LOG_TAG = ParseJSONtoDatabase.class.getSimpleName();
    private EventDbHelper mDbHelper;
    private SQLiteDatabase db;
    private String concertJsonStr;

    public ParseJSONtoDatabase(Context mContext, String json){
        // Creating event and location databases
        mDbHelper = new EventDbHelper(mContext);
        // Gets the data repository in write mode
        db = mDbHelper.getWritableDatabase();
        concertJsonStr = json;
    }

    public void parseData() {

        // These are the names of the JSON objects that need to be extracted.
        final String CON_LIST = "events";
        final String CON_EVENT = "event";
        final String CON_TITLE = "title";
        final String CON_ARTISTS = "artists";
        final String CON_ARTIST = "artist";
        final String CON_ARTIST_WEB = "website";
        final String CON_IMAGE = "image";
        final String CON_START_DATE = "startDate";
        final String CON_DESCRIPTION = "description";
        final String CON_LOC_VENUE = "venue";
        final String CON_LOC_NAME = "name";
        final String CON_LOC_LOCATION = "location";
        final String CON_LOC_CITY = "city";
        final String CON_LOC_COUNTRY = "country";
        final String CON_LOC_STREET = "street";
        final String CON_LOC_POSTAL_CODE = "postalcode";
        final String CON_LOC_GEO_POINT = "geo:point";
        final String CON_LOC_GEO_LAT = "geo:lat";
        final String CON_LOC_GEO_LONG = "geo:long";
        final String CON_LOC_WEB = "website";

        try {
            JSONObject concertJson = new JSONObject(concertJsonStr);
            JSONObject eventsJSON = concertJson.getJSONObject(CON_LIST);
            JSONArray eventArray = eventsJSON.getJSONArray(CON_EVENT);

            for(int i = 0; i < eventArray.length(); i++) {

                String title;
                ArrayList<String> artistsArray = new ArrayList<String>();
                String locName;
                String locWeb;
                String locCity;
                String locCountry;
                String locStreet;
                String locPostalCode;
                double locGeoLatitude;
                double locGeoLongitude;
                String image;
                String startDate;
                String artistWeb;
                String description;

                // Get the JSON object representing the event
                JSONObject event = eventArray.getJSONObject(i);

                title = event.getString(CON_TITLE);
                image = event.getString(CON_IMAGE);
                startDate = event.getString(CON_START_DATE);
                artistWeb = event.getString(CON_ARTIST_WEB);
                description = event.getString(CON_DESCRIPTION);

                JSONObject artistsJSON = event.getJSONObject(CON_ARTISTS);
                Object item = artistsJSON.get(CON_ARTIST);
                if (item instanceof JSONArray) {
                    JSONArray artistsJSONArray = (JSONArray) item;
                    for (int j = 0; j < artistsJSONArray.length(); j++) {
                        String artist = artistsJSONArray.getString(j);
                        Log.d(LOG_TAG, artist);
                        artistsArray.add(artist);
                    }
                } else {
                    String artist = artistsJSON.getString(CON_ARTIST);
                    Log.d(LOG_TAG, artist);
                    artistsArray.add(artist);
                }

                JSONObject venueJSON = event.getJSONObject(CON_LOC_VENUE);
                locName = venueJSON.getString(CON_LOC_NAME);
                locWeb = venueJSON.getString(CON_LOC_WEB);
                JSONObject locJSON = venueJSON.getJSONObject(CON_LOC_LOCATION);
                locCity = locJSON.getString(CON_LOC_CITY);
                locCountry = locJSON.getString(CON_LOC_COUNTRY);
                locStreet = locJSON.getString(CON_LOC_STREET);
                locPostalCode = locJSON.getString(CON_LOC_POSTAL_CODE);
                JSONObject geoJSON = locJSON.getJSONObject(CON_LOC_GEO_POINT);
                locGeoLatitude = geoJSON.getDouble(CON_LOC_GEO_LAT);
                locGeoLongitude = geoJSON.getDouble(CON_LOC_GEO_LONG);

                ContentValues eventValues = new ContentValues();
                ContentValues locationValues = new ContentValues();

                // Create a new map of values for location, where column names are the keys
                locationValues.put(EventContract.LocationEntry.COLUMN_LOC_NAME, locName);
                locationValues.put(EventContract.LocationEntry.COLUMN_LOC_CITY, locCity);
                locationValues.put(EventContract.LocationEntry.COLUMN_LOC_COUNTRY, locCountry);
                locationValues.put(EventContract.LocationEntry.COLUMN_LOC_STREET, locStreet);
                locationValues.put(EventContract.LocationEntry.COLUMN_LOC_POSTAL_CODE, locPostalCode);
                locationValues.put(EventContract.LocationEntry.COLUMN_LOC_WEB, locWeb);
                locationValues.put(EventContract.LocationEntry.COLUMN_LOC_GEO_LAT, locGeoLatitude);
                locationValues.put(EventContract.LocationEntry.COLUMN_LOC_GEO_LONG, locGeoLongitude);

                // Insert the new row, returning the primary key value of the new row
                long newRowId;
                newRowId = db.insert(
                        EventContract.LocationEntry.TABLE_NAME,
                        null,
                        locationValues);
                Log.d(LOG_TAG, "Location id"+String.valueOf(newRowId));

                // Create a new map of values for event, where column names are the keys
                eventValues.put(EventContract.EventEntry.COLUMN_LOC_KEY, newRowId);
                eventValues.put(EventContract.EventEntry.COLUMN_EVENT_TITLE, title);
                eventValues.put(EventContract.EventEntry.COLUMN_EVENT_ARTIST, artistsArray.get(0));
                eventValues.put(EventContract.EventEntry.COLUMN_EVENT_ARTIST_WEB, artistWeb);
                eventValues.put(EventContract.EventEntry.COLUMN_EVENT_IMAGE, image);
                eventValues.put(EventContract.EventEntry.COLUMN_EVENT_DESC, description);
                eventValues.put(EventContract.EventEntry.COLUMN_START_DATE, startDate);

                long newRowIdEvent;
                newRowIdEvent = db.insert(
                        EventContract.EventEntry.TABLE_NAME,
                        null,
                        eventValues);
                Log.d(LOG_TAG, "Event id"+String.valueOf(newRowIdEvent));

                artistsArray.clear();
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }
}
