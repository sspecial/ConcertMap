package de.berlin.special.concertmap.service;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;

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
        final String CON_ID = "id";
        final String CON_NAME = "name";
        final String CON_START_AT = "starts_at";
        final String CON_IMAGE_JSON_KEY = "photos";
        final String CON_IMAGE = "mobile";


        final String ART_JSON_KEY = "artists";
        final String ART_ID = "id";
        final String ART_NAME = "name";

        final String VEN_JSON_KEY = "venue";
        final String VEN_ID = "id";
        final String VEN_NAME = "name";
        final String VEN_STREET = "address1";
        final String VEN_CITY = "city";
        final String VEN_GEO_LAT = "latitude";
        final String VEN_GEO_LONG = "longitude";
        final String VEN_WEB = "official_url";

        try {

            JSONArray eventArray = new JSONArray(concertJsonStr);

            for(int i = 0; i < eventArray.length(); i++) {

                String conID;
                String conName;
                String conStartAt;
                String conImage;

                Hashtable artList = new Hashtable();

                String venID;
                String venName;
                String venStreet;
                String venCity;
                double venGeoLat;
                double venGeoLong;
                String venWeb;

                // Get the JSON object representing the event
                JSONObject event = eventArray.getJSONObject(i);

                conID = event.getString(CON_ID);
                conName = event.getString(CON_NAME);
                conStartAt = event.getString(CON_START_AT);
                JSONObject photosJSONObject = event.getJSONObject(CON_IMAGE_JSON_KEY);
                conImage = photosJSONObject.getString(CON_IMAGE);

                JSONArray artistsJSONArray = event.getJSONArray(ART_JSON_KEY);
                for (int j = 0; j < artistsJSONArray.length(); j++) {
                    JSONObject artistObject = eventArray.getJSONObject(j);
                    artList.put(artistObject.getString(ART_NAME), artistObject.getString(ART_ID));
                }

                JSONObject venueJSON = event.getJSONObject(VEN_JSON_KEY);
                venID = venueJSON.getString(VEN_ID);
                venName = venueJSON.getString(VEN_NAME);
                venStreet = venueJSON.getString(VEN_STREET);
                venCity = venueJSON.getString(VEN_CITY);
                venGeoLat = venueJSON.getDouble(VEN_GEO_LAT);
                venGeoLong = venueJSON.getDouble(VEN_GEO_LONG);
                venWeb = venueJSON.getString(VEN_WEB);
                /*
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
                eventValues.put(EventContract.EventEntry.COLUMN_EVENT_START_DATE, startDate);

                long newRowIdEvent;
                newRowIdEvent = db.insert(
                        EventContract.EventEntry.TABLE_NAME,
                        null,
                        eventValues);
                Log.d(LOG_TAG, "Event id"+String.valueOf(newRowIdEvent));
                */

                artList.clear();
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }
}
