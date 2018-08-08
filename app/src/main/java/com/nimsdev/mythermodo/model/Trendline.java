package com.nimsdev.mythermodo.model;

public class Trendline {

    public final float intersection;
    public final float slope;

    public Trendline(float slope, float intersection) {
        this.slope = slope;
        this.intersection = intersection;
    }

}
