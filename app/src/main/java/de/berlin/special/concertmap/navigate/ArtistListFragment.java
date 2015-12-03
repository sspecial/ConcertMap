package de.berlin.special.concertmap.navigate;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;

import de.berlin.special.concertmap.R;
import de.berlin.special.concertmap.util.Utility;
import de.berlin.special.concertmap.artist.ArtistActivity;
import de.berlin.special.concertmap.data.Query;

/**
 * Created by Saeed on 10-Nov-15.
 */

public class ArtistListFragment extends Fragment {

    private View rootView;
    private final String LOG_TAG = ArtistListFragment.class.getSimpleName();

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
        GridView trackedArtistsGrid = (GridView) rootView.findViewById(R.id.tracked_artists_grid);

        // Query database for the tracked artists
        String argTracked = "WHERE artist.artist_tracked = " + Utility.ARTIST_TRACKED_YES + ";";
        String favArtistQueryStr = Query.favArtistQueryStr + argTracked;
        favArtistCursor = Utility.db.rawQuery(favArtistQueryStr, null);
        Log.v(LOG_TAG + " Fav-Artist-Cursor:", DatabaseUtils.dumpCursorToString(favArtistCursor));

        ArtistGridAdapter artistGridAdapter = new ArtistGridAdapter(getActivity(), favArtistCursor, 0);

        trackedArtistsGrid.setAdapter(artistGridAdapter);

        TextView emptyView = (TextView) rootView.findViewById(R.id.artistEmptyTextView);
        emptyView.setText("When you track an artist it will be added here :)");
        trackedArtistsGrid.setEmptyView(emptyView);

        return rootView;
    }
}

class ArtistGridAdapter extends CursorAdapter {

    private TextView nameView;
    private ImageView imageView;
    private final String LOG_TAG = ArtistGridAdapter.class.getSimpleName();


    public ArtistGridAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.artist_grid_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        final int artistThrillID = cursor.getInt(Query.COL_ARTIST_THRILL_ID);
        String artistName = cursor.getString(Query.COL_ARTIST_NAME);
        String imageURL = cursor.getString(Query.COL_ARTIST_IMAGE_MOBILE);

        nameView = (TextView) view.findViewById(R.id.artist_name_text);
        imageView = (ImageView) view.findViewById(R.id.artist_image_view);

        // Artist Name
        nameView.setText(artistName);

        // Artist Image
        File imageDir = new File(Utility.IMAGE_DIR_ARTIST);
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
            new DownloadImageTask(imageView, imageDir, imageName).execute(imageURL);
        }

        // Setting listener
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ArtistActivity.class);
                intent.putExtra(String.valueOf(Query.COL_ARTIST_THRILL_ID), artistThrillID);
                context.startActivity(intent);
            }
        });
    }
}

class SquareImageView extends ImageView {
    public SquareImageView(Context context) {
        super(context);
    }

    public SquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth()); //Snap to width
    }
}