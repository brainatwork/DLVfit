package it.unical.mat.dlvfit;

/**
 * Created by Brain At Work on 28/08/2015.
 */
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.unical.mat.dlvfit.contentprovider.BurnedCaloriesUtil;
import it.unical.mat.dlvfit.contentprovider.InputDataUtil;
import it.unical.mat.dlvfit.contentprovider.OptimizeUtil;
import it.unical.mat.dlvfit.contentprovider.SQLiteDBManager;
import it.unical.mat.dlvfit.utils.ActivityToDo;
import it.unical.mat.dlvfit.utils.LogicProgram;
import it.unical.mat.dlvfit.utils.RecognitionApiConstants;
import it.unical.mat.dlvfit.utils.Utils;
import it.unical.mat.embasp.base.ASPHandler;
import it.unical.mat.embasp.base.AnswerSet;
import it.unical.mat.embasp.base.AnswerSetCallback;
import it.unical.mat.embasp.base.AnswerSets;
import it.unical.mat.embasp.dlv.DLVHandler;

/**
 * Created by Brain At Work on 28/08/2015.
 */

/**
 * This class provide an asynchronous execution of EMBAsp for workouts elaboration
 */
public class AsyncWorkoutElaboration extends AsyncTask<Void, Void, Void> implements AnswerSetCallback {

    /**
     * Notification Manager for notity and Notification ID {@link android.support.v4.app.NotificationManagerCompat}
     */
    private NotificationManager mNotificationManager;
    private final int notificationId = 22056;
    private Context mContext;
    private boolean thresholdAlert; //if minutes are not enough to burn calories inserted
    private boolean caloriesAlert; //if there are not calories to burn
    protected static final String TAG = "AsyncWorkoutElaboration";
    private SQLiteDBManager dbManager;
    private ArrayList<String> atomList;//contains all atoms
    private ArrayList<Integer> answerSetSizeList;//contains the number of atoms per answer set
    public static final String ATOMS_KEY = "ATOMS_LIST_KEY";
    public static final String ANSWERSETS_SIZE_KEY = "ANSWERSETS_SIZE_LIST_KEY";


    public AsyncWorkoutElaboration(Context context) {
        this.mContext = context;

        //Get the notification manager
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
    }


    protected void onPreExecute() {

    }

    protected Void doInBackground(Void... params) {
        thresholdAlert = false;
        caloriesAlert = false;
        atomList = new ArrayList<String>();
        answerSetSizeList = new ArrayList<Integer>();


        dbManager = new SQLiteDBManager(mContext);

        ArrayList<InputDataUtil> inputDataUtils = dbManager.retrieveInputData();
        int workoutTime = inputDataUtils.get(0).getWorkoutTime();
        int caloriesToBurnNeeding = (int) inputDataUtils.get(0).getCalories();

        int remainingCaloriesToBurn = remainingCaloriesToBurn();
        //Elaboration start
        embaspStart(dbManager, remainingCaloriesToBurn, workoutTime, caloriesToBurnNeeding);
        return null;
    }

    protected void onCancelled() {

    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if(thresholdAlert){
            Toast.makeText(mContext,R.string.no_workouts_toast , Toast.LENGTH_LONG).show();
        }
        if(caloriesAlert){
            Toast.makeText(mContext,R.string.no_calories_to_burn_toast , Toast.LENGTH_LONG).show();
        }

    }

    /**
     * Create and show notification for background running
     *
     * @param mNotificationManager
     * @param notificationId
     * @see NotificationCompat
     * @see NotificationManager
     */
    public void notification(NotificationManager mNotificationManager, int notificationId, int workouts, ArrayList<String> atomList, ArrayList<Integer> answerSetSizeList) {
        // Builder for Notification
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext);
        //Notification options setup
        mBuilder.setContentTitle(mContext.getString(R.string.app_name));
        if (workouts > 0) {
            mBuilder.setContentText(mContext.getString(R.string.workouts_elaborates_notification) + workouts);
        } else {
            mBuilder.setContentText(mContext.getString(R.string.no_workouts_notification));
        }
        mBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        mBuilder.setAutoCancel(false);
        mBuilder.setSmallIcon(getNotificationIcon(mBuilder));
        if (workouts > 0) {
            // Creates a specific Intent that will open an activity showing workouts.
            Intent resultIntent = new Intent(mContext, AsyncResultVisualization.class);
            resultIntent.putStringArrayListExtra(ATOMS_KEY, atomList);
            resultIntent.putIntegerArrayListExtra(ANSWERSETS_SIZE_KEY, answerSetSizeList);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
            stackBuilder.addParentStack(AsyncResultVisualization.class);

            //Adds Intent at the top of stack
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            mBuilder.setContentIntent(resultPendingIntent);
        } else {
            // Creates a specific Intent that open a specific activity. MainActivity in this case.
            Intent resultIntent = new Intent(mContext, MainActivity.class);
            resultIntent.putExtra(MainActivity.FRAGMENT_INTENT_KEY, 0);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
            stackBuilder.addParentStack(MainActivity.class);

            //Adds Intent at the top of stack
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            mBuilder.setContentIntent(resultPendingIntent);
        }
        mNotificationManager = (NotificationManager) mContext.getSystemService(mContext.NOTIFICATION_SERVICE);

        //show Notification with a specific id that keep track of the Notification
        mNotificationManager.notify(notificationId, mBuilder.build());
    }

    //utility func to retrieve the right icon
    private int getNotificationIcon(NotificationCompat.Builder n) {
        boolean whiteIcon = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
        if (whiteIcon) {
            n.setColor(mContext.getResources().getColor(R.color.icon_backG));
            return R.mipmap.silhouette;
        } else {
            return R.mipmap.ic_launcher;
        }
    }

    /**
     * Calculate remaining calories to burn
     *
     * @return
     */
    private int remainingCaloriesToBurn() {

        int caloriesToBurn = 0;

        int total = 0;//total burned calories

        //temporary variables used for calculations
        int minutes = 0; //temporary variable for minutes dedicated to a particular activity

        int seconds = (int) RecognitionApiConstants.DETECTION_INTERVAL_IN_MILLISECONDS / 1000; //recognition interval is 2000 ms!

        int burnedInAminute = 0; //temporary variable for burned calories in a minute

        int burned = 0; //temporary variable for calories burned in particoular activity

        Utils utils = new Utils();

        //activityGroup set to show on chart
        ArrayList<String> activities = utils.getMonitoredActivity();
        activities.remove("STILL");

        SQLiteDBManager dbManager = new SQLiteDBManager(mContext);
        InputDataUtil user = dbManager.retrieveInputData().get(0);
        caloriesToBurn = (int) user.getCalories();

        Log.i(TAG, "Calories to burn: " + caloriesToBurn);


        /*MINUTES DEDICATED TO AN ACTIVITY AND TOTAL CALORIES BURNED FROM USER CALCULATIONS*/

        for (int i = 0; i < activities.size(); i++) {

            //to obtain the time in seconds we have to multiply the total number of a particular recognized activity,
            // with a certain confidence level, and 10. To obtain the time in minutes we have to divide for 60.

            String activity_name = activities.get(i);

            minutes = (dbManager.retrieveActivity((int) RecognitionApiConstants.ADMISSIBLE_CONFIDENCE_LEVEL, activity_name).size() * (seconds)) / 60;
            Log.i(TAG, "Minutes per activity: " + activity_name + " " + minutes);

            burnedInAminute = dbManager.retrieveBurnedCaloriesMinGroup(activity_name).getBurnedCaloriesMin();
            burned = burnedInAminute * minutes;

            Log.i(TAG, "Calories burned per activity: " + activity_name + " " + burned);

            //total calories burned
            //do sum of all calories burned
            total += burned;
        }

        return caloriesToBurn;
    }

    /**
     * Calculates how_long_max and how_long aggregate and starts embasp framework
     *
     * @param remainingCaloriesToBurn
     */
    public void embaspStart(SQLiteDBManager dbManager, int remainingCaloriesToBurn, int workoutTime, int calories) {

        ASPHandler aspHandler = new DLVHandler();

        //contains how_long_max for activity monitored
        Map<String, Integer> howLongMax = new HashMap<String, Integer>();
        //contains how_long for activities monitored
        Map<String, ArrayList<Integer>> howLong = new HashMap<String, ArrayList<Integer>>();

        final int SURPLUS = 100;
        final int STEP = 10; //step for how_long calculation

        //temporary variables used for calculations
        int burnedInAminute = 0; //temporary variable for burned calories in a minute

        int how_long_max = 0;//temporary variable for how_long_max calculation

        ArrayList<Integer> howLongArray = new ArrayList<Integer>();//temporary array for how_long calculation

        if (remainingCaloriesToBurn > 0) {

            ArrayList<BurnedCaloriesUtil> allActivities = dbManager.retrieveBurnedCaloriesMinGroups(); //contains all activities for how_long and how_long_max calculation

            //AGGREGATE EXTRACTION FOR EMBASP FRAMEWORK
            for (int ac = 0; ac < allActivities.size(); ac++) {
                //insert aggregate for elaboration. EX. calories_burnt_per_activity("WALKING", 10).
                aspHandler.addRawInput(LogicProgram.caloriesBurntPerActivity(allActivities.get(ac).getActivityGroup(), allActivities.get(ac).getBurnedCaloriesMin()));
                Log.i(TAG, LogicProgram.caloriesBurntPerActivity(allActivities.get(ac).getActivityGroup(), allActivities.get(ac).getBurnedCaloriesMin()));
            }

            for (int ac = 0; ac < allActivities.size(); ac++) {
                String activity_name = allActivities.get(ac).getActivityGroup();
                //calories burned in a minute for a specific activity
                burnedInAminute = dbManager.retrieveBurnedCaloriesMinGroup(activity_name).getBurnedCaloriesMin();

                //how_long_max calculation per activity
                if (burnedInAminute != 0) {

                    how_long_max = (remainingCaloriesToBurn + SURPLUS) / burnedInAminute;

                    howLongMax.put(activity_name, how_long_max);//HashMap that stores how_long_max value per activity

                    howLongArray = howLongCalculation(how_long_max, STEP, workoutTime);

                    Log.i(TAG, "How long max for activity: " + activity_name + " " + how_long_max);

                    for (int i = 0; i < howLongArray.size(); i++) {
                        //AGGREGATE EXTRACTION FOR EMBASP FRAMEWORK
                        Log.i(TAG, LogicProgram.howLong(activity_name, howLongArray.get(i)));
                        //insert aggregate for elaborattion. EX. how_long("WALKING", 5).
                        aspHandler.addRawInput(LogicProgram.howLong(activity_name, howLongArray.get(i))); //LogicProgram.howLong
                    }

                    howLong.put(activity_name, howLongArray);//HashMap that  stores howlong values for activity

                }

            }

            ArrayList<BurnedCaloriesUtil> burnedCaloriesUtilArrayList = dbManager.retrieveBurnedCaloriesMinGroups();
            int maxCaloriesInAminute = 0;//max calories amount that an activity can burn in a minute

            for (int i = 0; i < burnedCaloriesUtilArrayList.size(); i++) {
                if ((int) burnedCaloriesUtilArrayList.get(i).getBurnedCaloriesMin() > maxCaloriesInAminute) {
                    maxCaloriesInAminute = burnedCaloriesUtilArrayList.get(i).getBurnedCaloriesMin();
                }
            }

            //check if there is not workout that can burn all calories with the help of a threshold value
            //int threshold = maxCaloriesInAminute * workoutTime;
            int threshold = maxCaloriesInAminute * workoutTime;
            Log.i(TAG, "threshold: " + threshold + " calories: " + calories);
            if (threshold >= calories) {
                //AGGREGATE EXTRACTION FOR EMBASP FRAMEWORK
                Log.i(TAG, LogicProgram.maxTime(workoutTime));
                //insert aggregate for elaboration. EX. max_time(200).
                aspHandler.addRawInput(LogicProgram.maxTime(workoutTime));//LogicProgram.maxTime

                Log.i(TAG, LogicProgram.remainingCaloriesToBurn(remainingCaloriesToBurn));
                //insert aggregate for elaboration. EX. remaining_calories_to_burn(1000).
                aspHandler.addRawInput(LogicProgram.remainingCaloriesToBurn(remainingCaloriesToBurn));//LogicProgram.remainingCalories

                //calculate maxint value for logic program
                int max = findMax(workoutTime, remainingCaloriesToBurn + SURPLUS, dbManager.retrieveBurnedCaloriesMinGroups().size());

                Log.i(TAG, LogicProgram.maxInt(max));
                //insert input for elaboration. EX. #maxint = 1000.
                aspHandler.addRawInput(LogicProgram.maxInt(max));//LogicProgram.maxInt

                Log.i(TAG, LogicProgram.surplus(SURPLUS));
                //insert input for elaboration. EX. #maxint = 1000.
                aspHandler.addRawInput(LogicProgram.surplus(SURPLUS));//LogicProgram.surplus

                ArrayList<OptimizeUtil> optimizations = dbManager.retrieveOptimizations();
                //add the correct optimization values
                for (int i = 0; i < optimizations.size(); i++) {
                    if (optimizations.get(i).getOptimizationName().equals(mContext.getString(R.string.optimization_1_db)) &&
                            optimizations.get(i).getSecondLevel() > 0) {

                        aspHandler.addRawInput(LogicProgram.optimize(
                                optimizations.get(i).getOptimizationName(),
                                optimizations.get(i).getFirstLevel(),
                                optimizations.get(i).getSecondLevel()));

                        Log.i(TAG, LogicProgram.optimize(
                                optimizations.get(i).getOptimizationName(),
                                optimizations.get(i).getFirstLevel(),
                                optimizations.get(i).getSecondLevel()));

                    }
                    if (optimizations.get(i).getOptimizationName().equals(mContext.getString(R.string.optimization_2_db)) &&
                            optimizations.get(i).getSecondLevel() > 0) {

                        aspHandler.addRawInput(LogicProgram.optimize(
                                optimizations.get(i).getOptimizationName(),
                                optimizations.get(i).getFirstLevel(),
                                optimizations.get(i).getSecondLevel()));

                        Log.i(TAG, LogicProgram.optimize(
                                optimizations.get(i).getOptimizationName(),
                                optimizations.get(i).getFirstLevel(),
                                optimizations.get(i).getSecondLevel()));

                    }
                    if (!optimizations.get(i).getOptimizationName().equals(mContext.getString(R.string.optimization_1_db)) &&
                            !optimizations.get(i).getOptimizationName().equals(mContext.getString(R.string.optimization_2_db))
                            && optimizations.get(i).getSecondLevel() > 0
                            && optimizations.get(i).getFirstLevel() != Utils.NEUTRAL_VALUE) {

                        aspHandler.addRawInput(LogicProgram.optimize(
                                optimizations.get(i).getOptimizationName(),
                                optimizations.get(i).getFirstLevel(),
                                optimizations.get(i).getSecondLevel()));

                        Log.i(TAG, LogicProgram.optimize(
                                optimizations.get(i).getOptimizationName(),
                                optimizations.get(i).getFirstLevel(),
                                optimizations.get(i).getSecondLevel()));

                    }
                }

                //insert logic program for elaboration
                aspHandler.addRawInput(LogicProgram.getProgram());

                //set filter for answer sets elaboration
                aspHandler.setFilter(ActivityToDo.class);

                aspHandler.start(mContext.getApplicationContext(), this);
            } else {
                notification(mNotificationManager, notificationId, 0, atomList, answerSetSizeList);
                thresholdAlert = true;
            }
        } else {
            caloriesAlert = true;
            notification(mNotificationManager, notificationId, 0, atomList, answerSetSizeList);
        }
    }

    //utility function that calculate how_long values for a specific how_long_max value
    private ArrayList<Integer> howLongCalculation(int howLongMaxValue, int step, int workoutTime) {

        ArrayList<Integer> howLong = new ArrayList<Integer>();

        if (howLongMaxValue < step) {
            return howLong;
        }

        int numberOfSteps = howLongMaxValue / step;

        int hlv = 0;

        for (int i = 1; i <= numberOfSteps; i++) {
            hlv = i * step;
            if (hlv <= workoutTime) {
                howLong.add(hlv);
            }
        }

        return howLong;
    }

    //utility function for max calculation between int values
    private int findMax(int... vals) {
        int max = 0;

        for (int d : vals) {
            if (d > max) max = d;
        }
        return max;
    }

    //callback called on result from EMBASP Framework
    @Override
    public void callback(AnswerSets answerSets) {

        List<AnswerSet> answerSetList = answerSets.getAnswerSetsList();

        int answerSetsCounter = 1;
        int atomCounter = 0;
        for (AnswerSet answerSet : answerSetList) {
            try {
                atomCounter = 0;
                for (Object obj : answerSet.getAnswerObjects()) {
                    Log.i(TAG, "ATOM " + obj.toString() + " ANSWER SET: " + answerSetsCounter);
                    atomList.add(resultFormat(obj.toString()));
                    atomCounter++;
                }
                Log.i(TAG, "Answer Set: " + answerSetsCounter + " Size: " + atomCounter);
                answerSetsCounter++;
                answerSetSizeList.add(atomCounter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        //SHOW NOTIFICATION
        notification(mNotificationManager, notificationId, answerSetList.size(), atomList, answerSetSizeList);

    }

    //utility function for result format
    private String resultFormat(String str) {
        String notFormattedName = str;
        String activityName = "";

        if (!notFormattedName.contains("ON_")) {
            activityName = notFormattedName.substring(0, 1).toUpperCase() + notFormattedName.substring(1).toLowerCase();
        } else {
            notFormattedName = notFormattedName.replace("ON_", "");
            activityName = notFormattedName.substring(0, 1).toUpperCase() + notFormattedName.substring(1).toLowerCase();
        }
        return activityName;
    }
}
