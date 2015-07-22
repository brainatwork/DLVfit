package it.unical.mat.dlvfit;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.unical.mat.dlvfit.contentprovider.BurnedCaloriesUtil;
import it.unical.mat.dlvfit.contentprovider.InputDataUtil;
import it.unical.mat.dlvfit.contentprovider.OptimizeUtil;
import it.unical.mat.dlvfit.contentprovider.SQLiteDBManager;
import it.unical.mat.dlvfit.resultelaborationlist.ExpandableListAdapter;
import it.unical.mat.dlvfit.resultelaborationlist.ResultGroup;
import it.unical.mat.dlvfit.utils.ActivityToDo;
import it.unical.mat.dlvfit.utils.CaloriesCalculator;
import it.unical.mat.dlvfit.utils.LogicProgram;
import it.unical.mat.dlvfit.utils.RecognitionApiConstants;
import it.unical.mat.dlvfit.utils.Utils;
import it.unical.mat.embasp.base.ASPHandler;
import it.unical.mat.embasp.base.AnswerSet;
import it.unical.mat.embasp.base.AnswerSetCallback;
import it.unical.mat.embasp.base.AnswerSets;
import it.unical.mat.embasp.dlv.DLVHandler;


public class ResultElaboration extends AppCompatActivity implements AnswerSetCallback {
    private Toolbar mToolbar;

    protected static final String TAG = "ResultElaboration";
    private SQLiteDBManager dbManager;
    private ProgressDialog elaboration;
    private AlertDialog answerSetAlert;

    // more efficient than HashMap for mapping integers to objects
    private SparseArray<ResultGroup> groups = new SparseArray<ResultGroup>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_elaboration_result);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbManager = new SQLiteDBManager(getApplicationContext());

        ArrayList<InputDataUtil> inputDataUtils = dbManager.retrieveInputData();
        int workoutTime = inputDataUtils.get(0).getWorkoutTime();
        int caloriesToBurnNeeding = (int) inputDataUtils.get(0).getCalories();

        int remainingCaloriesToBurn = remainingCaloriesToBurn();

        embaspStart(dbManager, remainingCaloriesToBurn, workoutTime, caloriesToBurnNeeding);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_elaboration_result, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
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

        SQLiteDBManager dbManager = new SQLiteDBManager(getApplicationContext());
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

    //utility function for max calculation between int values
    private int findMax(int... vals) {
        int max = 0;

        for (int d : vals) {
            if (d > max) max = d;
        }
        return max;
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

    /**
     * Calculates how_long_max and how_long_max aggregate and starts embasp framework
     *
     * @param remainingCaloriesToBurn
     */
    public void embaspStart(SQLiteDBManager dbManager, int remainingCaloriesToBurn, int workoutTime, int calories) {

        ASPHandler aspHandler = new DLVHandler();
        elaboration = ProgressDialog.show(this, "Wait...", "Elaborating your personal workouts.", true);

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
                aspHandler.addRawInput(LogicProgram.caloriesBurntPerActivity(allActivities.get(ac).getActivityGroup(),allActivities.get(ac).getBurnedCaloriesMin()));
                Log.i(TAG,LogicProgram.caloriesBurntPerActivity(allActivities.get(ac).getActivityGroup(),allActivities.get(ac).getBurnedCaloriesMin()));
            }

            for (int ac = 0; ac < allActivities.size(); ac++) {
                String activity_name = allActivities.get(ac).getActivityGroup();
                burnedInAminute = dbManager.retrieveBurnedCaloriesMinGroup(activity_name).getBurnedCaloriesMin();

                //how_long_max calculation per activity
                if (burnedInAminute != 0) {

                    how_long_max = (remainingCaloriesToBurn + SURPLUS) / burnedInAminute;

                    howLongMax.put(activity_name, how_long_max);//HashMap that stores how_long_max value per activity

                    howLongArray = howLongCalculation(how_long_max, STEP, workoutTime);
                    Log.i(TAG, "How long max for activity: " + activity_name + " " + how_long_max);
                    for (int i = 0; i < howLongArray.size(); i++) {
                        //AGGREGATE EXTRACTION FOR EMBASP FRAMEWORK
                        Log.i(TAG, LogicProgram.howLong(activity_name,howLongArray.get(i)));
                        //insert aggregate for elaborattion. EX. how_long("WALKING", 5).
                        aspHandler.addRawInput(LogicProgram.howLong(activity_name,howLongArray.get(i))); //LogicProgram.howLong
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

            //there is not workout that can burn all calories
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

                for (int i = 0; i < optimizations.size(); i++) {
                    if (optimizations.get(i).getOptimizationName().equals(getString(R.string.optimization_1_db)) &&
                            optimizations.get(i).getSecondLevel() > 0) {

                        aspHandler.addRawInput(LogicProgram.optimize(
                                optimizations.get(i).getOptimizationName(),
                                optimizations.get(i).getFirstLevel(),
                                optimizations.get(i).getSecondLevel()));

                        Log.i(TAG,LogicProgram.optimize(
                                optimizations.get(i).getOptimizationName(),
                                optimizations.get(i).getFirstLevel(),
                                optimizations.get(i).getSecondLevel()));

                    }
                    if (optimizations.get(i).getOptimizationName().equals(getString(R.string.optimization_2_db)) &&
                            optimizations.get(i).getSecondLevel() > 0) {

                        aspHandler.addRawInput(LogicProgram.optimize(
                                optimizations.get(i).getOptimizationName(),
                                optimizations.get(i).getFirstLevel(),
                                optimizations.get(i).getSecondLevel()));

                        Log.i(TAG,LogicProgram.optimize(
                                optimizations.get(i).getOptimizationName(),
                                optimizations.get(i).getFirstLevel(),
                                optimizations.get(i).getSecondLevel()));

                    }
                    if(!optimizations.get(i).getOptimizationName().equals(getString(R.string.optimization_1_db)) &&
                            !optimizations.get(i).getOptimizationName().equals(getString(R.string.optimization_2_db))
                            && optimizations.get(i).getSecondLevel() > 0
                            && optimizations.get(i).getFirstLevel() != Utils.NEUTRAL_VALUE){

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

                //the logic program for workouts elaboration
                //insert logic program for elaboration
                aspHandler.addRawInput(LogicProgram.getProgram());

                //set filter for answer sets elaboration

                aspHandler.setFilter(ActivityToDo.class);

                //start EMBASP Framework answer sets elaboration
                aspHandler.start(getApplicationContext(), this);
            } else {
                if (!isFinishing()) {
                    answerSetAlert = new AlertDialog.Builder(ResultElaboration.this)
                            .setTitle("DLVfit")
                            .setMessage("No workouts elaborated!\n\n" + workoutTime + " minutes are not enough to burn " + calories + " calories!")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    answerSetAlert.dismiss();
                                }
                            })
                            .setIcon(R.mipmap.ic_launcher)
                            .show();
                }
                elaboration.dismiss();
            }
        } else {
            elaboration.dismiss();
        }
    }

    //callback called on result from EMBASP Framework
    @Override
    public void callback(AnswerSets answerSets) {
        List<AnswerSet> answerSetList = answerSets.getAnswerSetsList();
        Log.i(TAG, "Answer sets generated: " + answerSetList.size());
        int count = 0;
        int item = 1;
        for (AnswerSet answerSet : answerSetList) {

            ResultGroup group = new ResultGroup("Workout " + item);////
            try {
                for (Object obj : answerSet.getAnswerObjects()) {
                    Log.i(TAG, "ATOM " + obj.toString());

                    group.children.add(resultFormat(obj.toString()));////
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            groups.append(count, group);////
            count++;
            item++;
        }

        if (answerSetList.isEmpty()) {
            if (!isFinishing()) {
                answerSetAlert = new AlertDialog.Builder(ResultElaboration.this)
                        .setTitle("DLVfit")
                        .setMessage("Sorry, no workouts elaborated!")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                answerSetAlert.dismiss();
                            }
                        })
                        .setIcon(R.mipmap.ic_launcher)
                        .show();
            }
        }else{
            ExpandableListView listView = (ExpandableListView) findViewById(R.id.listView);
            ExpandableListAdapter adapter = new ExpandableListAdapter(this,
                    groups);
            listView.setAdapter(adapter);
        }

        //calculation ended. Progress Dialog for elabortion is deleted
        elaboration.dismiss();
    }

    private String resultFormat(String str){
        String notFormattedName = str;
        String activityName="";

        if(!notFormattedName.contains("ON_")){
            activityName = notFormattedName.substring(0,1).toUpperCase() + notFormattedName.substring(1).toLowerCase();
        }
        else{
            notFormattedName = notFormattedName.replace("ON_","");
            activityName = notFormattedName.substring(0,1).toUpperCase() + notFormattedName.substring(1).toLowerCase();
        }
        return activityName;
    }
}
