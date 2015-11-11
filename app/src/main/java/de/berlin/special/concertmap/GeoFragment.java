package de.berlin.special.concertmap;

import android.content.Intent;
import android.support.v4.app.Fragment;
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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import de.berlin.special.concertmap.event.EventActivity;
import de.berlin.special.concertmap.service.ParseJSONtoDatabase;


public class GeoFragment extends Fragment {

    private View rootView;
    // To figure out if images should be taken from image-folder or downloaded
    private String args;

    public GeoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        args = getArguments().getString(NavigationActivity.FRAG_GEO_TYPE);
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
            ListView todayListView = (ListView) rootView.findViewById(R.id.geo_list_view);
            // Setup cursor adapter
            TodayCursorAdapter todayCursorAdapter = new TodayCursorAdapter(getActivity(), eventCursor, 0, args);
            // Attach cursor adapter to the ListView
            todayListView.setAdapter(todayCursorAdapter);
            // Setup OnClickListener
            todayListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getActivity(), EventActivity.class);
                    intent.putExtra("position", position);
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

    // These indices are tied to CURSOR_COLUMNS
    private final int COL_EVENT_ID = 0;
    private final int COL_EVENT_NAME = 1;
    private final int COL_EVENT_START_AT = 2;
    private final int COL_EVENT_IMAGE = 3;
    private final int COL_VENUE_NAME = 4;
    private final int COL_VENUE_STREET = 5;
    private final int COL_VENUE_CITY = 6;

    private ImageView imageView;
    private TextView nameView;
    private TextView addressView;
    private TextView dateView;
    private final String LOG_TAG = TodayCursorAdapter.class.getSimpleName();

    private final String imageDirPath = "/sdcard/ImageDir/";
    File imageDir;

    public TodayCursorAdapter(Context context, Cursor c, int flags, String args) {
        super(context, c, flags);

        // Only if it is the first time GeoFragment constructed delete the image folder
        // Otherwise use the already downloaded images
        if (args.equals(NavigationActivity.FRAG_GEO_ADD)) {
            File dir = new File(imageDirPath);
            if (dir.exists()) {
                for (File imFile : dir.listFiles()) {
                    imFile.delete();
                }
                dir.delete();
            }
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
                    .execute(cursor.getString(COL_EVENT_IMAGE));
        }
        // Artists Names
        String artNames = cursor.getString(COL_EVENT_NAME);
        int beginIndex = 0;
        int endIndex = artNames.indexOf("@");
        if(endIndex != -1)
            artNames = artNames.substring(beginIndex, endIndex);
        nameView.setText(artNames);

        // Venue Name & City
        addressView.setText(cursor.getString(COL_VENUE_NAME)
                + ", "
                + cursor.getString(COL_VENUE_CITY));

        // Event time
        String dateStr = cursor.getString(COL_EVENT_START_AT);
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