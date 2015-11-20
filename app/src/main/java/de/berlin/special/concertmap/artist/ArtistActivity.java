package de.berlin.special.concertmap.artist;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import de.berlin.special.concertmap.R;
import de.berlin.special.concertmap.Utility;
import de.berlin.special.concertmap.event.EventActivityFragment;

public class ArtistActivity extends AppCompatActivity {

    Fragment artistActivityFragment = new ArtistActivityFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);

        Intent intent = this.getIntent();
        Bundle args = new Bundle();

        args.putInt(String.valueOf(Utility.COL_ARTIST_THRILL_ID)
                , intent.getIntExtra(String.valueOf(Utility.COL_ARTIST_THRILL_ID), -1));

        artistActivityFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.artist_container, artistActivityFragment)
                .commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_artist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
