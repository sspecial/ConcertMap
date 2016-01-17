package de.berlin.special.concertmap.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import de.berlin.special.concertmap.data.EventDbHelper;
import de.berlin.special.concertmap.util.Utility;
import de.berlin.special.concertmap.data.EventContract.FavArtistEntry;

/**
 * Created by Saeed on 19-Nov-15.
 */
public class ParseArtistInfo {

    private final String LOG_TAG = ParseArtistInfo.class.getSimpleName();
    private SQLiteDatabase liteDatabase;
    private String artistJsonStr;

    public ParseArtistInfo(Context mContext, String json) {
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

            JSONObject artistObj = new JSONObject(artistJsonStr);

            String artName;
            int artThrillID;
            String artOfficialURL;
            String artWikipediaURL;
            String artThrillURL;
            String artImageMobile;

            artThrillID = artistObj.getInt(ART_THRILL_ID);
            artName = artistObj.getString(ART_NAME);
            artOfficialURL = artistObj.getString(ART_OFFICIAL_URL);
            artWikipediaURL = artistObj.getString(ART_WIKIPEDIA_URL);
            artThrillURL = artistObj.getString(ART_THRILL_URL);
            JSONObject photosJSONObject = artistObj.getJSONObject(ART_IMAGE_JSON_KEY);
            artImageMobile = photosJSONObject.getString(ART_IMAGE_MOBILE);

            ContentValues artistValues = new ContentValues();
            // Create a new map of values for artist, where column names are the keys
            artistValues.put(FavArtistEntry.COL_FAV_ART_THRILL_ID, artThrillID);
            artistValues.put(FavArtistEntry.COL_FAV_ART_NAME, artName);
            artistValues.put(FavArtistEntry.COL_FAV_ART_OFFICIAL_URL, artOfficialURL);
            artistValues.put(FavArtistEntry.COL_FAV_ART_WIKIPEDIA_URL, artWikipediaURL);
            artistValues.put(FavArtistEntry.COL_FAV_ART_THRILL_URL, artThrillURL);
            artistValues.put(FavArtistEntry.COL_FAV_ART_IMAGE_MOBILE, artImageMobile);
            artistValues.put(FavArtistEntry.COL_FAV_ART_TRACKED, Utility.ARTIST_TRACKED_NO);
            // Insert the new venue row
            long newRowIdArtist;
            newRowIdArtist = liteDatabase.insert(
                    FavArtistEntry.TABLE_NAME,
                    null,
                    artistValues);

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }
}

