package de.berlin.special.concertmap.city;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import de.berlin.special.concertmap.R;

public class CityActivity extends AppCompatActivity {

    Fragment cityFragment = new CityFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.city_container, cityFragment)
                .commit();
    }

}
