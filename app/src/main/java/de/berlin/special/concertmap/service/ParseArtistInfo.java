package de.berlin.special.concertmap.service;

import android.content.ContentValues;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import de.berlin.special.concertmap.util.Utility;
import de.berlin.special.concertmap.data.EventContract.FavArtistEntry;

/**
 * Created by Saeed on 19-Nov-15.
 */
public class ParseArtistInfo {

    private final String LOG_TAG = ParseArtistInfo.class.getSimpleName();
    private String artistJsonStr;

    public ParseArtistInfo(String json) {
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

            JSONObject artistObj = new JSONObject(artistJsonStr);

            String artName;
            int artThrillID;
            String artOfficialURL;
            String artImageLarge;
            String artImageMobile;

            artThrillID = artistObj.getInt(ART_THRILL_ID);
            artName = artistObj.getString(ART_NAME);
            artOfficialURL = artistObj.getString(ART_OFFICIAL_URL);
            JSONObject photosJSONObject = artistObj.getJSONObject(ART_IMAGE_JSON_KEY);
            artImageLarge = photosJSONObject.getString(ART_IMAGE_LARGE);
            artImageMobile = photosJSONObject.getString(ART_IMAGE_MOBILE);

            ContentValues artistValues = new ContentValues();
            // Create a new map of values for artist, where column names are the keys
            artistValues.put(FavArtistEntry.COL_FAV_ART_THRILL_ID, artThrillID);
            artistValues.put(FavArtistEntry.COL_FAV_ART_NAME, artName);
            artistValues.put(FavArtistEntry.COL_FAV_ART_OFFICIAL_URL, artOfficialURL);
            artistValues.put(FavArtistEntry.COL_FAV_ART_IMAGE_LARGE, artImageLarge);
            artistValues.put(FavArtistEntry.COL_FAV_ART_IMAGE_MOBILE, artImageMobile);
            artistValues.put(FavArtistEntry.COL_FAV_ART_TRACKED, Utility.ARTIST_TRACKED_NO);
            // Insert the new venue row
            long newRowIdArtist;
            newRowIdArtist = Utility.db.insert(
                    FavArtistEntry.TABLE_NAME,
                    null,
                    artistValues);

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }
}

