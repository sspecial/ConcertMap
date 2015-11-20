package de.berlin.special.concertmap.artist;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.berlin.special.concertmap.R;
import de.berlin.special.concertmap.Utility;

/**
 * A placeholder fragment containing a simple view.
 */
public class ArtistActivityFragment extends Fragment {

    private View rootView;
    private final String LOG_TAG = ArtistActivityFragment.class.getSimpleName();

    private int artistThrillID;

    public ArtistActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_artist, container, false);
        artistThrillID = getArguments().getInt(String.valueOf(Utility.COL_ARTIST_THRILL_ID), -1);

        String argThrillID = "WHERE artist.artist_thrill_ID = " + artistThrillID + ";";
        String favArtistQueryStr = Utility.favArtistQueryStr + argThrillID;
        Cursor favArtistCursor = Utility.db.rawQuery(favArtistQueryStr, null);

        // setting title of activity
        favArtistCursor.moveToFirst();
        getActivity().setTitle(favArtistCursor.getString(Utility.COL_ARTIST_NAME));

        Log.v(LOG_TAG + " Fav-Artist-Cursor:", DatabaseUtils.dumpCursorToString(favArtistCursor));
        return rootView;
    }
}
