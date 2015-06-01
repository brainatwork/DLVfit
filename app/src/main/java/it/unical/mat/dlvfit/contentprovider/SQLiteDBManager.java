package it.unical.mat.dlvfit.contentprovider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Dario Campisano on 14/04/2015.
 */

/**
 * ActivitiesSQLiteManager provides method for adding and retrieving {@code ActivityUtil} objects on SQLite Database in {@code RecognitionActivityProvider},
 * a method for SQLite Database resetting.
 * @see Context
 * @see Cursor
 * @see Uri
 * @see ActivityUtil
 * @see ContentValues
 * @see android.content.ContentProvider
 * @see ArrayList
 */
public class SQLiteDBManager {
    private static final String TAG = "SQLiteDBManager";
    private Context context;

    /**
     * Constructor get the {@link Context}
     * @param context
     */
    public SQLiteDBManager(Context context){
        this.context = context;
    }

    /**
     * Inserts an {@code InputDataUtil} object into {@code DLVfitProvider}
     */

    public Uri createInputData(InputDataUtil _inputData){
        ContentValues values = new ContentValues();
        values.put(DLVfitProvider.GENDER, _inputData.getGender());
        values.put(DLVfitProvider.AGE, _inputData.getAge());
        values.put(DLVfitProvider.WEIGHT, _inputData.getAge());
        values.put(DLVfitProvider.WORKOUT_TIME, _inputData.getWorkoutTime());
        values.put(DLVfitProvider.CALORIES, _inputData.getCalories());

        Uri uri = context.getContentResolver().insert(DLVfitProvider.INPUT_DATA_CONTENT_URI,
                values);

        Log.d(TAG, "Row inserted in inputdata table: " + _inputData.getGender() + " " + _inputData.getAge() + " " +
                _inputData.getWeight() + " " + _inputData.getWorkoutTime() + " " + _inputData.getCalories());

        return uri;
    }

    /**
     * Resets activities table{@code DLVfitProvider}
     */
    public void resetInputDataTable(){//delete all raws
        deleteInputData();
        Log.d(TAG, "inputdata table resetted");
    }

    public ArrayList<InputDataUtil> retrieveInputData() {
        // Retrieve inputdata records
        ArrayList<InputDataUtil> inputdata = new ArrayList<InputDataUtil>();

        String URL = DLVfitProvider.INPUT_DATA_URL;
        Uri _uri = Uri.parse(URL);

        Cursor c = context.getContentResolver().query(_uri, null, null, null,
                null);

        while (c.moveToNext()) {

            inputdata.add(new InputDataUtil(c.getString(c
                    .getColumnIndex(DLVfitProvider.GENDER)), c.getInt(c
                    .getColumnIndex(DLVfitProvider.AGE)), c.getDouble(c
                    .getColumnIndex(DLVfitProvider.WEIGHT)), c.getInt(c
                    .getColumnIndex(DLVfitProvider.WORKOUT_TIME)), c.getDouble(c
                    .getColumnIndex(DLVfitProvider.CALORIES))));

        }
        Log.d(TAG, "Rows retrieved in inputdata table: " + inputdata.size());

        return inputdata;
    }

    /**
     * Inserts an {@code ActivityUtil} object into {@code DLVfitProvider}
     */
    public Uri createActivity(ActivityUtil _activity){
        // Add a new activities table row
        ContentValues values = new ContentValues();

        values.put(DLVfitProvider.TIMESTAMP, _activity.getTimestamp());

        values.put(DLVfitProvider.ACTIVITY, _activity.getActivityName());

        values.put(DLVfitProvider.CONFIDENCE, _activity.getConfidenceLevel());

        Uri uri = context.getContentResolver().insert(DLVfitProvider.ACTIVITIES_CONTENT_URI,
                values);

        Log.d(TAG, "Row inserted in activities table: " + _activity.getTimestamp() + " " + _activity.getActivityName() + " " + _activity.getConfidenceLevel());

        return uri;
    }

    /**
     * Inserts an {@code BurnedCaloriesUtil} object into {@code DLVfitProvider}
     */

    public Uri createBurnedCaloriesMinGroup(BurnedCaloriesUtil _burned){
        // Add a new burned calories table row
        ContentValues values = new ContentValues();

        values.put(DLVfitProvider.ACTIVITY_GROUP_MIN, _burned.getActivityGroup());

        values.put(DLVfitProvider.BURNEDCALORIES_MIN, _burned.getBurnedCaloriesMin());

        Uri uri = context.getContentResolver().insert(DLVfitProvider.BURNEDCALORIES_MIN_CONTENT_URI,
                values);

        Log.d(TAG, "Row inserted in burnedcalories table: " + _burned.getActivityGroup() + " " + _burned.getBurnedCaloriesMin());

        return uri;
    }

    /**
     * Resets activities table{@code DLVfitProvider}
     */
    public void resetActivitiesTable(){//delete all raws
        deleteActivities();
        Log.d(TAG, "activities table resetted");
    }

    /**
     * Resets burnedcalories table{@code DLVfitProvider}
     */
    public void resetBurnedCaloriesTable(){//reset DB
        deleteBurnedCaloriesMin();
        Log.d(TAG, "burnedcalories table resetted");
    }

    /**
     * Retrieves activities and return an {@code ActivityUtil} Arraylist
     * @return ArrayList
     */
    public ArrayList<ActivityUtil> retrieveActivities() {
        // Retrieve levels records
        ArrayList<ActivityUtil> activities = new ArrayList<ActivityUtil>();

        String URL = DLVfitProvider.ACTIVITIES_URL;
        Uri _uri = Uri.parse(URL);

        Cursor c = context.getContentResolver().query(_uri, null, null, null,
                null);

        while (c.moveToNext()) {

            activities.add(new ActivityUtil(c.getString(c
                    .getColumnIndex(DLVfitProvider._ID_ACTIVITY)),c.getInt(c
                    .getColumnIndex(DLVfitProvider.TIMESTAMP)), c.getString(c
                    .getColumnIndex(DLVfitProvider.ACTIVITY)), c.getInt(c
                    .getColumnIndex(DLVfitProvider.CONFIDENCE))));

        }
        Log.d(TAG,"Rows retrieved in activities table: " + activities.size());

        return activities;
    }




    /**
     * Retrieve burned calories in a minute and insert it into an {@code BurnedCaloriesUtil} ArraList
     * @return ArrayList
     */
    public ArrayList<BurnedCaloriesUtil> retrieveBurnedCaloriesMinGroups() {
        // Retrieve levels records
        ArrayList<BurnedCaloriesUtil> burnedCalories = new ArrayList<BurnedCaloriesUtil>();

        String URL = DLVfitProvider.BURNEDCALORIES_MIN_URL;
        Uri _uri = Uri.parse(URL);

        Cursor c = context.getContentResolver().query(_uri, null, null, null,
                null);

        while (c.moveToNext()) {

            burnedCalories.add(new BurnedCaloriesUtil(c.getString(c
                    .getColumnIndex(DLVfitProvider.ACTIVITY_GROUP_MIN)), c.getInt(c
                    .getColumnIndex(DLVfitProvider.BURNEDCALORIES_MIN))));

        }
        Log.d(TAG,"Rows retrieved in burnedcalories table: " + burnedCalories.size());

        return burnedCalories;
    }

    /**
     * Retrieve burned calories in a minute for a particoular activity and insert it into a {@code BurnedCaloriesUtil} object
     * @return BurnedCaloriesUtil
     */

    public BurnedCaloriesUtil retrieveBurnedCaloriesMinGroup(String activity_name) {
        // Retrieve levels records
        BurnedCaloriesUtil burnedCalories = new BurnedCaloriesUtil();

        String URL = DLVfitProvider.BURNEDCALORIES_MIN_URL;
        Uri _uri = Uri.parse(URL);
        String whereClause = DLVfitProvider.ACTIVITY_GROUP_MIN +" = \"" + activity_name+ "\"";
        Cursor c = context.getContentResolver().query(_uri, null, whereClause, null,
                null);
        burnedCalories = new BurnedCaloriesUtil("",0);
        while (c.moveToNext()) {

            burnedCalories = new BurnedCaloriesUtil(c.getString(c
                    .getColumnIndex(DLVfitProvider.ACTIVITY_GROUP_MIN)), c.getInt(c
                    .getColumnIndex(DLVfitProvider.BURNEDCALORIES_MIN)));

        }

        Log.d(TAG,"Rows retrieved in burnedcalories table: " + burnedCalories.getActivityGroup() + " " + burnedCalories.getBurnedCaloriesMin());

        return burnedCalories;
    }

    /**
     * Retrieve activities with a confidence level greater than confidenceLevel
     * and inserts data into an {@code ActivityUtil} ArrayList
     * @param confidenceLevel
     * @return ArrayList
     */
    public ArrayList<ActivityUtil> retrieveActivities(int confidenceLevel){
        ArrayList<ActivityUtil> activities = new ArrayList<ActivityUtil>();

        String URL = DLVfitProvider.ACTIVITIES_URL;
        Uri _uri = Uri.parse(URL);
        String whereClause = DLVfitProvider.CONFIDENCE +" >= " + confidenceLevel;
        Cursor c = context.getContentResolver().query(_uri, null, whereClause, null, null);

        while (c.moveToNext()) {

            Log.i(TAG, "Row with confidence level >= in activities table: " + confidenceLevel + " : "+c.getString(0) + " "+ c.getString(1) + " " + c.getString(2) + " " + c.getString(3));

            activities.add(new ActivityUtil(c.getString(0),c.getInt(1),c.getString(2),c.getInt(3)));
        }
        return activities;
    }

    /**
     * Retrieve a specific activity with a specific condidence level
     * and inserts data into an {@code ActivityUtil} ArrayList
     * @param confidenceLevel
     * @param activityName
     * @return ArrayList
     */
    public ArrayList<ActivityUtil> retrieveActivity(int confidenceLevel, String activityName){
        ArrayList<ActivityUtil> activities = new ArrayList<ActivityUtil>();

        String URL = DLVfitProvider.ACTIVITIES_URL;
        Uri _uri = Uri.parse(URL);
        String whereClause = DLVfitProvider.CONFIDENCE + " >= " + confidenceLevel +
                " AND " + DLVfitProvider.ACTIVITY + " == \"" + activityName+"\"";

        Cursor c = context.getContentResolver().query(_uri, null, whereClause, null,
                null);

        while (c.moveToNext()) {
            Log.i(TAG, "Row with confidenze level >= in activities table: " + confidenceLevel + " & " + "Activity Name = " + activityName);
            Log.i(TAG, "Row selected in activities table: " + confidenceLevel + " : " +c.getString(0) + " "+ c.getString(1) + " " + c.getString(2) + " " + c.getString(3));
            activities.add(new ActivityUtil(c.getString(0), c.getInt(1), c.getString(2), c.getInt(3)));
        }
        return activities;
    }

    /**
     * Update specific row in burnedcalories table with a new calories burned in a minute value
     * @param activityName
     * @return rows
     */
    public int updateBurnedCaloriesMin(String activityName, int caloriesBurnedInAMinute){
        // Add a new burned calories table row
        ContentValues values = new ContentValues();

        values.put(DLVfitProvider.BURNEDCALORIES_MIN, caloriesBurnedInAMinute);

        String URL = DLVfitProvider.BURNEDCALORIES_MIN_URL;
        Uri _uri = Uri.parse(URL);

        int rows = context.getContentResolver().update(_uri, values, DLVfitProvider.ACTIVITY_GROUP_MIN + " = \"" + activityName + "\"", null);
        Log.d(TAG, "Row updated in burnedcalories table: " + activityName + " " + caloriesBurnedInAMinute);

        return rows;
    }

    /**
     * Inserts an {@code OptimizeUtil} object into {@code DLVfitProvider}
     */

    public Uri createOptimization(OptimizeUtil _optimizeUtil){
        ContentValues values = new ContentValues();
        values.put(DLVfitProvider.OPTIMIZATION, _optimizeUtil.getOptimizationName());
        values.put(DLVfitProvider.FIRSTLEVEL, _optimizeUtil.getFirstLevel());
        values.put(DLVfitProvider.SECONDLEVEL, _optimizeUtil.getSecondLevel());

        Uri uri = context.getContentResolver().insert(DLVfitProvider.OPTIMIZATIONS_CONTENT_URI,
                values);

        Log.d(TAG, "Row inserted in optimizations table: " + _optimizeUtil.getOptimizationName() + " " + _optimizeUtil.getFirstLevel() + " " +
                _optimizeUtil.getSecondLevel());

        return uri;
    }

    /**
     * Retrieve optimizations into a {@code OptimizeUtil} ArraList
     * @return ArrayList
     */
    public ArrayList<OptimizeUtil> retrieveOptimizations() {
        // Retrieve levels records
        ArrayList<OptimizeUtil> optimizations = new ArrayList<OptimizeUtil>();

        String URL = DLVfitProvider.OPTIMIZATIONS_DATA_URL;
        Uri _uri = Uri.parse(URL);

        Cursor c = context.getContentResolver().query(_uri, null, null, null,
                null);

        while (c.moveToNext()) {

            optimizations.add(new OptimizeUtil(c.getString(c
                    .getColumnIndex(DLVfitProvider.OPTIMIZATION)), c.getInt(c
                    .getColumnIndex(DLVfitProvider.FIRSTLEVEL)), c.getInt(c
                    .getColumnIndex(DLVfitProvider.SECONDLEVEL))));

        }
        Log.d(TAG,"Rows retrieved in optimizations table: " + optimizations.size());

        return optimizations;
    }

    /**
     * Update specific row in optimizations table
     * @param optimizationName
     * @param newFirstLevel
     * @return rows
     */
    public int updateOptimizationsFirstLevel(String optimizationName, int newFirstLevel){

        // Add a new optimizations table row
        ContentValues values = new ContentValues();

        values.put(DLVfitProvider.FIRSTLEVEL, newFirstLevel);

        String URL = DLVfitProvider.OPTIMIZATIONS_DATA_URL;
        Uri _uri = Uri.parse(URL);

        int rows = context.getContentResolver().update(_uri, values, DLVfitProvider.OPTIMIZATION + " = \"" + optimizationName + "\"", null);
        Log.d(TAG, "Row updated in optimization table: " + optimizationName + " " + newFirstLevel);

        return rows;
    }

    /**
     * Update specific row in optimizations table
     * @param optimizationName
     * @param newSecondLevel
     * @return rows
     */
    public int updateOptimizationsSecondLevel(String optimizationName, int newSecondLevel){

        // Add a new optimizations table row
        ContentValues values = new ContentValues();

        values.put(DLVfitProvider.SECONDLEVEL, newSecondLevel);

        String URL = DLVfitProvider.OPTIMIZATIONS_DATA_URL;
        Uri _uri = Uri.parse(URL);

        int rows = context.getContentResolver().update(_uri, values, DLVfitProvider.OPTIMIZATION + " = \"" + optimizationName + "\"", null);
        Log.d(TAG, "Row updated in optimization table: " + optimizationName + " " + newSecondLevel);

        return rows;
    }

    /**
     * Update specific row in optimizations table. All row where optimizations is not equal to "time" and activities
     * @param newSecondLevel
     * @return rows
     */
    public int updateOptimizationsSecondLevel(int newSecondLevel){

        // Add a new optimizations table row
        ContentValues values = new ContentValues();

        values.put(DLVfitProvider.SECONDLEVEL, newSecondLevel);

        String URL = DLVfitProvider.OPTIMIZATIONS_DATA_URL;
        Uri _uri = Uri.parse(URL);

        int rows = context.getContentResolver().update(_uri, values, DLVfitProvider.OPTIMIZATION + " != \"time\" AND " + DLVfitProvider.OPTIMIZATION + " != \"activities\"", null);
        Log.d(TAG, "Rows updated in optimization table: " + rows);

        return rows;
    }
    /**
     * Resets optimizations table {@code DLVfitProvider}
     */
    public void resetOptimizationsTable(){//delete all raws
        deleteOptimizations();
        Log.d(TAG, "optimizations table resetted");
    }

    public int deleteInputData(){

        String URL = DLVfitProvider.INPUT_DATA_URL;
        Uri _uri = Uri.parse(URL);

        int rows = context.getContentResolver().delete(_uri, null, null);
        Log.d(TAG, "Rows deleted in inputdata table: " + rows);

        return rows;
    }

    public int deleteActivities(){

        String URL = DLVfitProvider.ACTIVITIES_URL;
        Uri _uri = Uri.parse(URL);

        int rows = context.getContentResolver().delete(_uri, null, null);
        Log.d(TAG, "Rows deleted in activities table: " + rows);

        return rows;
    }

    public int deleteBurnedCaloriesMin(){

        String URL = DLVfitProvider.BURNEDCALORIES_MIN_URL;
        Uri _uri = Uri.parse(URL);

        int rows = context.getContentResolver().delete(_uri, null, null);
        Log.d(TAG, "Rows deleted in burnedcalories table: " + rows);

        return rows;
    }

    public int deleteOptimizations(){

        String URL = DLVfitProvider.OPTIMIZATIONS_DATA_URL;
        Uri _uri = Uri.parse(URL);

        int rows = context.getContentResolver().delete(_uri, null, null);
        Log.d(TAG, "Rows deleted in optimizations table: " + rows);

        return rows;
    }


}
