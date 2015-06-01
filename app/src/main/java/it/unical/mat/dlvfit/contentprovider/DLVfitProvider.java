package it.unical.mat.dlvfit.contentprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.HashMap;

/**
 * Created by Dario Campisano on 14/04/2015.
 */

public class DLVfitProvider extends ContentProvider{
    //To query our content provider, we need to specify the query string in the form of a URI.
    static final String PROVIDER_NAME = "it.unical.mat.dlvfit.contentprovider.DLVfit";

    //URI addresses which will be used to access the content
    //#1
    public static final String ACTIVITIES_URL = "content://" + PROVIDER_NAME + "/activities";
    public static final Uri ACTIVITIES_CONTENT_URI = Uri.parse(ACTIVITIES_URL);

    //#2
    public static final String BURNEDCALORIES_MIN_URL ="content://" + PROVIDER_NAME + "/burnedcalories";
    public static final Uri BURNEDCALORIES_MIN_CONTENT_URI = Uri.parse(BURNEDCALORIES_MIN_URL);

    //#3
    public static final String INPUT_DATA_URL ="content://" + PROVIDER_NAME +"/inputdata";
    public static final Uri INPUT_DATA_CONTENT_URI = Uri.parse(INPUT_DATA_URL);

    //#4
    public static final String OPTIMIZATIONS_DATA_URL ="content://" + PROVIDER_NAME +"/optimizations";
    public static final Uri OPTIMIZATIONS_CONTENT_URI = Uri.parse(OPTIMIZATIONS_DATA_URL);

    //activities table columns
    public static final String _ID_ACTIVITY = "_id"; //column corresponding to an autoincremented value created automatically in sqlite db tables
    public static final String TIMESTAMP = "timestamp"; //timestamp column
    public static final String ACTIVITY = "activity"; //activity column
    public static final String CONFIDENCE = "confidence"; //confidence column

    //burned calories in a minute table columns
    public static final String ACTIVITY_GROUP_MIN = "activity_group"; //activity group column
    public static final String BURNEDCALORIES_MIN = "calories"; //burned calories in a  minute column

    //input data table
    public static final String GENDER = "gender";
    public static final String AGE = "age";
    public static final String WEIGHT = "weight";
    public static final String WORKOUT_TIME = "workout_time";
    public static final String CALORIES = "calories";

    //optimizations table columns
    public static final String OPTIMIZATION = "optimization";
    public static final String FIRSTLEVEL = "firstlevel";
    public static final String SECONDLEVEL = "secondlevel";


    //projection maps
    private static HashMap<String, String> ACTIVITIES_PROJECTION_MAP;
    private static HashMap<String, String> BURNEDCALORIES_MIN_PROJECTION_MAP;
    private static HashMap<String, String> INPUTDATA_PROJECTION_MAP;
    private static HashMap<String, String> OPTIMIZATIONS_PROJECTION_MAP;

    //switching uri cases for activities tables
    static final int ACTIVITY_CASE = 1;
    static final int ACTIVITY_ID_CASE = 2;

    static final int BURNEDCALORIES_MIN_CASE = 3;
    static final int INPUTDATA_CASE = 4;

    static final int OPTIMIZATIONS_CASE = 5;

    static final UriMatcher uriMatcher;
    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "activities", ACTIVITY_CASE);
        uriMatcher.addURI(PROVIDER_NAME, "activities/#", ACTIVITY_ID_CASE);

        uriMatcher.addURI(PROVIDER_NAME, "burnedcalories", BURNEDCALORIES_MIN_CASE);
        uriMatcher.addURI(PROVIDER_NAME, "inputdata", INPUTDATA_CASE);

        uriMatcher.addURI(PROVIDER_NAME, "optimizations", OPTIMIZATIONS_CASE);
    }

    //Database specific constant declarations
    private static SQLiteDatabase db;

    static final String DATABASE_NAME = "DLVfit";

    static final String ACTIVITIES_TABLE_NAME = "activities";
    static final String BURNEDCALORIES_MIN_TABLE_NAME = "burnedcalories";
    static final String INPUTDATA_TABLE_NAME = "inputdata";
    static final String OPTIMIZATIONS_TABLE_NAME = "optimizations";

    static final int DATABASE_VERSION = 1;

    //statements for creating tables
    private static final String CREATE_ACTIVITIES_TABLE = "create table " // or	"create table if not exists "
            + ACTIVITIES_TABLE_NAME + "("
            + _ID_ACTIVITY
            + " integer primary key autoincrement, "
            + TIMESTAMP
            + " integer not null, "
            + ACTIVITY
            + " text not null, "
            + CONFIDENCE
            + " integer not null "
            + " );";

    private static final String CREATE_BURNEDCALORIES_MIN_TABLE = "create table " // or	"create table if not exists "
            + BURNEDCALORIES_MIN_TABLE_NAME + "("
            + ACTIVITY_GROUP_MIN
            + " text not null, "
            + BURNEDCALORIES_MIN
            + " int not null, "
            + "PRIMARY KEY ("+ACTIVITY_GROUP_MIN+", "+BURNEDCALORIES_MIN+") ON CONFLICT IGNORE"
            + " );";

    private static final String CREATE_INPUTDATA_TABLE = "create table "
            + INPUTDATA_TABLE_NAME + "("
            + GENDER
            + " text not null, "
            + AGE
            + " integer not null, "
            + WEIGHT
            + " real not null, "
            + WORKOUT_TIME
            + " integer not null, "
            + CALORIES
            + " real not null "
            + " );";

    private static final String CREATE_OPTIMIZATIONS_TABLE = "create table "
            + OPTIMIZATIONS_TABLE_NAME + "("
            + OPTIMIZATION
            + " text primary key on conflict ignore, "
            + FIRSTLEVEL
            + " integer not null, "
            + SECONDLEVEL
            + " integer not null "
            + " );";

    /*
     * Helper class that actually creates and manages the provider's underlying
     * data repository.
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        /**
         * Create Database tables
         * @param db
         */
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_ACTIVITIES_TABLE);
            db.execSQL(CREATE_BURNEDCALORIES_MIN_TABLE);
            db.execSQL(CREATE_INPUTDATA_TABLE);
            db.execSQL(CREATE_OPTIMIZATIONS_TABLE);
        }

        /**
         * Upgrade Database
         * @param db
         * @param oldVersion
         * @param newVersion
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + ACTIVITIES_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + BURNEDCALORIES_MIN_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + INPUTDATA_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + OPTIMIZATIONS_TABLE_NAME);
            onCreate(db);
        }

    }

    private static DatabaseHelper dbHelper;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        dbHelper = new DatabaseHelper(context);
        /**
         * Create a writeable database which will trigger its creation if it
         * doesn't already exist.
         */
        db = dbHelper.getWritableDatabase();
        return (db == null) ? false : true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        switch (uriMatcher.match(uri)) {
            case ACTIVITY_CASE:
                qb.setTables(ACTIVITIES_TABLE_NAME);
                qb.setProjectionMap(ACTIVITIES_PROJECTION_MAP);
                break;
            case ACTIVITY_ID_CASE:
                qb.setTables(ACTIVITIES_TABLE_NAME);
                qb.appendWhere(_ID_ACTIVITY + "=" + uri.getPathSegments().get(1));
                break;
            case BURNEDCALORIES_MIN_CASE:
                qb.setTables(BURNEDCALORIES_MIN_TABLE_NAME);
                qb.setProjectionMap(BURNEDCALORIES_MIN_PROJECTION_MAP);
                break;
            case INPUTDATA_CASE:
                qb.setTables(INPUTDATA_TABLE_NAME);
                qb.setProjectionMap(INPUTDATA_PROJECTION_MAP);
                break;
            case OPTIMIZATIONS_CASE:
                qb.setTables(OPTIMIZATIONS_TABLE_NAME);
                qb.setProjectionMap(OPTIMIZATIONS_PROJECTION_MAP);
                break;
            default: throw new IllegalArgumentException("Unknown URI " + uri);
        }

        Cursor c = qb.query(db,	projection,	selection, selectionArgs,
                null, null, sortOrder);

        //register to watch a content URI for changes
        c.setNotificationUri(getContext().getContentResolver(), uri);

        return c;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)){
            //Get all activities records
            case ACTIVITY_CASE:
                return "vnd.android.cursor.dir/vnd.activities";
            //Get a particular activity
            case ACTIVITY_ID_CASE:
                return "vnd.android.cursor.item/vnd.activities";
            //Get all burned calories groups
            case BURNEDCALORIES_MIN_CASE:
                return "vnd.android.cursor.dir/vnd.burnedcalories";
            case INPUTDATA_CASE:
                return "vnd.android.cursor.dir/vnd.inputdata";
            case OPTIMIZATIONS_CASE:
                return "vnd.android.cursor.dir/vnd.optimizations";
            default: throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri _uri = null;
        switch (uriMatcher.match(uri)){
            case ACTIVITY_CASE:
                // Add a new activities table record
                long rowID1 = db.insert(ACTIVITIES_TABLE_NAME, "", values);
                // If record is added successfully
                if (rowID1 > 0) {
                    _uri = ContentUris.withAppendedId(ACTIVITIES_CONTENT_URI, rowID1);
                    getContext().getContentResolver().notifyChange(_uri, null);
                }
                break;
            case BURNEDCALORIES_MIN_CASE:
                // Add a new activities table record
                long rowID2 = db.insert(BURNEDCALORIES_MIN_TABLE_NAME, "", values);
                // If record is added successfully
                if (rowID2 > 0) {
                    _uri = ContentUris.withAppendedId(BURNEDCALORIES_MIN_CONTENT_URI, rowID2);
                    getContext().getContentResolver().notifyChange(_uri, null);
                }
                break;
            case INPUTDATA_CASE:
                // Add a new activities table record
                long rowID3 = db.insert(INPUTDATA_TABLE_NAME, "", values);
                // If record is added successfully
                if (rowID3 > 0) {
                    _uri = ContentUris.withAppendedId(INPUT_DATA_CONTENT_URI, rowID3);
                    getContext().getContentResolver().notifyChange(_uri, null);
                }
                break;
            case OPTIMIZATIONS_CASE:
                // Add a new activities table record
                long rowID4 = db.insert(OPTIMIZATIONS_TABLE_NAME, "", values);
                // If record is added successfully
                if (rowID4 > 0) {
                    _uri = ContentUris.withAppendedId(OPTIMIZATIONS_CONTENT_URI, rowID4);
                    getContext().getContentResolver().notifyChange(_uri, null);
                }
                break;
            default: throw new SQLException("Failed to add a record into " + uri);
        }
        return _uri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)){
            case ACTIVITY_CASE:
                count = db.delete(ACTIVITIES_TABLE_NAME, selection, selectionArgs);
                break;
            case ACTIVITY_ID_CASE:
                String id1 = uri.getPathSegments().get(1);
                count = db.delete( ACTIVITIES_TABLE_NAME, _ID_ACTIVITY +  " = " + id1 +
                        (!TextUtils.isEmpty(selection) ? " AND (" +
                                selection + ')' : ""), selectionArgs);
                break;
            case BURNEDCALORIES_MIN_CASE:
                count = db.delete(BURNEDCALORIES_MIN_TABLE_NAME, selection, selectionArgs);
                break;
            case INPUTDATA_CASE:
                count = db.delete(INPUTDATA_TABLE_NAME, selection, selectionArgs);
                break;
            case OPTIMIZATIONS_CASE:
                count = db.delete(OPTIMIZATIONS_TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int count = 0;

        switch (uriMatcher.match(uri)){
            case ACTIVITY_CASE:
                count = db.update(ACTIVITIES_TABLE_NAME, values,
                        selection, selectionArgs);
                break;
            case ACTIVITY_ID_CASE:
                count = db.update(ACTIVITIES_TABLE_NAME, values, _ID_ACTIVITY +
                        " = " + uri.getPathSegments().get(1) +
                        (!TextUtils.isEmpty(selection) ? " AND (" +
                                selection + ')' : ""), selectionArgs);
                break;
            case BURNEDCALORIES_MIN_CASE:
                count = db.update(BURNEDCALORIES_MIN_TABLE_NAME, values,
                        selection, selectionArgs);
                break;
            case INPUTDATA_CASE:
                count = db.update(INPUTDATA_TABLE_NAME, values,
                        selection, selectionArgs);
                break;
            case OPTIMIZATIONS_CASE:
                count = db.update(OPTIMIZATIONS_TABLE_NAME, values,
                        selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri );
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
