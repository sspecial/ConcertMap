package de.berlin.special.concertmap.event;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.FileInputStream;

import de.berlin.special.concertmap.R;
import de.berlin.special.concertmap.Utility;
import de.berlin.special.concertmap.data.EventContract;
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

    private ListPopupWindow listPopupWindow;

    public EventActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        eventID = getArguments().getInt(String.valueOf(Utility.COL_EVENT_ID), -1);
        eventStartAt = getArguments().getString(String.valueOf(Utility.COL_EVENT_START_AT), "START_AT");
        imagePath = getArguments().getString(String.valueOf(Utility.COL_EVENT_IMAGE), Utility.imageDirPath());
        attended = getArguments().getInt(String.valueOf(Utility.COL_EVENT_ATTEND), Utility.EVENT_ATTEND_NO);
        venueName = getArguments().getString(String.valueOf(Utility.COL_VENUE_NAME), "VENUE_NAME");
        venueAddress = getArguments().getString(String.valueOf(Utility.COL_VENUE_STREET), "VENUE_STREET")
                + " , " + getArguments().getString(String.valueOf(Utility.COL_VENUE_CITY), "VENUE_CITY");
        geoLat = getArguments().getDouble(String.valueOf(Utility.COL_VENUE_GEO_LAT), Utility.GEO_DEFAULT_LAT);
        geoLong = getArguments().getDouble(String.valueOf(Utility.COL_VENUE_GEO_LONG), Utility.GEO_DEFAULT_LONG);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_event, container, false);
        LinearLayout eventInfo = (LinearLayout) rootView.findViewById(R.id.linear_event_info);
        ImageView imageView = (ImageView) rootView.findViewById(R.id.event_mobile_image);
        TextView startAtView = (TextView) rootView.findViewById(R.id.textview_event_artist_name);
        TextView venueNameView = (TextView) rootView.findViewById(R.id.textview_event_venue_name);
        TextView venueAddressView = (TextView) rootView.findViewById(R.id.textview_event_venue_street);

        attendBtn = (Button) rootView.findViewById(R.id.button_attend);
        if(attended == Utility.EVENT_ATTEND_NO){
            attendBtn.setText("Attend");
            eventInfo.setBackgroundColor(getResources().getColor(R.color.blue_sky));
        } else{
            attendBtn.setText("Attended!");
            eventInfo.setBackgroundColor(getResources().getColor(R.color.orange_sky));
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
        String dateArr[] = Utility.retrieveDateAndTime(eventStartAt);
        startAtView.setText("On " + dateArr[0] + " , At " + dateArr[1]);
        venueNameView.setText(venueName);
        venueAddressView.setText(venueAddress);
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
                String eventQueryStr = "SELECT artists._ID, artists.artist_name, artists.artist_thrill_ID " +
                        "FROM artists " +
                        "WHERE artists.event_ID = " + String.valueOf(eventID) + ";";

                Cursor artistsCursor = Utility.db.rawQuery(eventQueryStr, null);
                Log.v("Artist-Cursor", DatabaseUtils.dumpCursorToString(artistsCursor));

                if(artistsCursor.getCount()>1) {
                    listPopupWindow = new ListPopupWindow(rootView.getContext());
                    String[] fromColumns = new String[]{EventContract.ArtistEntry.COLUMN_ART_NAME};
                    int[] toViews = new int[]{android.R.id.text1};
                    SimpleCursorAdapter mAdapter = new SimpleCursorAdapter(rootView.getContext(),
                            android.R.layout.simple_list_item_1, artistsCursor, fromColumns, toViews, 0);

                    listPopupWindow.setAdapter(mAdapter);
                    listPopupWindow.setAnchorView(rootView.findViewById(R.id.linear_event_buttons));
                    listPopupWindow.setModal(true);
                    listPopupWindow.show();
                } else {
                    artistsCursor.moveToFirst();
                    Log.v("THrill-ID: ", String.valueOf(artistsCursor.getInt(2)));
                    new DataFetchService(getContext(), artistsCursor.getInt(2), Utility.URL_ARTIST_INFO).execute();
                }
            }
        });
    }

}
