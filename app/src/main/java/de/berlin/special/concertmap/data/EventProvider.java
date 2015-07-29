package de.berlin.special.concertmap.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
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
    private static final int EVENT_ID = 101;
    private static final int EVENT_WITH_LOCATION = 102;
    private static final int EVENT_WITH_LOCATION_AND_DATE = 103;
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
        matcher.addURI(authority, EventContract.PATH_EVENT + "/#", EVENT_ID);
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
            selectionArgs = new String[]{locationSetting, startDate};
            selection = sLocationSettingWithStartDateSelection;
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
            // "event/#"
            case EVENT_ID: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        EventContract.EventEntry.TABLE_NAME,
                        projection,
                        EventContract.EventEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "event/*"
            case EVENT_WITH_LOCATION: {
                retCursor = getEventByLocationSetting(uri, projection, sortOrder);
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
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
