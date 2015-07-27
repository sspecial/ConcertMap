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

        String method = "geo.getevents";
        String format = "json";
        String api_key = "41c56a91f8fa9d230c7e1c1282adca1a";
        int numDays = 14;

        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            final String FORECAST_BASE_URL =
                    "http://ws.audioscrobbler.com/2.0/?";
            final String METHOD_PARAM = "method";
            final String FORMAT_PARAM = "format";
            final String KEY_PARAM = "api_key";
            final String LOCATION_PARAM = "location";

            Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                    .appendQueryParameter(METHOD_PARAM, method)
                    .appendQueryParameter(FORMAT_PARAM, format)
                    .appendQueryParameter(KEY_PARAM, api_key)
                    .appendQueryParameter(LOCATION_PARAM, locationQuery)
                    .build();

            URL url = new URL(builtUri.toString());

            // Create the request to OpenWeatherMap, and open the connection
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
            // If the code didn't successfully get the weather data, there's no point in attempting
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

        // Now we have a String representing the complete forecast in JSON Format.
        // Fortunately parsing is easy:  constructor takes the JSON string and converts it
        // into an Object hierarchy for us.

        ParseJSONtoDatabase parseJSONtoDatabase;
        parseJSONtoDatabase = new ParseJSONtoDatabase(getApplicationContext(), concertJsonStr);
        parseJSONtoDatabase.parseData();

    }
}