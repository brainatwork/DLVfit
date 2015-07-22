package it.unical.mat.dlvfit.contentprovider;

/**
 * Created by Brain At Work on 27/04/2015.
 */

/**
 * InputDataUtil is a utility class to represent an object that contains user input data
 */
public class InputDataUtil {
    private double _weight;
    private int _age;
    private double _calories;
    private int _workoutTime;
    private String _gender;

    public InputDataUtil() {

    }

    /**
     * Constructor for input to insert in SQLite DB
     * @param _gender
     * @param _age
     * @param _weight
     * @param _workoutTime
     * @param _calories
     */
    public InputDataUtil(String _gender, int _age, double _weight, int _workoutTime, double _calories){
        this._gender = _gender;
        this._age = _age;
        this._weight = _weight;
        this._workoutTime = _workoutTime;
        this._calories = _calories;
    }

    //getters and setters
    public double getWeight() {
        return _weight;
    }

    public void setWeight(double _weight) {
        this._weight = _weight;
    }

    public int getAge() {
        return _age;
    }

    public void setAge(int _age) {
        this._age = _age;
    }

    public double getCalories() {
        return _calories;
    }

    public void setCalories(int _calories) {
        this._calories = _calories;
    }

    public int getWorkoutTime() {
        return _workoutTime;
    }

    public void setWorkoutTime(int _workoutTime) {
        this._workoutTime = _workoutTime;
    }

    public String getGender() {
        return _gender;
    }

    public void setGender(String _gender) {this._gender = _gender;
    }
}
