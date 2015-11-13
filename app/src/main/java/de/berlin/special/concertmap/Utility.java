package de.berlin.special.concertmap;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Saeed on 11-Nov-15.
 */
public class Utility {

    public static final double GEO_DEFAULT_LAT = 52.5194;
    public static final double GEO_DEFAULT_LONG = 13.4067;

    // These indices are tied to CURSOR_COLUMNS
    public static final int COL_EVENT_ID = 0;
    public static final int COL_EVENT_NAME = 1;
    public static final int COL_EVENT_START_AT = 2;
    public static final int COL_EVENT_IMAGE = 3;
    public static final int COL_VENUE_NAME = 4;
    public static final int COL_VENUE_STREET = 5;
    public static final int COL_VENUE_CITY = 6;
    public static final int COL_VENUE_GEO_LAT = 7;
    public static final int COL_VENUE_GEO_LONG = 8;

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
            return str.replaceFirst(" ", "\n");
        } else {
            return str;
        }
    }
    // To make a break when the venue name is too long
    public static String venueNamePartition(String str){
        if (str.length() > 28) {
            return str.replaceFirst(" ", "\n");
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
