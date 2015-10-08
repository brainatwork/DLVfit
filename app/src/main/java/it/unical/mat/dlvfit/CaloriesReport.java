package it.unical.mat.dlvfit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

import it.unical.mat.dlvfit.contentprovider.InputDataUtil;
import it.unical.mat.dlvfit.contentprovider.SQLiteDBManager;
import it.unical.mat.dlvfit.utils.CaloriesCalculator;
import it.unical.mat.dlvfit.utils.RecognitionApiConstants;
import it.unical.mat.dlvfit.utils.Utils;

/**
 * Shows a graphic report for burned calories
 */
public class CaloriesReport extends AppCompatActivity {
    protected static final String TAG = "CaloriesReport";
    private TextView reportTxt1, reportTxt2, reportTxt3;
    private Toolbar mToolbar;
    private SQLiteDBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_calories_report);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        reportTxt1 = (TextView) findViewById(R.id.calories_report_txt1);
        reportTxt2 = (TextView) findViewById(R.id.calories_report_txt2);
        reportTxt3 = (TextView) findViewById(R.id.calories_report_txt3);

        TextView[] reportTxts = {reportTxt1, reportTxt2, reportTxt3};

        int caloriesToBurn = 0;//calories user need to burn

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

        dbManager = new SQLiteDBManager(getApplicationContext());
        InputDataUtil user = dbManager.retrieveInputData().get(0);
        caloriesToBurn = (int) user.getCalories();

        Log.i(TAG, "Calories to burn: " + caloriesToBurn);

        ArrayList<Entry> entries = new ArrayList<>();

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
            entries.add(new Entry(burned, i));

            reportTxts[i].setText(burned + "cal");

            //total calories burned
            //do sum of all calories burned
            total += burned;
        }

        //add an Entry object representing the remaining calories to burn
        entries.add(new Entry(CaloriesCalculator.caloriesToBurn(caloriesToBurn, total), activities.size()));


        //entries ex.
        /*entries.add(new Entry(16f, 0));
        reportTxts[0].setText(16f + "cal");

        entries.add(new Entry(16f, 1));
        reportTxts[1].setText(16f + "cal");

        entries.add(new Entry(54f, 2));
        reportTxts[2].setText(54f + "cal");

        entries.add(new Entry(14f, 3));*/
        //

        //set chart
        PieDataSet dataset = new PieDataSet(entries, "");
        dataset.setColors(ColorTemplate.COLORFUL_COLORS);
        ArrayList<String> labels = new ArrayList<String>();
        dataset.setDrawValues(false);

        labels.add("Bicycle");
        labels.add("Walking");
        labels.add("Running");
        labels.add("Remaining Calories");

        PieChart chart = (PieChart)
        findViewById(R.id.pieChart1);
        chart.setDrawSliceText(false);
        chart.setUsePercentValues(true);
        chart.setClickable(false);
        //chart.setCenterText("Total Burned Calories\n 86");
        chart.setCenterText("Total Burned Calories\n " + total);
        chart.setDescription("");
        /*chart.getLegend().setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
        chart.getLegend().setTextSize(12.0f);
        chart.getLegend().setFormSize(12.0f);*/
        chart.getLegend().setEnabled(false);
        PieData data = new PieData(labels, dataset);
        chart.setData(data);
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
}
