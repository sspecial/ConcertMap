package de.berlin.special.concertmap.navigate;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.berlin.special.concertmap.R;

/**
 * Created by Saeed on 10-Nov-15.
 */

public class ArtistListFragment extends Fragment {

    private View rootView;

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

        for (int i = 0; i < 6; i++) {
            View rowView = inflater.inflate(R.layout.custom_artist_row, container, false);

            ImageButton img1 = (ImageButton) rowView.findViewById(R.id.artist_imageButton1);
            TextView text1 = (TextView) rowView.findViewById(R.id.artist_name_textview1);
            ImageButton img2 = (ImageButton) rowView.findViewById(R.id.artist_imageButton2);
            TextView text2 = (TextView) rowView.findViewById(R.id.artist_name_textview2);


            img1.setImageResource(R.drawable.helene_mobile);
            img2.setImageResource(R.drawable.helene_mobile);
            text1.setText("--" + i);
            text2.setText("--" + i);

            trackedArtistsLayout.addView(rowView, i);
        }
        return rootView;
    }
}