package it.unical.mat.dlvfit.utils;

/**
 * Created by Brain At Work on 05/06/2015.
 */

/**
 * Contains the logic program for workouts calculation.
 */
public class LogicProgram {
    private static String program = "activity_to_do(A, HL) | not_activity_to_do(A, HL) :- how_long(A, HL).\n" +
            "total_calories_activity_to_do(CB) :- #sum{CBA, A : activity_to_do(A, HL), calories_burnt_per_activity(A, TNTB), CBA = TNTB * HL} = CB, #int(CB).%}\n" +
            "total_time_activity_to_do(TS) :- #sum{HL, A : activity_to_do(A, HL)} = TS, #int(TS).%}\n" +
            ":- activity_to_do(A, HL1), activity_to_do(A, HL2), HL1 != HL2.\n" +
            ":- remaining_calories_to_burn(RC), total_calories_activity_to_do(CB), RC > CB.\n" +
            ":- remaining_calories_to_burn(RC), total_calories_activity_to_do(CB), CB > RCsurplus, RCsurplus = RC + surplus.\n" +
            ":- max_time(MTS), total_time_activity_to_do(TS), MTS < TS.\n" +
            ":~ optimize(time, _, P), activity_to_do(_, HL). [HL:P]\n" +
            ":~ optimize(activities, _, P), #count{A, HL : activity_to_do(A, HL)} = HM, #int(HM). [HM:P] %}\n" +
            ":~ optimize(A, W, P), activity_to_do(A, _). [W:P]";

    public static String getProgram(){
        return program;
    }
    //inputs for logic program
    public static String caloriesBurntPerActivity(String _activity_group, int _burnedcalories_min){
        return "calories_burnt_per_activity(\""+_activity_group+"\", "+_burnedcalories_min+").";
    }

    public static String howLong(String activity_name, int howLongValue){
        return "how_long(\"" + activity_name + "\", " + howLongValue +").";
    }

    public static String maxTime(int workoutTime){
        return "max_time(" + workoutTime + ").";
    }

    public static String remainingCaloriesToBurn(int remainingCaloriesToBurn) {
        return "remaining_calories_to_burn(" + remainingCaloriesToBurn + ").";

    }

    public static String maxInt(int max){
        return "#maxint = " + max + ".";
    }

    public static String surplus(int surplus){
        return "#const surplus = " + surplus + ".";
    }

    public static String optimize(String optimizationName, int firstLevel, int secondLevel){
        return "optimize(\""+optimizationName+"\", "+firstLevel+", "+secondLevel+").";
    }
}
