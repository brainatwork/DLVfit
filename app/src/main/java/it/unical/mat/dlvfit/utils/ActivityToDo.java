package it.unical.mat.dlvfit.utils;

/**
 * Created by Dario Campisano on 24/04/2015.
 */


import it.unical.mat.embasp.mapper.Predicate;
import it.unical.mat.embasp.mapper.Term;

/**
 * This class represents a DLV aggregate "activity_to_do". Ex. activity_to_do(A, HL)
 */
@Predicate("activity_to_do")
public class ActivityToDo {
    @Term(0)
    private String type;
    @Term(1)
    private int howLong;

    public ActivityToDo(){}

    public ActivityToDo(String type, int howLong) {
        this.type = type;
        this.howLong = howLong;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getHowLong() {
        return howLong;
    }

    public void setHowLong(int howLong) {
        this.howLong = howLong;
    }

    @Override
    public String toString() {
        String newType = this.type.replace("\"","");

        return newType + " for "+ this.howLong +" min.";
    }
}
