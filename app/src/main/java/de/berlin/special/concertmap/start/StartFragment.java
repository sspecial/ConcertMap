package de.berlin.special.concertmap.start;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import de.berlin.special.concertmap.R;
import de.berlin.special.concertmap.service.DataFetchService;
import de.berlin.special.concertmap.util.Utility;

public class StartFragment extends Fragment {

    private View rootView;
    private SharedPreferences settings;
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
        settings = getActivity().getSharedPreferences(Utility.PREFS_NAME, Context.MODE_PRIVATE);

        dataProcessPI = (ProgressBar) rootView.findViewById(R.id.parse_data_progress);
        cityViewLayout = (LinearLayout) rootView.findViewById(R.id.city_view_layout);
        locationView = (TextView) rootView.findViewById(R.id.city_text_view);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        cityViewLayout.setVisibility(View.VISIBLE);
        dataProcessPI.setVisibility(View.VISIBLE);
        locationView.setText(settings.getString(Utility.SETTING_LOCATION, Utility.CITY_IS_UNKNOWN));

        int readStoragePermission = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);
        int writeStoragePermission = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (readStoragePermission != PackageManager.PERMISSION_GRANTED && writeStoragePermission != PackageManager.PERMISSION_GRANTED) {
            startActivity(new Intent(getContext(), PermissionActivity.class));
        } else {
            // Fetching data from Thrillcall API based on Geo information
            new DataFetchService(getActivity(), rootView, Utility.URL_GEO_EVENTS).execute();
        }
    }
}
