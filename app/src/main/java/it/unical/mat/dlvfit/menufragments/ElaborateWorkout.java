package it.unical.mat.dlvfit.menufragments;

/**
 * Created by Brain At Work on 13/05/2015.
 */
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

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

        /*for(int i = 0; i < optimizations.size(); i++){
            Log.i(TAG, optimizations.get(i).getOptimizationName() + "" + optimizations.get(i).getSecondLevel());
        }
        */
        //shows current optimization
        //initialization is based on the initial position in which they are added into the database in the initialization phase
        mOpt1.setText(R.string.optimization_1);
        setLevelIcon(mImageOpt1, optimizations.get(0).getSecondLevel());//first entry in db on initialization: "time"
        mOpt2.setText(R.string.optimization_2);
        setLevelIcon(mImageOpt2, optimizations.get(1).getSecondLevel());//second entry in db on initialization: "activities"
        mOpt3.setText(R.string.optimization_3);
        setLevelIcon(mImageOpt3, optimizations.get(2).getSecondLevel());//from third entry in db on initialization: "activity type"

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
                    Intent openElaborate = new Intent(getActivity(), ResultElaboration.class);
                    startActivity(openElaborate);
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
}

