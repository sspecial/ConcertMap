package de.berlin.special.concertmap.service;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Hashtable;

import de.berlin.special.concertmap.R;
import de.berlin.special.concertmap.artist.ArtistActivity;
import de.berlin.special.concertmap.data.Query;
import de.berlin.special.concertmap.navigate.NavigationActivity;
import de.berlin.special.concertmap.service.parseJSON.ParseArtistEventsInfo;
import de.berlin.special.concertmap.service.parseJSON.ParseArtistInfo;
import de.berlin.special.concertmap.service.parseJSON.ParseArtistSearchInfo;
import de.berlin.special.concertmap.service.parseJSON.ParseJSONtoDatabase;
import de.berlin.special.concertmap.util.BuildURL;
import de.berlin.special.concertmap.util.Utility;

/**
 * Created by Saeed on 30-Mar-15.
 */

public class DataFetchService extends AsyncTask<Void, Void, String> {

    private final String LOG_TAG = DataFetchService.class.getSimpleName();

    private Context mContext;
    private View mView;
    private URL url;
    private int artistID;

    // Deciding to fetch geo-events data, artist-events data, or artist-info data
    private int dataFetchType;

    // Constructor - Geo events
    public DataFetchService(Context context, View view, int fetchType){
        mContext = context;
        mView = view;
        dataFetchType = fetchType;
        url = new BuildURL(mContext).buildGeoEventsURL();
    }

    // Constructor - Artist Info & Events
    public DataFetchService(Context context, int artistID, int fetchType){
        mContext = context;
        dataFetchType = fetchType;
        this.artistID = artistID;

        if(dataFetchType == Utility.URL_ARTIST_EVENTS)
            url = new BuildURL(mContext).buildArtistEventsURL(artistID);
        else if(dataFetchType == Utility.URL_ARTIST_INFO)
            url = new BuildURL(mContext).buildArtistInfoURL(artistID);
    }

    // Constructor - Artist search
    public DataFetchService(Context context, View view, String artistName, int fetchType){
        mContext = context;
        mView = view;
        dataFetchType = fetchType;
        url = new BuildURL(mContext).buildArtistSearchURL(artistName);
    }

    @Override
    protected String doInBackground(Void... params) {
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String concertJSONStr = null;
        try {
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
                return Utility.ERROR_MSG;
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
                return Utility.ERROR_MSG;
            }
            concertJSONStr = buffer.toString();
            // Returning JSON data containing complete event list
            Log.d(LOG_TAG+"-Fetched JSON data: ", concertJSONStr);
            return concertJSONStr;

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the Event data, there's no point in attempting
            // to parse it.
            return Utility.ERROR_MSG;
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

    @Override
    protected void onPostExecute(String JSON) {

        // To-Do when JSON data is unavailable
        // Now we have a String representing the complete event list in JSON Format.

        // Geo Events
        if(dataFetchType == Utility.URL_GEO_EVENTS) {

            ProgressBar dataProcessPI = (ProgressBar) mView.findViewById(R.id.parse_data_progress);
            // To decide what error message to show in GeoListFragment
            if (!JSON.equals(Utility.ERROR_MSG)) {
                Utility.ERROR_OBTAINING_DATA = false;
            } else {
                Utility.ERROR_OBTAINING_DATA = true;
            }
            ParseJSONtoDatabase parseJSONtoDatabase;
            parseJSONtoDatabase = new ParseJSONtoDatabase(mContext, JSON);
            parseJSONtoDatabase.parseData();
            //Displaying the 'CONTINUE' button after parsing data into database
            dataProcessPI.setVisibility(View.GONE);

            Intent intent = new Intent(mContext, NavigationActivity.class);
            mContext.startActivity(intent);

            // Finishing start activity
            ((Activity)mContext).finish();

        }
        // Artist Info based on ID
        else if(dataFetchType == Utility.URL_ARTIST_INFO) {

            ParseArtistInfo artistInfo;
            artistInfo = new ParseArtistInfo(mContext, JSON);
            artistInfo.parseArtistData();

            new DataFetchService(mContext, artistID, Utility.URL_ARTIST_EVENTS).execute();
        }
        // Artist search based on name
        else if(dataFetchType == Utility.URL_ARTIST_SEARCH) {

            final TextView artistSearchCommentView = (TextView) mView.findViewById(R.id.artist_comment_view);
            if (!JSON.equals(Utility.ERROR_MSG)) {
                ParseArtistSearchInfo searchInfo;
                searchInfo = new ParseArtistSearchInfo(mContext, JSON);
                searchInfo.parseArtistData();

                final Hashtable<String, Integer> artIDList = searchInfo.getArtistIDList();
                final String[] artNameArr = artIDList.keySet().toArray(new String[artIDList.keySet().size()]);

                    // When the name user is searching does not return any result
                if (artNameArr.length == 0) {
                    artistSearchCommentView.setVisibility(View.VISIBLE);
                    artistSearchCommentView.setText(Utility.ERROR_NO_DATA_ARTIST);

                    // When it returns only one match
                } else if (artNameArr.length == 1) {
                    artistSearchCommentView.setVisibility(View.INVISIBLE);
                    new DataFetchService(mContext, artIDList.get(artNameArr[0]), Utility.URL_ARTIST_EVENTS).execute();

                    // When it returns more than one match
                } else if (artNameArr.length > 1) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(mContext, android.R.style.Theme_Holo_Light_Dialog));
                    builder.setTitle(R.string.pick_the_artist)
                            .setItems(artNameArr, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    String artistName = artNameArr[which];
                                    artistSearchCommentView.setVisibility(View.INVISIBLE);
                                    new DataFetchService(mContext, artIDList.get(artistName), Utility.URL_ARTIST_EVENTS).execute();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }

            } else {
                artistSearchCommentView.setVisibility(View.VISIBLE);
                artistSearchCommentView.setText(Utility.ERROR_OBTAINING_DATA_ARTIST);
            }

        }
        // Coming events of an artist
        else if(dataFetchType == Utility.URL_ARTIST_EVENTS) {

            ParseArtistEventsInfo artistEventsInfo;
            artistEventsInfo = new ParseArtistEventsInfo(mContext, JSON, artistID);
            artistEventsInfo.parseData();

            Intent intent = new Intent(mContext, ArtistActivity.class);
            intent.putExtra(String.valueOf(Query.COL_ARTIST_THRILL_ID), artistID);
            mContext.startActivity(intent);
        }
    }
}