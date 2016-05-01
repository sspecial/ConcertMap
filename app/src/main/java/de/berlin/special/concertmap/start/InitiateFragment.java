package de.berlin.special.concertmap.start;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;

import java.util.List;
import java.util.Locale;

import de.berlin.special.concertmap.R;
import de.berlin.special.concertmap.service.DataFetchService;
import de.berlin.special.concertmap.util.GetGeoInfo;
import de.berlin.special.concertmap.util.Utility;

/**
 * Created by Saeed on 18-Nov-14.
 */
public class InitiateFragment extends Fragment implements ConnectionCallbacks, OnConnectionFailedListener {

    private static final String LOG_TAG = InitiateFragment.class.getSimpleName();
    private static final String ENTER_CITY = "Location is not available, Enter the city.";
    private static final String FINDING_YOUR_LOCATION = "Finding your location...";

    // Location Permissions
    private static final int REQUEST_LOCATION_ACCESS = 1;
    private boolean locationPermission = false;
    private static String[] PERMISSIONS_LOCATION = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private static String[] PERMISSIONS_ALL = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private static final int LOCATION_AVAILABLE = 100;
    private static final int LOCATION_NOT_AVAILABLE = 101;

    private View rootView;
    private SharedPreferences settings;
    private LinearLayout foundLayout;
    private LinearLayout notFoundLayout;
    private ProgressBar dataProcessPI;
    private TextView locationView;
    private TextView commentView;
    private EditText userEntry;
    private ImageButton searchCityBtn;

    // Client to request last known location
    private GoogleApiClient mGoogleApiClient;
    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;
    // Status of connection to google location service
    private int connStat;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create a GoogleApiClient instance
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_initiate, container, false);
        settings = getActivity().getSharedPreferences(Utility.PREFS_NAME, Context.MODE_PRIVATE);
        dataProcessPI = (ProgressBar) rootView.findViewById(R.id.parse_data_progress);
        foundLayout = (LinearLayout) rootView.findViewById(R.id.found_layout);
        notFoundLayout = (LinearLayout) rootView.findViewById(R.id.not_found_layout);
        locationView = (TextView) rootView.findViewById(R.id.location_view);
        commentView = (TextView) rootView.findViewById(R.id.comment_view);
        userEntry = (EditText) rootView.findViewById(R.id.enter_city_edit_text);
        searchCityBtn = (ImageButton) rootView.findViewById(R.id.validate_city_button);

        commentView.setText(FINDING_YOUR_LOCATION);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        /**
         * Checks if the app has permission to access the Location
         * If the app does not has permission then the user will be prompted to grant permissions
         */
        int fineLocationPermission = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
        int coarseLocationPermission = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION);
        int readStoragePermission = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);
        int writeStoragePermission = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (fineLocationPermission != PackageManager.PERMISSION_GRANTED && coarseLocationPermission != PackageManager.PERMISSION_GRANTED) {
            if (readStoragePermission != PackageManager.PERMISSION_GRANTED && writeStoragePermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(PERMISSIONS_ALL, REQUEST_LOCATION_ACCESS);
            } else {
                requestPermissions(PERMISSIONS_LOCATION, REQUEST_LOCATION_ACCESS);
            }
        } else {
            locationPermission = true;
            if (!mResolvingError) {
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int i = 0; i < grantResults.length; i++) {
            Log.d(LOG_TAG, String.format("permission '%s' : granted result '%d'", permissions[i], grantResults[i]));
        }

        switch (requestCode) {

            case REQUEST_LOCATION_ACCESS: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    locationPermission = true;
                }
                if (!mResolvingError) {
                    mGoogleApiClient.connect();
                }
                return;
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) throws SecurityException {

        if (locationPermission) {
            getLastKnownLocation(LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient));
        }
        else {
            connStat = LOCATION_NOT_AVAILABLE;
        }

        // When location is NOT available!
        if (connStat == LOCATION_NOT_AVAILABLE) {

            foundLayout.setVisibility(View.INVISIBLE);
            dataProcessPI.setVisibility(View.INVISIBLE);
            notFoundLayout.setVisibility(View.VISIBLE);
            commentView.setText(ENTER_CITY);
            searchCityBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Obtaining lat & long for the user entry city
                    GetGeoInfo getGeoInfo = new GetGeoInfo(getContext());

                    String entry = userEntry.getText().toString();
                    if (entry.lastIndexOf(" ") == (entry.length()-1))
                        entry = entry.substring(0, entry.length()-1);

                    boolean correctName = getGeoInfo.getGeoInfoFromCityName(entry);

                    // To see if the user entry is a valid city name
                    if (correctName) {

                        notFoundLayout.setVisibility(View.INVISIBLE);
                        foundLayout.setVisibility(View.VISIBLE);
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

                    } else {
                        commentView.setText(Utility.CITY_NAME_NOT_VALID);
                    }
                }
            });
        // When location is available!
        } else if (connStat == LOCATION_AVAILABLE) {

            notFoundLayout.setVisibility(View.INVISIBLE);
            foundLayout.setVisibility(View.VISIBLE);
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

    @Override
    public void onConnectionSuspended(int i) {
        return;
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {

        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(getActivity(), REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            // Show dialog using GoogleApiAvailability.getErrorDialog()
            Log.d(LOG_TAG, String.valueOf(result.getErrorCode()));
            mResolvingError = true;
        }
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    public void getLastKnownLocation(Location mLastLocation) {

        List<Address> addresses = null;
        try {
            Geocoder gcd = new Geocoder(getContext(), Locale.getDefault());
            addresses = gcd.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1);
            if (addresses != null && addresses.size() > 0) {
                Address a = addresses.get(0);

                String cityStr;
                if (a.getLocality() != null)
                    cityStr = a.getLocality();
                else
                    cityStr = a.getAddressLine(0);

                String countryStr;
                if (a.getCountryName() != null)
                    countryStr = a.getCountryName();
                else
                    countryStr = a.getAddressLine(1);

                settings.edit().putString(Utility.SETTING_CITY, cityStr).commit();
                settings.edit().putString(Utility.SETTING_LOCATION, String.format("%s, %s", cityStr, countryStr)).commit();
                settings.edit().putFloat(Utility.SETTING_GEO_LAT, (float) a.getLatitude()).commit();
                settings.edit().putFloat(Utility.SETTING_GEO_LONG, (float) a.getLongitude()).commit();

                connStat = LOCATION_AVAILABLE;

            } else {
                connStat = LOCATION_NOT_AVAILABLE;
            }

        } catch (Exception e) {
            connStat = LOCATION_NOT_AVAILABLE;
        }
    }
}
