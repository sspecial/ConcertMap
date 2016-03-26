package de.berlin.special.concertmap.service.parseJSON;

/**
 * Created by Saeed on 28-Nov-15.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;

import de.berlin.special.concertmap.data.EventContract;
import de.berlin.special.concertmap.data.EventDbHelper;
import de.berlin.special.concertmap.util.Utility;

public class ParseArtistSearchInfo {

    private final String LOG_TAG = ParseArtistInfo.class.getSimpleName();
    private SQLiteDatabase liteDatabase;
    private String artistJsonStr;
    Hashtable<String, Integer> artList = new Hashtable<String, Integer>();

    public ParseArtistSearchInfo(Context mContext, String json) {
        artistJsonStr = json;
        liteDatabase = mContext.openOrCreateDatabase(EventDbHelper.DATABASE_NAME, Context.MODE_PRIVATE, null);
    }

    public void parseArtistData() {

        final String ART_NAME = "name";
        final String ART_THRILL_ID = "id";
        final String ART_OFFICIAL_URL = "official_url";
        final String ART_WIKIPEDIA_URL = "wikipedia_url";
        final String ART_THRILL_URL = "url";
        final String ART_IMAGE_JSON_KEY = "photos";
        final String ART_IMAGE_MOBILE = "mobile";

        try {

            JSONArray artistArray = new JSONArray(artistJsonStr);

            int length = artistArray.length();
            int artThrillID;
            String artName;
            String artOfficialURL;
            String artWikipediaURL;
            String artThrillURL;
            String artImageMobile;

            for (int i = 0; i < artistArray.length(); i++) {

                JSONObject artistObj = artistArray.getJSONObject(i);

                artThrillID = artistObj.getInt(ART_THRILL_ID);
                artName = artistObj.getString(ART_NAME);
                artOfficialURL = artistObj.getString(ART_OFFICIAL_URL);
                artWikipediaURL = artistObj.getString(ART_WIKIPEDIA_URL);
                artThrillURL = artistObj.getString(ART_THRILL_URL);
                JSONObject photosJSONObject = artistObj.getJSONObject(ART_IMAGE_JSON_KEY);
                artImageMobile = photosJSONObject.getString(ART_IMAGE_MOBILE);

                ContentValues artistValues = new ContentValues();
                // Create a new map of values for artist, where column names are the keys
                artistValues.put(EventContract.FavArtistEntry.COL_FAV_ART_THRILL_ID, artThrillID);
                artistValues.put(EventContract.FavArtistEntry.COL_FAV_ART_NAME, artName);
                artistValues.put(EventContract.FavArtistEntry.COL_FAV_ART_OFFICIAL_URL, artOfficialURL);
                artistValues.put(EventContract.FavArtistEntry.COL_FAV_ART_WIKIPEDIA_URL, artWikipediaURL);
                artistValues.put(EventContract.FavArtistEntry.COL_FAV_ART_THRILL_URL, artThrillURL);
                artistValues.put(EventContract.FavArtistEntry.COL_FAV_ART_IMAGE_MOBILE, artImageMobile);
                artistValues.put(EventContract.FavArtistEntry.COL_FAV_ART_TRACKED, Utility.ARTIST_TRACKED_NO);
                // Insert the new venue row
                long newRowIdArtist;
                newRowIdArtist = liteDatabase.insert(
                        EventContract.FavArtistEntry.TABLE_NAME,
                        null,
                        artistValues);

                artList.put(artName, artThrillID);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();

        } finally {
            try {
                if (liteDatabase != null) {
                    liteDatabase.close();
                    liteDatabase = null;
                }
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
        }
    }

    public Hashtable<String, Integer> getArtistIDList() {
        return artList;
    }
}

