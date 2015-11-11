package de.berlin.special.concertmap.event;

import android.graphics.BitmapFactory;
import android.media.Image;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;

import de.berlin.special.concertmap.R;
import de.berlin.special.concertmap.Utility;

/**
 * A placeholder fragment containing a event view.
 */
public class EventActivityFragment extends Fragment {

    private View rootView;
    private final String LOG_TAG = EventActivityFragment.class.getSimpleName();

    private String imagePath;
    private String eventStartAt;
    private String venueName;
    private String venueAddress;

    public EventActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagePath = getArguments().getString(String.valueOf(Utility.COL_EVENT_IMAGE), Utility.imageDirPath);
        eventStartAt = getArguments().getString(String.valueOf(Utility.COL_EVENT_START_AT), "START_AT");
        venueName = getArguments().getString(String.valueOf(Utility.COL_VENUE_NAME), "VENUE_NAME");
        venueAddress = getArguments().getString(String.valueOf(Utility.COL_VENUE_STREET), "VENUE_STREET")
                + " , " + getArguments().getString(String.valueOf(Utility.COL_VENUE_CITY), "VENUE_CITY");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_event, container, false);

        ImageView imageView = (ImageView) rootView.findViewById(R.id.event_mobile_image);
        TextView startAtView = (TextView) rootView.findViewById(R.id.textview_event_artist_name);
        TextView venueNameView = (TextView) rootView.findViewById(R.id.textview_event_venue_name);
        TextView venueAddressView = (TextView) rootView.findViewById(R.id.textview_event_venue_street);

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
}
