package de.berlin.special.concertmap.service;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import de.berlin.special.concertmap.R;
import de.berlin.special.concertmap.Utility;

/**
 * Created by Saeed on 30-Mar-15.
 */

public class DataFetchService extends AsyncTask<Double, Void, String> {

    private final String LOG_TAG = DataFetchService.class.getSimpleName();
    private final String Error_MSG = "Error obtaining data from remote server!";
    private Context mContext;
    private Button mButton;

    public DataFetchService(Context context, View view){
        mContext = context;
        mButton = (Button) view.findViewById(R.id.continue_button);
    }

    @Override
    protected String doInBackground(Double... params) {
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String concertJSONStr = null;

        double geoLat;
        if (params[0] != null)
            geoLat = params[0];
        else
            geoLat = Utility.GEO_DEFAULT_LAT;

        double geoLong;
        if (params[1] != null)
            geoLong = params[1];
        else
            geoLong = Utility.GEO_DEFAULT_LONG;

        String eventLimit = "20";
        String api_key = "d90d066add515bff";

        try {
            // Construct the URL for the api.thrillcall query
            final String FORECAST_BASE_URL =
                    "https://api.thrillcall.com/api/v3/events?";
            final String LAT_PARAM = "lat";
            final String LONG_PARAM = "long";
            final String LIMIT_PARAM = "limit";
            final String KEY_PARAM = "api_key";

            Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                    .appendQueryParameter(LAT_PARAM, String.valueOf(geoLat))
                    .appendQueryParameter(LONG_PARAM, String.valueOf(geoLong))
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
                return Error_MSG;
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
                return Error_MSG;
            }
            concertJSONStr = buffer.toString();
            // Returning JSON data containing complete event list
            Log.d(LOG_TAG, concertJSONStr);
            return concertJSONStr;

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the Event data, there's no point in attempting
            // to parse it.
            return Error_MSG;
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

    protected void onPostExecute(String concertJSONStr) {
        // Now we have a String representing the complete event list in JSON Format.
        ParseJSONtoDatabase parseJSONtoDatabase;
        parseJSONtoDatabase = new ParseJSONtoDatabase(mContext, concertJSONStr);
        parseJSONtoDatabase.parseData();
        //Displaying the 'CONTINUE' button after parsing data into database
        mButton.setVisibility(View.VISIBLE);
    }
}