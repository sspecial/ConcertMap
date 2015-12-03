package de.berlin.special.concertmap.city;

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
import de.berlin.special.concertmap.util.GetGeoInfo;
import de.berlin.special.concertmap.util.Utility;
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
                GetGeoInfo getGeoInfo = new GetGeoInfo(getContext());
                Double[] geoArr = getGeoInfo.getGeoInfoFromCityName(userEntry.getText().toString());

                // To see if the user entry is a valid city name
                if (geoArr != null) {

                    searchCityLayout.setVisibility(View.INVISIBLE);
                    commentView.setVisibility(View.INVISIBLE);
                    cityViewLayout.setVisibility(View.VISIBLE);
                    dataProcessPI.setVisibility(View.VISIBLE);
                    if (!Utility.city.equals(Utility.CITY_IS_UNKNOWN))
                        locationView.setText(Utility.lastKnownLocation);
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
                if (!Utility.city.equals(Utility.CITY_IS_UNKNOWN)) {
                    Utility.settings.edit().putString(Utility.SETTING_CITY, Utility.city).commit();
                }

                // Finishing start activity
                getActivity().finish();
            }
        });
    }

}
