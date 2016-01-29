package de.berlin.special.concertmap.service;

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
import de.berlin.special.concertmap.model.Artist;
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
        final String EVENTS_JSON_KEY = "events";

        final String CON_ID = "id";
        final String CON_NAME = "title";
        final String CON_START_AT = "datetime_local";
        final String CON_URL = "url";

        final String ART_JSON_KEY = "performers";
        final String ART_ID = "id";
        final String ART_NAME = "name";
        final String ART_IMAGE = "image";

        final String VEN_JSON_KEY = "venue";
        final String VEN_ID = "id";
        final String VEN_NAME = "name";
        final String VEN_STREET = "address";
        final String VEN_CITY = "city";
        final String VEN_COUNTRY = "country";
        final String VEN_LOCATION = "display_location";
        final String VEN_GEO_JSON_KEY = "location";
        final String VEN_GEO_LAT = "lat";
        final String VEN_GEO_LONG = "lon";

        try {

            JSONObject mainObj = new JSONObject(concertJsonStr);
            JSONArray eventArray = mainObj.getJSONArray(EVENTS_JSON_KEY);

            for(int i = 0; i < eventArray.length(); i++) {

                int conID;
                String conName;
                String conStartAt;
                String conTicket;

                Hashtable<Integer, Artist> artList = new Hashtable<Integer, Artist>();

                int venID;
                String venName;
                String venStreet;
                String venCity;
                String venCountry;
                String venLocation;
                double venGeoLat;
                double venGeoLong;

                // Get the JSON object representing the event
                JSONObject event = eventArray.getJSONObject(i);

                conID = event.getInt(CON_ID);
                conName = event.getString(CON_NAME);
                conStartAt = event.getString(CON_START_AT);
                conTicket = event.getString(CON_URL);

                JSONArray artistsJSONArray = event.getJSONArray(ART_JSON_KEY);
                for (int j = 0; j < artistsJSONArray.length(); j++) {
                    JSONObject artistObj = artistsJSONArray.getJSONObject(j);
                    int id = artistObj.getInt(ART_ID);
                    String name = artistObj.getString(ART_NAME);
                    String image = artistObj.getString(ART_IMAGE);
                    artList.put(j, new Artist(id, name, image));
                }

                JSONObject venueJSON = event.getJSONObject(VEN_JSON_KEY);
                venID = venueJSON.getInt(VEN_ID);
                venName = venueJSON.getString(VEN_NAME);
                venStreet = venueJSON.getString(VEN_STREET);
                venCity = venueJSON.getString(VEN_CITY);
                venCountry = venueJSON.getString(VEN_COUNTRY);
                venLocation = venueJSON.getString(VEN_LOCATION);
                JSONObject venueGeo = venueJSON.getJSONObject(VEN_GEO_JSON_KEY);
                venGeoLat = venueGeo.getDouble(VEN_GEO_LAT);
                venGeoLong = venueGeo.getDouble(VEN_GEO_LONG);

                ContentValues eventValues = new ContentValues();
                // Create a new map of values for event, where column names are the keys
                eventValues.put(EventEntry.COLUMN_CON_API_ID, conID);
                eventValues.put(EventEntry.COLUMN_CON_NAME, conName);
                eventValues.put(EventEntry.COLUMN_CON_START_AT, conStartAt);
                eventValues.put(EventEntry.COLUMN_CON_TICKET, conTicket);
                for (int key : artList.keySet()) {
                    if(!artList.get(key).getImage().equals("null")){
                        eventValues.put(EventEntry.COLUMN_CON_IMAGE, artList.get(key).getImage());
                        break;
                    }
                    eventValues.put(EventEntry.COLUMN_CON_IMAGE, "null");
                }
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
                locationValues.put(VenueEntry.COLUMN_VEN_API_ID, venID);
                locationValues.put(VenueEntry.COLUMN_VEN_NAME, venName);
                locationValues.put(VenueEntry.COLUMN_VEN_STREET, venStreet);
                locationValues.put(VenueEntry.COLUMN_VEN_CITY, venCity);
                locationValues.put(VenueEntry.COLUMN_VEN_COUNTRY, venCountry);
                locationValues.put(VenueEntry.COLUMN_VEN_LOCATION, venLocation);
                locationValues.put(VenueEntry.COLUMN_VEN_GEO_LAT, venGeoLat);
                locationValues.put(VenueEntry.COLUMN_VEN_GEO_LONG, venGeoLong);

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
                    artistValues.put(ArtistEntry.COLUMN_ART_API_ID, artList.get(key).getId());
                    artistValues.put(ArtistEntry.COLUMN_ART_NAME, artList.get(key).getName());
                    artistValues.put(ArtistEntry.COLUMN_ART_IMAGE, artList.get(key).getImage());
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
