package it.unical.mat.dlvfit.dlvelaborationpref.preferences;

/**
 * Created by Brain At Work on 23/05/2015.
 */

/**
 * Represents a preference list item
 */
public class ListItem {

    private String activityName;
    private int rate;

    public ListItem(String activityName) {
        this.activityName = activityName;
        this.rate = 0;
    }

    public ListItem(String activityName, int rate) {
        this.activityName = activityName;
        this.rate = rate;
    }
    public ListItem(int rate) {this.rate = rate;}

    public String getActivityName() {
        return this.activityName;
    }

    public void setNome(String activityName) {
        this.activityName = activityName;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }
}
