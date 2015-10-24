package de.berlin.special.concertmap.service;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Saeed on 30-Mar-15.
 */

public class DataFetchService extends IntentService {

    public static final String LOCATION_EXTRA = "city";
    private final String LOG_TAG = DataFetchService.class.getSimpleName();
    public DataFetchService() {
        super("ConcertMap");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String locationQuery = intent.getStringExtra(LOCATION_EXTRA);

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String concertJsonStr = null;

        String geoLat = "52.5194";
        String geoLong = "13.4067";
        String eventLimit = "2";
        String api_key = "d90d066add515bff";
        int numDays = 14;

        try {
            // Construct the URL for the api.thrillcall query
            final String FORECAST_BASE_URL =
                    "https://api.thrillcall.com/api/v3/events?";
            final String LAT_PARAM = "lat";
            final String LONG_PARAM = "long";
            final String LIMIT_PARAM = "limit";
            final String KEY_PARAM = "api_key";

            Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                    .appendQueryParameter(LAT_PARAM, geoLat)
                    .appendQueryParameter(LONG_PARAM, geoLong)
                    .appendQueryParameter(LIMIT_PARAM, eventLimit)
                    .appendQueryParameter(KEY_PARAM, api_key)
                    .build();

            URL url = new URL(builtUri.toString());

            Log.d(LOG_TAG, "URL: "+url.toString());

            // Create the request to OpenEventMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line);
                buffer.append("\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return;
            }
            concertJsonStr = buffer.toString();
            Log.d(LOG_TAG, concertJsonStr);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the Event data, there's no point in attempting
            // to parse it.
            return;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        // Now we have a String representing the complete event list in JSON Format.
        ParseJSONtoDatabase parseJSONtoDatabase;
        parseJSONtoDatabase = new ParseJSONtoDatabase(getApplicationContext(), concertJsonStr);
        parseJSONtoDatabase.parseData();
    }
}