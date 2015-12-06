package de.berlin.special.concertmap.settings;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import de.berlin.special.concertmap.R;

public class TimeActivity extends AppCompatActivity {

    Fragment timeFragment = new TimeFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.time_container, timeFragment)
                .commit();
    }

}
