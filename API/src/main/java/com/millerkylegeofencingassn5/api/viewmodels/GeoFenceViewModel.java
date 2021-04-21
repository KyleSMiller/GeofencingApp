package com.millerkylegeofencingassn5.api.viewmodels;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class GeoFenceViewModel extends ViewModel {
    private ArrayList<Double[]> points;

    public GeoFenceViewModel(){
        points = new ArrayList<>();
    }

    public void addPoint(Double lat, Double lng){
        Double[] point = new Double[] {lat, lng};
        points.add(point);
    }

    public double calculateArea(){
        return 0.0;
    }

    private double measureLength(Double lat1, Double lng1, Double lat2, Double lng2){
        return 0.0;
    }

}
