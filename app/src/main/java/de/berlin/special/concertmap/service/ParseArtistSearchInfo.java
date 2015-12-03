package de.berlin.special.concertmap.service;

/**
 * Created by Saeed on 28-Nov-15.
 */

import android.content.ContentValues;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;

import de.berlin.special.concertmap.util.Utility;
import de.berlin.special.concertmap.data.EventContract;

public class ParseArtistSearchInfo {

    private final String LOG_TAG = ParseArtistInfo.class.getSimpleName();
    private String artistJsonStr;
    Hashtable<String, Integer> artList = new Hashtable<String, Integer>();

    public ParseArtistSearchInfo(String json) {
        artistJsonStr = json;
    }

    public void parseArtistData() {

        final String ART_NAME = "name";
        final String ART_THRILL_ID = "id";
        final String ART_OFFICIAL_URL = "official_url";
        final String ART_IMAGE_JSON_KEY = "photos";
        final String ART_IMAGE_LARGE = "large";
        final String ART_IMAGE_MOBILE = "mobile";

        try {

            JSONArray artistArray = new JSONArray(artistJsonStr);

            int length = artistArray.length();
            int artThrillID;
            String artName;
            String artOfficialURL;
            String artImageLarge;
            String artImageMobile;

            for (int i = 0; i < artistArray.length(); i++) {

                JSONObject artistObj = artistArray.getJSONObject(i);

                artThrillID = artistObj.getInt(ART_THRILL_ID);
                artName = artistObj.getString(ART_NAME);
                artOfficialURL = artistObj.getString(ART_OFFICIAL_URL);
                JSONObject photosJSONObject = artistObj.getJSONObject(ART_IMAGE_JSON_KEY);
                artImageLarge = photosJSONObject.getString(ART_IMAGE_LARGE);
                artImageMobile = photosJSONObject.getString(ART_IMAGE_MOBILE);

                ContentValues artistValues = new ContentValues();
                // Create a new map of values for artist, where column names are the keys
                artistValues.put(EventContract.FavArtistEntry.COL_FAV_ART_THRILL_ID, artThrillID);
                artistValues.put(EventContract.FavArtistEntry.COL_FAV_ART_NAME, artName);
                artistValues.put(EventContract.FavArtistEntry.COL_FAV_ART_OFFICIAL_URL, artOfficialURL);
                artistValues.put(EventContract.FavArtistEntry.COL_FAV_ART_IMAGE_LARGE, artImageLarge);
                artistValues.put(EventContract.FavArtistEntry.COL_FAV_ART_IMAGE_MOBILE, artImageMobile);
                artistValues.put(EventContract.FavArtistEntry.COL_FAV_ART_TRACKED, Utility.ARTIST_TRACKED_NO);
                // Insert the new venue row
                long newRowIdArtist;
                newRowIdArtist = Utility.db.insert(
                        EventContract.FavArtistEntry.TABLE_NAME,
                        null,
                        artistValues);
                Log.d(LOG_TAG, "Fav-Artist id" + String.valueOf(newRowIdArtist));

                artList.put(artName, artThrillID);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    public Hashtable<String, Integer> getArtistIDList() {
        return artList;
    }
}

