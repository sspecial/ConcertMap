package de.berlin.special.concertmap;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
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

import de.berlin.special.concertmap.service.DataFetchService;

/**
 * Created by Saeed on 18-Nov-14.
 */
public class InitiateFragment extends Fragment {

    private static final String LOG_TAG = InitiateFragment.class.getSimpleName();
    private static final String noAddressFound = "No address found.";
    private static final String locationNotFound = "Location not found.";
    private static final String enterTheCity = "Enter the city.";
    private LocationManager locMgr;
    private LinearLayout locationFoundLayout;
    private LinearLayout locationNotFoundLayout;
    private LinearLayout addressNotFoundLayout;
    private ProgressBar progressIndicator;
    private TextView locationView;
    private TextView commentView;
    private ImageButton locationRefreshBtn;
    private ImageButton searchCityBtn;
    private Button continueBtn;


    public InitiateFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_start, container, false);

        locMgr = (LocationManager)
                getActivity().getSystemService(Context.LOCATION_SERVICE);

        progressIndicator = (ProgressBar) rootView.findViewById(R.id.address_progress);
        locationFoundLayout = (LinearLayout) rootView.findViewById(R.id.locationFoundLayout);
        locationNotFoundLayout = (LinearLayout) rootView.findViewById(R.id.locationNotFoundLayout);
        addressNotFoundLayout = (LinearLayout) rootView.findViewById(R.id.addressNotFoundLayout);
        locationNotFoundLayout = (LinearLayout) rootView.findViewById(R.id.locationNotFoundLayout);
        locationView = (TextView) rootView.findViewById(R.id.location_view);
        commentView = (TextView) rootView.findViewById(R.id.comment_view);
        locationRefreshBtn = (ImageButton) rootView.findViewById(R.id.location_refresh_button);
        searchCityBtn = (ImageButton) rootView.findViewById(R.id.validate_city_button);
        continueBtn = (Button) rootView.findViewById(R.id.continue_button);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
         updateConcertInfo();
    }

    @Override
    public void onResume() {
        super.onResume();
        // getAddress();
        // Temporary:
        addressNotFoundLayout.setVisibility(View.INVISIBLE);
        locationNotFoundLayout.setVisibility(View.INVISIBLE);
        locationFoundLayout.setVisibility(View.VISIBLE);
        locationView.setText("Berlin");

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.putExtra(DataFetchService.LOCATION_EXTRA, "Berlin");
                getActivity().startActivity(intent);
            }
        });
    }

    // Fetching data from remote server.. concerts info from last.fm
    private void updateConcertInfo(){
        Intent intent = new Intent(getActivity(), DataFetchService.class);
        intent.putExtra(DataFetchService.LOCATION_EXTRA, "Berlin");
        getActivity().startService(intent);
    }

    public void getAddress() {
        // Ensure that a Geocoder services is available
        if (Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.GINGERBREAD
                &&
                Geocoder.isPresent()) {
            // Show the activity indicator
            progressIndicator.setVisibility(View.VISIBLE);
            /*
             * Reverse geocoding is long-running and synchronous.
             * Run it on a background thread.
             * Pass the current location to the background task.
             * When the task finishes,
             * onPostExecute() displays the address.
             */

            if (!locMgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){

                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setMessage("Concert Map needs to access your location. Please turn on location access.");
                dialog.setPositiveButton("Setting", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 100);
                    }
                });
                dialog.setNegativeButton("Ignore", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        // TODO Auto-generated method stub
                    }
                });
                dialog.show();
            }
            else {
                Location loc = locMgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                (new GetAddressTask(getActivity())).execute(loc);
            }
        }
    }

    private class GetAddressTask extends AsyncTask<Location, Void, String> {
        Context mContext;

        public GetAddressTask(Context context) {
            super();
            mContext = context;
        }

        /**
         * Get a Geocoder instance, get the latitude and longitude
         * look up the address, and return it
         *
         * @params params One or more Location objects
         * @return A string containing the address of the current
         * location, or an empty string if no address can be found,
         * or an error message
         */
        @Override
        protected String doInBackground(Location... params) {

            Geocoder geocoder =
                    new Geocoder(mContext, Locale.getDefault());
            // Get the current location from the input parameter list

            Location loc = params[0];

            if (loc != null) {

                // Create a list to contain the result address
                List<Address> addresses = null;
                try {

                 /*Return 1 address.*/

                    addresses = geocoder.getFromLocation(loc.getLatitude(),
                            loc.getLongitude(), 1);
                } catch (IOException e1) {
                    Log.e(LOG_TAG, "IO Exception in getFromLocation()");
                    e1.printStackTrace();
                    return ("IO Exception trying to get address");
                } catch (IllegalArgumentException e2) {
                    // Error message to post in the log
                    String errorString = "Illegal arguments " +
                            Double.toString(loc.getLatitude()) +
                            " , " +
                            Double.toString(loc.getLongitude()) +
                            " passed to address service";
                    Log.e(LOG_TAG, errorString);
                    e2.printStackTrace();
                    return errorString;
                }
                // If the reverse geocode returned an address
                if (addresses != null && addresses.size() > 0) {
                    // Get the first address
                    Address address = addresses.get(0);

                 /** Format the first line of address (if available),
                 city, and country name.*/

                    String addressText = String.format(
                            "%s, %s",
                            // Locality is usually a city
                            address.getLocality(),
                            // The country of the address
                            address.getCountryName());
                    // Return the City + Country
                    return addressText;

                } else {
                    // return noAddressFound;
                    return noAddressFound;
                }
            } else {
                return locationNotFound;
            }
        }

        @Override
        protected void onPostExecute(String address) {
            // Set activity indicator visibility to "gone"
            progressIndicator.setVisibility(View.GONE);
            // Display the results of the lookup.
            if (address.equals(noAddressFound)){
                locationFoundLayout.setVisibility(View.INVISIBLE);
                locationNotFoundLayout.setVisibility(View.INVISIBLE);
                addressNotFoundLayout.setVisibility(View.VISIBLE);
                commentView.setText(enterTheCity);
                searchCityBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getActivity(),
                                "Button is clicked", Toast.LENGTH_LONG).show();
                    }
                });

            } else
            if (address.equals(locationNotFound)){
                addressNotFoundLayout.setVisibility(View.INVISIBLE);
                locationFoundLayout.setVisibility(View.INVISIBLE);
                locationNotFoundLayout.setVisibility(View.VISIBLE);
                commentView.setText(locationNotFound);
                locationRefreshBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        commentView.setText(null);
                        Location loc = locMgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        (new GetAddressTask(getActivity())).execute(loc);
                    }
                });

            } else {
                addressNotFoundLayout.setVisibility(View.INVISIBLE);
                locationNotFoundLayout.setVisibility(View.INVISIBLE);
                locationFoundLayout.setVisibility(View.VISIBLE);
                locationView.setText(address);
            }

        }
    }

}
