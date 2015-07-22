package it.unical.mat.dlvfit.contentprovider;

/**
 * Created by Brain At Work on 30/05/2015.
 */

/**
 * Utility class representing an object that contains optimizations for the logic program handled by embasp data
 */
public class OptimizeUtil {
    private String _optimizationName;
    private int _firstLevel;
    private int _secondLevel;

    /**
     * Constructor for optimizations to insert in SQLite DB
     * @param _optimizationName
     * @param _firtLevel
     * @param _secondLevel
     */
    public OptimizeUtil(String _optimizationName, int _firtLevel, int _secondLevel) {
        this._optimizationName = _optimizationName;
        this._firstLevel = _firtLevel;
        this._secondLevel = _secondLevel;
    }

    public String getOptimizationName() {
        return _optimizationName;
    }

    public void setOptimizationName(String _optimizationName) {
        this._optimizationName = _optimizationName;
    }

    public int getFirstLevel() {
        return _firstLevel;
    }

    public void setFirstLevel(int _firstLevel) {
        this._firstLevel = _firstLevel;
    }

    public int getSecondLevel() {
        return _secondLevel;
    }

    public void setSecondLevel(int _secondLevel) {
        this._secondLevel = _secondLevel;
    }
}
