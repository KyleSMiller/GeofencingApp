package com.millerkylegeofencingassn5.api.viewmodels;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class GeoFenceViewModel extends ViewModel {
    private ArrayList<Double[]> points;
    private Double[] origin;
    private final Double[] convertedOrigin = {0.0, 0.0};
    private double area;

    private final double RADIUS = 6378.137; // radius of the earth in km

    public GeoFenceViewModel(){
        points = new ArrayList<>();
    }

    /**
     * Add the origin point of the geo-fence
     * @param lat  the latitude of the origin
     * @param lng  the longitude of the origin
     */
    public void addOrigin(Double lat, Double lng){
        this.origin = new Double[] {lat, lng};
        this.addPoint(lat, lng);
    }

    /**
     * Add a point to the geo-fence perimeter
     * @param lat  the latitude of the new point
     * @param lng  the longitude of the new point
     */
    public void addPoint(Double lat, Double lng){
        Double[] point = new Double[] {lat, lng};
        points.add(point);
    }

    /**
     * Get the area of the geo-fence
     * @return  the area of the geo-fence in square meters
     */
    public double getArea(){
        if (area == 0.0){
            area = calculateArea();
        }
        return area;
    }

    /**
     * Calculate the area of the geo-fence
     * Using the Shoelace formula
     * @return  the area of the geo-fence in square meters
     */
    private double calculateArea(){
        ArrayList<Double[]> convertedPoints = convertToMetricPlane();
        double area = 0.0;

        int j = convertedPoints.size() - 1;  // j is previous vertex
        for (int i = 0; i < convertedPoints.size(); i++){
            area += (convertedPoints.get(j)[0] + convertedPoints.get(i)[0]) * (convertedPoints.get(j)[1] + convertedPoints.get(i)[1]);
            j = i;
        }
        
        return Math.abs(area / 2);
    }

    /**
     * Convert the provided (Lat, Lng) points into points on a flat metric cartesian plane
     * @return  The list of points formatted in {x, y}
     */
    private ArrayList<Double[]> convertToMetricPlane(){
        ArrayList<Double[]> convertedPoints = new ArrayList<>();
        convertedPoints.add(convertedOrigin);
        for (int i = 1; i < points.size(); i++){  // start at 1 to skip origin
            double distX = measureLength(origin[0], origin[1], points.get(i)[0], origin[1]);
            double distY = measureLength(origin[0], origin[1], origin[0], points.get(i)[0]);
            convertedPoints.add(new Double[] {distX, distY});
        }
        return convertedPoints;
    }

    /**
     * Find the distance, in meters, between two points defined by Latitude and Longitude
     * Using the Haversine formula to account for curvature of the earth
     * @param lat1  Latitude of point 1 (Must be the origin)
     * @param lng1  Longitude of point 1 (Must be the origin)
     * @param lat2  Latitude of point 2
     * @param lng2  Longitude of point 2
     * @return  the distance between the points in meters
     */
    private double measureLength(Double lat1, Double lng1, Double lat2, Double lng2){
        double dLat = lat2 * Math.PI / 180 - lat1 * Math.PI / 180;
        double dLng = lng2 * Math.PI / 180 - lng1 * Math.PI / 180;
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
                   Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = RADIUS * c;
        double dMeters =  d * 1000;  // in meters

        // Haversine only finds the distance between 2 points, does not care whether the distance is "up" or "down"
        // Since we are using the distances found with this algorithm to construct a coordinate plane, we do care about "up" and "down"
        // This bit of code should maintain the "up" and "down" relationship of these found distances
        if (lat2 < lat1 || lng2 < lng1){  // distance is negative relative to the origin point {0, 0}
            return 0 - dMeters;
        }
        return dMeters;
    }

}
