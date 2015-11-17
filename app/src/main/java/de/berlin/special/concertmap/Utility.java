package de.berlin.special.concertmap;

import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Saeed on 11-Nov-15.
 */
public class Utility {

    public static String city;
    public static final double GEO_DEFAULT_LAT = 52.5194;
    public static final double GEO_DEFAULT_LONG = 13.4067;
    public static final String EVENT_LIMIT = "20";
    public static final String THRILLCALL_API_KEY = "d90d066add515bff";

    // To decide if the event image folder should be kept ot not!
    public static final String FRAG_GEO_TYPE = "type";
    public static final String FRAG_GEO_ADD = "add";
    public static final String FRAG_GEO_REPLACE = "replace";

    public static SQLiteDatabase db;

    // These indices are tied to CURSOR_COLUMNS
    public static final int COL_EVENT_ID = 0;
    public static final int COL_EVENT_NAME = 1;
    public static final int COL_EVENT_START_AT = 2;
    public static final int COL_EVENT_IMAGE = 3;
    public static final int COL_EVENT_ATTEND = 4;
    public static final int COL_VENUE_NAME = 5;
    public static final int COL_VENUE_STREET = 6;
    public static final int COL_VENUE_CITY = 7;
    public static final int COL_VENUE_GEO_LAT = 8;
    public static final int COL_VENUE_GEO_LONG = 9;

    public static final int EVENT_ATTEND_YES = 1;
    public static final int EVENT_ATTEND_NO = 0;

    private static final String imageDirBase = "/sdcard/ImageDir/";
    public static String imageDirPath(){
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        return imageDirBase + date;
    }

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
                str = str.substring(0, spaceIndex) + "..";
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
                str = str.substring(0, spaceIndex) + "..";
            } else {
                str = str.substring(0, 28) + "..";
            }
            return str;
        } else {
            return str;
        }
    }

    public static String[] retrieveDateAndTime(String dateStr){
        String dayStr = dateStr.split("T")[0];
        String timeStr = dateStr.split("T")[1];
        dayStr = dayStr.substring(0,dayStr.length());
        timeStr = timeStr.substring(0,timeStr.length()-4);

        return new String[]{dayStr, timeStr};
    }
}
