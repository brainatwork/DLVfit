package it.unical.mat.dlvfit.menu_fragments;

/**
 * Created by Brain At Work on 13/05/2015.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;

import it.unical.mat.dlvfit.CaloriesReport;
import it.unical.mat.dlvfit.TimeReport;
import it.unical.mat.dlvfit.utils.RecognitionApiConstants;
import it.unical.mat.dlvfit.MainActivity;
import it.unical.mat.dlvfit.R;
import it.unical.mat.dlvfit.contentprovider.SQLiteDBManager;
import it.unical.mat.dlvfit.service.DetectedActivitiesIntentService;

/**
 * Created by Brain At Work on 13/05/2015.
 */
public class StartWorkout extends Fragment implements GoogleApiClient.ConnectionCallbacks, OnConnectionFailedListener, ResultCallback<Status> {

    protected static final String TAG = "StartWorkoutFragment";
    /**
     * Notification Manager for notity and Notification ID {@link android.support.v4.app.NotificationManagerCompat}
     */
    private NotificationManager mNotificationManager;
    private final int notificationId = 22056;
    /**
     * DB manager
     */
    private SQLiteDBManager dbManager;

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * The DetectedActivities that we track in this sample. We also use this for persisting state in
     * {@code onSaveInstanceState()} and restoring it in {@code onCreate()}. This ensures that each
     * activity is displayed with the correct confidence level upon orientation changes.
     */
    private ArrayList<DetectedActivity> mDetectedActivities;

    /**
     * Used when requesting or removing activity detection updates.
     */
    private PendingIntent mActivityDetectionPendingIntent;

    // UI elements.
    private Button mRequestActivityUpdatesButton;
    private Button mRemoveActivityUpdatesButton;
    private ImageView timeReport;
    private ImageView caloriesReport;

    public StartWorkout() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbManager = new SQLiteDBManager(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_start_workout, container, false);

        // Get the UI widgets.
        mRequestActivityUpdatesButton = (Button) rootView.findViewById(R.id.start_btn);
        mRemoveActivityUpdatesButton = (Button) rootView.findViewById(R.id.stop_btn);
        timeReport = (ImageView) rootView.findViewById(R.id.stat_icon1);
        caloriesReport = (ImageView) rootView.findViewById(R.id.stat_icon2);


        final boolean noActivateFunction = dbManager.retrieveInputData().isEmpty();

        //button listeners
        mRequestActivityUpdatesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestActivityUpdatesButtonHandler(v);
            }
        });

        mRemoveActivityUpdatesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeActivityUpdatesButtonHandler(v);
            }
        });

        timeReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent openElaborate = new Intent(getActivity(), TimeReport.class);
                startActivity(openElaborate);

            }
        });

        caloriesReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openElaborate = new Intent(getActivity(), CaloriesReport.class);
                startActivity(openElaborate);
            }
        });

        if (noActivateFunction) {
            //Toast.makeText(getActivity(), "You should fill out the form!", Toast.LENGTH_LONG).show();
            caloriesReport.setEnabled(false);
            timeReport.setEnabled(false);
        }
        // Kick off the request to build GoogleApiClient.
        buildGoogleApiClient();

        // Inflate the layout for this fragment
        return rootView;
    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * ActivityRecognition API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(ActivityRecognition.API)
                .build();
    }

    @Override
    public void onStart() {
        Log.i(TAG, "onStart");
        super.onStart();
        mGoogleApiClient.connect();//connects to Google Play Servise
    }

    @Override
    public void onStop() {
        Log.i(TAG, "onStop");
        super.onStop();
        mGoogleApiClient.disconnect();//disconnects to Google Play Services
    }


    @Override
    public void onResume() {
        Log.i(TAG, "onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.i(TAG, "onPause");
        super.onPause();
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Connected to GoogleApiClient");
    }

    /**
     * Called when Googley Play services connection fails.
     *
     * @param result
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    /**
     * Called when Google Play sevices connection is lost.
     *
     * @param cause
     */
    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    /**
     * Registers for activity recognition updates using
     * {@link com.google.android.gms.location.ActivityRecognitionApi#requestActivityUpdates} which
     * returns a {@link com.google.android.gms.common.api.PendingResult}. Since this activity
     * implements the PendingResult interface, the activity itself receives the callback, and the
     * code within {@code onResult} executes. Note: once {@code requestActivityUpdates()} completes
     * successfully, the {@code DetectedActivitiesIntentService} starts receiving callbacks when
     * activities are detected.
     *
     * @see PendingIntent
     * @see com.google.android.gms.location.ActivityRecognitionApi
     */
    public void requestActivityUpdatesButtonHandler(View view) {

        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(getActivity(), getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }
        //reset DB first
        dbManager.resetActivitiesTable();

        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(
                mGoogleApiClient,
                RecognitionApiConstants.DETECTION_INTERVAL_IN_MILLISECONDS,
                getActivityDetectionPendingIntent()
        ).setResultCallback(this);

        //Show notification
        notification(mNotificationManager, notificationId);
    }

    /**
     * Removes activity recognition updates using
     * {@link com.google.android.gms.location.ActivityRecognitionApi#removeActivityUpdates} which
     * returns a {@link com.google.android.gms.common.api.PendingResult}. Since this activity
     * implements the PendingResult interface, the activity itself receives the callback, and the
     * code within {@code onResult} executes. Note: once {@code removeActivityUpdates()} completes
     * successfully, the {@code DetectedActivitiesIntentService} stops receiving callbacks about
     * detected activities.
     *
     * @see PendingIntent
     * @see com.google.android.gms.location.ActivityRecognitionApi
     */
    public void removeActivityUpdatesButtonHandler(View view) {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(getActivity(), getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }
        // Remove all activity updates for the PendingIntent that was used to request activity
        // updates.
        ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(
                mGoogleApiClient,
                getActivityDetectionPendingIntent()
        ).setResultCallback(this);

        //Remove the Notification after recognition activity update end.
        mNotificationManager = (NotificationManager) getActivity().getSystemService(getActivity().NOTIFICATION_SERVICE);
        mNotificationManager.cancel(notificationId);
    }

    /**
     * Runs when the result of calling requestActivityUpdates() and removeActivityUpdates() becomes
     * available. Either method can complete successfully or with an error.
     *
     * @param status The Status returned through a PendingIntent when requestActivityUpdates()
     *               or removeActivityUpdates() are called.
     */
    public void onResult(Status status) {
        if (status.isSuccess()) {
            // Toggle the status of activity updates requested, and save in shared preferences.
            boolean requestingUpdates = !getUpdatesRequestedState();
            setUpdatesRequestedState(requestingUpdates);

            // Update the UI. Requesting activity updates enables the Remove Activity Updates
            // button, and removing activity updates enables the Add Activity Updates button.
            setButtonsEnabledState();

            Toast.makeText(getActivity(), getString(requestingUpdates ? R.string.activity_updates_added : R.string.activity_updates_removed), Toast.LENGTH_SHORT).show();
        } else {
            Log.e(TAG, "Error adding or removing activity detection: " + status.getStatusMessage());
        }
    }

    /**
     * Gets a PendingIntent to be sent for each activity detection.
     *
     * @see PendingIntent
     */
    private PendingIntent getActivityDetectionPendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mActivityDetectionPendingIntent != null) {
            return mActivityDetectionPendingIntent;
        }
        Intent intent = new Intent(getActivity(), DetectedActivitiesIntentService.class);

        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // requestActivityUpdates() and removeActivityUpdates().
        return PendingIntent.getService(getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Ensures that only one button is enabled at any time. The Request Activity Updates button is
     * enabled if the user hasn't yet requested activity updates. The Remove Activity Updates button
     * is enabled if the user has requested activity updates.
     */
    private void setButtonsEnabledState() {
        if (getUpdatesRequestedState()) {
            //mRequestActivityUpdatesButton.setEnabled(false);
            //mRemoveActivityUpdatesButton.setEnabled(true);
        } else {
            //mRequestActivityUpdatesButton.setEnabled(true);
            //mRemoveActivityUpdatesButton.setEnabled(false);
        }
    }

    /**
     * Retrieves a SharedPreference object used to store or read values in this app. If a
     * preferences file passed as the first argument to {@link #getActivity().getSharedPreferences}
     * does not exist, it is created when {@link SharedPreferences.Editor} is used to commit
     * data.
     */
    private SharedPreferences getSharedPreferencesInstance() {
        return this.getActivity().getSharedPreferences(RecognitionApiConstants.SHARED_PREFERENCES_NAME, getActivity().MODE_PRIVATE);
    }

    /**
     * Retrieves the boolean from SharedPreferences that tracks whether we are requesting activity
     * updates.
     */
    private boolean getUpdatesRequestedState() {
        return getSharedPreferencesInstance()
                .getBoolean(RecognitionApiConstants.ACTIVITY_UPDATES_REQUESTED_KEY, false);
    }

    /**
     * Sets the boolean in SharedPreferences that tracks whether we are requesting activity
     * updates.
     *
     * @see SharedPreferences
     */
    private void setUpdatesRequestedState(boolean requestingUpdates) {
        getSharedPreferencesInstance()
                .edit()
                .putBoolean(RecognitionApiConstants.ACTIVITY_UPDATES_REQUESTED_KEY, requestingUpdates)
                .commit();
    }


    /**
     * Stores the list of detected activities in the Bundle.
     *
     * @see Bundle
     */
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putSerializable(RecognitionApiConstants.DETECTED_ACTIVITIES, mDetectedActivities);
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * Create and show notification for background running
     *
     * @param mNotificationManager
     * @param notificationId
     * @see NotificationCompat
     * @see NotificationManager
     */
    public void notification(NotificationManager mNotificationManager, int notificationId) {
        // Builder for Notification
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getActivity());
        //Notification options setup
        mBuilder.setContentTitle(getString(R.string.app_name));
        mBuilder.setContentText(getString(R.string.notification_txt));
        mBuilder.setAutoCancel(false);
        mBuilder.setSmallIcon(getNotificationIcon(mBuilder));

        // Creates a specific Intent that open a specific activity. MainActivity in this case.
        Intent resultIntent = new Intent(getActivity(), MainActivity.class);
        resultIntent.putExtra("FRAGMENT_KEY", 1);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getActivity());
        stackBuilder.addParentStack(MainActivity.class);

        //Adds Intent at the top of stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);

        mNotificationManager = (NotificationManager) getActivity().getSystemService(getActivity().NOTIFICATION_SERVICE);

        //show Notification with a specific id that keep track of the Notification
        mNotificationManager.notify(notificationId, mBuilder.build());
    }

    private int getNotificationIcon(NotificationCompat.Builder n) {
        boolean whiteIcon = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
        if(whiteIcon){
            n.setColor(getResources().getColor(R.color.icon_backG));
         return R.mipmap.silhouette;
        }else{
            return R.mipmap.ic_launcher;
        }
    }
}
