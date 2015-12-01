package de.berlin.special.concertmap.start;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

import de.berlin.special.concertmap.R;
import de.berlin.special.concertmap.Utility;
import de.berlin.special.concertmap.navigate.NavigationActivity;
import de.berlin.special.concertmap.service.DataFetchService;

public class StartFragment extends Fragment {

    private View rootView;
    private LinearLayout cityViewLayout;
    private ProgressBar dataProcessPI;
    private TextView locationView;
    private TextView commentView;
    private Button continueBtn;

    private static final String LOG_TAG = StartFragment.class.getSimpleName();
    // City & Country presented to the user
    private String lastKnownLocation;

    public StartFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_start, container, false);

        dataProcessPI = (ProgressBar) rootView.findViewById(R.id.parse_data_progress);
        cityViewLayout = (LinearLayout) rootView.findViewById(R.id.city_view_layout);
        locationView = (TextView) rootView.findViewById(R.id.city_text_view);
        commentView = (TextView) rootView.findViewById(R.id.comment_view);
        continueBtn = (Button) rootView.findViewById(R.id.continue_button);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Fetching data from Thrillcall API based on Geo information
        Double[] geoArr = getGeoInfoFromCityName(Utility.city);
        if (geoArr != null) {
            commentView.setVisibility(View.INVISIBLE);
            cityViewLayout.setVisibility(View.VISIBLE);
            dataProcessPI.setVisibility(View.VISIBLE);
            if (!Utility.city.equals(Utility.CITY_IS_UNKNOWN))
                locationView.setText(lastKnownLocation);
            else
                locationView.setText(Utility.CITY_IS_UNKNOWN);
            // Fetching data from Thrillcall API based on Geo information
            new DataFetchService(getActivity(), rootView, geoArr, Utility.URL_GEO_EVENTS).execute();
        } else {
            commentView.setText("Geo information is not available");
            new DataFetchService(getActivity(), rootView, geoArr, Utility.URL_GEO_EVENTS).execute();
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NavigationActivity.class);
                getActivity().startActivity(intent);
                // Finishing start activity
                getActivity().finish();
            }
        });
    }

    // Obtaining lat & long for the user entry city
    public Double[] getGeoInfoFromCityName(String location){

        Double[] geoArr = null;
        try {
            Geocoder gc = new Geocoder(getActivity());
            // get the found Address Objects
            List<Address> addresses = gc.getFromLocationName(location, 1);
            for (Address a : addresses) {
                if (a.hasLatitude() && a.hasLongitude()) {
                    geoArr = new Double[]{a.getLatitude(), a.getLongitude()};
                    lastKnownLocation = String.format(
                            "%s, %s",
                            // Locality is usually a city
                            a.getLocality(),
                            // The country of the address
                            a.getCountryName());
                }
            }
        } catch (IOException e) {
            geoArr = new Double[]{Utility.GEO_DEFAULT_LAT, Utility.GEO_DEFAULT_LONG};
            Log.e(LOG_TAG, "Geo-Coder is not available. Default values are utilized!");
        }
        return geoArr;
    }

}
