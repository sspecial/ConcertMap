package de.berlin.special.concertmap;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import java.util.Map;
import java.util.Set;

import de.berlin.special.concertmap.data.EventContract;
import de.berlin.special.concertmap.data.EventContract.EventEntry;
import de.berlin.special.concertmap.data.EventContract.LocationEntry;
import de.berlin.special.concertmap.data.EventDbHelper;

public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();
    static final String TEST_LOCATION = "99705";
    static final String TEST_DATE = "20141205";

    public void testCreateDb() throws Throwable {
        mContext.deleteDatabase(EventDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new EventDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();
    }

    public void testInsertReadDb() {

        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        EventDbHelper dbHelper = new EventDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = createLocationTestValues();

        long locationRowId;
        locationRowId = db.insert(LocationEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New row id: " + locationRowId);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = db.query(
                LocationEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        validateCursor(cursor, testValues);

        // Fantastic.  Now that we have a location, add some weather!
        ContentValues concertValues = createconcertValues(locationRowId);

        long weatherRowId = db.insert(EventEntry.TABLE_NAME, null, concertValues);
        assertTrue(weatherRowId != -1);

        // A cursor is your primary interface to the query results.
        Cursor concertCursor = db.query(
                EventEntry.TABLE_NAME,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null  // sort order
        );

        validateCursor(concertCursor, concertValues);

        dbHelper.close();
    }

    static ContentValues createconcertValues(long locationRowId) {
        ContentValues concertValues = new ContentValues();
        concertValues.put(EventEntry.COLUMN_LOC_KEY, locationRowId);
        concertValues.put(EventContract.EventEntry.COLUMN_EVENT_TITLE, "eventTitle");
        concertValues.put(EventContract.EventEntry.COLUMN_EVENT_ARTIST, "artists");
        concertValues.put(EventContract.EventEntry.COLUMN_EVENT_ARTIST_WEB, "artistWeb");
        concertValues.put(EventContract.EventEntry.COLUMN_EVENT_IMAGE, "image");
        concertValues.put(EventContract.EventEntry.COLUMN_EVENT_DESC, "description");
        concertValues.put(EventContract.EventEntry.COLUMN_START_DATE, "startDate");

        return concertValues;
    }

    static ContentValues createLocationTestValues() {
        // Create a new map of values, where column names are the keys
        ContentValues locationValues = new ContentValues();

        locationValues.put(EventContract.LocationEntry.COLUMN_LOC_NAME, "locName");
        locationValues.put(EventContract.LocationEntry.COLUMN_LOC_CITY, "locCity");
        locationValues.put(EventContract.LocationEntry.COLUMN_LOC_COUNTRY, "locCountry");
        locationValues.put(EventContract.LocationEntry.COLUMN_LOC_STREET, "locStreet");
        locationValues.put(EventContract.LocationEntry.COLUMN_LOC_POSTAL_CODE, "locPostalCode");
        locationValues.put(EventContract.LocationEntry.COLUMN_LOC_WEB, "locWeb");
        locationValues.put(EventContract.LocationEntry.COLUMN_LOC_GEO_LAT, 64.7488);
        locationValues.put(EventContract.LocationEntry.COLUMN_LOC_GEO_LONG, -147.353);

        return locationValues;
    }

    static void validateCursor(Cursor valueCursor, ContentValues expectedValues) {

        assertTrue(valueCursor.moveToFirst());

        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse(idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals(expectedValue, valueCursor.getString(idx));
        }
        valueCursor.close();
    }
}
