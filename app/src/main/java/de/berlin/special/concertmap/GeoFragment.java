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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import de.berlin.special.concertmap.service.ParseJSONtoDatabase;


public class GeoFragment extends Fragment {

    // These indices are tied to CURSOR_COLUMNS
    public static final int COL_EVENT_ID = 0;
    public static final int COL_EVENT_NAME = 1;
    public static final int COL_EVENT_START_AT = 2;
    public static final int COL_EVENT_IMAGE = 3;
    public static final int COL_VENUE_NAME = 4;
    public static final int COL_VENUE_STREET = 5;
    public static final int COL_VENUE_CITY = 6;

    private View rootView;

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

        rootView = inflater.inflate(R.layout.fragment_geo, container, false);

        String eventQueryStr = "SELECT event._ID, " +
                "event.event_name, event.event_start_at, event.event_image, " +
                "venue.venue_name, venue.venue_street, venue.venue_city " +
                "FROM event " +
                "INNER JOIN venue " +
                "ON event._ID = venue.event_ID " +
                "GROUP BY event._ID;";
        try{
            Cursor eventCursor = ParseJSONtoDatabase.db.rawQuery(eventQueryStr, null);
            Log.v("Event Cursor", DatabaseUtils.dumpCursorToString(eventCursor));

            // Find ListView to populate
            ListView todayListItems = (ListView) rootView.findViewById(R.id.geo_list_view);
            // Setup cursor adapter
            TodayCursorAdapter todayCursorAdapter = new TodayCursorAdapter(getActivity(), eventCursor, 0);
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

    private ImageView imageView;
    private TextView nameView;
    private TextView addressView;
    private TextView dateView;
    private final String LOG_TAG = TodayCursorAdapter.class.getSimpleName();

    private final String imageDirPath = "/sdcard/ImageDir/";
    File imageDir;

    public TodayCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);

        File dir = new File(imageDirPath);
        if (dir.exists()) {
            for (File imFile : dir.listFiles()) {
                imFile.delete();
            }
            dir.delete();
        }
        imageDir = new File(imageDirPath);
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
        String imageName =String.valueOf(cursor.getPosition());
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
                    .execute(cursor.getString(GeoFragment.COL_EVENT_IMAGE));
        }
        // Artists Names
        String artNames = cursor.getString(GeoFragment.COL_EVENT_NAME);
        int beginIndex = 0;
        int endIndex = artNames.indexOf("@");
        if(endIndex != -1)
            artNames = artNames.substring(beginIndex, endIndex);
        nameView.setText(artNames);

        // Venue Name & City
        addressView.setText(cursor.getString(GeoFragment.COL_VENUE_NAME)
                + ", "
                + cursor.getString(GeoFragment.COL_VENUE_CITY));

        // Event time
        String dateStr = cursor.getString(GeoFragment.COL_EVENT_START_AT);
        String dayStr = dateStr.split("T")[0];
        String timeStr = dateStr.split("T")[1];
        dayStr = dayStr.substring(0,dayStr.length());
        timeStr = timeStr.substring(0,timeStr.length()-4);
        dateView.setText(dayStr + "  " + timeStr);
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