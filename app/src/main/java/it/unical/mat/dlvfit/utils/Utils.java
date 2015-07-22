package it.unical.mat.dlvfit.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Brain At Work on 21/05/2015.
 */
public class Utils {
    //optimization first level constants
    public final static int HIGH_VALUE = 3;
    public final static int MEDIUM_VALUE = 2;
    public final static int LOW_VALUE = 1;
    public final static int NULL_VALUE = 0;
    public final static int NEUTRAL_VALUE = -1;

    private Map<String, Integer> heartRatePerActivity ;//stores activities and the corresponding heart rate

    private ArrayList<String> monitoredActivities;
    private ArrayList<String> appDefaultActivities;

    public Utils(){
        heartRatePerActivity = new HashMap<String, Integer>();
        //init an hashmap containing activity monitored and is average heart rate
        heartRatePerActivity.put("WALKING", 80);
        heartRatePerActivity.put("RUNNING", 140);
        heartRatePerActivity.put("ON_BICYCLE",100);
        heartRatePerActivity.put("PUSHUPS", 160);
        heartRatePerActivity.put("CRUNCHES", 160);

        appDefaultActivities = new ArrayList<String>(heartRatePerActivity.keySet());

        monitoredActivities = new ArrayList<String>();
        //contanis default activities monitored by DLVfit
        monitoredActivities.add("ON_BICYCLE");
        monitoredActivities.add("WALKING");
        monitoredActivities.add("RUNNING");
        monitoredActivities.add("STILL");
    }
    //getters
    public ArrayList<String> getMonitoredActivity(){
        return monitoredActivities;
    }
    public ArrayList<String> getAppDefaultActivities(){
        return appDefaultActivities;
    }

    public Map<String, Integer> getHeartRatePerActivityMap(){
        return heartRatePerActivity;
    }
}
