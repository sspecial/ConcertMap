package de.berlin.special.concertmap.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Saeed on 16-Apr-15.
 */
public class EventContract {

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "de.berlin.special.concertmap";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_EVENT = "event";
    public static final String PATH_LOCATION = "location";

    /* Inner class that defines the table contents of the location table */
    public static final class LocationEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;

        // Table name
        public static final String TABLE_NAME = "location";

        public static final String COLUMN_LOC_NAME = "loc_name";
        public static final String COLUMN_LOC_CITY = "city_name";
        public static final String COLUMN_LOC_COUNTRY = "country_name";
        public static final String COLUMN_LOC_STREET = "street_address";
        public static final String COLUMN_LOC_POSTAL_CODE = "postal_code";
        public static final String COLUMN_LOC_WEB = "loc_web";

        // In order to uniquely pinpoint the location on the map when we launch the
        // map intent, we store the latitude and longitude as returned by last.fm.
        public static final String COLUMN_LOC_GEO_LAT = "geo_lat";
        public static final String COLUMN_LOC_GEO_LONG = "geo_long";

        public static Uri buildLocationUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class EventEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_EVENT).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_EVENT;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_EVENT;

        public static final String TABLE_NAME = "event";

        // Column with the foreign key into the location table.
        public static final String COLUMN_LOC_KEY = "location_id";

        // Information of event:
        public static final String COLUMN_EVENT_TITLE = "event_title";
        public static final String COLUMN_EVENT_ARTIST = "event_artist";
        public static final String COLUMN_EVENT_ARTIST_WEB = "event_artist_web";
        public static final String COLUMN_EVENT_IMAGE = "event_image";
        public static final String COLUMN_EVENT_DESC = "event_desc";
        // Date, stored as Text with format yyyy-MM-dd
        public static final String COLUMN_START_DATE = "start_date";

        public static Uri buildEventUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildEventLocation(String locationSetting) {
            return CONTENT_URI.buildUpon().appendPath(locationSetting).build();
        }
    }
}
