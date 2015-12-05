package de.berlin.special.concertmap.util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.util.List;

/**
 * Created by Saeed on 03-Dec-15.
 */
public class GetGeoInfo {

    private Context mContext;
    public Double[] geoArr = new Double[2];

    public GetGeoInfo(Context context) {
        mContext = context;
    }

    // Obtaining lat & long for the user entry city
    public Double[] getGeoInfoFromCityName(String city){

        try {
            Geocoder gc = new Geocoder(mContext);
            // get the found Address Objects
            List<Address> addresses = gc.getFromLocationName(city, 1);

                for (Address a : addresses) {
                    if (a.hasLatitude() && a.hasLongitude()) {

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

                        geoArr[0] = a.getLatitude();
                        geoArr[1] = a.getLongitude();

                        Utility.city = cityStr;
                        Utility.lastKnownLocation = String.format("%s, %s", cityStr, countryStr);
                    }
                }

        } catch (Exception e) {
            Utility.city = Utility.CITY_IS_UNKNOWN;
            Utility.lastKnownLocation = Utility.CITY_IS_UNKNOWN;
        }
        return geoArr;
    }
}
