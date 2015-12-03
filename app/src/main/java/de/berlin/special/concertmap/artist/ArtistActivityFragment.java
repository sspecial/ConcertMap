package de.berlin.special.concertmap.artist;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;

import de.berlin.special.concertmap.R;
import de.berlin.special.concertmap.util.Utility;
import de.berlin.special.concertmap.data.EventContract;
import de.berlin.special.concertmap.data.Query;
import de.berlin.special.concertmap.event.EventActivity;
import de.berlin.special.concertmap.navigate.DownloadImageTask;

/**
 * A placeholder fragment containing a simple view.
 */
public class ArtistActivityFragment extends Fragment {

    private View rootView;
    private final String LOG_TAG = ArtistActivityFragment.class.getSimpleName();

    private int artistID;
    private int artistThrillID;
    private String artistName;
    private String artistOfficialWebsite;
    private String artistImageLarge;
    private String artistImageMobile;
    private int artistTracked;

    private CheckBox trackBtn;
    private Button webBtn;

    public ArtistActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        artistThrillID = getArguments().getInt(String.valueOf(Query.COL_ARTIST_THRILL_ID), -1);

        // Query database for the artist info using thrill ID
        String argThrillID = "WHERE artist.artist_thrill_ID = " + artistThrillID + ";";
        String favArtistQueryStr = Query.favArtistQueryStr + argThrillID;
        Cursor favArtistCursor = Utility.db.rawQuery(favArtistQueryStr, null);
        Log.v(LOG_TAG + " Fav-Artist-Cursor:", DatabaseUtils.dumpCursorToString(favArtistCursor));

        // setting title of activity
        favArtistCursor.moveToFirst();
        artistID = favArtistCursor.getInt(Query.COL_ARTIST_ID);
        artistName = favArtistCursor.getString(Query.COL_ARTIST_NAME);
        artistOfficialWebsite = favArtistCursor.getString(Query.COL_ARTIST_OFFICIAL_URL);
        artistImageLarge = favArtistCursor.getString(Query.COL_ARTIST_IMAGE_LARGE);
        artistImageMobile = favArtistCursor.getString(Query.COL_ARTIST_IMAGE_MOBILE);
        artistTracked = favArtistCursor.getInt(Query.COL_ARTIST_TRACKED);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_artist, container, false);
        getActivity().setTitle(artistName);

        trackBtn = (CheckBox) rootView.findViewById(R.id.button_favorite);
        webBtn = (Button) rootView.findViewById(R.id.button_website);

        if(artistTracked == Utility.ARTIST_TRACKED_NO){
            trackBtn.setText(Utility.ARTIST_TRACKED_TEXT_NO);
            trackBtn.setChecked(false);
        } else{
            trackBtn.setText(Utility.ARTIST_TRACKED_TEXT_YES);
            trackBtn.setChecked(true);
        }

        // Image view
        ImageView imageView = (ImageView) rootView.findViewById(R.id.artist_mobile_image);
        // Image dir
        File imageDir = new File(Utility.imageDirToday());
        // Image name
        String imageName = String.valueOf(artistThrillID);

        // Let's see if it is necessary to download the image file
        File file = new File(imageDir, imageName);
        if (file.exists()) {
            try {
                FileInputStream in = new FileInputStream(file);
                imageView.setImageBitmap(BitmapFactory.decodeStream(in));
                in.close();
            } catch (Exception e) {
                Log.e(LOG_TAG, "Error reading the image from file");
                Log.e(LOG_TAG, e.getMessage());
            }
        } else {
            imageView.setImageResource(R.drawable.concert2);
            new DownloadImageTask(imageView, imageDir, imageName)
                    .execute(artistImageMobile);
        }

        // Find ListView to populate
        ListView artistEventsListView = (ListView) rootView.findViewById(R.id.list_view_artist_events);
        fillArtistEventsList(artistEventsListView, artistThrillID);
        return rootView;
    }

    public void fillArtistEventsList(ListView artistEventsListView, final int artistID) {

        String eventQueryStr = Query.eventQueryStr
                + "WHERE event.event_belong_to_artist = "
                + artistID
                + " AND "
                + "event.event_attended = "
                + Utility.EVENT_ATTEND_NO + " GROUP BY event._ID;";
        try{
            final Cursor eventCursor = Utility.db.rawQuery(eventQueryStr, null);
            Log.v("Event Cursor", DatabaseUtils.dumpCursorToString(eventCursor));

            // Setup cursor adapter
            ArtistCursorAdapter artistCursorAdapter = new ArtistCursorAdapter(getActivity(), eventCursor, 0);
            // Attach cursor adapter to the ListView
            artistEventsListView.setAdapter(artistCursorAdapter);

            TextView emptyView = (TextView) rootView.findViewById(R.id.artistEmptyTextView);
            emptyView.setText(Utility.NO_ARTIST_PLAN);
            artistEventsListView.setEmptyView(emptyView);

            // Setup OnClickListener
            artistEventsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    eventCursor.moveToPosition(position);
                    int eventID = eventCursor.getInt(Query.COL_EVENT_ID);
                    String artistsName = Utility.retrieveArtistName(eventCursor.getString(Query.COL_EVENT_NAME));
                    String startAt = eventCursor.getString(Query.COL_EVENT_START_AT);
                    String imagePath = Utility.imageDirToday() + "/" + String.valueOf(artistID);
                    int attended = eventCursor.getInt(Query.COL_EVENT_ATTEND);
                    String venueName = eventCursor.getString(Query.COL_VENUE_NAME);
                    String venueStreet = eventCursor.getString(Query.COL_VENUE_STREET);
                    String venueCity = eventCursor.getString(Query.COL_VENUE_CITY);
                    double venueLat = eventCursor.getDouble(Query.COL_VENUE_GEO_LAT);
                    double venueLong = eventCursor.getDouble(Query.COL_VENUE_GEO_LONG);

                    Intent intent = new Intent(getActivity(), EventActivity.class);
                    intent.putExtra(String.valueOf(Query.COL_EVENT_ID), eventID);
                    intent.putExtra(String.valueOf(Query.COL_EVENT_NAME), artistsName);
                    intent.putExtra(String.valueOf(Query.COL_EVENT_START_AT), startAt);
                    intent.putExtra(String.valueOf(Query.COL_EVENT_IMAGE), imagePath);
                    intent.putExtra(String.valueOf(Query.COL_EVENT_ATTEND), attended);
                    intent.putExtra(String.valueOf(Query.COL_VENUE_NAME), venueName);
                    intent.putExtra(String.valueOf(Query.COL_VENUE_STREET), venueStreet);
                    intent.putExtra(String.valueOf(Query.COL_VENUE_CITY), venueCity);
                    intent.putExtra(String.valueOf(Query.COL_VENUE_GEO_LAT), venueLat);
                    intent.putExtra(String.valueOf(Query.COL_VENUE_GEO_LONG), venueLong);
                    getActivity().startActivity(intent);
                }
            });
        }
        catch (Exception e){
            Log.e("error..." , e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        trackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues args = new ContentValues();
                if(trackBtn.isChecked()) {
                    args.put(EventContract.FavArtistEntry.COL_FAV_ART_TRACKED, Utility.ARTIST_TRACKED_YES);
                    trackBtn.setText(Utility.ARTIST_TRACKED_TEXT_YES);
                } else {
                    args.put(EventContract.FavArtistEntry.COL_FAV_ART_TRACKED, Utility.ARTIST_TRACKED_NO);
                    trackBtn.setText(Utility.ARTIST_TRACKED_TEXT_NO);
                }
                int row = Utility.db.update(
                        EventContract.FavArtistEntry.TABLE_NAME,
                        args,
                        EventContract.FavArtistEntry._ID + "=" + artistID,
                        null);
            }
        });

        webBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!artistOfficialWebsite.equals("null")) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(artistOfficialWebsite));
                    startActivity(browserIntent);
                } else {
                    Toast toast = Toast.makeText(getContext(), Utility.NO_OFFICIAL_WEBSITE, Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }
}

class ArtistCursorAdapter extends CursorAdapter {

    private TextView nameView;
    private TextView addressView;
    private TextView dayView;
    private TextView timeView;

    public ArtistCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.custom_artist_event_row, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        nameView = (TextView) view.findViewById(R.id.textview_venue_name);
        addressView = (TextView) view.findViewById(R.id.textview_venue_street);
        dayView = (TextView) view.findViewById(R.id.artist_item_day_textview);
        timeView = (TextView) view.findViewById(R.id.artist_item_time_textview);

        // Artists Names
        nameView.setText(cursor.getString(Query.COL_VENUE_NAME));

        // Venue Name & City
        String venueNameCity = cursor.getString(Query.COL_VENUE_STREET)
                + ", "
                + cursor.getString(Query.COL_VENUE_CITY);
        addressView.setText(venueNameCity);

        // Event time
        String[] dateArr = Utility.retrieveDateAndTime(cursor.getString(Query.COL_EVENT_START_AT));
        dayView.setText(dateArr[0]);
        timeView.setText(dateArr[1]);
    }
}
