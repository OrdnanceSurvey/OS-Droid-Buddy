package uk.co.ordnancesurvey.droidcon2013.android.content;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class GeoTweetProvider extends ContentProvider {

    private static final String DATABASE_NAME = "tweets.db";
    private static final String UNSUPPORTED_URI = "Unsupported URI: ";

    private static final int DATABASE_VERSION = 1;

    private static final int TWEET = 1;
    private static final int TWEET_ID = 2;

    private SQLiteDatabase mDatabase;

    private static final UriMatcher URI_MATCHER;

    static {

        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

        URI_MATCHER.addURI(GeoTweetContract.AUTHORITY, "tweets/", TWEET);
        URI_MATCHER.addURI(GeoTweetContract.AUTHORITY, "tweets/#", TWEET_ID);
    }

    @Override
    public boolean onCreate() {

        DatabaseHelper dbHelper = new DatabaseHelper(getContext());

        mDatabase = dbHelper.getWritableDatabase();

        return (mDatabase == null) ? false : true;
    }

    @Override
    public String getType(Uri uri) {

        switch (URI_MATCHER.match(uri)) {

            case TWEET:
                return GeoTweetContract.Tweets.CONTENT_TYPE;
            case TWEET_ID:
                return GeoTweetContract.Tweets.CONTENT_ITEMTYPE;
            default: throw new IllegalArgumentException(UNSUPPORTED_URI + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        final ContentValues inputValues;

        if (values == null) {
            inputValues = new ContentValues();
        } else {
            inputValues = values;
        }

        switch (URI_MATCHER.match(uri)) {
            case TWEET_ID:
                // fall through
            case TWEET:
                return insertTweet(inputValues);
            default: throw new IllegalArgumentException(UNSUPPORTED_URI + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        switch (URI_MATCHER.match(uri)) {

            case TWEET_ID:
                qb.appendWhere(GeoTweetContract.Tweets._ID + " = " + uri.getLastPathSegment());
                // fall through
            case TWEET:
                qb.setTables(GeoTweetContract.Tweets.Table.NAME);
                break;
            default: throw new IllegalArgumentException(UNSUPPORTED_URI + uri);
        }

        Cursor c = qb.query(mDatabase, projection, selection, selectionArgs, null, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);

        return c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count;
        switch (URI_MATCHER.match(uri)) {
            case TWEET:
                count = mDatabase.update(GeoTweetContract.Tweets.Table.NAME, values, selection,
                        selectionArgs);
                break;
            case TWEET_ID:
                String segment = uri.getPathSegments().get(1);
                count = mDatabase.update(GeoTweetContract.Tweets.Table.NAME, values,
                        buildSelection(GeoTweetContract.Tweets._ID, segment, selection), selectionArgs);
                break;
            default: throw new IllegalArgumentException(UNSUPPORTED_URI + uri);
        }
        return count;
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        int count;

        switch (URI_MATCHER.match(uri)) {
            case TWEET: {
                count = mDatabase.delete(GeoTweetContract.Tweets.Table.NAME, where, whereArgs);
                break;
            }
            case TWEET_ID: {
                String segment = uri.getLastPathSegment();
                count = mDatabase.delete(GeoTweetContract.Tweets.Table.NAME,
                        buildWhereClause(GeoTweetContract.Tweets._ID, segment, where), whereArgs);
                break;
            }
            default:
                throw new IllegalArgumentException(UNSUPPORTED_URI + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    private Uri insertTweet(ContentValues values) {
        return insert(GeoTweetContract.Tweets.CONTENT_URI, GeoTweetContract.Tweets.Table.NAME, values);
    }

    private Uri insert(Uri contentUri, String tableName, ContentValues values) {

        long rowId = mDatabase.insert(tableName, "nullColumn", values);

        if (rowId > 0) {
            Uri uri = ContentUris.withAppendedId(contentUri, rowId);
            getContext().getContentResolver().notifyChange(uri, null);
            return uri;
        }
        throw new SQLiteException("Failed to insert row into " + tableName
                + bracket(contentUri.toString()));
    }

    private String buildSelection(String id, String segment, String where) {
        return id + " = " + segment + (TextUtils.isEmpty(where) ? "" : " AND " + bracket(where));
    }

    private String buildWhereClause(String id, String segment, String where) {
        return "_id = " + segment + (TextUtils.isEmpty(where) ? "" : " AND " + bracket(where));
    }

    private String bracket(String string) {
        return "(" + string + ")";
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(GeoTweetContract.Tweets.Table.Sql.CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w("DatabaseHelper", "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");

            db.execSQL("DROP TABLE IF EXISTS " + GeoTweetContract.Tweets.Table.NAME);

            onCreate(db);
        }
    }
}
