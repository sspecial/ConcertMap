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

    public GetGeoInfo(Context context) {
        mContext = context;
    }

    // Obtaining lat & long for the user entry city
    public Double[] getGeoInfoFromCityName(String city){

        Double[] geoArr = null;
        try {
            Geocoder gc = new Geocoder(mContext);
            // get the found Address Objects
            List<Address> addresses = gc.getFromLocationName(city, 1);

            for (Address a : addresses) {
                if (a.hasLatitude() && a.hasLongitude()) {
                    geoArr = new Double[]{a.getLatitude(), a.getLongitude()};

                    String cityStr = a.getAddressLine(0);
                    String countryStr = a.getAddressLine(1);

                    Utility.settings.edit().putFloat(Utility.SETTING_GEO_LAT, (float) a.getLatitude()).commit();
                    Utility.settings.edit().putFloat(Utility.SETTING_GEO_LONG, (float) a.getLongitude()).commit();
                    Utility.city = cityStr;
                    Utility.lastKnownLocation = String.format("%s, %s", cityStr, countryStr);
                }
            }
        } catch (Exception e) {
            geoArr = new Double[2];
            Utility.city = Utility.CITY_IS_UNKNOWN;
            Utility.lastKnownLocation = Utility.CITY_IS_UNKNOWN;
        }
        return geoArr;
    }
}
