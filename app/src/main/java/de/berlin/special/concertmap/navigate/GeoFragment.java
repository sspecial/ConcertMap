package de.berlin.special.concertmap.navigate;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.berlin.special.concertmap.R;
import de.berlin.special.concertmap.Utility;
import de.berlin.special.concertmap.event.EventActivity;


public class GeoFragment extends Fragment {

    private View rootView;
    private String args;

    public GeoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        args = getArguments().getString(Utility.FRAG_GEO_TYPE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_geo, container, false);

        String eventQueryStr = "SELECT event._ID, " +
                "event.event_name, event.event_start_at, event.event_image, " +
                "venue.venue_name, venue.venue_street, venue.venue_city, " +
                "venue.venue_geo_lat, venue.venue_geo_long " +
                "FROM event " +
                "INNER JOIN venue " +
                "ON event._ID = venue.event_ID " +
                "GROUP BY event._ID;";
        try{
            final Cursor eventCursor = Utility.db.rawQuery(eventQueryStr, null);
            Log.v("Event Cursor", DatabaseUtils.dumpCursorToString(eventCursor));

            // Find ListView to populate
            ListView todayListView = (ListView) rootView.findViewById(R.id.geo_list_view);
            // Setup cursor adapter
            TodayCursorAdapter todayCursorAdapter = new TodayCursorAdapter(getActivity(), eventCursor, 0, args);
            // Attach cursor adapter to the ListView
            todayListView.setAdapter(todayCursorAdapter);
            // Setup OnClickListener
            todayListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    eventCursor.moveToPosition(position);
                    int eventID = eventCursor.getInt(Utility.COL_EVENT_ID);
                    String artistsName = Utility.retrieveArtistName(eventCursor.getString(Utility.COL_EVENT_NAME));
                    String startAt = eventCursor.getString(Utility.COL_EVENT_START_AT);
                    String imagePath = Utility.imageDirPath() +"/"+ String.valueOf(position);
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

class TodayCursorAdapter extends CursorAdapter {

    private ImageView imageView;
    private TextView nameView;
    private TextView addressView;
    private TextView dateView;
    private final String LOG_TAG = TodayCursorAdapter.class.getSimpleName();

    File imageDir;

    public TodayCursorAdapter(Context context, Cursor c, int flags, String args) {
        super(context, c, flags);

        // Only if it is the first time GeoFragment constructed delete the image folder
        // Otherwise use the already downloaded images
        if (args.equals(Utility.FRAG_GEO_ADD)) {
            File dir = new File(Utility.imageDirPath());
            if (dir.exists()) {
                // Only download event images once per day
                // To_Do considering change of city for downloading images using SharedPreferences
                if (!dir.getName().equals(new SimpleDateFormat("yyyy-MM-dd").format(new Date()))) {
                    for (File imFile : dir.listFiles()) {
                        imFile.delete();
                    }
                    dir.delete();
                }
            }
        }
        imageDir = new File(Utility.imageDirPath());
        imageDir.mkdirs();
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

        // Event image
        String imageName = String.valueOf(cursor.getPosition());
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
        }else {
            imageView.setImageResource(R.drawable.concert2);
            new DownloadImageTask(imageView, imageDir, imageName)
                    .execute(cursor.getString(Utility.COL_EVENT_IMAGE));
        }
        // Artists Names
        nameView.setText(Utility.artistNamePartition(Utility.retrieveArtistName(cursor.getString(Utility.COL_EVENT_NAME))));


        // Venue Name & City
        String venueNameCity = cursor.getString(Utility.COL_VENUE_NAME)
                + " , "
                + cursor.getString(Utility.COL_VENUE_CITY);
        addressView.setText(Utility.venueNamePartition(venueNameCity));

        // Event time
        String dateArr[] = Utility.retrieveDateAndTime(cursor.getString(Utility.COL_EVENT_START_AT));
        dateView.setText(dateArr[0] + "  " + dateArr[1]);
    }

}

class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView imageView;
    File imageDir;
    String imageName;
    private final String LOG_TAG = DownloadImageTask.class.getSimpleName();

    public DownloadImageTask(ImageView imageView, File imageDir, String imageName) {
        this.imageView = imageView;
        this.imageDir = imageDir;
        this.imageName = imageName;
    }
    protected Bitmap doInBackground(String... urls) {
        String imageURL = urls[0];
        Bitmap mIcon = null;
        try {
            InputStream in = new java.net.URL(imageURL).openStream();
            mIcon = BitmapFactory.decodeStream(in);
            in.close();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error downloading the image");
            Log.e(LOG_TAG, e.getMessage());
        }
        return mIcon;
    }

    protected void onPostExecute(Bitmap imageToSave) {
        imageView.setImageBitmap(imageToSave);

        File file = new File(imageDir, imageName);
        try {
            FileOutputStream out = new FileOutputStream(file);
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error writing the image file to sdcard");
            Log.e(LOG_TAG, e.getMessage());
        }
    }
}