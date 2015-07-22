package it.unical.mat.dlvfit.utils;

/**
 * Created by Dario Campisano on 16/04/2015.
 * CaloriesCalculator is an utility class used for calculus.
 */
public class CaloriesCalculator {

    /**
     * Constructor
     */
    public CaloriesCalculator() {

    }

    /**
     * Calculate burned calories per minute with the help of the following parameters
     * @param age
     * @param weight
     * @param heartRate
     * @param gender
     * @return
     */
    public static double burnedCaloriesPerMinunte(int age, double weight, int heartRate, String gender) {
        if(gender == "M"){
            return (-55.0969+(0.6309*heartRate) +( 0.1988*weight) + (0.2017*age)) * 0.24;
        }
        return (-20.4022+(0.4472*heartRate) -( 0.1263*weight) + (0.074*age)) * 0.24;

    }

    /**
     * calculate calories remaining to burn after a activity recognizations from GoogleRecognitionApi
     * @param needToBurn
     * @param totalCaloriesBurned
     * @return
     */
    public static int caloriesToBurn(int needToBurn, int totalCaloriesBurned) {
        //calories to burn
        //as difference between "needToBurn" (user wants to burn) and "totalCaloriesBurned" for all activities
        int caloriesToBurn = needToBurn - totalCaloriesBurned;

        //if difference between calories (user wants to burn) and total calories burned for all activities
        //is negative, the amount of calories to burn is 0 because burned calories are much more of the calories the user wanted to burn

        if(caloriesToBurn < 0){
            caloriesToBurn = 0;
        }

        return caloriesToBurn;
    }




}
