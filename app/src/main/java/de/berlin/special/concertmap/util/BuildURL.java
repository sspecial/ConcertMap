package de.berlin.special.concertmap.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import java.net.URL;
import java.util.Calendar;

/**
 * Created by Saeed on 10-Mar-16.
 */
public class BuildURL {

    private SharedPreferences settings;
    private final String LOG_TAG = BuildURL.class.getSimpleName();

    public BuildURL(Context context){
        settings = context.getSharedPreferences(Utility.PREFS_NAME, Context.MODE_PRIVATE);
    }

    // build geo events URL
    public URL buildGeoEventsURL() {
        URL url = null;
        try {
            // Checking geo parameters
            double geoLat = (double) settings.getFloat(Utility.SETTING_GEO_LAT, (float)Utility.GEO_DEFAULT_LAT);
            double geoLong = (double) settings.getFloat(Utility.SETTING_GEO_LONG, (float)Utility.GEO_DEFAULT_LONG);

            // Checking date parameters
            Calendar today = Calendar.getInstance();
            Calendar tomorrow = Calendar.getInstance();
            tomorrow.add(Calendar.DAY_OF_YEAR, 1);
            String minDate = Utility.MIN_DATE_DEFAULT();
            String maxDate = Utility.MAX_DATE_DEFAULT();

            if (Utility.MIN_DATE.DAY_OF_YEAR >= today.DAY_OF_YEAR) {
                minDate = Utility.simpleDate(Utility.MIN_DATE);
                maxDate = Utility.simpleDate(Utility.MAX_DATE);
            } else {
                if (Utility.MAX_DATE.DAY_OF_YEAR > today.DAY_OF_YEAR) {
                    Utility.MIN_DATE = today;
                    maxDate = Utility.simpleDate(Utility.MAX_DATE);
                } else {
                    Utility.MIN_DATE = today;
                    Utility.MAX_DATE = tomorrow;
                }
            }

            // Construct the URL for the api.thrillcall query
            final String LAT_PARAM = "lat";
            final String LONG_PARAM = "long";
            final String LIMIT_PARAM = "limit";
            final String MIN_DATE_PARAM = "min_date";
            final String MAX_DATE_PARAM = "max_date";
            final String KEY_PARAM = "api_key";

            Uri builtUri = Uri.parse(Utility.THRILLCALL_GEO_BASE_URL).buildUpon()
                    .appendQueryParameter(LAT_PARAM, String.valueOf(geoLat))
                    .appendQueryParameter(LONG_PARAM, String.valueOf(geoLong))
                    .appendQueryParameter(MIN_DATE_PARAM, minDate)
                    .appendQueryParameter(MAX_DATE_PARAM, maxDate)
                    .appendQueryParameter(LIMIT_PARAM, Utility.EVENT_LIMIT_STR)
                    .appendQueryParameter(KEY_PARAM, Utility.THRILLCALL_API_KEY)
                    .build();

            url = new URL(builtUri.toString());
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error making URL: ", e);
        }
        return url;
    }

    // build artist info URL
    public URL buildArtistInfoURL(int artistID) {
        URL url = null;
        try {
            // Construct the URL for the api.thrillcall query
            final String artistURL = Utility.THRILLCALL_ARTIST_BASE_URL + String.valueOf(artistID);
            final String KEY_PARAM = "api_key";

            Uri builtUri = Uri.parse(artistURL).buildUpon()
                    .appendQueryParameter(KEY_PARAM, Utility.THRILLCALL_API_KEY)
                    .build();

            url = new URL(builtUri.toString());
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error making URL: ", e);
        }
        return url;
    }

    // build artist events URL
    public URL buildArtistEventsURL(int artistID) {
        URL url = null;
        try {
            // Construct the URL for the api.thrillcall query
            final String artistURL = Utility.THRILLCALL_ARTIST_BASE_URL + String.valueOf(artistID);
            final String artistEventsURL = artistURL + "/events";
            final String KEY_PARAM = "api_key";

            Uri builtUri = Uri.parse(artistEventsURL).buildUpon()
                    .appendQueryParameter(KEY_PARAM, Utility.THRILLCALL_API_KEY)
                    .build();

            url = new URL(builtUri.toString());
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error making URL: ", e);
        }
        return url;
    }

    // build artist search URL
    public URL buildArtistSearchURL(String artistName){
        URL url = null;
        try {
            // Construct the URL for the api.thrillcall query
            final String artistSearchURL = Utility.THRILLCALL_SEARCH_BASE_URL + artistName;
            final String KEY_PARAM = "api_key";

            Uri builtUri = Uri.parse(artistSearchURL).buildUpon()
                    .appendQueryParameter(KEY_PARAM, Utility.THRILLCALL_API_KEY)
                    .build();

            url = new URL(builtUri.toString());
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error making URL: ", e);
        }
        return url;
    }
}
