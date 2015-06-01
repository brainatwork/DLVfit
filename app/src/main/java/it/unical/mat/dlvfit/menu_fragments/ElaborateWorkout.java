package it.unical.mat.dlvfit.menu_fragments;

/**
 * Created by Brain At Work on 13/05/2015.
 */
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import it.unical.mat.dlvfit.ElaborationResult;
import it.unical.mat.dlvfit.R;
import it.unical.mat.dlvfit.contentprovider.OptimizeUtil;
import it.unical.mat.dlvfit.contentprovider.SQLiteDBManager;
import it.unical.mat.dlvfit.dlvelaborationpref.options.OptimizationsFragment;
import it.unical.mat.dlvfit.dlvelaborationpref.preferences.PreferencesFragment;

/**
 * Created by Brain At Work on 13/05/2015.
 */
public class ElaborateWorkout extends Fragment  {

    private ImageView mOpenOptions, mOpenPreferences, mImageOpt1, mImageOpt2, mImageOpt3;
    private Button mElaborate;
    private TextView mOpt1, mOpt2, mOpt3;
    private SQLiteDBManager dbManager;
    private OptimizationsFragment optionsFragment;
    private PreferencesFragment preferencesFragment;

    protected static final String TAG = "ElaborateWorkoutFragment";

    public ElaborateWorkout() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_elaborate_workout, container, false);

        dbManager = new SQLiteDBManager(getActivity());

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

        mOpt1.setText(R.string.optimization_1);
        setLevelIcon(mImageOpt1, optimizations.get(0).getSecondLevel());//first entry in db: "time"
        mOpt2.setText(R.string.optimization_2);
        setLevelIcon(mImageOpt2, optimizations.get(1).getSecondLevel());//second entry in db: "activities"
        mOpt3.setText(R.string.optimization_3);
        setLevelIcon(mImageOpt3, optimizations.get(2).getSecondLevel());//from third entry in db: "activity type"

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

        mElaborate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openElaborate = new Intent(getActivity(), ElaborationResult.class);

                startActivity(openElaborate);
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

