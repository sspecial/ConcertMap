package de.berlin.special.concertmap.artist;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import de.berlin.special.concertmap.R;
import de.berlin.special.concertmap.data.Query;

public class ArtistActivity extends AppCompatActivity {

    Fragment artistActivityFragment = new ArtistActivityFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);

        Intent intent = this.getIntent();
        Bundle args = new Bundle();

        args.putInt(String.valueOf(Query.COL_ARTIST_API_ID)
                , intent.getIntExtra(String.valueOf(Query.COL_ARTIST_API_ID), -1));

        artistActivityFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.artist_container, artistActivityFragment)
                .commit();
    }
}
