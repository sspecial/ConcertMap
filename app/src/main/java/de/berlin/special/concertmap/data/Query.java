package de.berlin.special.concertmap.data;

/**
 * Created by Saeed on 30-Nov-15.
 */
public class Query {

    public static final String eventQueryStr = "SELECT event._ID, " +
            "event.event_API_ID, event.event_name, " +
            "event.event_start_at, event.event_url, " +
            "event.event_image, event.event_attended, " +
            "venue.venue_name, venue.venue_street, " +
            "venue.venue_city, venue.venue_country, " +
            "venue.venue_geo_lat, venue.venue_geo_long " +
            "FROM event " +
            "INNER JOIN venue " +
            "ON event._ID = venue.event_ID ";
    // These indices are tied to EVENT CURSOR_COLUMNS
    public static final int COL_EVENT_ID = 0;
    public static final int COL_EVENT_THRILL_ID = 1;
    public static final int COL_EVENT_NAME = 2;
    public static final int COL_EVENT_START_AT = 3;
    public static final int COL_EVENT_THRILL_URL = 4;
    public static final int COL_EVENT_IMAGE = 5;
    public static final int COL_EVENT_ATTEND = 6;
    public static final int COL_VENUE_NAME = 7;
    public static final int COL_VENUE_STREET = 8;
    public static final int COL_VENUE_CITY = 9;
    public static final int COL_VENUE_COUNTRY_CODE = 10;
    public static final int COL_VENUE_GEO_LAT = 11;
    public static final int COL_VENUE_GEO_LONG = 12;

    public static final String artistQueryStr = "SELECT artists._ID, " +
            "artists.artist_thrill_ID, artists.artist_name " +
            "FROM artists ";
    public static final String favArtistQueryStr = "SELECT artist._ID, " +
            "artist.artist_thrill_ID, artist.artist_name, " +
            "artist.artist_official_url, " +
            "artist.artist_wikipedia_url, " +
            "artist.artist_thrill_url, " +
            "artist.artist_image_mobile, artist.artist_tracked " +
            "FROM artist ";
    // These indices are tied to FAV-ARTIST CURSOR_COLUMNS
    public static final int COL_ARTIST_ID = 0;
    public static final int COL_ARTIST_THRILL_ID = 1;
    public static final int COL_ARTIST_NAME = 2;
    public static final int COL_ARTIST_OFFICIAL_URL = 3;
    public static final int COL_ARTIST_WIKIPEDIA_URL = 4;
    public static final int COL_ARTIST_THRILL_URL = 5;
    public static final int COL_ARTIST_IMAGE_MOBILE = 6;
    public static final int COL_ARTIST_TRACKED = 7;

    public static final String ticketQueryStr = "SELECT tickets._ID, " +
            "tickets.ticket_name, tickets.ticket_url " +
            "FROM tickets ";
    // These indices are tied to TICKETS CURSOR_COLUMNS
    public static final int COL_TICKET_ID = 0;
    public static final int COL_TICKET_NAME = 1;
    public static final int COL_TICKET_URL = 2;
}
