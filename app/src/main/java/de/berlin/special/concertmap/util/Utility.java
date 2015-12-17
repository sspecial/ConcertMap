package de.berlin.special.concertmap.util;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Saeed on 11-Nov-15.
 */
public class Utility {

    // Settings keys
    public static final String PREFS_NAME = "CONCERT_MAP_PREFS";
    public static final String SETTING_CITY = "CITY";
    public static final String SETTING_LOCATION = "LOCATION";
    public static final String SETTING_EVENT_NUMBER = "EVENT_NUMBER";
    public static final String SETTING_GEO_LAT = "GEO_LAT";
    public static final String SETTING_GEO_LONG = "GEO_LONG";

    // Settings default values
    public static final double GEO_DEFAULT_LAT = 52.5194;
    public static final double GEO_DEFAULT_LONG = 13.4067;
    public static final String EVENT_LIMIT_STR = "40";
    public static final int EVENT_LIMIT_NUMBER = 20;

    // Time duration
    public static Calendar MIN_DATE;
    public static Calendar MAX_DATE;
    public static String URL_MIN_DATE = MIN_DATE_DEFAULT();
    public static String URL_MAX_DATE = MAX_DATE_DEFAULT();
    // Default Today - MIN_DATE
    public static String MIN_DATE_DEFAULT(){
        Calendar calendar = Calendar.getInstance();
        MIN_DATE = calendar;
        Date today = calendar.getTime();
        String date = new SimpleDateFormat("yyyy-MM-dd").format(today);
        return date;
    }
    // Default Tomorrow - MAX_DATE
    public static String MAX_DATE_DEFAULT(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        MAX_DATE = calendar;
        Date tomorrow = calendar.getTime();
        String date = new SimpleDateFormat("yyyy-MM-dd").format(tomorrow);
        return date;
    }

    public static final String CITY_NAME_NOT_VALID = "Please enter a valid city name.";
    public static final String CITY_IS_UNKNOWN = "City is Unknown!";
    public static String city = CITY_IS_UNKNOWN;
    public static String lastKnownLocation = CITY_IS_UNKNOWN;

    public static final String THRILLCALL_API_KEY = "d90d066add515bff";
    public static final String THRILLCALL_GEO_BASE_URL = "https://api.thrillcall.com/api/v3/events";
    public static final String THRILLCALL_ARTIST_BASE_URL = "https://api.thrillcall.com/api/v3/artist/";
    public static final String THRILLCALL_SEARCH_BASE_URL = "https://api.thrillcall.com/api/v3/search/artists/";

    public static final String ERROR_MSG = "Error obtaining data.";
    public static boolean ERROR_OBTAINING_DATA = false;
    public static final String ERROR_OBTAINING_DATA_GEO = "No data is available. Please try later!";
    public static final String ERROR_OBTAINING_DATA_ARTIST = "No data is available. Please try again!";
    public static final String ERROR_NO_DATA_GEO = "Oops, Not so much happening here..";
    public static final String ERROR_NO_DATA_ARTIST = "The name you entered didn't return any result.";

    // URL to get data from ThrillCall API
    public static final int URL_GEO_EVENTS = 0;
    public static final int URL_ARTIST_EVENTS = 1;
    public static final int URL_ARTIST_INFO = 2;
    public static final int URL_ARTIST_SEARCH = 3;

    // To decide if we want a list of geo or attended events
    public static final String FRAG_EL_GEO = "geo";
    public static final String FRAG_EL_ATTENDED = "attended";

    public static SQLiteDatabase db;
    public static SharedPreferences settings;

    public static final int CON_BELONG_TO_ARTIST_DEFAULT = -1;

    public static final int EVENT_ATTEND_YES = 1;
    public static final int EVENT_ATTEND_NO = 0;
    public static final String EVENT_ATTEND_TEXT_NO = "Attend";
    public static final String EVENT_ATTEND_TEXT_YES = "Attended!";

    public static final int ARTIST_TRACKED_YES = 1;
    public static final int ARTIST_TRACKED_NO = 0;
    public static final String ARTIST_TRACKED_TEXT_NO = "Track Artist";
    public static final String ARTIST_TRACKED_TEXT_YES = "Tracked Artist!";

    public static final String NO_TICKET_PROVIDER = "No Ticket Provider!";
    public static final String NO_ARTIST_PLAN = "No Further Plan!";
    public static final String NO_OFFICIAL_WEBSITE = "No Official Website!";

    private static final String IMAGE_BADE_DIR = "/sdcard/ConcertMap/";
    public static final String IMAGE_DIR_ARTIST = IMAGE_BADE_DIR + "TrackedArtists";
    public static final String IMAGE_DIR_EVENT = IMAGE_BADE_DIR + "AttendedEvents";
    public static final String IMAGE_DIR_DAILY = IMAGE_BADE_DIR + "DailyEvents";

    public static String imageDirToday(){
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        return IMAGE_DIR_DAILY + "/" +date;
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
    public static String[] retrieveDateAndTime(String dateStr){
        String dayStr = dateStr.split("T")[0];
        String timeStr = dateStr.split("T")[1];
        dayStr = dayStr.substring(0,dayStr.length());
        timeStr = timeStr.substring(0,timeStr.length()-4);

        return new String[]{dayStr, timeStr};
    }
}
