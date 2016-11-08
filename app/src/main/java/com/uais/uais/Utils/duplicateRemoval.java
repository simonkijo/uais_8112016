package com.uais.uais.Utils;

import java.util.ArrayList;
import java.util.LinkedHashSet;

/**
 * Created by HP on 11/7/2016.
 */

public class duplicateRemoval {

    public static ArrayList<String> dupSender(ArrayList<String> sender){
        return new ArrayList<>(new LinkedHashSet<>(sender));
    }
    public static ArrayList<String> dupSubject(ArrayList<String> subject){
        return new ArrayList<>(new LinkedHashSet<>(subject));
    }
    public static ArrayList<String> dupTime(ArrayList<String> time){
        return new ArrayList<>(new LinkedHashSet<>(time));
    }
}
