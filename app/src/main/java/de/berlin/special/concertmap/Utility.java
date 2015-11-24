package de.berlin.special.concertmap;

import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Saeed on 11-Nov-15.
 */
public class Utility {

    public static final String CITY_IS_UNKNOWN = "City is Unknown!";
    public static String city = CITY_IS_UNKNOWN;

    public static final double GEO_DEFAULT_LAT = 52.5194;
    public static final double GEO_DEFAULT_LONG = 13.4067;
    public static final String EVENT_LIMIT = "20";
    public static final String THRILLCALL_API_KEY = "d90d066add515bff";
    public static final String THRILLCALL_GEO_BASE_URL = "https://api.thrillcall.com/api/v3/events";
    public static final String THRILLCALL_ARTIST_BASE_URL = "https://api.thrillcall.com/api/v3/artist/";

    // To decide if we want a list of geo or attended events
    public static final String FRAG_EL_GEO = "geo";
    public static final String FRAG_EL_ATTENDED = "attended";

    // URL to get data from ThrillCall API
    public static final int URL_GEO_EVENTS = 0;
    public static final int URL_ARTIST_EVENTS = 1;
    public static final int URL_ARTIST_INFO = 2;

    public static SQLiteDatabase db;

    public static final int CON_BELONG_TO_ARTIST_DEFAULT = -1;

    public static final int EVENT_ATTEND_YES = 1;
    public static final int EVENT_ATTEND_NO = 0;

    public static final int ARTIST_TRACKED_YES = 1;
    public static final int ARTIST_TRACKED_NO = 0;

    public static final String eventQueryStr = "SELECT event._ID, " +
            "event.event_thrill_ID, event.event_name, " +
            "event.event_start_at, event.event_image, event.event_attended, " +
            "venue.venue_name, venue.venue_street, venue.venue_city, " +
            "venue.venue_geo_lat, venue.venue_geo_long " +
            "FROM event " +
            "INNER JOIN venue " +
            "ON event._ID = venue.event_ID ";
    // These indices are tied to EVENT CURSOR_COLUMNS
    public static final int COL_EVENT_ID = 0;
    public static final int COL_EVENT_THRILL_ID = 1;
    public static final int COL_EVENT_NAME = 2;
    public static final int COL_EVENT_START_AT = 3;
    public static final int COL_EVENT_IMAGE = 4;
    public static final int COL_EVENT_ATTEND = 5;
    public static final int COL_VENUE_NAME = 6;
    public static final int COL_VENUE_STREET = 7;
    public static final int COL_VENUE_CITY = 8;
    public static final int COL_VENUE_GEO_LAT = 9;
    public static final int COL_VENUE_GEO_LONG = 10;

    public static final String artistQueryStr = "SELECT artists._ID, " +
            "artists.artist_thrill_ID, artists.artist_name " +
            "FROM artists ";
    public static final String favArtistQueryStr = "SELECT artist._ID, " +
            "artist.artist_thrill_ID, artist.artist_name, " +
            "artist.artist_official_url, artist.artist_image_large, " +
            "artist.artist_image_mobile, artist.artist_tracked " +
            "FROM artist ";
    // These indices are tied to FAV-ARTIST CURSOR_COLUMNS
    public static final int COL_ARTIST_ID = 0;
    public static final int COL_ARTIST_THRILL_ID = 1;
    public static final int COL_ARTIST_NAME = 2;
    public static final int COL_ARTIST_OFFICIAL_URL = 3;
    public static final int COL_ARTIST_IMAGE_LARGE = 4;
    public static final int COL_ARTIST_IMAGE_MOBILE = 5;
    public static final int COL_ARTIST_TRACKED = 6;

    private static final String imageDirBase = "/sdcard/ImageDir/";
    public static String imageDirPath(){
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        return imageDirBase + date;
    }

    // To get Artist-name out of Event-name
    public static String retrieveArtistName(String eventName){
        String artNames = null;
        int beginIndex = 0;
        int endIndex = eventName.indexOf("@");
        if(endIndex != -1)
            artNames = eventName.substring(beginIndex, endIndex);
        return artNames;
    }
    // To make a break when the artist name is too long
    public static String artistNamePartition(String str){
        if (str.length() > 18) {
            int spaceIndex = str.indexOf(" ", 10);
            if (spaceIndex != -1)
                str = str.substring(0, spaceIndex) + " ..";
            else
                str = str.substring(0, 18) + "..";
            return str;
        } else {
            return str;
        }
    }
    // To make a break when the venue name is too long
    public static String venueNamePartition(String str){
        if (str.length() > 28) {
            int spaceIndex = str.indexOf(" ", 20);
            if (spaceIndex != -1) {
                str = str.substring(0, spaceIndex) + " ..";
            } else {
                str = str.substring(0, 28) + "..";
            }
            return str;
        } else {
            return str;
        }
    }
    // To divide Date-str into Day-str and Time-str
    public static String retrieveDateAndTime(String dateStr){
        String dayStr = dateStr.split("T")[0];
        String timeStr = dateStr.split("T")[1];
        dayStr = dayStr.substring(0,dayStr.length());
        timeStr = timeStr.substring(0,timeStr.length()-4);

        return ("On " + dayStr + ", At " + timeStr);
    }
}
