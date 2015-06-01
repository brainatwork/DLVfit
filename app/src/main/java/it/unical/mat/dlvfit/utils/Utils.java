package it.unical.mat.dlvfit.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Brain At Work on 21/05/2015.
 */
public class Utils {

    private Map<String, Integer> heartRatePerActivity ;//stores activities and the corresponding heart rate

    private ArrayList<String> monitoredActivities;

    public Utils(){
        heartRatePerActivity = new HashMap<String, Integer>();
        heartRatePerActivity.put("WALKING", 80);
        heartRatePerActivity.put("RUNNING", 140);
        heartRatePerActivity.put("ON_BICYCLE",100);
        heartRatePerActivity.put("PUSHUPS", 160);
        heartRatePerActivity.put("CRUNCHES", 160);

        monitoredActivities = new ArrayList<String>();

        monitoredActivities.add("ON_BICYCLE");
        monitoredActivities.add("WALKING");
        monitoredActivities.add("RUNNING");
        monitoredActivities.add("STILL");
    }

    public ArrayList<String> getMonitoredActivity(){
        return monitoredActivities;
    }

    public Map<String, Integer> getHeartRatePerActivityMap(){
        return heartRatePerActivity;
    }
}
