package it.unical.mat.dlvfit.menufragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import it.unical.mat.dlvfit.R;
import it.unical.mat.dlvfit.contentprovider.BurnedCaloriesUtil;
import it.unical.mat.dlvfit.contentprovider.InputDataUtil;
import it.unical.mat.dlvfit.contentprovider.OptimizeUtil;
import it.unical.mat.dlvfit.contentprovider.SQLiteDBManager;
import it.unical.mat.dlvfit.utils.CaloriesCalculator;
import it.unical.mat.dlvfit.utils.Utils;

/**
 * Created by Brain At Work on 13/05/2015.
 */

/**
 * This fragment show a form preparatory for app usage and initialize SQLite DB at first execution
 */
public class UserDataFragment extends Fragment {
    /**
     * Database Manager Instance for tables handling
     */
    private SQLiteDBManager dbManager;

    //input data
    private static int gender;

    private EditText mEditWeight;
    private EditText mEditAge;
    private EditText mEditCalories;
    private EditText mEditWorkoutTime;
    private RadioGroup mRadioGender;

    private Button mConfirm;
    private Button mClear;
    private AlertDialog alertDialog;
    private boolean allowModifications;

    protected static final String TAG = "UserDataFragment";

    public UserDataFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbManager = new SQLiteDBManager(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_data, container, false);

        allowModifications = !dbManager.retrieveInputData().isEmpty();
        //DB init
        initBurnedCaloriesTable(dbManager);
        initOptimizationsTable(dbManager);
        //UI
        mEditWeight = (EditText) rootView.findViewById(R.id.weight_edit);
        mEditAge = (EditText) rootView.findViewById(R.id.age_edit);
        mEditCalories = (EditText) rootView.findViewById(R.id.calories_edit);
        mEditWorkoutTime = (EditText) rootView.findViewById(R.id.workout_time_edit);
        mRadioGender = (RadioGroup) rootView.findViewById((R.id.radioGrp));

        gender = 0;

        mConfirm = (Button) rootView.findViewById(R.id.confirm_btn);
        mClear = (Button) rootView.findViewById(R.id.clear_btn);

        if (dbManager.retrieveInputData().size() > 0) {
            InputDataUtil inputData = dbManager.retrieveInputData().get(0);
            mEditAge.setHint(getString(R.string.age_txt) + ": " + String.valueOf(inputData.getAge()));
            mEditWeight.setHint(getString(R.string.weight_txt) + ": " + String.valueOf(inputData.getWeight()));
            mEditCalories.setHint(getString(R.string.calories_txt) + ": " + String.valueOf(inputData.getCalories()));
            mEditWorkoutTime.setHint(getString(R.string.workout_time) + ": " + String.valueOf(inputData.getWorkoutTime()));
            mRadioGender.check(mRadioGender.getChildAt(genderToInt(inputData.getGender())).getId());
        }
        //listeners for widgets
        mRadioGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                View radioButton = radioGroup.findViewById(i);
                int index = radioGroup.indexOfChild(radioButton);
                gender = index;
            }
        });

        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (allowModifications) {

                    dbManager.updateGender(genderToString(gender));

                    if (!mEditAge.getText().toString().equals("")) {
                        dbManager.updateAge(Integer.valueOf(mEditAge.getText().toString()));
                    }
                    if (!mEditWeight.getText().toString().equals("")) {
                        dbManager.updateWeight(Double.valueOf(mEditWeight.getText().toString()));
                    }
                    if (!mEditWorkoutTime.getText().toString().equals("")) {
                        dbManager.updateWorkoutTime(Integer.valueOf(mEditWorkoutTime.getText().toString()));
                    }
                    if (!mEditCalories.getText().toString().equals("")) {
                        dbManager.updateCalories(Double.valueOf(mEditCalories.getText().toString()));
                    }
                    Toast.makeText(getActivity(), R.string.toast_confirmed_txt, Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    //check for blank input data
                    if (mEditWorkoutTime.getText().toString().equals("") || mEditWeight.getText().toString().equals("") ||
                            mEditAge.getText().toString().equals("") || mEditCalories.getText().toString().equals("")) {

                        Toast.makeText(getActivity(), R.string.toast_blank_values_txt, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    //check for zero value
                    if (mEditWorkoutTime.getText().toString().equals("0") || mEditWeight.getText().toString().equals("0") ||
                            mEditAge.getText().toString().equals("0") || mEditCalories.getText().toString().equals("0")) {

                        Toast.makeText(getActivity(), R.string.toast_zero_values_txt, Toast.LENGTH_SHORT).show();
                        return;
                    }
                }


                dbManager.deleteInputData();

                dbManager.createInputData(new InputDataUtil(genderToString(gender),
                        Integer.valueOf(mEditAge.getText().toString()),
                        Double.valueOf(mEditWeight.getText().toString()),
                        Integer.valueOf(mEditWorkoutTime.getText().toString()),
                        Double.valueOf(mEditCalories.getText().toString())));

                initBurnedCaloriesInAMinuteTable(dbManager, Integer.valueOf(mEditAge.getText().toString()),
                        Double.valueOf(mEditWeight.getText().toString()),
                        genderToString(gender));

                Toast.makeText(getActivity(), R.string.toast_confirmed_txt, Toast.LENGTH_SHORT).show();

            }
        });

        mClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (allowModifications) {
                    alertDialog = new AlertDialog.Builder(getActivity())
                            .setTitle(R.string.app_name)
                            .setMessage(R.string.alert_remove_data_txt)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    alertDialog.dismiss();
                                }
                            })
                            .setIcon(R.mipmap.ic_launcher)
                            .show();

                    //reset input data table
                    dbManager.resetActivitiesTable();
                    dbManager.resetInputDataTable();

                    //reset input parameters
                    mEditAge.setHint(R.string.form_age);
                    mEditCalories.setHint(R.string.form_calories);
                    mEditWorkoutTime.setHint(R.string.form_workout);
                    mEditWeight.setHint(R.string.form_weight);
                    mRadioGender.check(mRadioGender.getChildAt(0).getId());
                }else{
                    Toast.makeText(getActivity(), R.string.fill_form_toast_txt, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
    //util func for data conversion
    private String genderToString(int gender) {
        String genderString = new String();
        if (gender == 0) {
            genderString = "M";
        } else {
            genderString = "F";
        }
        return genderString;
    }
    //util func for data conversion
    private int genderToInt(String gender) {
        int genderInt = 0;
        if (gender.equals("M")) {
            genderInt = 0;
        } else {
            genderInt = 1;
        }
        return genderInt;
    }

    /**
     * This function initialize burnedcalories table in DB or update burnedcalories table if is the case
     */
    private void initBurnedCaloriesInAMinuteTable(SQLiteDBManager dbManager, int age, double weight, String gender) {

        Utils utils = new Utils();
        Map<String, Integer> heartRatePerActivity = utils.getHeartRatePerActivityMap();

        int burnedCaloriesTableSize = dbManager.retrieveBurnedCaloriesMinGroups().size();

        Iterator ac = heartRatePerActivity.keySet().iterator();//iterate on heartRatePerActivity keys

        ac = heartRatePerActivity.keySet().iterator();
        int calMin = 0; //calories for minute temporary variable

        while (ac.hasNext()) {

            String activity_name = String.valueOf(ac.next());
            // burned calories from a single activity in a minute
            calMin = (int) CaloriesCalculator.burnedCaloriesPerMinunte(age, weight, heartRatePerActivity.get(activity_name), gender);

            //if burned calories in a minute for a particolar activity is different from the value stored in the db, update the value
            if (dbManager.retrieveBurnedCaloriesMinGroup(activity_name).getBurnedCaloriesMin() != calMin) {
                //update the vale
                dbManager.updateBurnedCaloriesMin(activity_name, calMin);
                Log.i(TAG, "SQLite DB Row updated: " + activity_name + " " + calMin);
            }
        }

    }

    /**
     * Initialize optimizations table
     *
     * @param dbManager
     */
    private void initOptimizationsTable(SQLiteDBManager dbManager) {
        int optimizationsTableSize = dbManager.retrieveOptimizations().size();
        Utils utils = new Utils();
        if (optimizationsTableSize == 0) {
            int allActivitiesSize = dbManager.retrieveBurnedCaloriesMinGroups().size();
            ArrayList<String> allActivities = utils.getAppDefaultActivities();///////

            //1 element time optimization
            dbManager.createOptimization(new OptimizeUtil(getString(R.string.optimization_1_db), Utils.NULL_VALUE, Utils.MEDIUM_VALUE));
            Log.i(TAG, "optimizations table initialized. Row inserted: time, " + Utils.NULL_VALUE + ", " + Utils.MEDIUM_VALUE + "");

            //2 element activities number optimization
            dbManager.createOptimization(new OptimizeUtil(getString(R.string.optimization_2_db), Utils.NULL_VALUE, Utils.LOW_VALUE));
            Log.i(TAG, "optimizations table initialized. Row inserted: activities, " + Utils.NULL_VALUE + ", " + Utils.LOW_VALUE + "");

            //>3 element activities optimization
            for (int i = 0; i < allActivities.size(); i++) {
                dbManager.createOptimization(new OptimizeUtil(allActivities.get(i), Utils.NEUTRAL_VALUE, Utils.HIGH_VALUE));
                Log.i(TAG, "optimizations table initialized. Row inserted: " + allActivities.get(i) + ", " + Utils.MEDIUM_VALUE + ", " + Utils.HIGH_VALUE + "");
            }


        }
    }

    /**
     * init burned calories table in DLVfitProvider
     * @param dbManager
     */
    private void initBurnedCaloriesTable(SQLiteDBManager dbManager) {
        Utils utils = new Utils();
        Map<String, Integer> heartRatePerActivity = utils.getHeartRatePerActivityMap();

        int burnedCaloriesTableSize = dbManager.retrieveBurnedCaloriesMinGroups().size();

        Iterator ac = heartRatePerActivity.keySet().iterator();//iterate on heartRatePerActivity keys

        //if burnedcalories table is empty
        if (burnedCaloriesTableSize == 0) {
            //create values in db
            while (ac.hasNext()) {
                String activity_name = String.valueOf(ac.next());
                //insert in burnedcalories (in a minute) table
                dbManager.createBurnedCaloriesMinGroup(new BurnedCaloriesUtil(activity_name, 0));
                Log.i(TAG, "burnedcalories table initialized. Row inserted: " + activity_name + ", Cal. in a min:" + 0);
            }
        }
    }
}
