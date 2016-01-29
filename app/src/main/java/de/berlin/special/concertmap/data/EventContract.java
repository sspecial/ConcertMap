package de.berlin.special.concertmap.data;

import android.provider.BaseColumns;

/**
 * Created by Saeed on 16-Apr-15.
 */
public class EventContract {

    /* Inner class that defines the table contents of the event table */
    public static final class EventEntry implements BaseColumns {

        public static final String TABLE_NAME = "event";
        // Information of event:
        public static final String COLUMN_CON_API_ID = "event_API_ID";
        public static final String COLUMN_CON_NAME = "event_name";
        public static final String COLUMN_CON_START_AT = "event_start_at";
        public static final String COLUMN_CON_TICKET = "event_ticket";
        public static final String COLUMN_CON_IMAGE = "event_image";
        public static final String COLUMN_CON_ATTEND = "event_attended";
        public static final String COLUMN_CON_BELONG_TO_ARTIST = "event_belong_to_artist";
    }

    /* Inner class that defines the table contents of the artist table */
    public static final class ArtistEntry implements BaseColumns {

        // Table name
        public static final String TABLE_NAME = "artists";
        // Information of artists:
        public static final String COLUMN_ART_CON_ID = "event_ID";
        public static final String COLUMN_ART_API_ID = "artist_API_ID";
        public static final String COLUMN_ART_NAME = "artist_name";
        public static final String COLUMN_ART_IMAGE = "artist_image";
    }

    /* Inner class that defines the table contents of the location table */
    public static final class VenueEntry implements BaseColumns {

        // Table name
        public static final String TABLE_NAME = "venue";
        // Information of venue:
        public static final String COLUMN_VEN_CON_ID = "event_ID";
        public static final String COLUMN_VEN_API_ID = "venue_API_ID";
        public static final String COLUMN_VEN_NAME = "venue_name";
        public static final String COLUMN_VEN_STREET = "venue_street";
        public static final String COLUMN_VEN_CITY = "venue_city";
        public static final String COLUMN_VEN_COUNTRY = "venue_country";
        public static final String COLUMN_VEN_LOCATION = "venue_location";
        public static final String COLUMN_VEN_GEO_LAT = "venue_geo_lat";
        public static final String COLUMN_VEN_GEO_LONG = "venue_geo_long";
    }

    /* Inner class that defines the table contents of the favorite artist table */
    public static final class FavArtistEntry implements BaseColumns {

        // Table name
        public static final String TABLE_NAME = "artist";
        // Information of artists:
        public static final String COL_FAV_ART_API_ID = "artist_API_ID";
        public static final String COL_FAV_ART_NAME = "artist_name";
        public static final String COL_FAV_ART_API_URL = "artist_API_url";
        public static final String COL_FAV_ART_IMAGE = "artist_image";
        public static final String COL_FAV_ART_UPCOMING_EVENTS = "artist_upcoming_events";
        public static final String COL_FAV_ART_TRACKED = "artist_tracked";
    }

    /* Inner class that defines the table contents of the artist-links table */
    public static final class LinkEntry implements BaseColumns {

        // Table name
        public static final String TABLE_NAME = "links";
        // Information of artists:
        public static final String COLUMN_LINK_ART_ID = "artist_ID";
        public static final String COLUMN_LINK_PROVIDER = "link_provider";
        public static final String COLUMN_LINK_URL = "link_url";
    }
}
