package it.unical.mat.dlvfit.menufragments;

/**
 * Created by Brain At Work on 13/05/2015.
 */
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import it.unical.mat.dlvfit.AsyncResultVisualization;
import it.unical.mat.dlvfit.AsyncWorkoutElaboration;
import it.unical.mat.dlvfit.ResultElaboration;
import it.unical.mat.dlvfit.R;
import it.unical.mat.dlvfit.contentprovider.OptimizeUtil;
import it.unical.mat.dlvfit.contentprovider.SQLiteDBManager;
import it.unical.mat.dlvfit.dlvelaborationpref.optimizations.OptimizationsFragment;
import it.unical.mat.dlvfit.dlvelaborationpref.preferences.PreferencesFragment;

/**
 * Created by Brain At Work on 13/05/2015.
 */

/**
 * This fragment is preparatory for workouts elaborations. Shows to user the current optimizations setted and allows
 * him to entry in preferences and optimizations settings.
 */
public class ElaborateWorkout extends Fragment {

    private ImageView mOpenOptions, mOpenPreferences, mImageOpt1, mImageOpt2, mImageOpt3;
    private Button mElaborate;
    private TextView mOpt1, mOpt2, mOpt3;
    private SQLiteDBManager dbManager;
    private OptimizationsFragment optionsFragment;
    private PreferencesFragment preferencesFragment;
    private boolean allowElaboration;

    protected static final String TAG = "ElaborateWorkoutFragment";

    public ElaborateWorkout() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbManager = new SQLiteDBManager(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_elaborate_workout, container, false);

        allowElaboration = !dbManager.retrieveInputData().isEmpty();
        mOpenOptions = (ImageView) rootView.findViewById(R.id.options_icon1);
        mOpenPreferences = (ImageView) rootView.findViewById(R.id.preferences_icon2);
        mElaborate = (Button) rootView.findViewById(R.id.elaborate_btn);

        mOpt1  = (TextView) rootView.findViewById(R.id.opt_txt1);
        mOpt2  = (TextView) rootView.findViewById(R.id.opt_txt2);
        mOpt3 = (TextView) rootView.findViewById(R.id.opt_txt3);

        mImageOpt1 = (ImageView) rootView.findViewById(R.id.opt_icon1);
        mImageOpt2 = (ImageView) rootView.findViewById(R.id.opt_icon2);
        mImageOpt3 = (ImageView) rootView.findViewById(R.id.opt_icon3);

        ArrayList<OptimizeUtil> optimizations = dbManager.retrieveOptimizations();

        int maxSecondlevel = findMax(optimizations.get(0).getSecondLevel(),
                optimizations.get(1).getSecondLevel(),
                optimizations.get(2).getSecondLevel());

        //shows current optimization (rank visualization)
        //initialization is based on the initial position in which they are added into the database in the initialization phase
        mOpt1.setText(R.string.optimization_1);
        if (optimizations.get(0).getSecondLevel() != 0) {
            setLevelIcon(mImageOpt1, (maxSecondlevel + 1) - optimizations.get(0).getSecondLevel());//first entry in db on initialization: "time"
        } else {
            setLevelIcon(mImageOpt1, 0);//first entry in db on initialization: "time"
        }
        Log.i(TAG, "Time per workout: " + optimizations.get(0).getSecondLevel());

        mOpt2.setText(R.string.optimization_3);
        if (optimizations.get(2).getSecondLevel() != 0) {
            setLevelIcon(mImageOpt2, (maxSecondlevel + 1) - optimizations.get(2).getSecondLevel());//third entry in db on initialization: "activities"
        } else {
            setLevelIcon(mImageOpt2, 0);//third entry in db on initialization: "activities"
        }
        Log.i(TAG, "Activity type: " + optimizations.get(2).getSecondLevel());

        mOpt3.setText(R.string.optimization_2);
        if (optimizations.get(1).getSecondLevel() != 0) {
            setLevelIcon(mImageOpt3, (maxSecondlevel + 1) - optimizations.get(1).getSecondLevel());//second entry in db on initialization: "activity type"
        }
        else{
            setLevelIcon(mImageOpt3, 0);//second entry in db on initialization: "activity type"
        }
        Log.i(TAG, "Activities per workout: " + optimizations.get(1).getSecondLevel());

        /*
        //shows current optimization (DB values visualization)
        //initialization is based on the initial position in which they are added into the database in the initialization phase
        mOpt1.setText(R.string.optimization_1);
        setLevelIcon(mImageOpt1, optimizations.get(0).getSecondLevel());//first entry in db on initialization: "time"
        mOpt2.setText(R.string.optimization_2);
        setLevelIcon(mImageOpt2, optimizations.get(1).getSecondLevel());//second entry in db on initialization: "activities"
        mOpt3.setText(R.string.optimization_3);
        setLevelIcon(mImageOpt3, optimizations.get(2).getSecondLevel());//from third entry in db on initialization: "activity type"
        */
        optionsFragment = new OptimizationsFragment();
        preferencesFragment = new PreferencesFragment();

        mOpenOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show DialogFragment
                optionsFragment.show(getFragmentManager(), "Options Fragment");
            }
        });

        mOpenPreferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Show DialogFragment
                preferencesFragment.show(getFragmentManager(), "Preferences Fragment");
            }
        });

        //starts a new activity to show answer sets that embasp framework will generate
        mElaborate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(allowElaboration){
                    //synchronous elaboration
                    //Intent elaborationStart = new Intent(getActivity(), ResultElaboration.class);
                    //startActivity(elaborationStart);

                    Toast.makeText(getActivity(),R.string.background_elaboration_toast_txt, Toast.LENGTH_LONG).show();
                    //asynchronous elaboration with notification
                    new AsyncWorkoutElaboration(getActivity()).execute();

                }else{
                    Toast.makeText(getActivity(),R.string.fill_form_toast_txt, Toast.LENGTH_LONG).show();
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
    //the function associates with the correct image to the optimization level setted
    private void setLevelIcon(ImageView iv, int secondLevel){
        if(secondLevel == 0){
            iv.setImageResource(R.drawable.cross);
        }
        if(secondLevel == 1){
            iv.setImageResource(R.drawable.number1);
        }
        if(secondLevel == 2){
            iv.setImageResource(R.drawable.number2);
        }
        if(secondLevel == 3){
            iv.setImageResource(R.drawable.number3);
        }
    }

    //utility function for max calculation between int values
    private int findMax(int... vals) {
        int max = 0;

        for (int d : vals) {
            if (d > max) max = d;
        }
        return max;
    }
}

