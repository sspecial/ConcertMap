package de.berlin.special.concertmap.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.berlin.special.concertmap.R;
import de.berlin.special.concertmap.service.DataFetchService;
import de.berlin.special.concertmap.util.GetGeoInfo;
import de.berlin.special.concertmap.util.Utility;

public class CityFragment extends Fragment {

    private View rootView;
    private SharedPreferences settings;
    private LinearLayout cityViewLayout;
    private LinearLayout searchCityLayout;
    private TextView locationView;
    private TextView commentView;
    private EditText cityEntry;
    private EditText countryEntry;
    private ImageButton searchCityBtn;

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
        settings = getActivity().getSharedPreferences(Utility.PREFS_NAME, Context.MODE_PRIVATE);
        searchCityLayout = (LinearLayout) rootView.findViewById(R.id.search_city_layout);
        cityViewLayout = (LinearLayout) rootView.findViewById(R.id.city_view_layout);
        locationView = (TextView) rootView.findViewById(R.id.city_text_view);
        commentView = (TextView) rootView.findViewById(R.id.comment_view);
        cityEntry = (EditText) rootView.findViewById(R.id.enter_city_edit_text);
        countryEntry = (EditText) rootView.findViewById(R.id.enter_country_edit_text);
        searchCityBtn = (ImageButton) rootView.findViewById(R.id.validate_city_button);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        searchCityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Obtaining lat & long for the user entry city
                GetGeoInfo getGeoInfo = new GetGeoInfo(getContext());

                String city = cityEntry.getText().toString();
                String country = countryEntry.getText().toString();

                String entry = city + " " + country;

                boolean correctName = getGeoInfo.getGeoInfoFromCityName(entry);

                // To see if the user entry is a valid city name
                if (correctName) {

                    searchCityLayout.setVisibility(View.INVISIBLE);
                    commentView.setVisibility(View.INVISIBLE);
                    cityViewLayout.setVisibility(View.VISIBLE);
                    locationView.setText(settings.getString(Utility.SETTING_LOCATION, Utility.CITY_IS_UNKNOWN));

                    // Fetching data from Thrillcall API based on Geo information
                    new DataFetchService(getActivity(), rootView, Utility.URL_GEO_EVENTS).execute();
                } else {
                    commentView.setText(Utility.CITY_NAME_NOT_VALID);
                }
            }
        });
    }
}
