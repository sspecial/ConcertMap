package de.berlin.special.concertmap.start;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import de.berlin.special.concertmap.R;
import de.berlin.special.concertmap.util.GetGeoInfo;
import de.berlin.special.concertmap.util.Utility;
import de.berlin.special.concertmap.service.DataFetchService;

public class StartFragment extends Fragment {

    private View rootView;
    private LinearLayout cityViewLayout;
    private ProgressBar dataProcessPI;
    private TextView locationView;

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

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Retrieving Geo information of last know location from shared preferences

        GetGeoInfo getGeoInfo = new GetGeoInfo(getContext());
        Double[] geoArr = getGeoInfo.getGeoInfoFromCityName(Utility.city);

        cityViewLayout.setVisibility(View.VISIBLE);
        dataProcessPI.setVisibility(View.VISIBLE);
        if(!Utility.lastKnownLocation.equals(Utility.CITY_IS_UNKNOWN))
            locationView.setText(Utility.lastKnownLocation);
        else
            locationView.setText(Utility.city);

        // Fetching data from Thrillcall API based on Geo information
        new DataFetchService(getActivity(), rootView, geoArr, Utility.URL_GEO_EVENTS).execute();

    }

}
