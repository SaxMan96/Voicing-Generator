package me.tomassetti.examples.MarkupParser;

import java.util.Vector;

public class Voicing {
    private Vector<Integer> degrees;
    private boolean set;

    public Voicing(){
        degrees = new Vector<>();
        set = false;
    }

    public int getDegree(int index) {
        return degrees.get(index);
    }
    public int size(){
        return degrees.size();
    }

    public void create(MarkupParser.VoicingRuleContext context) {
        for (MarkupParser.DegreeContext node : context.degree())
            degrees.add(visitDegree(node));
        set = true;
    }

    private int visitDegree(MarkupParser.DegreeContext context) {
        int retVal = 0;
        if      (context.Root()     != null) retVal = 1;
        else if (context.Two()      != null) retVal = 2;
        else if (context.Nine()     != null) retVal = 2;
        else if (context.Three()    != null) retVal = 3;
        else if (context.Four()     != null) retVal = 4;
        else if (context.Eleven()   != null) retVal = 4;
        else if (context.Five()     != null) retVal = 5;
        else if (context.Six()      != null) retVal = 6;
        else if (context.Thirteen() != null) retVal = 6;
        else if (context.Seven()    != null) retVal = 7;
        return retVal;
    }
    @Override
    public String toString(){
        if(degrees.size() == 0)
            return "[]";
        String retVal = "[";
        for(int i: degrees)
            retVal += i+", ";
        return retVal.substring(0,retVal.length()-2) + "]";
    }

    public void clear() {
        degrees.clear();
        set = false;
    }

    public boolean isSet() {
        return set;
    }
}

