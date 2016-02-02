package de.berlin.special.concertmap.artist;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.ContextThemeWrapper;
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
import de.berlin.special.concertmap.data.EventContract;
import de.berlin.special.concertmap.data.EventDbHelper;
import de.berlin.special.concertmap.data.Query;
import de.berlin.special.concertmap.event.EventActivity;
import de.berlin.special.concertmap.navigate.DownloadImageTask;
import de.berlin.special.concertmap.util.Utility;

/**
 * A placeholder fragment containing a simple view.
 */
public class ArtistActivityFragment extends Fragment {

    private View rootView;
    private SQLiteDatabase liteDatabase;
    private final String LOG_TAG = ArtistActivityFragment.class.getSimpleName();

    private int artistID;
    private int artistApiID;
    private String artistName;
    private String artistApiWebsite;
    private String artistImage;
    private String artistUpcomingEvents;
    private int artistTracked;

    private ImageView imageView;
    private CheckBox trackBtn;
    private Button webBtn;

    public ArtistActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        artistApiID = getArguments().getInt(String.valueOf(Query.COL_ARTIST_API_ID), -1);

        // Query database for the artist info using thrill ID
        String argApiID = "WHERE artist.artist_API_ID = " + artistApiID + ";";
        String favArtistQueryStr = Query.favArtistQueryStr + argApiID;
        liteDatabase = getActivity().openOrCreateDatabase(EventDbHelper.DATABASE_NAME, Context.MODE_PRIVATE, null);
        Cursor favArtistCursor = liteDatabase.rawQuery(favArtistQueryStr, null);
        // Log.v(LOG_TAG + " Fav-Artist-Cursor:", DatabaseUtils.dumpCursorToString(favArtistCursor));

        // setting title of activity
        favArtistCursor.moveToFirst();
        artistID = favArtistCursor.getInt(Query.COL_ARTIST_ID);
        artistName = favArtistCursor.getString(Query.COL_ARTIST_NAME);
        artistApiWebsite = favArtistCursor.getString(Query.COL_ARTIST_API_URL);
        artistImage = favArtistCursor.getString(Query.COL_ARTIST_IMAGE);
        artistUpcomingEvents = favArtistCursor.getString(Query.COL_ARTIST_IMAGE);
        artistTracked = favArtistCursor.getInt(Query.COL_ARTIST_TRACKED);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_artist, container, false);
        getActivity().setTitle(artistName);

        trackBtn = (CheckBox) rootView.findViewById(R.id.button_favorite);
        webBtn = (Button) rootView.findViewById(R.id.button_official_website);

        if(artistTracked == Utility.ARTIST_TRACKED_NO){
            trackBtn.setText(Utility.ARTIST_TRACKED_TEXT_NO);
            trackBtn.setChecked(false);
        } else{
            trackBtn.setText(Utility.ARTIST_TRACKED_TEXT_YES);
            trackBtn.setChecked(true);
        }

        // Image view
        imageView = (ImageView) rootView.findViewById(R.id.artist_mobile_image);
        // Image dir
        File imageDir = new File(Utility.IMAGE_DIR_TODAY);
        // Image name
        String imageName = String.valueOf(artistApiID);

        // Let's see if it is necessary to download the image file
        if (!artistImage.equals("null")) {
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
                imageView.setImageResource(R.drawable.artist2);
                new DownloadImageTask(imageView, imageDir, imageName)
                        .execute(artistImage);
            }
        }

        // Find ListView to populate
        ListView artistEventsListView = (ListView) rootView.findViewById(R.id.list_view_artist_events);
        fillArtistEventsList(artistEventsListView, artistApiID);
        return rootView;
    }

    public void fillArtistEventsList(ListView artistEventsListView, final int artistID) {

        String eventQueryStr = Query.eventQueryStr
                + "WHERE event.event_belong_to_artist = "
                + artistID
                + " GROUP BY event._ID;";
        try{
            final Cursor eventCursor = liteDatabase.rawQuery(eventQueryStr, null);
            // Log.v("Event Cursor", DatabaseUtils.dumpCursorToString(eventCursor));

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
                    String eventURL = eventCursor.getString(Query.COL_EVENT_TICKET);
                    String imagePath = Utility.IMAGE_DIR_TODAY + "/" + String.valueOf(artistID);
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
                    intent.putExtra(String.valueOf(Query.COL_EVENT_TICKET), eventURL);
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
                if (trackBtn.isChecked()) {
                    args.put(EventContract.FavArtistEntry.COL_FAV_ART_TRACKED, Utility.ARTIST_TRACKED_YES);
                    trackBtn.setText(Utility.ARTIST_TRACKED_TEXT_YES);
                } else {
                    args.put(EventContract.FavArtistEntry.COL_FAV_ART_TRACKED, Utility.ARTIST_TRACKED_NO);
                    trackBtn.setText(Utility.ARTIST_TRACKED_TEXT_NO);
                }
                int row = liteDatabase.update(
                        EventContract.FavArtistEntry.TABLE_NAME,
                        args,
                        EventContract.FavArtistEntry._ID + "=" + artistID,
                        null);
            }
        });

        webBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String argArtistID = "WHERE links.artist_ID = " + String.valueOf(artistID) + ";";
                String linkQueryStr = Query.linkQueryStr + argArtistID;
                final Cursor linksCursor = liteDatabase.rawQuery(linkQueryStr, null);

                if (linksCursor.getCount() == 0) {

                    Toast toast = Toast.makeText(getContext(), Utility.NO_LINK_AVAILABLE, Toast.LENGTH_SHORT);
                    toast.show();

                } else if (linksCursor.getCount() == 1) {

                    linksCursor.moveToFirst();
                    String providerURL = linksCursor.getString(Query.COL_LINK_URL);
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(providerURL));
                    startActivity(browserIntent);

                } else if (linksCursor.getCount() > 1) {

                    String[] ticketArr = new String[linksCursor.getCount()];
                    for (int k = 0; k < linksCursor.getCount(); k++) {
                        linksCursor.moveToPosition(k);
                        ticketArr[k] = linksCursor.getString(Query.COL_LINK_PROVIDER);
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), android.R.style.Theme_Holo_Light_Dialog));
                    builder.setTitle(R.string.choose_ticket_provider)
                            .setItems(ticketArr, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    linksCursor.moveToPosition(which);
                                    String providerURL = linksCursor.getString(Query.COL_LINK_URL);
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(providerURL));
                                    startActivity(browserIntent);
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
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
        String venueNameCity = cursor.getString(Query.COL_VENUE_CITY)
                + ", "
                + cursor.getString(Query.COL_VENUE_COUNTRY);
        addressView.setText(venueNameCity);

        // Event time
        String[] dateArr = Utility.retrieveDateAndTime(cursor.getString(Query.COL_EVENT_START_AT));
        dayView.setText(dateArr[0]);
        timeView.setText(dateArr[1]);
    }
}
