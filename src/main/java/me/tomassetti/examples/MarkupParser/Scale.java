package me.tomassetti.examples.MarkupParser;

import java.util.Vector;

class Scale {
    private static final int SCALE_SIZE = 7;
    Vector<Integer> degrees;

    public Scale() {
        degrees = new Vector<>(SCALE_SIZE);
        degrees.add(0); //root
        degrees.add(2); //maj ninth
        degrees.add(4); //maj third
        degrees.add(5); //perfect eleventh
        degrees.add(7); //perfect fifth
        degrees.add(9); //maj thirtinh
        degrees.add(10);//min seventh
    }

    public void setDegrees(Vector<Integer> degrees) {
        this.degrees = degrees;
    }

    public int getDegree(int index) {
        return degrees.get(normalize(index));
    }

    private int normalize(int degree) {
        if(degree == 0)
            return 0;
        return (degree-1)%SCALE_SIZE;
    }

    public void sharp(int degree) {
        degree = normalize(degree);
        this.degrees.set(degree, degrees.get(degree) + 1);
    }

    public void flat(int degree) {
        degree = normalize(degree);
        this.degrees.set(degree, degrees.get(degree) - 1);
    }

    public void sharp(int[] degrees) {
        for (int i : degrees) sharp(i);
    }

    public void flat(int[] degrees) {
        for (int i : degrees) flat(i);
    }

    public void sharpflat(int degreesSharp, int degreesFlat) {
        sharp(degreesSharp);
        flat(degreesFlat);
    }
    public void sharpflat(int[] degreesSharp, int degreesFlat) {
        for (int i : degreesSharp) sharp(i);
        flat(degreesFlat);
    }
    public void sharpflat(int degreesSharp, int[] degreesFlat) {
        sharp(degreesSharp);
        for (int i : degreesFlat) flat(i);
    }
    public void sharpflat(int[] degreesSharp, int[] degreesFlat) {
        for (int i : degreesSharp) sharp(i);
        for (int i : degreesFlat) flat(i);
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
}