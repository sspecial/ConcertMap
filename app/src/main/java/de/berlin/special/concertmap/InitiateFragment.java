package de.berlin.special.concertmap;

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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;

import de.berlin.special.concertmap.service.DataFetchService;

/**
 * Created by Saeed on 18-Nov-14.
 */
public class InitiateFragment extends Fragment implements ConnectionCallbacks, OnConnectionFailedListener{

    private static final String LOG_TAG = InitiateFragment.class.getSimpleName();
    private static final String ENTER_CITY = "Location is not available, Enter the city.";
    private static final String PROCESS_MESSAGE = "Finding your city...";

    private static final int LOCATION_AVAILABLE = 100;
    private static final int LOCATION_NOT_AVAILABLE = 101;

    private String lastKnownLocation;
    private LinearLayout locationFoundLayout;
    private LinearLayout addressNotFoundLayout;
    private ProgressBar progressIndicator;
    private TextView locationView;
    private TextView commentView;
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
        View rootView = inflater.inflate(R.layout.fragment_start, container, false);

        progressIndicator = (ProgressBar) rootView.findViewById(R.id.address_progress);
        locationFoundLayout = (LinearLayout) rootView.findViewById(R.id.locationFoundLayout);
        addressNotFoundLayout = (LinearLayout) rootView.findViewById(R.id.addressNotFoundLayout);
        locationView = (TextView) rootView.findViewById(R.id.location_view);
        commentView = (TextView) rootView.findViewById(R.id.comment_view);
        searchCityBtn = (ImageButton) rootView.findViewById(R.id.validate_city_button);
        continueBtn = (Button) rootView.findViewById(R.id.continue_button);

        progressIndicator.setVisibility(View.VISIBLE);
        locationView.setText(PROCESS_MESSAGE);

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
                intent.putExtra(DataFetchService.LOCATION_EXTRA, "Berlin");
                getActivity().startActivity(intent);
            }
        });
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        getLastKnownLocation(mLastLocation);

        if (connStat == LOCATION_NOT_AVAILABLE) {

            locationFoundLayout.setVisibility(View.INVISIBLE);
            addressNotFoundLayout.setVisibility(View.VISIBLE);
            commentView.setVisibility(View.VISIBLE);
            commentView.setText(ENTER_CITY);
            searchCityBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(),
                            "Button is clicked", Toast.LENGTH_LONG).show();
                }
            });
        } else if (connStat == LOCATION_AVAILABLE) {

            addressNotFoundLayout.setVisibility(View.INVISIBLE);
            commentView.setVisibility(View.INVISIBLE);
            locationFoundLayout.setVisibility(View.VISIBLE);
            progressIndicator.setVisibility(View.GONE);
            locationView.setText(lastKnownLocation);
            // Fetching data from last.fm based on retrieved location
            Intent intent = new Intent(getActivity(), DataFetchService.class);
            intent.putExtra(DataFetchService.LOCATION_EXTRA, "Berlin");
            getActivity().startService(intent);
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

    public void getLastKnownLocation(Location loc) {

        Geocoder gcd = new Geocoder(getContext(), Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Exception in getFromLocation()");
            e.printStackTrace();
            connStat = LOCATION_NOT_AVAILABLE;
        }

        if (addresses != null && addresses.size() > 0) {

            Address address = addresses.get(0);
            String addressText = String.format(
                    "%s, %s",
                    // Locality is usually a city
                    address.getLocality(),
                    // The country of the address
                    address.getCountryName());
            // Return the City + Country
            lastKnownLocation = addressText;
            connStat = LOCATION_AVAILABLE;
        } else {
            connStat = LOCATION_NOT_AVAILABLE;
        }
    }
}
