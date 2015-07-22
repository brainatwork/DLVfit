package it.unical.mat.dlvfit.dlvelaborationpref.preferences;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import it.unical.mat.dlvfit.MainActivity;
import it.unical.mat.dlvfit.R;
import it.unical.mat.dlvfit.contentprovider.OptimizeUtil;
import it.unical.mat.dlvfit.contentprovider.SQLiteDBManager;
import it.unical.mat.dlvfit.utils.Utils;

/**
 * Created by Brain At Work on 22/05/2015.
 */
public class PreferencesFragment extends DialogFragment {
    protected static final String TAG = "PreferencesFragment";

    private ImageView mReset, mConfirm;
    private ListView listView;
    private AlertDialog alertDialog;
    private ArrayList<OptimizeUtil> optimizations;
    int firstLevel;
    List list;
    private SQLiteDBManager dbManager;

    public PreferencesFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialogfragment_preferences, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        //UI
        mReset = (ImageView) rootView.findViewById(R.id.reset_preferences);
        mConfirm = (ImageView) rootView.findViewById(R.id.confirm_preferences);
        listView = (ListView) rootView.findViewById(R.id.listViewDemo);

        dbManager = new SQLiteDBManager(getActivity());
        optimizations = dbManager.retrieveOptimizations();

        list = new LinkedList();

        firstLevel = 0;
        final ArrayList<String> optimizationsStoredNames = new ArrayList<String>();

        //{@ListItem} initialization from SQLite db entries
        for (int i = 0; i < optimizations.size(); i++) {
            if (!optimizations.get(i).getOptimizationName().equals(getString(R.string.optimization_1_db)) &&
                    !optimizations.get(i).getOptimizationName().equals(getString(R.string.optimization_2_db))) {

                firstLevel = optimizations.get(i).getFirstLevel();

                String notFormattedName = optimizations.get(i).getOptimizationName();
                optimizationsStoredNames.add(notFormattedName);
                String activityName = "";

                if (!notFormattedName.contains("ON_")) {
                    activityName = notFormattedName.substring(0, 1).toUpperCase() + notFormattedName.substring(1).toLowerCase();
                } else {
                    notFormattedName = notFormattedName.replace("ON_", "");
                    activityName = notFormattedName.substring(0, 1).toUpperCase() + notFormattedName.substring(1).toLowerCase();
                }
                if (optimizations.get(i).getFirstLevel() == -1) {
                    list.add(new ListItem(activityName, Utils.NULL_VALUE));
                } else {
                    list.add(new ListItem(activityName, Utils.HIGH_VALUE - firstLevel));
                }
            }

        }

        final CustomAdapter adapter = new CustomAdapter(getActivity(), list);
        listView.setAdapter(adapter);

        Log.i(TAG, "List View Elements " + adapter.getCount());

        //reset all preferences setted
        mReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.clear();
                for (int i = 0; i < optimizations.size(); i++) {
                    if (!optimizations.get(i).getOptimizationName().equals(getString(R.string.optimization_1_db)) &&
                            !optimizations.get(i).getOptimizationName().equals(getString(R.string.optimization_2_db))) {

                        String notFormattedName = optimizations.get(i).getOptimizationName();
                        optimizationsStoredNames.add(notFormattedName);
                        String activityName = "";

                        if (!notFormattedName.contains("ON_")) {
                            activityName = notFormattedName.substring(0, 1).toUpperCase() + notFormattedName.substring(1).toLowerCase();
                        } else {
                            notFormattedName = notFormattedName.replace("ON_", "");
                            activityName = notFormattedName.substring(0, 1).toUpperCase() + notFormattedName.substring(1).toLowerCase();
                        }
                        if (optimizations.get(i).getFirstLevel() == -1) {
                            list.add(new ListItem(activityName, Utils.NULL_VALUE));
                        } else {
                            list.add(new ListItem(activityName, Utils.HIGH_VALUE - firstLevel));
                        }

                        //list.add(new ListItem(optimizations.get(i).getOptimizationName(), Utils.NULL_VALUE));
                    }
                    listView.setAdapter(adapter);
                }
            }
        });

        //save the current preferences setted by user
        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                                for (int i = 0; i < adapter.getCount(); i++) {
                                    ListItem item = (ListItem) listView.getAdapter().getItem(i);
                                    Log.i(TAG, "Preference value of " + item.getActivityName() + " = " + " " + optimizationsStoredNames.get(i) + " " + item.getRate());
                                    dbManager.updateOptimizationsFirstLevel(optimizationsStoredNames.get(i), (Utils.HIGH_VALUE - item.getRate()));
                                }
                getDialog().dismiss();
            }
        });

        return rootView;
    }

}
