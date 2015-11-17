package de.berlin.special.concertmap.navigate;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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

        LinearLayout ael = (LinearLayout) rootView.findViewById(R.id.tracked_artists_layout);
        for(int i =0; i< 5; i++){

            LinearLayout ll = new LinearLayout(getContext());
            ImageView img  = new ImageView(getContext());
            TextView text = new TextView(getContext());
            img.setImageResource(R.drawable.concert2);
            img.setMaxHeight(40);
            text.setText("--"+i);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            ll.addView(img, params);
            ll.addView(text, params);

            ViewGroup.LayoutParams parentParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            ael.addView(ll,i,parentParams);
        }
        return rootView;
    }
}