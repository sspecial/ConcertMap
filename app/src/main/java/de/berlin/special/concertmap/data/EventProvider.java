package de.berlin.special.concertmap.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by Saeed on 29-Jul-15.
 */
public class EventProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private EventDbHelper mOpenHelper;

    private static final int EVENT = 100;
    private static final int EVENT_WITH_LOCATION = 101;
    private static final int EVENT_WITH_LOCATION_AND_DATE = 102;
    private static final int LOCATION = 300;
    private static final int LOCATION_ID = 301;

    private static final SQLiteQueryBuilder sEventByLocationSettingQueryBuilder;

    static{
        sEventByLocationSettingQueryBuilder = new SQLiteQueryBuilder();
        sEventByLocationSettingQueryBuilder.setTables(
                EventContract.EventEntry.TABLE_NAME + " INNER JOIN " +
                        EventContract.LocationEntry.TABLE_NAME +
                        " ON " + EventContract.EventEntry.TABLE_NAME +
                        "." + EventContract.EventEntry.COLUMN_LOC_KEY +
                        " = " + EventContract.LocationEntry.TABLE_NAME +
                        "." + EventContract.LocationEntry._ID);
    }

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = EventContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, EventContract.PATH_EVENT, EVENT);
        matcher.addURI(authority, EventContract.PATH_EVENT + "/*", EVENT_WITH_LOCATION);
        matcher.addURI(authority, EventContract.PATH_EVENT + "/*/*", EVENT_WITH_LOCATION_AND_DATE);
        matcher.addURI(authority, EventContract.PATH_LOCATION, LOCATION);
        matcher.addURI(authority, EventContract.PATH_LOCATION + "/#", LOCATION_ID);

        return matcher;
    }

    private Cursor getEventByLocationSetting(Uri uri, String[] projection, String sortOrder) {

        final String sLocationSettingSelection =
                EventContract.LocationEntry.TABLE_NAME+
                        "." + EventContract.LocationEntry.COLUMN_LOC_SETTING + " = ? ";
        final String sLocationSettingWithStartDateSelection =
                EventContract.LocationEntry.TABLE_NAME+
                        "." + EventContract.LocationEntry.COLUMN_LOC_SETTING + " = ? AND " +
                        EventContract.EventEntry.COLUMN_EVENT_START_DATE + " >= ? ";

        String locationSetting = EventContract.EventEntry.getLocationSettingFromUri(uri);
        String startDate = EventContract.EventEntry.getStartDateFromUri(uri);

        String[] selectionArgs;
        String selection;

        if (startDate == null) {
            selection = sLocationSettingSelection;
            selectionArgs = new String[]{locationSetting};
        } else {
            selection = sLocationSettingWithStartDateSelection;
            selectionArgs = new String[]{locationSetting, startDate};
        }

        return sEventByLocationSettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getEventByLocationSettingAndDate(
            Uri uri, String[] projection, String sortOrder) {

        final String sLocationSettingAndDaySelection =
                EventContract.LocationEntry.TABLE_NAME +
                        "." + EventContract.LocationEntry.COLUMN_LOC_SETTING + " = ? AND " +
                        EventContract.EventEntry.COLUMN_EVENT_START_DATE + " = ? ";

        String locationSetting = EventContract.EventEntry.getLocationSettingFromUri(uri);
        String date = EventContract.EventEntry.getDateFromUri(uri);

        return sEventByLocationSettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sLocationSettingAndDaySelection,
                new String[]{locationSetting, date},
                null,
                null,
                sortOrder
        );
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new EventDbHelper(getContext());
        return (mOpenHelper == null)? false:true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "event"
            case EVENT:
            {
                retCursor = mOpenHelper.getReadableDatabase().query(
                    EventContract.EventEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
                );
                break;
            }
            // "event/*"
            case EVENT_WITH_LOCATION:
            {
                retCursor = getEventByLocationSetting(uri, projection, sortOrder);
                break;
            }
            // "Event/*/*"
            case EVENT_WITH_LOCATION_AND_DATE:
            {
                retCursor = getEventByLocationSettingAndDate(uri, projection, sortOrder);
                break;
            }
            // "location"
            case LOCATION:
            {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        EventContract.LocationEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "location/#"
            case LOCATION_ID: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        EventContract.LocationEntry.TABLE_NAME,
                        projection,
                        EventContract.LocationEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public String getType(Uri uri) {
        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case EVENT_WITH_LOCATION_AND_DATE:
                return EventContract.EventEntry.CONTENT_ITEM_TYPE;
            case EVENT_WITH_LOCATION:
                return EventContract.EventEntry.CONTENT_TYPE;
            case EVENT:
                return EventContract.EventEntry.CONTENT_TYPE;
            case LOCATION:
                return EventContract.LocationEntry.CONTENT_TYPE;
            case LOCATION_ID:
                return EventContract.LocationEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case EVENT: {
                long _id = db.insert(EventContract.EventEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = EventContract.EventEntry.buildEventUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case LOCATION: {
                long _id = db.insert(EventContract.LocationEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = EventContract.LocationEntry.buildLocationUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        switch (match) {
            case EVENT:
                rowsDeleted = db.delete(
                        EventContract.EventEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case LOCATION:
                rowsDeleted = db.delete(
                        EventContract.LocationEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (selection == null || rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case EVENT:
                rowsUpdated = db.update(EventContract.EventEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case LOCATION:
                rowsUpdated = db.update(EventContract.LocationEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
