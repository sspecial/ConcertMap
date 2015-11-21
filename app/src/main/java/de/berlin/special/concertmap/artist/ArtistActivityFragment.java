package de.berlin.special.concertmap.artist;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;

import de.berlin.special.concertmap.R;
import de.berlin.special.concertmap.Utility;
import de.berlin.special.concertmap.navigate.DownloadImageTask;

/**
 * A placeholder fragment containing a simple view.
 */
public class ArtistActivityFragment extends Fragment {

    private View rootView;
    private final String LOG_TAG = ArtistActivityFragment.class.getSimpleName();

    private int artistID;
    private int artistThrillID;
    private String artistName;
    private String artistOfficialWebsite;
    private String artistImageLarge;
    private String artistImageMobile;
    private int artistTracked;

    private Button trackBtn;
    private Button webBtn;

    public ArtistActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        artistThrillID = getArguments().getInt(String.valueOf(Utility.COL_ARTIST_THRILL_ID), -1);

        // Query database for the artist info using thrill ID
        String argThrillID = "WHERE artist.artist_thrill_ID = " + artistThrillID + ";";
        String favArtistQueryStr = Utility.favArtistQueryStr + argThrillID;
        Cursor favArtistCursor = Utility.db.rawQuery(favArtistQueryStr, null);
        Log.v(LOG_TAG + " Fav-Artist-Cursor:", DatabaseUtils.dumpCursorToString(favArtistCursor));

        // setting title of activity
        favArtistCursor.moveToFirst();
        artistID = favArtistCursor.getInt(Utility.COL_ARTIST_ID);
        artistName = favArtistCursor.getString(Utility.COL_ARTIST_NAME);
        artistOfficialWebsite = favArtistCursor.getString(Utility.COL_ARTIST_OFFICIAL_URL);
        artistImageLarge = favArtistCursor.getString(Utility.COL_ARTIST_IMAGE_LARGE);
        artistImageMobile = favArtistCursor.getString(Utility.COL_ARTIST_IMAGE_MOBILE);
        artistTracked = favArtistCursor.getInt(Utility.COL_ARTIST_TRACKED);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_artist, container, false);
        getActivity().setTitle(artistName);

        trackBtn = (Button) rootView.findViewById(R.id.button_favorite);
        webBtn = (Button) rootView.findViewById(R.id.button_website);

        if(artistTracked == Utility.ARTIST_TRACKED_NO){
            trackBtn.setText("Track");
        } else{
            trackBtn.setText("Tracked!");
        }

        // Image view
        ImageView imageView = (ImageView) rootView.findViewById(R.id.artist_mobile_image);
        // Image dir
        File imageDir = new File(Utility.imageDirPath());
        // Image name
        String imageName = String.valueOf(artistThrillID);

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
            imageView.setImageResource(R.drawable.concert2);
            new DownloadImageTask(imageView, imageDir, imageName)
                    .execute(artistImageMobile);
        }
         return rootView;
    }
}
