package de.berlin.special.concertmap.service.parseJSON;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;

import de.berlin.special.concertmap.data.EventContract.ArtistEntry;
import de.berlin.special.concertmap.data.EventContract.EventEntry;
import de.berlin.special.concertmap.data.EventContract.VenueEntry;
import de.berlin.special.concertmap.data.EventDbHelper;
import de.berlin.special.concertmap.util.Utility;

/**
 * Created by Saeed on 23-Nov-15.
 */
public class ParseArtistEventsInfo {

    private final String LOG_TAG = ParseArtistEventsInfo.class.getSimpleName();
    private SQLiteDatabase liteDatabase;
    private String concertJsonStr;
    private int artistID;

    public ParseArtistEventsInfo(Context mContext, String json, int artistID){
        this.artistID = artistID;
        concertJsonStr = json;
        liteDatabase = mContext.openOrCreateDatabase(EventDbHelper.DATABASE_NAME, Context.MODE_PRIVATE, null);
    }

    public void parseData() {

        // These are the names of the JSON objects that need to be extracted.
        final String CON_Thrill_ID = "id";
        final String CON_NAME = "name";
        final String CON_START_AT = "starts_at_local";
        final String CON_THRILL_URL = "url";
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
        final String VEN_COUNTRY_CODE = "country_code";
        final String VEN_GEO_LAT = "latitude";
        final String VEN_GEO_LONG = "longitude";
        final String VEN_WEB = "official_url";

        try {

            JSONArray eventArray = new JSONArray(concertJsonStr);

            for(int i = 0; i < eventArray.length(); i++) {

                int conThrillID;
                String conName;
                String conStartAt;
                String conThrillURL;
                String conImage;

                Hashtable<Integer, String> artList = new Hashtable<Integer, String>();

                int venThrillID;
                String venName;
                String venStreet;
                String venCity;
                String venCountryCode;
                double venGeoLat;
                double venGeoLong;
                String venWeb;

                // Get the JSON object representing the event
                JSONObject event = eventArray.getJSONObject(i);

                conThrillID = event.getInt(CON_Thrill_ID);
                conName = event.getString(CON_NAME);
                conStartAt = event.getString(CON_START_AT);
                conThrillURL = event.getString(CON_THRILL_URL);
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
                venCountryCode = venueJSON.getString(VEN_COUNTRY_CODE);
                venGeoLat = venueJSON.getDouble(VEN_GEO_LAT);
                venGeoLong = venueJSON.getDouble(VEN_GEO_LONG);
                venWeb = venueJSON.getString(VEN_WEB);


                ContentValues eventValues = new ContentValues();
                // Create a new map of values for event, where column names are the keys
                eventValues.put(EventEntry.COLUMN_CON_THRILL_ID, conThrillID);
                eventValues.put(EventEntry.COLUMN_CON_NAME, conName);
                eventValues.put(EventEntry.COLUMN_CON_START_AT, conStartAt);
                eventValues.put(EventEntry.COLUMN_CON_THRILL_URL, conThrillURL);
                eventValues.put(EventEntry.COLUMN_CON_IMAGE, conImage);
                eventValues.put(EventEntry.COLUMN_CON_ATTEND, Utility.EVENT_ATTEND_NO);
                eventValues.put(EventEntry.COLUMN_CON_BELONG_TO_ARTIST, artistID);

                // Insert the new event row
                long newRowIdEvent;
                newRowIdEvent = liteDatabase.insert(
                        EventEntry.TABLE_NAME,
                        null,
                        eventValues);

                ContentValues locationValues = new ContentValues();
                // Create a new map of values for location, where column names are the keys
                locationValues.put(VenueEntry.COLUMN_VEN_CON_ID, newRowIdEvent);
                locationValues.put(VenueEntry.COLUMN_VEN_THRILL_ID, venThrillID);
                locationValues.put(VenueEntry.COLUMN_VEN_NAME, venName);
                locationValues.put(VenueEntry.COLUMN_VEN_STREET, venStreet);
                locationValues.put(VenueEntry.COLUMN_VEN_CITY, venCity);
                locationValues.put(VenueEntry.COLUMN_VEN_COUNTRY_CODE, venCountryCode);
                locationValues.put(VenueEntry.COLUMN_VEN_GEO_LAT, venGeoLat);
                locationValues.put(VenueEntry.COLUMN_VEN_GEO_LONG, venGeoLong);
                locationValues.put(VenueEntry.COLUMN_VEN_WEB, venWeb);

                // Insert the new venue row
                long newRowIdVenue;
                newRowIdVenue = liteDatabase.insert(
                        VenueEntry.TABLE_NAME,
                        null,
                        locationValues);

                for (int key : artList.keySet()) {
                    ContentValues artistValues = new ContentValues();
                    // Create a new map of values for artist, where column names are the keys
                    artistValues.put(ArtistEntry.COLUMN_ART_CON_ID, newRowIdEvent);
                    artistValues.put(ArtistEntry.COLUMN_ART_THRILL_ID, key);
                    artistValues.put(ArtistEntry.COLUMN_ART_NAME, artList.get(key));
                    // Insert the new venue row
                    long newRowIdArtist;
                    newRowIdArtist = liteDatabase.insert(
                            ArtistEntry.TABLE_NAME,
                            null,
                            artistValues);
                }

                artList.clear();
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }
}
