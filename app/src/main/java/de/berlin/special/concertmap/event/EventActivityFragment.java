package de.berlin.special.concertmap.event;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.FileInputStream;

import de.berlin.special.concertmap.R;
import de.berlin.special.concertmap.Utility;
import de.berlin.special.concertmap.data.EventContract.EventEntry;
import de.berlin.special.concertmap.service.DataFetchService;

/**
 * A placeholder fragment containing a event view.
 */
public class EventActivityFragment extends Fragment {

    private View rootView;
    private final String LOG_TAG = EventActivityFragment.class.getSimpleName();

    private int eventID;
    private String imagePath;
    private int attended;
    private String eventStartAt;
    private String venueName;
    private String venueAddress;
    private double geoLat;
    private double geoLong;
    private GoogleMap map;

    private Button attendBtn;
    private Button artistBtn;

    public EventActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        eventID = getArguments().getInt(String.valueOf(Utility.COL_EVENT_ID), -1);
        eventStartAt = getArguments().getString(String.valueOf(Utility.COL_EVENT_START_AT), "START_AT");
        imagePath = getArguments().getString(String.valueOf(Utility.COL_EVENT_IMAGE), Utility.imageDirToday());
        attended = getArguments().getInt(String.valueOf(Utility.COL_EVENT_ATTEND), Utility.EVENT_ATTEND_NO);
        venueName = getArguments().getString(String.valueOf(Utility.COL_VENUE_NAME), "VENUE_NAME");
        venueAddress = getArguments().getString(String.valueOf(Utility.COL_VENUE_STREET), "VENUE_STREET")
                + ", " + getArguments().getString(String.valueOf(Utility.COL_VENUE_CITY), "VENUE_CITY");
        geoLat = getArguments().getDouble(String.valueOf(Utility.COL_VENUE_GEO_LAT), Utility.GEO_DEFAULT_LAT);
        geoLong = getArguments().getDouble(String.valueOf(Utility.COL_VENUE_GEO_LONG), Utility.GEO_DEFAULT_LONG);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_event, container, false);
        LinearLayout eventInfo = (LinearLayout) rootView.findViewById(R.id.linear_event_info);
        ImageView imageView = (ImageView) rootView.findViewById(R.id.event_mobile_image);
        TextView venueNameView = (TextView) rootView.findViewById(R.id.textview_event_venue_name);
        TextView venueAddressView = (TextView) rootView.findViewById(R.id.textview_event_venue_street);
        TextView dayView = (TextView) rootView.findViewById(R.id.event_item_day_textview);
        TextView timeView = (TextView) rootView.findViewById(R.id.event_item_time_textview);

        attendBtn = (Button) rootView.findViewById(R.id.button_attend);
        if(attended == Utility.EVENT_ATTEND_NO){
            attendBtn.setText("Attend");
            eventInfo.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.blue_sky));
        } else{
            attendBtn.setText("Attended!");
            eventInfo.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.orange_sky));
        }
        artistBtn = (Button) rootView.findViewById(R.id.button_artist);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        map = mapFragment.getMap();

        // Populating UI elements with event info
        File file = new File(imagePath);
        if (file.exists()) {
            try {
                FileInputStream in = new FileInputStream(file);
                imageView.setImageBitmap(BitmapFactory.decodeStream(in));
                in.close();
            } catch (Exception e) {
                Log.e(LOG_TAG, "Error reading the image from file");
                Log.e(LOG_TAG, e.getMessage());
            }
        }

        venueNameView.setText(venueName);
        venueAddressView.setText(venueAddress);
        String[] dateArr = Utility.retrieveDateAndTime(eventStartAt);
        dayView.setText(dateArr[0]);
        timeView.setText(dateArr[1]);
        return rootView;
    }

    @Override
    public void onStart(){
        super.onStart();
        // Setting up the map
        LatLng eventGeo = new LatLng(geoLat, geoLong);
        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(eventGeo, 13));
        map.addMarker(new MarkerOptions()
                .title(venueName)
                .position(eventGeo));
    }

    @Override
    public void onResume(){
        super.onResume();
        attendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues args = new ContentValues();
                args.put(EventEntry.COLUMN_CON_ATTEND, Utility.EVENT_ATTEND_YES);
                int row = Utility.db.update(
                        EventEntry.TABLE_NAME,
                        args,
                        EventEntry._ID + "=" + eventID,
                        null);
                attendBtn.setText("Attended!");

                /*
                String eventQueryStr = "SELECT event._ID " +
                        "FROM event " +
                        "WHERE " +
                        EventEntry.COLUMN_CON_ATTEND + " = " + Utility.EVENT_ATTEND_YES + ";";

                Cursor cursor = Utility.db.rawQuery(eventQueryStr, null);
                String msg = "";
                while (cursor.moveToNext()) {
                    msg += String.valueOf(cursor.getInt(Utility.COL_EVENT_ID))+"\n";
                }
                Toast.makeText(getActivity(),
                        msg, Toast.LENGTH_LONG).show();*/
            }
        });

        artistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String argEventID = "WHERE artists.event_ID = " + String.valueOf(eventID) + ";";
                String artistQueryStr = Utility.artistQueryStr + argEventID;
                final Cursor artistsCursor = Utility.db.rawQuery(artistQueryStr, null);

                // When event has multiple artists
                // AletDialog to show artists and choose between them
                if(artistsCursor.getCount()>1) {

                    String[] artArr = new String[artistsCursor.getCount()];
                    for (int i = 0; i < artistsCursor.getCount(); i++) {
                        artistsCursor.moveToPosition(i);
                        artArr[i] = artistsCursor.getString(Utility.COL_ARTIST_NAME);
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), android.R.style.Theme_Holo_Light_Dialog));
                    builder.setTitle(R.string.pick_the_artist)
                            .setItems(artArr, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    artistsCursor.moveToPosition(which);
                                    int artistThrillID = artistsCursor.getInt(Utility.COL_ARTIST_THRILL_ID);
                                    new DataFetchService(getContext(), artistThrillID, Utility.URL_ARTIST_INFO).execute();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();

                // When event has only one artist
                } else {
                    artistsCursor.moveToFirst();
                    int artistThrillID = artistsCursor.getInt(Utility.COL_ARTIST_THRILL_ID);
                    new DataFetchService(getContext(), artistThrillID, Utility.URL_ARTIST_INFO).execute();
                }
            }
        });
    }

}
