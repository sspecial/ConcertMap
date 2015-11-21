package de.berlin.special.concertmap.navigate;

/**
 * Created by Saeed on 21-Nov-15.
 */

import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import de.berlin.special.concertmap.R;
import de.berlin.special.concertmap.Utility;
import de.berlin.special.concertmap.event.EventActivity;

public class GeoListFragment extends Fragment {

    private View rootView;

    public GeoListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_events, container, false);

        String eventQueryStr = Utility.eventQueryStr
                + "WHERE event.event_attended = "
                + Utility.EVENT_ATTEND_NO + " GROUP BY event._ID;";
        try{
            final Cursor eventCursor = Utility.db.rawQuery(eventQueryStr, null);
            Log.v("Event Cursor", DatabaseUtils.dumpCursorToString(eventCursor));

            // Find ListView to populate
            ListView todayListView = (ListView) rootView.findViewById(R.id.list_view_events);
            // Setup cursor adapter
            EventCursorAdapter eventCursorAdapter = new EventCursorAdapter(getActivity(), eventCursor, 0, Utility.FRAG_EL_GEO);
            // Attach cursor adapter to the ListView
            todayListView.setAdapter(eventCursorAdapter);
            // Setup OnClickListener
            todayListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    eventCursor.moveToPosition(position);
                    int eventID = eventCursor.getInt(Utility.COL_EVENT_ID);
                    String artistsName = Utility.retrieveArtistName(eventCursor.getString(Utility.COL_EVENT_NAME));
                    String startAt = eventCursor.getString(Utility.COL_EVENT_START_AT);
                    String imagePath = Utility.imageDirPath() +"/"+ String.valueOf(eventCursor.getInt(Utility.COL_EVENT_THRILL_ID));
                    int attended = eventCursor.getInt(Utility.COL_EVENT_ATTEND);
                    String venueName = eventCursor.getString(Utility.COL_VENUE_NAME);
                    String venueStreet = eventCursor.getString(Utility.COL_VENUE_STREET);
                    String venueCity = eventCursor.getString(Utility.COL_VENUE_CITY);
                    double venueLat = eventCursor.getDouble(Utility.COL_VENUE_GEO_LAT);
                    double venueLong = eventCursor.getDouble(Utility.COL_VENUE_GEO_LONG);

                    Intent intent = new Intent(getActivity(), EventActivity.class);
                    intent.putExtra(String.valueOf(Utility.COL_EVENT_ID), eventID);
                    intent.putExtra(String.valueOf(Utility.COL_EVENT_NAME), artistsName);
                    intent.putExtra(String.valueOf(Utility.COL_EVENT_START_AT), startAt);
                    intent.putExtra(String.valueOf(Utility.COL_EVENT_IMAGE), imagePath);
                    intent.putExtra(String.valueOf(Utility.COL_EVENT_ATTEND), attended);
                    intent.putExtra(String.valueOf(Utility.COL_VENUE_NAME), venueName);
                    intent.putExtra(String.valueOf(Utility.COL_VENUE_STREET), venueStreet);
                    intent.putExtra(String.valueOf(Utility.COL_VENUE_CITY), venueCity);
                    intent.putExtra(String.valueOf(Utility.COL_VENUE_GEO_LAT), venueLat);
                    intent.putExtra(String.valueOf(Utility.COL_VENUE_GEO_LONG), venueLong);
                    getActivity().startActivity(intent);
                }
            });
        }
        catch (Exception e){
            Log.e("error..." , e.getMessage());
        }
        return rootView;
    }
}
