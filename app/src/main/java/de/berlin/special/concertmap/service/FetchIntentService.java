package de.berlin.special.concertmap.service;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import de.berlin.special.concertmap.service.parseJSON.ParseJSONtoDatabase;
import de.berlin.special.concertmap.util.BuildURL;
import de.berlin.special.concertmap.util.Utility;

/**
 * Created by Saeed on 09-Mar-16.
 */
public class FetchIntentService extends IntentService {

    private final String LOG_TAG = FetchIntentService.class.getSimpleName();

    public FetchIntentService() {
        super("FetchIntentService");
    }

    //Calling IntentService to fetch data
    @Override
    protected void onHandleIntent(Intent intent) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String concertJSONStr = null;
        try {
            URL url = new URL(intent.getStringExtra(Utility.URL));
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
                onPostExecute(Utility.ERROR_MSG);
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
                onPostExecute(Utility.ERROR_MSG);
            }
            concertJSONStr = buffer.toString();
            // Returning JSON data containing complete event list
            Log.d(LOG_TAG+"-Fetched JSON data: ", concertJSONStr);

            onPostExecute(concertJSONStr);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the Event data, there's no point in attempting
            // to parse it.
            onPostExecute(Utility.ERROR_MSG);
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
    }

    public void onPostExecute(String JSON) {
        // To decide what error message to show in GeoListFragment
        if (!JSON.equals(Utility.ERROR_MSG)) {
            Utility.ERROR_OBTAINING_DATA = false;
        } else {
            Utility.ERROR_OBTAINING_DATA = true;
        }
        ParseJSONtoDatabase parseJSONtoDatabase;
        parseJSONtoDatabase = new ParseJSONtoDatabase(getApplicationContext(), JSON);
        parseJSONtoDatabase.parseData();
    }

    public static class AlarmReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Intent sendIntent = new Intent(context, FetchIntentService.class);
            sendIntent.putExtra(Utility.URL, BuildURL.instance().buildGeoEventsURL().toString());
            context.startService(sendIntent);
        }
    }

}
