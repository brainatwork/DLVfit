package it.unical.mat.dlvfit.dlvelaborationpref.optimizations;

import android.support.v4.app.DialogFragment;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import it.unical.mat.dlvfit.MainActivity;
import it.unical.mat.dlvfit.R;
import it.unical.mat.dlvfit.contentprovider.OptimizeUtil;
import it.unical.mat.dlvfit.contentprovider.SQLiteDBManager;

/**
 * Created by Brain At Work on 22/05/2015.
 */

/**
 * OptimizationsFragment contains a graphical interface for setting optimizations using drag and drop
 */
public class OptimizationsFragment extends DialogFragment {
    private static TextView[] choices; //contains text views corresponding to user choices
    private static TextView[] options; //contains text views correspondint to user options
    private static String[] choice_txts;
    private boolean noChoiceSelected;
    private static ArrayList<String> optimizationsChoosed;//contains optimizations choosed by user
    private static ArrayList<String> optimizationsNotChoosed;//contains optimizations not choosed by user
    private static SQLiteDBManager dbManager; //reference to SQLite db manager
    private ArrayList<OptimizeUtil> optimizations;
    //private AlertDialog alertDialog;
    private int level = 0;
    protected static final String TAG = "OptimizationsFragment";

    //text views being dragged and dropped onto
    private TextView option1, option2, option3, choice1, choice2, choice3, dropTitle;
    private ImageView mReset, mConfirm;


    public OptimizationsFragment() {

        choices = new TextView[3];
        options = new TextView[3];
        optimizationsChoosed = new ArrayList<String>();
        optimizationsNotChoosed = new ArrayList<String>();
        noChoiceSelected = true;
        level = 0;

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        final View rootView = inflater.inflate(R.layout.dialogfragment_optimizations, container, false);

        dbManager = new SQLiteDBManager(getActivity());
        optimizations = dbManager.retrieveOptimizations();

        //UI
        dropTitle = (TextView) rootView.findViewById(R.id.drop_title);
        mReset = (ImageView) rootView.findViewById(R.id.reset_optimizations);
        mConfirm = (ImageView) rootView.findViewById(R.id.confirm_optimizations);
        //get both sets of text views
        //views to drag
        option1 = (TextView) rootView.findViewById(R.id.option_1);
        option2 = (TextView) rootView.findViewById(R.id.option_2);
        option3 = (TextView) rootView.findViewById(R.id.option_3);
        //save TextViews content String into an array
        options[0] = option1;
        options[1] = option2;
        options[2] = option3;

        /*for (int i = 0; i < options.length; i++) {
            Log.i(TAG, "OPTIONS " + options[i].getText().toString());
        }*/


        //views to drop onto
        choice1 = (TextView) rootView.findViewById(R.id.choice_1);
        choice2 = (TextView) rootView.findViewById(R.id.choice_2);
        choice3 = (TextView) rootView.findViewById(R.id.choice_3);

        //save TextViews content String into an array
        choices[0] = choice1;
        choices[1] = choice2;
        choices[2] = choice3;

        /*for (int i = 0; i < choices.length; i++) {
            Log.i(TAG, "CHOICES " + choices[i].getText().toString());
        }*/

        choice_txts = new String[choices.length];
        for (int i = 0; i < choice_txts.length; i++) {
            choice_txts[i] = choices[i].getText().toString();
            Log.i(TAG, "CHOICE_TXTS " + choice_txts[i]);
        }

        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //contains the algorithm for optimizations setting
                setOptimizations();
            }
        });

        //reset user choices
        mReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < choice_txts.length; i++) {
                    choices[i].setText(choice_txts[i]);
                }
                for (int i = 0; i < options.length; i++) {
                    options[i].setVisibility(View.VISIBLE);
                }
                optimizationsChoosed.clear();
                optimizationsNotChoosed.clear();
            }
        });

        //set touch listeners
        option1.setOnTouchListener(new ChoiceTouchListener());
        option2.setOnTouchListener(new ChoiceTouchListener());
        option3.setOnTouchListener(new ChoiceTouchListener());

        //set drag listeners
        choice1.setOnDragListener(new ChoiceDragListener());
        choice2.setOnDragListener(new ChoiceDragListener());
        choice3.setOnDragListener(new ChoiceDragListener());

        return rootView;
    }

    /**
     * ChoiceTouchListener will handle touch events on draggable views
     */
    private final class ChoiceTouchListener implements View.OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                /*
                 * Drag details: we only need default behavior
				 * - clip data could be set to pass data as part of drag
				 * - shadow can be tailored
				 */
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                //start dragging the item touched
                view.startDrag(data, shadowBuilder, view, 0);
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * DragListener will handle dragged views being dropped on the drop area
     * - only the drop action will have processing added to it as we are not
     * - amending the default behavior for other parts of the drag process
     */
    private class ChoiceDragListener implements View.OnDragListener {

        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    //no action necessary
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    //no action necessary
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    //no action necessary
                    break;
                case DragEvent.ACTION_DROP:
                    noChoiceSelected = false;
                    //handle the dragged view being dropped over a drop view
                    View view = (View) event.getLocalState();

                    //stop displaying the view where it was before it was dragged
                    view.setVisibility(View.INVISIBLE);

                    //stop using the view where it was before it was dragged
                    //view.setEnabled(false);

                    //view dragged item is being dropped on
                    TextView dropTarget = (TextView) v;

                    //view being dragged and dropped
                    TextView dropped = (TextView) view;

                    //update the text in the target view to reflect the data being dropped
                    dropTarget.setText(dropped.getText());

                    //make it bold to highlight the fact that an item has been dropped
                    //dropTarget.setTypeface(Typeface.DEFAULT_BOLD);

                    //if an item has already been dropped here, there will be a tag
                    Object tag = dropTarget.getTag();

                    //if there is already an item here, set it back visible in its original place
                    if (tag != null) {
                        //the tag is the view id already dropped here
                        int existingID = (Integer) tag;

                        //set the original view visible again
                        getDialog().findViewById(existingID).setVisibility(View.VISIBLE);
                    }

                    //set the tag in the target view being dropped on - to the ID of the view being dropped
                    dropTarget.setTag(dropped.getId());
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    //no action necessary
                    break;
                default:
                    break;
            }
            return true;
        }

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    //handle optimizations choosed by user and inserts them into SQLite db with {@SQLiteDBManager}
    private void setOptimizations() {
        boolean selected = false;

        String choosed = new String();

        for (int i = 0; i < options.length; i++) {
            optimizationsNotChoosed.add(options[i].getText().toString());
        }

        for (int j = 0; j < choices.length; j++) {
            choosed = choices[j].getText().toString();
            //Log.i(TAG, "CHOOSED FOR 1 " + choosed);
            selected = false;
            for (int i = 0; i < options.length; i++) {
                //Log.i(TAG, "CHOOSED FOR 2 " + options[i].getText().toString());
                if (choosed.equals(options[i].getText().toString())) {
                    selected = true;
                }
            }
            if (selected) {
                //Log.i(TAG, "SELECTED " + choosed);
                optimizationsChoosed.add(choosed);
                optimizationsNotChoosed.remove(choosed);
            }
        }

        level = optimizationsChoosed.size();

        for (int i = 0; i < optimizationsChoosed.size(); i++) {
            Log.i(TAG, "Choosed " + optimizationsChoosed.get(i) + " level " + level);
            updateChoosed(i, level);
            level--;
        }
        for (int i = 0; i < optimizationsNotChoosed.size(); i++) {
            Log.i(TAG, "Not Choosed " + optimizationsNotChoosed.get(i) + " level " + 0);
            updateNotChoosed(i);
        }
        optimizationsChoosed.clear();
        optimizationsNotChoosed.clear();
        getDialog().dismiss();
    }

    /**
     * update in SQLite db the correct optimizations choosed by user
     *
     * @param index
     * @param level
     */
    private void updateChoosed(int index, int level) {
        if (optimizationsChoosed.get(index).equals(getString(R.string.optimization_1))) {
            Log.i(TAG, "update " + getString(R.string.optimization_1_db) + " level " + level);
            dbManager.updateOptimizationsSecondLevel(getString(R.string.optimization_1_db), level);
        }
        if (optimizationsChoosed.get(index).equals(getString(R.string.optimization_2))) {
            Log.i(TAG, "update " + getString(R.string.optimization_2_db) + " level " + level);
            dbManager.updateOptimizationsSecondLevel(getString(R.string.optimization_2_db), level);
        }
        if (!optimizationsChoosed.get(index).equals(getString(R.string.optimization_1)) &&
                !optimizationsChoosed.get(index).equals(getString(R.string.optimization_2))) {

            for (int i = 0; i < optimizations.size(); i++) {
                if (!optimizations.get(i).getOptimizationName().equals(getString(R.string.optimization_1_db))
                        && !optimizations.get(i).getOptimizationName().equals(getString(R.string.optimization_2_db))) {

                    Log.i(TAG, "update " + getString(R.string.optimization_3) + " level " + level);
                    dbManager.updateOptimizationsSecondLevel(optimizations.get(i).getOptimizationName(), level);

                }
            }
        }
    }

    /**
     * update in SQLite db the correct optimizations unchoosed by user
     *
     * @param index
     */
    private void updateNotChoosed(int index) {
        if (optimizationsNotChoosed.get(index).equals(getString(R.string.optimization_1))) {
            dbManager.updateOptimizationsSecondLevel(getString(R.string.optimization_1_db), 0);
        }
        if (optimizationsNotChoosed.get(index).equals(getString(R.string.optimization_2))) {
            dbManager.updateOptimizationsSecondLevel(getString(R.string.optimization_2_db), 0);
        }
        if (!optimizationsNotChoosed.get(index).equals(getString(R.string.optimization_1)) &&
                !optimizationsNotChoosed.get(index).equals(getString(R.string.optimization_2))) {

            for (int i = 0; i < optimizations.size(); i++) {
                if (!optimizations.get(i).getOptimizationName().equals(getString(R.string.optimization_1_db))
                        && !optimizations.get(i).getOptimizationName().equals(getString(R.string.optimization_2_db))) {

                    Log.i(TAG, "update IF 3" + getString(R.string.optimization_3) + " level " + 0);
                    dbManager.updateOptimizationsSecondLevel(optimizations.get(i).getOptimizationName(), 0);

                }
            }
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        /**
         * Return to MainActivity and open ElaborateWorkout Fragment with the correct optimizations choosed
         */
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(MainActivity.FRAGMENT_INTENT_KEY, 2);
        startActivity(intent);

    }
}

