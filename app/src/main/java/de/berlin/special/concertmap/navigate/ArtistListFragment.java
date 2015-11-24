package de.berlin.special.concertmap.navigate;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.BitmapFactory;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;

import de.berlin.special.concertmap.R;
import de.berlin.special.concertmap.Utility;
import de.berlin.special.concertmap.artist.ArtistActivity;
import de.berlin.special.concertmap.data.EventContract;

/**
 * Created by Saeed on 10-Nov-15.
 */

public class ArtistListFragment extends Fragment {

    private View rootView;
    private final String LOG_TAG = ArtistListFragment.class.getSimpleName();

    private int artistThrillID;
    private String artistName;
    private Cursor favArtistCursor;

    public ArtistListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_artists, container, false);
        LinearLayout trackedArtistsLayout = (LinearLayout) rootView.findViewById(R.id.tracked_artists_layout);

        // Query database for the tracked artists
        String argTracked = "WHERE artist.artist_tracked = " + Utility.ARTIST_TRACKED_YES + ";";
        String favArtistQueryStr = Utility.favArtistQueryStr + argTracked;
        favArtistCursor = Utility.db.rawQuery(favArtistQueryStr, null);
        Log.v(LOG_TAG + " Fav-Artist-Cursor:", DatabaseUtils.dumpCursorToString(favArtistCursor));

        File imageDir = new File(Utility.imageDirPath());

        for (int i = 0; i < favArtistCursor.getCount(); i++) {

            favArtistCursor.moveToPosition(i);
            artistThrillID = favArtistCursor.getInt(Utility.COL_ARTIST_THRILL_ID);
            artistName = favArtistCursor.getString(Utility.COL_ARTIST_NAME);
            View rowView = inflater.inflate(R.layout.custom_artist_row, container, false);

            ImageButton img1 = (ImageButton) rowView.findViewById(R.id.artist_imageButton1);
            // Setting ImageButton image
            String imageName = String.valueOf(artistThrillID);
            File file = new File(imageDir, imageName);
            if (file.exists()) {
                try {
                    FileInputStream in = new FileInputStream(file);
                    img1.setImageBitmap(BitmapFactory.decodeStream(in));
                    in.close();
                } catch (Exception e) {
                    Log.e(LOG_TAG, "Error reading the image from file");
                    Log.e(LOG_TAG, e.getMessage());
                }
            }
            // Setting ImageButton listener
            img1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), ArtistActivity.class);
                    intent.putExtra(String.valueOf(Utility.COL_ARTIST_THRILL_ID), artistThrillID);
                    getActivity().startActivity(intent);
                }
            });
            TextView text1 = (TextView) rowView.findViewById(R.id.artist_name_textview1);
            // Setting artist name
            text1.setText(artistName);


            ImageButton img2 = (ImageButton) rowView.findViewById(R.id.artist_imageButton2);
            TextView text2 = (TextView) rowView.findViewById(R.id.artist_name_textview2);
            img2.setImageResource(R.drawable.helene_mobile);
            text2.setText("--");

            trackedArtistsLayout.addView(rowView, i);
        }
        return rootView;
    }
}