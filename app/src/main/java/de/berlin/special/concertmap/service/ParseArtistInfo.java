package de.berlin.special.concertmap.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;

import de.berlin.special.concertmap.data.EventContract.FavArtistEntry;
import de.berlin.special.concertmap.data.EventContract.LinkEntry;
import de.berlin.special.concertmap.data.EventDbHelper;
import de.berlin.special.concertmap.model.Link;
import de.berlin.special.concertmap.util.Utility;

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

        final String ART_JSON_KEY = "performers";
        final String ART_API_ID = "id";
        final String ART_NAME = "name";
        final String ART_API_URL = "url";
        final String ART_IMAGE = "image";
        final String ART_HAS_UPCOMING_EVENTS = "has_upcoming_events";

        final String LINKS_JSON_KEY = "links";
        final String LINK_PROVIDER = "provider";
        final String LINK_URL = "url";

        int artID;
        String artName;
        String artURL;
        String artImage;
        String artHasUpcomingEvents;
        Hashtable<Integer, Link> links = new Hashtable<Integer, Link>();

        try {

            JSONObject mainObj = new JSONObject(artistJsonStr);
            JSONArray artistArray = mainObj.getJSONArray(ART_JSON_KEY);
            JSONObject artistObj = artistArray.getJSONObject(0);

            artID = artistObj.getInt(ART_API_ID);
            artName = artistObj.getString(ART_NAME);
            artURL = artistObj.getString(ART_API_URL);
            artImage = artistObj.getString(ART_IMAGE);
            artHasUpcomingEvents = artistObj.getString(ART_HAS_UPCOMING_EVENTS);

            JSONArray linksJSONArray = artistObj.getJSONArray(LINKS_JSON_KEY);
            for (int j = 0; j < linksJSONArray.length(); j++) {
                JSONObject link = linksJSONArray.getJSONObject(j);
                String provider = link.getString(LINK_PROVIDER);
                if (!provider.equals("musicbrainz")) {
                    String URL = link.getString(LINK_URL);
                    links.put(j, new Link(provider, URL));
                }
            }

            ContentValues artistValues = new ContentValues();
            // Create a new map of values for artist, where column names are the keys
            artistValues.put(FavArtistEntry.COL_FAV_ART_API_ID, artID);
            artistValues.put(FavArtistEntry.COL_FAV_ART_NAME, artName);
            artistValues.put(FavArtistEntry.COL_FAV_ART_API_URL, artURL);
            artistValues.put(FavArtistEntry.COL_FAV_ART_IMAGE, artImage);
            artistValues.put(FavArtistEntry.COL_FAV_ART_UPCOMING_EVENTS, artHasUpcomingEvents);
            artistValues.put(FavArtistEntry.COL_FAV_ART_TRACKED, Utility.ARTIST_TRACKED_NO);
            // Insert the new venue row
            long newRowIdArtist;
            newRowIdArtist = liteDatabase.insert(
                    FavArtistEntry.TABLE_NAME,
                    null,
                    artistValues);

            for (int key : links.keySet()) {
                ContentValues linkValues = new ContentValues();
                // Create a new map of values for artist, where column names are the keys
                linkValues.put(LinkEntry.COLUMN_LINK_ART_ID, newRowIdArtist);
                linkValues.put(LinkEntry.COLUMN_LINK_PROVIDER, links.get(key).getProvider());
                linkValues.put(LinkEntry.COLUMN_LINK_URL, links.get(key).getURL());
                // Insert the new venue row
                long newRowIdLink;
                newRowIdLink = liteDatabase.insert(
                        LinkEntry.TABLE_NAME,
                        null,
                        linkValues);
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }
}

