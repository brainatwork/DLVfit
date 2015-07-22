package it.unical.mat.dlvfit.menufragments;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import it.unical.mat.dlvfit.R;
import it.unical.mat.dlvfit.contentprovider.BurnedCaloriesUtil;
import it.unical.mat.dlvfit.contentprovider.OptimizeUtil;
import it.unical.mat.dlvfit.contentprovider.SQLiteDBManager;
import it.unical.mat.dlvfit.utils.Utils;

/**
 * Created by Brain At Work on 14/05/2015.
 */

/**
 * This fragment helps user to add a new activity for workouts calculation
 */
public class AddActivity extends Fragment{

    private EditText mEditActivity, mEditCaloriesMin;
    private Button mAdd;

    protected static final String TAG = "AddActivityFragment";

    /**
     * Database Manager Instance for tables handling
     */
    private SQLiteDBManager dbManager;

    public AddActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbManager = new SQLiteDBManager(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_activity, container, false);
        mEditActivity = (EditText) rootView.findViewById(R.id.activity_edit);
        mEditCaloriesMin = (EditText) rootView.findViewById(R.id.calories_min_edit);
        mAdd = (Button) rootView.findViewById(R.id.add_activity_btn);

        Utils utils = new Utils();
        Map<String, Integer> heartRatePerActivity = utils.getHeartRatePerActivityMap();

        final int burnedCaloriesTableSize = dbManager.retrieveBurnedCaloriesMinGroups().size();
        Iterator ac = heartRatePerActivity.keySet().iterator();//iterate on heartRatePerActivity keys

        //if burnedcalories table is empty is initialized with zero values
        if(burnedCaloriesTableSize == 0){
            //create values in db
            while (ac.hasNext()) {
                String activity_name = String.valueOf(ac.next());
                //insert in burnedcalories (in a minute) table
                dbManager.createBurnedCaloriesMinGroup(new BurnedCaloriesUtil(activity_name, 0));
                Log.i(TAG, "burnedcalories table initialized. Row inserted: " + activity_name + ", Cal. in a min:" + 0);
            }
        }

        //confirm and add a new activity in SQLite DB
        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String _activity = mEditActivity.getText().toString().toUpperCase();
                int _calories = 0;
                if(!mEditCaloriesMin.getText().toString().equals("")){
                    _calories = Integer.valueOf(mEditCaloriesMin.getText().toString());
                }

                BurnedCaloriesUtil burnedCaloriesUtil = new BurnedCaloriesUtil();
                burnedCaloriesUtil = dbManager.retrieveBurnedCaloriesMinGroup(_activity);

                if(burnedCaloriesUtil.getActivityGroup().equals("") && !_activity.equals("") && _calories != 0 && valueControl(_activity)){
                    dbManager.createBurnedCaloriesMinGroup(new BurnedCaloriesUtil(_activity, _calories));
                    //first and second value for optimizations that will be used by the logic program via embasp framework
                    int toInsert_firstLevel = Utils.NEUTRAL_VALUE;
                    int toInsert_secondLevel = 0;

                    //get actual first and second level from optimizations except "time" and "activities per workout"
                    ArrayList<OptimizeUtil> optimizations = dbManager.retrieveOptimizations();

                    //search for the correct second level
                    for(int i = 0; i < optimizations.size(); i++){
                        if(!optimizations.get(i).getOptimizationName().equals(R.string.optimization_1_db)
                                && !optimizations.get(i).getOptimizationName().equals(R.string.optimization_2_db)){
                            toInsert_secondLevel = optimizations.get(i).getSecondLevel();
                            break;
                        }
                    }
                    //create a default optimization for the activity added
                    dbManager.createOptimization(new OptimizeUtil(_activity, toInsert_firstLevel, toInsert_secondLevel));

                    Log.i(TAG, "Activity added: " + _activity + " " + _calories);
                    Log.i(TAG, "Optimization initialized. Row inserted: "+ _activity +" , "+ toInsert_firstLevel +" ," + toInsert_secondLevel);

                    Toast.makeText(getActivity(), R.string.add_activity_toast, Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getActivity(), R.string.add_activity_error_toast, Toast.LENGTH_LONG).show();
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
    //utility func
    private boolean valueControl(String _activity){
        if(_activity.equals("WALKING") || _activity.equals("BICYCLE") || _activity.equals("STILL") || _activity.equals("RUNNING")){
            return false;
        }
        return true;
    }
}
