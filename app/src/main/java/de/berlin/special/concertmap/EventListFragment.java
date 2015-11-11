package de.berlin.special.concertmap;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.berlin.special.concertmap.R;

/**
 * Created by Saeed on 10-Nov-15.
 */

public class EventListFragment extends Fragment {

    private View rootView;

    public EventListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_events, container, false);
        return rootView;
    }
}
