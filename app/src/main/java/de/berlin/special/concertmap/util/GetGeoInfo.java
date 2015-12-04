package de.berlin.special.concertmap.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.view.ContextThemeWrapper;

import java.util.HashMap;
import java.util.List;

import de.berlin.special.concertmap.R;

/**
 * Created by Saeed on 03-Dec-15.
 */
public class GetGeoInfo {

    private Context mContext;
    private Double[] geoArr;

    public GetGeoInfo(Context context) {
        mContext = context;
    }

    // Obtaining lat & long for the user entry city
    public Double[] getGeoInfoFromCityName(String city, int maxResults){

        try {
            Geocoder gc = new Geocoder(mContext);
            // get the found Address Objects
            List<Address> addresses = gc.getFromLocationName(city, maxResults);

            if (addresses.size() == 1) {
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

                        geoArr = new Double[]{a.getLatitude(), a.getLongitude()};
                        Utility.settings.edit().putFloat(Utility.SETTING_GEO_LAT, (float) a.getLatitude()).commit();
                        Utility.settings.edit().putFloat(Utility.SETTING_GEO_LONG, (float) a.getLongitude()).commit();
                        Utility.city = cityStr;
                        Utility.lastKnownLocation = String.format("%s, %s", cityStr, countryStr);
                    }
                }
            }

            if (addresses.size() > 1) {

                final HashMap<String, Address> cityArr = new HashMap<String, Address>();

                for (Address a : addresses) {

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

                    cityArr.put(String.format("%s, %s", cityStr, countryStr), a);
                }

                final String[] keyArray = cityArr.keySet().toArray(new String[cityArr.size()]);

                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(mContext, android.R.style.Theme_Holo_Light_Dialog));
                builder.setTitle(R.string.choose_ticket_provider)
                        .setItems(keyArray, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                Address a = cityArr.get(keyArray[which]);
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

                                    geoArr = new Double[]{a.getLatitude(), a.getLongitude()};
                                    Utility.settings.edit().putFloat(Utility.SETTING_GEO_LAT, (float) a.getLatitude()).commit();
                                    Utility.settings.edit().putFloat(Utility.SETTING_GEO_LONG, (float) a.getLongitude()).commit();
                                    Utility.city = cityStr;
                                    Utility.lastKnownLocation = String.format("%s, %s", cityStr, countryStr);
                                }
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }

        } catch (Exception e) {
            geoArr = new Double[2];
            Utility.city = Utility.CITY_IS_UNKNOWN;
            Utility.lastKnownLocation = Utility.CITY_IS_UNKNOWN;
        }
        return geoArr;
    }
}
