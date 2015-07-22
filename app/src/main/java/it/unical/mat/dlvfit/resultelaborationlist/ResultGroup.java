package it.unical.mat.dlvfit.resultelaborationlist;

/**
 * Created by Brain At Work on 07/06/2015.
 */
import java.util.ArrayList;
import java.util.List;

public class ResultGroup {

    public String string;
    public final List<String> children = new ArrayList<String>();

    public ResultGroup(String string) {
        this.string = string;
    }

}
