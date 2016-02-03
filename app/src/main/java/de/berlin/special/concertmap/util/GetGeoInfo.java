package de.berlin.special.concertmap.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;

import java.util.List;

/**
 * Created by Saeed on 03-Dec-15.
 */
public class GetGeoInfo {

    private Context mContext;
    private SharedPreferences settings;
    public Double[] geoArr = new Double[2];

    public GetGeoInfo(Context context) {
        mContext = context;
        settings = mContext.getSharedPreferences(Utility.PREFS_NAME, Context.MODE_PRIVATE);
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

                        // Adding setting to shared preferences
                        settings.edit().putString(Utility.SETTING_CITY, cityStr).commit();
                        settings.edit().putString(Utility.SETTING_LOCATION, String.format("%s, %s", cityStr, countryStr)).commit();
                        settings.edit().putFloat(Utility.SETTING_GEO_LAT, (float) a.getLatitude()).commit();
                        settings.edit().putFloat(Utility.SETTING_GEO_LONG, (float) a.getLongitude()).commit();
                    }
                }

        } catch (Exception e) {
            settings.edit().putString(Utility.SETTING_CITY, Utility.CITY_IS_UNKNOWN).commit();
            settings.edit().putString(Utility.SETTING_LOCATION, Utility.CITY_IS_UNKNOWN).commit();
        }
        return geoArr;
    }
}
