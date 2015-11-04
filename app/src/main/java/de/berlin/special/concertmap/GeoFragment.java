package de.berlin.special.concertmap;

import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.InputStream;

import de.berlin.special.concertmap.service.ParseJSONtoDatabase;


public class GeoFragment extends Fragment {

    // These indices are tied to CURSOR_COLUMNS
    public static final int COL_EVENT_ID = 0;
    public static final int COL_EVENT_START_AT = 1;
    public static final int COL_EVENT_IMAGE = 2;
    public static final int COL_ARTIST_NAME = 3;
    public static final int COL_VENUE_NAME = 4;
    public static final int COL_VENUE_STREET = 5;
    public static final int COL_VENUE_CITY = 6;

    public GeoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_geo, container, false);

        String queryStr = "SELECT event._ID, event.event_start_at, event.event_image, " +
                "artists.artist_name, " +
                "venue.venue_name, venue.venue_street, venue.venue_city " +
                "FROM event " +
                "INNER JOIN artists " +
                "ON event._ID = artists.event_ID " +
                "INNER JOIN venue " +
                "ON event._ID = venue.event_ID;";

            Cursor cursor = ParseJSONtoDatabase.db.rawQuery(queryStr, null);
            Log.v("Cursor Object", DatabaseUtils.dumpCursorToString(cursor));

            // Find ListView to populate
            ListView todayListItems = (ListView) rootView.findViewById(R.id.geo_list_view);
            // Setup cursor adapter using cursor from last step
        try{
            TodayCursorAdapter todayCursorAdapter = new TodayCursorAdapter(getActivity(), cursor, 0);
            // Attach cursor adapter to the ListView
            todayListItems.setAdapter(todayCursorAdapter);
        }
        catch (Exception e){
            Log.e("error..." , e.getMessage());
        }

        return rootView;
    }
}

class TodayCursorAdapter extends CursorAdapter {

    public ImageView imageView;
    public TextView nameView;
    public TextView addressView;
    public TextView dateView;

    public TodayCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.custom_event_row, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        imageView = (ImageView) view.findViewById(R.id.list_item_imageView);
        nameView = (TextView) view.findViewById(R.id.list_item_name_textview);
        addressView = (TextView) view.findViewById(R.id.list_item_address_textview);
        dateView = (TextView) view.findViewById(R.id.list_item_date_textview);

        imageView.setImageResource(R.drawable.concert);
        new DownloadImageTask(imageView).execute(cursor.getString(GeoFragment.COL_EVENT_IMAGE));
        nameView.setText(cursor.getString(GeoFragment.COL_ARTIST_NAME));
        addressView.setText(cursor.getString(GeoFragment.COL_VENUE_STREET));
        dateView.setText(cursor.getString(GeoFragment.COL_EVENT_START_AT));
    }
}

class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;

    public DownloadImageTask(ImageView bmImage) {
        this.bmImage = bmImage;
    }
    protected Bitmap doInBackground(String... urls) {
        String imageURL = urls[0];
        Bitmap mIcon = null;
        try {
            InputStream in = new java.net.URL(imageURL).openStream();
            mIcon = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon;
    }

    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);
    }
}
