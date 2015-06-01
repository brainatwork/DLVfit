package it.unical.mat.dlvfit.contentprovider;

/**
 * Created by Dario Campisano on 16/04/2015.
 */
/**
 * BurnedCaloriesUtil is an utility class that creates objects representing burned calories for one minute group
 */
public class BurnedCaloriesUtil {
    private String _activity_group;
    private int _burnedcalories_min;

    /**
     * Default Constructor
     */
    public BurnedCaloriesUtil(){

    }

    /**
     * Constructor for burned calories for one minute to insert in SQLite DB
     * @param _activity_group
     * @param _burnedcalories_min
     */
    public BurnedCaloriesUtil(String _activity_group, int _burnedcalories_min) {
        this._activity_group = _activity_group;
        this._burnedcalories_min = _burnedcalories_min;
    }

    public String getActivityGroup() {
        return _activity_group;
    }

    public int getBurnedCaloriesMin() {
        return _burnedcalories_min;
    }

    public void setActivityGroup(String _activity_group) {

        this._activity_group = _activity_group;
    }

    public void setBurnedCaloriesMin(int _burnedcalories_min) {
        this._burnedcalories_min = _burnedcalories_min;
    }

    //create a String corresponding to a calories_burnt_per_activity Fact
    public String toFact(){
        return "calories_burnt_per_activity(\""+_activity_group+"\", "+_burnedcalories_min+").";
    }
}
