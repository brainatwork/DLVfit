package it.unical.mat.dlvfit.contentprovider;

/**
 * Created by Dario Campisano on 14/04/2015.
 */

/**
 * ActivityUtil is an utility class that creates objects representing a Detected Activity {@link com.google.android.gms.location.DetectedActivity}
 * @see com.google.android.gms.location.DetectedActivity
 * @see com.google.android.gms.location.ActivityRecognitionApi
 */
public class ActivityUtil {
    private String _id;
    private int _timestamp;
    private String _activity_name;
    private int _confidence_level;

    /**
     * Constructor for Activity detected to retrieve from SQLite DB
     * @param _id
     * @param _timestamp
     * @param _activity_name
     * @param _confidence_level
     */
    public ActivityUtil(String _id, int _timestamp, String _activity_name, int _confidence_level) {
        this._id = _id;
        this._timestamp = _timestamp;
        this._activity_name = _activity_name;
        this._confidence_level = _confidence_level;
    }

    /**
     * Constructor for detected Activity to insert in SQLite DB.
     * In this case _timestamp is created in the DB because _timestamp is an auto incremented id!
     * @param _timestamp
     * @param _activity_name
     * @param _confidence_level
     */
    public ActivityUtil(int _timestamp, String _activity_name, int _confidence_level) {
        this._timestamp = _timestamp;
        this._activity_name = _activity_name;
        this._confidence_level = _confidence_level;
    }

    public int getTimestamp() {
        return _timestamp;
    }

    public void setTimestamp(int _timestamp) {
        this._timestamp = _timestamp;
    }

    public String getActivityName() {
        return _activity_name;
    }

    public void setActivityName(String _activity_name) {
        this._activity_name = _activity_name;
    }

    public int getConfidenceLevel() {
        return _confidence_level;
    }

    public void setConfidenceLevel(int _confidence_level) {
        this._confidence_level = _confidence_level;
    }

    public String getId() {
        return _id;
    }

    public void setId(String _id) {
        this._id = _id;
    }

    //create a String corresponding to a detected_activity Fact
    public String toFact(){
        return "detected_activity("+_timestamp+", \""+_activity_name+"\", "+_confidence_level+").";
    }

}
