package de.berlin.special.concertmap.event;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import de.berlin.special.concertmap.R;
import de.berlin.special.concertmap.util.Utility;
import de.berlin.special.concertmap.data.Query;

public class EventActivity extends AppCompatActivity {

    Fragment eventActivityFragment = new EventActivityFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        Intent intent = this.getIntent();
        Bundle args = new Bundle();
        // setting title of activity
        setTitle(intent.getStringExtra(String.valueOf(Query.COL_EVENT_NAME)));

        args.putInt(String.valueOf(Query.COL_EVENT_ID)
                , intent.getIntExtra(String.valueOf(Query.COL_EVENT_ID), -1));
        args.putString(String.valueOf(Query.COL_EVENT_NAME)
                , intent.getStringExtra(String.valueOf(Query.COL_EVENT_NAME)));
        args.putString(String.valueOf(Query.COL_EVENT_START_AT)
                , intent.getStringExtra(String.valueOf(Query.COL_EVENT_START_AT)));
        args.putString(String.valueOf(Query.COL_EVENT_THRILL_URL)
                , intent.getStringExtra(String.valueOf(Query.COL_EVENT_THRILL_URL)));
        args.putString(String.valueOf(Query.COL_EVENT_IMAGE)
                , intent.getStringExtra(String.valueOf(Query.COL_EVENT_IMAGE)));
        args.putInt(String.valueOf(Query.COL_EVENT_ATTEND)
                , intent.getIntExtra(String.valueOf(Query.COL_EVENT_ATTEND), Utility.EVENT_ATTEND_NO));
        args.putString(String.valueOf(Query.COL_VENUE_NAME)
                , intent.getStringExtra(String.valueOf(Query.COL_VENUE_NAME)));
        args.putString(String.valueOf(Query.COL_VENUE_STREET)
                , intent.getStringExtra(String.valueOf(Query.COL_VENUE_STREET)));
        args.putString(String.valueOf(Query.COL_VENUE_CITY)
                , intent.getStringExtra(String.valueOf(Query.COL_VENUE_CITY)));
        args.putDouble(String.valueOf(Query.COL_VENUE_GEO_LAT)
                , intent.getDoubleExtra(String.valueOf(Query.COL_VENUE_GEO_LAT), Utility.GEO_DEFAULT_LAT));
        args.putDouble(String.valueOf(Query.COL_VENUE_GEO_LONG)
                , intent.getDoubleExtra(String.valueOf(Query.COL_VENUE_GEO_LONG), Utility.GEO_DEFAULT_LONG));

        eventActivityFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.event_container, eventActivityFragment)
                .commit();
    }

}
