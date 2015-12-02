package de.berlin.special.concertmap.city;

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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

import de.berlin.special.concertmap.R;
import de.berlin.special.concertmap.Utility;
import de.berlin.special.concertmap.navigate.NavigationActivity;
import de.berlin.special.concertmap.service.DataFetchService;

public class CityFragment extends Fragment {

    private static final String ENTER_CITY = "Enter the city name.";

    private View rootView;
    private LinearLayout cityViewLayout;
    private LinearLayout searchCityLayout;
    private ProgressBar dataProcessPI;
    private TextView locationView;
    private TextView commentView;
    private EditText userEntry;
    private ImageButton searchCityBtn;
    private Button continueBtn;

    private static final String LOG_TAG = CityFragment.class.getSimpleName();
    // City & Country presented to the user
    private String lastKnownLocation;

    public CityFragment() {

    }

     @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_city, container, false);

        dataProcessPI = (ProgressBar) rootView.findViewById(R.id.parse_data_progress);
        cityViewLayout = (LinearLayout) rootView.findViewById(R.id.city_view_layout);
        searchCityLayout = (LinearLayout) rootView.findViewById(R.id.search_city_layout);
        locationView = (TextView) rootView.findViewById(R.id.city_text_view);
        commentView = (TextView) rootView.findViewById(R.id.comment_view);
        userEntry = (EditText) rootView.findViewById(R.id.enter_city_edit_text);
        searchCityBtn = (ImageButton) rootView.findViewById(R.id.validate_city_button);
        continueBtn = (Button) rootView.findViewById(R.id.continue_button);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        cityViewLayout.setVisibility(View.INVISIBLE);
        dataProcessPI.setVisibility(View.INVISIBLE);
        searchCityLayout.setVisibility(View.VISIBLE);
        commentView.setText(ENTER_CITY);
        searchCityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Obtaining lat & long for the user entry city
                Double[] geoArr = getGeoInfoFromCityName(userEntry.getText().toString());

                // To see if the user entry is a valid city name
                if (geoArr != null) {

                    searchCityLayout.setVisibility(View.INVISIBLE);
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
                    commentView.setText(Utility.CITY_NAME_NOT_VALID);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NavigationActivity.class);
                getActivity().startActivity(intent);

                // Adding setting to shared preferences
                SharedPreferences.Editor editor = Utility.settings.edit();
                editor.putString(Utility.SETTING_LOCATION, Utility.city);
                editor.commit();

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
                    // to present the city name in the navigation activity
                    Utility.city = a.getLocality();
                    Utility.settings.edit().putFloat(Utility.SETTING_GEO_LAT, (float)a.getLatitude()).commit();
                    Utility.settings.edit().putFloat(Utility.SETTING_GEO_LONG, (float)a.getLongitude()).commit();
                    lastKnownLocation = String.format(
                            "%s, %s",
                            // Locality is usually a city
                            a.getLocality(),
                            // The country of the address
                            a.getCountryName());
                }
            }
        } catch (IOException e) {
            geoArr = new Double[]{(double)Utility.settings.getFloat(Utility.SETTING_GEO_LAT, (float)Utility.GEO_DEFAULT_LAT)
                    , (double)Utility.settings.getFloat(Utility.SETTING_GEO_LONG, (float)Utility.GEO_DEFAULT_LONG)};
            Log.e(LOG_TAG, "Geo-Coder is not available. Default values are utilized!");
        }
        return geoArr;
    }

}
