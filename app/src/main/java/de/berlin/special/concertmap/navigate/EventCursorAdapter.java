package de.berlin.special.concertmap.navigate;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;

import de.berlin.special.concertmap.R;
import de.berlin.special.concertmap.util.Utility;
import de.berlin.special.concertmap.data.Query;

/**
 * Created by Saeed on 21-Nov-15.
 */
public class EventCursorAdapter extends CursorAdapter {

    private ImageView imageView;
    private TextView nameView;
    private TextView addressView;
    private TextView dayView;
    private TextView timeView;
    private final String LOG_TAG = EventCursorAdapter.class.getSimpleName();
    // To specify the color of event item when it is Geo or Attended
    private String fragType;
    File imageDir;

    public EventCursorAdapter(Context context, Cursor c, int flags, String type) {
        super(context, c, flags);
        fragType = type;

        if (fragType.equals(Utility.FRAG_EL_GEO)) {
            imageDir = new File(Utility.IMAGE_DIR_TODAY);
        }else {
            imageDir = new File(Utility.IMAGE_DIR_EVENT);
        }
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
        dayView = (TextView) view.findViewById(R.id.list_item_day_textview);
        timeView = (TextView) view.findViewById(R.id.list_item_time_textview);

        // Setting the background and text color based on fragment type
        if (fragType.equals(Utility.FRAG_EL_GEO)) {
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.blue_sky));
            nameView.setTextColor(ContextCompat.getColor(context, R.color.blue));
        } else {
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.orange_sky));
            nameView.setTextColor(ContextCompat.getColor(context, R.color.orange));
        }

        // Event image
        String imageName = String.valueOf(cursor.getInt(Query.COL_EVENT_API_ID));
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
            if (!cursor.getString(Query.COL_EVENT_IMAGE).equals("null")) {
                new DownloadImageTask(imageView, imageDir, imageName)
                        .execute(cursor.getString(Query.COL_EVENT_IMAGE));
            } else {
                imageView.setImageResource(R.drawable.concert2);
            }
        }

        // Artists Names
        nameView.setText(Utility.retrieveArtistName(cursor.getString(Query.COL_EVENT_NAME)));

        // Venue Name & City
        if (fragType.equals(Utility.FRAG_EL_GEO)) {
            String venueNameCity = cursor.getString(Query.COL_VENUE_NAME);
            addressView.setText(venueNameCity);
        } else {
            String venueNameCity = cursor.getString(Query.COL_VENUE_NAME)
                    + ", "
                    + cursor.getString(Query.COL_VENUE_CITY);
            addressView.setText(venueNameCity);
        }

        // Event time
        String[] dateArr = Utility.retrieveDateAndTime(cursor.getString(Query.COL_EVENT_START_AT));
        dayView.setText(dateArr[0]);
        timeView.setText(dateArr[1]);
    }
}
