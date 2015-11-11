package de.berlin.special.concertmap.event;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.berlin.special.concertmap.R;
import de.berlin.special.concertmap.navigation.NavigationActivity;

/**
 * A placeholder fragment containing a simple view.
 */
public class EventActivityFragment extends Fragment {

    private View rootView;
    private int position;

    public EventActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt("position");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_event, container, false);

        TextView eventPosition = (TextView) rootView.findViewById(R.id.event_position);
        eventPosition.setText(String.valueOf(position));
        return rootView;
    }
}
