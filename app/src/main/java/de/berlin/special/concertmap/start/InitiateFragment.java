package de.berlin.special.concertmap.start;

import android.content.Intent;
import android.content.IntentSender;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import de.berlin.special.concertmap.R;
import de.berlin.special.concertmap.Utility;
import de.berlin.special.concertmap.navigate.NavigationActivity;
import de.berlin.special.concertmap.service.DataFetchService;

/**
 * Created by Saeed on 18-Nov-14.
 */
public class InitiateFragment extends Fragment implements ConnectionCallbacks, OnConnectionFailedListener{

    private static final String LOG_TAG = InitiateFragment.class.getSimpleName();
    private static final String ENTER_CITY = "Location is not available, Enter the city.";
    private static final String CITY_NAME_NOT_VALID = "Please enter a valid city name.";
    private static final String FINDING_YOUR_LOCATION = "Finding your location...";

    private static final int LOCATION_AVAILABLE = 100;
    private static final int LOCATION_NOT_AVAILABLE = 101;

    private double geo_lat;
    private double geo_long;

    private View rootView;
    private LinearLayout locationFoundLayout;
    private LinearLayout addressNotFoundLayout;
    private ProgressBar dataProcessPI;
    private TextView locationView;
    private TextView commentView;
    private EditText userEntry;
    private ImageButton searchCityBtn;
    private Button continueBtn;

    // Client to request last known location
    private GoogleApiClient mGoogleApiClient;
    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;
    // Status of connection to google location service
    private int connStat;
    // Location to be retrieved
    private Location mLastLocation;
    // City & Country presented to the user
    private String lastKnownLocation;

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
        rootView = inflater.inflate(R.layout.fragment_start, container, false);

        dataProcessPI = (ProgressBar) rootView.findViewById(R.id.parse_data_progress);
        locationFoundLayout = (LinearLayout) rootView.findViewById(R.id.locationFoundLayout);
        addressNotFoundLayout = (LinearLayout) rootView.findViewById(R.id.addressNotFoundLayout);
        locationView = (TextView) rootView.findViewById(R.id.location_view);
        commentView = (TextView) rootView.findViewById(R.id.comment_view);
        userEntry = (EditText) rootView.findViewById(R.id.enter_city_edit_text);
        searchCityBtn = (ImageButton) rootView.findViewById(R.id.validate_city_button);
        continueBtn = (Button) rootView.findViewById(R.id.continue_button);

        commentView.setText(FINDING_YOUR_LOCATION);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!mResolvingError) {
            mGoogleApiClient.connect();
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

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        getLastKnownLocation(mLastLocation);

        // When location is NOT available!
        if (connStat == LOCATION_NOT_AVAILABLE) {

            locationFoundLayout.setVisibility(View.INVISIBLE);
            dataProcessPI.setVisibility(View.INVISIBLE);
            addressNotFoundLayout.setVisibility(View.VISIBLE);
            commentView.setText(ENTER_CITY);
            searchCityBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Obtaining lat & long for the user entry city
                    Double[] geoArr = getGeoInfoFromCityName(userEntry.getText().toString());

                    // To see if the user entry is a valid city name
                    if (geoArr != null) {

                        addressNotFoundLayout.setVisibility(View.INVISIBLE);
                        commentView.setVisibility(View.INVISIBLE);
                        locationFoundLayout.setVisibility(View.VISIBLE);
                        dataProcessPI.setVisibility(View.VISIBLE);
                        if (!Utility.city.equals(Utility.CITY_IS_UNKNOWN))
                            locationView.setText(lastKnownLocation);
                        else
                            locationView.setText(Utility.CITY_IS_UNKNOWN);

                        // Fetching data from Thrillcall API based on Geo information
                        new DataFetchService(getActivity(), rootView, geoArr, Utility.URL_GEO_EVENTS).execute();
                    } else {
                        commentView.setText(CITY_NAME_NOT_VALID);
                    }
                }
            });
        // When location is available!
        } else if (connStat == LOCATION_AVAILABLE) {

            // Latitude & Longitude
            geo_lat = mLastLocation.getLatitude();
            geo_long = mLastLocation.getLongitude();
            Double[] geoArr = new Double[]{geo_lat, geo_long};

            addressNotFoundLayout.setVisibility(View.INVISIBLE);
            commentView.setVisibility(View.INVISIBLE);
            locationFoundLayout.setVisibility(View.VISIBLE);
            if (!Utility.city.equals(Utility.CITY_IS_UNKNOWN))
                locationView.setText(lastKnownLocation);
            else
                locationView.setText(Utility.CITY_IS_UNKNOWN);

            // Fetching data from Thrillcall API based on Geo information
            new DataFetchService(getActivity(), rootView, geoArr, Utility.URL_GEO_EVENTS).execute();
        }
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

    public void getLastKnownLocation(Location loc) {

        List<Address> addresses = null;
        try {
            Geocoder gcd = new Geocoder(getContext(), Locale.getDefault());
            addresses = gcd.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);

                // to present the city name in the navigation activity
                Utility.city = address.getLocality();

                // Return the City + Country
                lastKnownLocation = String.format(
                        "%s, %s",
                        // Locality is usually a city
                        address.getLocality(),
                        // The country of the address
                        address.getCountryName());

                connStat = LOCATION_AVAILABLE;

            } else {
                connStat = LOCATION_NOT_AVAILABLE;
            }

        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
            connStat = LOCATION_NOT_AVAILABLE;
        }
    }
}
