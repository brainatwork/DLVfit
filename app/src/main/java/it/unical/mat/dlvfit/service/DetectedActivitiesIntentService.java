package it.unical.mat.dlvfit.service;

/**
 * Created by Dario Campisano on 13/04/2015.
 */

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import it.unical.mat.dlvfit.utils.RecognitionApiConstants;
import it.unical.mat.dlvfit.contentprovider.ActivityUtil;
import it.unical.mat.dlvfit.contentprovider.SQLiteDBManager;


/**
 *  IntentService for handling incoming intents that are generated as a result of requesting
 *  activity updates using
 *  {@link com.google.android.gms.location.ActivityRecognitionApi#requestActivityUpdates}.
 *  @see IntentService
 */
public class DetectedActivitiesIntentService extends IntentService {

    private SQLiteDBManager dbManager;
    private static int timestamp = 0;
    protected static final String TAG = "DetectionIntentService";

    /**
     * This constructor is required, and calls the super IntentService(String)
     * constructor with the name for a worker thread.
     */
    public DetectedActivitiesIntentService() {
        // Use the TAG to name the worker thread.
        super(TAG);
    }


    @Override
    public void onCreate() {

        super.onCreate();
        dbManager = new SQLiteDBManager(getApplicationContext());
    }


    /**
     * Handles incoming intents.
     * @param intent The Intent is provided (inside a PendingIntent) when requestActivityUpdates()
     *               is called.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
        Intent localIntent = new Intent(RecognitionApiConstants.BROADCAST_ACTION);


        // Get the list of the probable activities associated with the current state of the
        // device. Each activity is associated with a confidence level, which is an int between
        // 0 and 100.
        ArrayList<DetectedActivity> detectedActivities = (ArrayList) result.getProbableActivities();

        Date now = new Date();
        SimpleDateFormat dateformat = new SimpleDateFormat ("dd MMMM yyyy - HH:mm.ss");
        Log.i(TAG,"RECOGNITION TIME: "+ dateformat.format(now));
        //all activity recognized will be added into SQLite DB
        // Log each activity.
        Log.i(TAG, "activities detected");
        for (DetectedActivity da: detectedActivities) {
            Log.i(TAG, RecognitionApiConstants.getActivityString(getApplicationContext(), da.getType()) + " " + da.getConfidence() + "%");
            dbManager.createActivity(new ActivityUtil(timestamp, RecognitionApiConstants.getActivityString(getApplicationContext(), da.getType()), da.getConfidence()));
            Log.i(TAG, timestamp + " " + RecognitionApiConstants.getActivityString(getApplicationContext(), da.getType()) + " " + da.getConfidence());
        }
        timestamp++;
    }
}