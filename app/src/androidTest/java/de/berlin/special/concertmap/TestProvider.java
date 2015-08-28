package de.berlin.special.concertmap;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.test.AndroidTestCase;

import de.berlin.special.concertmap.data.EventContract.EventEntry;
import de.berlin.special.concertmap.data.EventContract.LocationEntry;

/**
 * Created by Saeed on 09-Aug-15.
 */
public class TestProvider extends AndroidTestCase {

    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    // brings our database to an empty state
    public void deleteAllRecords() {

        mContext.getContentResolver().delete(
                EventEntry.CONTENT_URI,
                null,
                null
        );
        mContext.getContentResolver().delete(
                LocationEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                EventEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals(0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                LocationEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals(0, cursor.getCount());
        cursor.close();
    }

    // Since we want each test to start with a clean slate, run deleteAllRecords
    // in setUp (called by the test runner before each test).
    public void setUp() {
        deleteAllRecords();
    }

    public void testInsertReadProvider() {

        ContentValues testValues = TestDb.createLocationTestValues();
        // testValues = "country_name=locCountry city_name=locCity postal_code=locPostalCode location_setting=Berlin geo_long=-147.353 geo_lat=64.7488 street_address=locStreet loc_web=locWeb loc_name=locName"

        Uri locationUri = mContext.getContentResolver().insert(LocationEntry.CONTENT_URI, testValues);  // locationUri = content://de.berlin.special.concertmap/location/1
        long locationRowId = ContentUris.parseId(locationUri);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                LocationEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestDb.validateCursor(cursor, testValues);

        // Now see if we can successfully query if we include the row id
        cursor = mContext.getContentResolver().query(
                LocationEntry.buildLocationUri(locationRowId),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestDb.validateCursor(cursor, testValues);

        // Fantastic.  Now that we have a location, add some Event!
        ContentValues EventValues = TestDb.createEventTestValues(locationRowId);
        // EventValues = "event_artist_web=artistWeb location_id=2 event_title=eventTitle event_artist=artists start_date=20150809 event_desc=description event_image=image"

        Uri EventInsertUri = mContext.getContentResolver()  // EventInsertUri = content://de.berlin.special.concertmap/event/12
                .insert(EventEntry.CONTENT_URI, EventValues);
        assertTrue(EventInsertUri != null);

        // A cursor is your primary interface to the query results.
        Cursor EventCursor = mContext.getContentResolver().query(
                EventEntry.CONTENT_URI,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // columns to group by
        );

        TestDb.validateCursor(EventCursor, EventValues);


        // Add the location values in with the Event data so that we can make
        // sure that the join worked and we actually get all the values back
        addAllContentValues(EventValues, testValues);

        Uri eventLocationUri = EventEntry.buildEventLocation(TestDb.TEST_LOCATION); // eventLocationUri = content://de.berlin.special.concertmap/event/Berlin
        // Get the joined Event and Location data
        EventCursor = mContext.getContentResolver().query(
                eventLocationUri,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        TestDb.validateCursor(EventCursor, EventValues);

        Uri eventLocationWithStartDateUri = EventEntry.buildEventLocationWithStartDate(
                TestDb.TEST_LOCATION, TestDb.TEST_START_DATE);  // eventLocationWithStartDateUri = content://de.berlin.special.concertmap/event/Berlin?start_date=20150809
        // Get the joined Event and Location data with a start date
        EventCursor = mContext.getContentResolver().query(
                eventLocationWithStartDateUri,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        TestDb.validateCursor(EventCursor, EventValues);

        Uri eventLocationWithDate = EventEntry.buildEventLocationWithDate(TestDb.TEST_LOCATION, TestDb.TEST_START_DATE);  // eventLocationWithDate = content://de.berlin.special.concertmap/event/Berlin/20150809
        // Get the joined Event data for a specific date
        EventCursor = mContext.getContentResolver().query(
                eventLocationWithDate,
                null,
                null,
                null,
                null
        );
        TestDb.validateCursor(EventCursor, EventValues);
    }

    // The target api annotation is needed for the call to keySet -- we wouldn't want
    // to use this in our app, but in a test it's fine to assume a higher target.
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    void addAllContentValues(ContentValues destination, ContentValues source) {
        for (String key : source.keySet()) {
            destination.put(key, source.getAsString(key));
        }
    }

}
