package de.berlin.special.concertmap.service;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.google.api.client.auth.oauth.OAuthHmacSigner;
import com.google.api.client.auth.oauth.OAuthParameters;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

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

    String consumerKey = "4e2279091ef16766282d";
    String consumerSecret = "fec0e1fbccab865838c7";
    @Override
    protected void onHandleIntent(Intent intent) {
        String locationQuery = intent.getStringExtra(LOCATION_EXTRA);

        try {
            // Construct the URL for the last.fm query
            final String EVENTFUL_BASE_URL =
                    "http://eventful.com/oauth/request_token/?";
            final String CALLBACK_PARAM = "oauth_callback";
            final String TIMES_TAMP_PARAM = "oauth_timestamp";
            final String CONSUMER_KEY_PARAM = "oauth_consumer_key";
            final String OAUTH_VERSION_PARAM = "oauth_version";
            final String OAUTH_NONCE_PARAM = "oauth_nonce";
            final String OAUTH_SIGNATURE_METHOD_PARAM = "oauth_signature_method";
            final String OAUTH_SIGNATURE_PARAM = "oauth_signature";

            Uri builtUri = Uri.parse(EVENTFUL_BASE_URL).buildUpon()
                    .appendQueryParameter(CALLBACK_PARAM, "")
                    .appendQueryParameter(TIMES_TAMP_PARAM, "")
                    .appendQueryParameter(CONSUMER_KEY_PARAM, consumerKey)
                    .appendQueryParameter(OAUTH_VERSION_PARAM, "")
                    .appendQueryParameter(OAUTH_NONCE_PARAM, "")
                    .appendQueryParameter(OAUTH_SIGNATURE_METHOD_PARAM, "")
                    .appendQueryParameter(OAUTH_SIGNATURE_PARAM, "")
                    .build();

            URL url = new URL(builtUri.toString());

            Log.d(LOG_TAG, "URL: "+url.toString());

            // Create the request to OpenEventMap, and open the connection
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.connect();

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            return;
        }

        // ------------------------------------------------------
        /*
        // Code to use Scribe Oauth
        OAuthService service = new ServiceBuilder()
                .apiKey(apiKey)
                .apiSecret(apiSecret)
                .build();

        Token requestToken = service.getRequestToken();

        String authUrl = service.getAuthorizationUrl(requestToken);

        Verifier v = new Verifier("verifier you got from the user");
        Token accessToken = service.getAccessToken(requestToken, v);

        OAuthRequest request = new OAuthRequest(Verb.GET, "http://api.twitter.com/1/account/verify_credentials.xml");
        service.signRequest(accessToken, request); // the access token from step 4
        Response response = request.send();
        Log.d(LOG_TAG, response.getBody());*/
        // ------------------------------------------------------

        /*
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
            // Construct the URL for the last.fm query
            final String FORECAST_BASE_URL =
                    "http://ws.audioscrobbler.com/2.0/?";
            final String METHOD_PARAM = "method";
            final String FORMAT_PARAM = "format";
            final String KEY_PARAM = "api_key";
            final String LOCATION_PARAM = "location";

            Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                    .appendQueryParameter(METHOD_PARAM, method)
                    .appendQueryParameter(LOCATION_PARAM, locationQuery)
                    .appendQueryParameter(KEY_PARAM, api_key)
                    .appendQueryParameter(FORMAT_PARAM, format)
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

        // Now we have a String representing the complete forecast in JSON Format.
        // Fortunately parsing is easy:  constructor takes the JSON string and converts it
        // into an Object hierarchy for us.

        ParseJSONtoDatabase parseJSONtoDatabase;
        parseJSONtoDatabase = new ParseJSONtoDatabase(getApplicationContext(), concertJsonStr);
        parseJSONtoDatabase.parseData();*/

    }
}