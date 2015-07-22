package it.unical.mat.dlvfit;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

import it.unical.mat.dlvfit.contentprovider.SQLiteDBManager;
import it.unical.mat.dlvfit.utils.RecognitionApiConstants;
import it.unical.mat.dlvfit.utils.Utils;

/**
 * Shows a graphic report for burned calories in time
 */

public class TimeReport extends AppCompatActivity {

    protected static final String TAG = "TimeReport";

    private TextView reportTxt1, reportTxt2,reportTxt3, reportTxt4;

    private Toolbar mToolbar;

    private SQLiteDBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_time_report);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        reportTxt1 = (TextView) findViewById(R.id.report_txt1);
        reportTxt2 = (TextView) findViewById(R.id.report_txt2);
        reportTxt3 = (TextView) findViewById(R.id.report_txt3);
        reportTxt4 = (TextView) findViewById(R.id.report_txt4);

        TextView[] reportTxts = {reportTxt1, reportTxt2, reportTxt3, reportTxt4};

        dbManager = new SQLiteDBManager(getApplicationContext());

        Utils utils = new Utils();

        //activityGroup set to show on chart
        ArrayList<String> activities = utils.getMonitoredActivity();

        int minutes = 0; //temporary variable for minutes dedicated to a particular activity

        int seconds = (int) RecognitionApiConstants.DETECTION_INTERVAL_IN_MILLISECONDS / 1000; //recognition interval is 2000 ms!

        ArrayList<BarEntry> entries = new ArrayList<>();

        for(int i = 0; i < activities.size(); i++) {
            //to obtain the time in seconds we have to multiply the total number of a particular recognized activity,
            // with a certain confidence level, and "seconds". To obtain the time in minutes we have to divide for 60.

            String activity_name = activities.get(i);

            minutes = (dbManager.retrieveActivity((int) RecognitionApiConstants.ADMISSIBLE_CONFIDENCE_LEVEL, activity_name).size() * (seconds)) / 60;
            Log.i(TAG, "Minutes per activity: " + activity_name + " " + minutes);

            entries.add(new BarEntry(minutes, i));
            reportTxts[i].setText(minutes+"min");
        }

        //entries ex.
        //entries.add(new BarEntry(4, 0));
        //entries.add(new BarEntry(8, 1));
        //entries.add(new BarEntry(6, 2));
        //entries.add(new BarEntry(12, 3));

        BarDataSet dataset = new BarDataSet(entries, "");
        dataset.setColors(ColorTemplate.COLORFUL_COLORS);
        ArrayList<String> labels = new ArrayList<String>();
        dataset.setDrawValues(false);
        labels.add("Bicycle");
        labels.add("Walking");
        labels.add("Running");
        labels.add("Still");

        BarChart chart = (BarChart) findViewById(R.id.chart);
        chart.setDrawValuesForWholeStack(false);
        chart.getLegend().setEnabled(false);
        BarData data = new BarData(labels, dataset);
        chart.setDescription("");
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
