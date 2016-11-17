package com.cs4521.bikeroute;

/**
 * Created by EVA Unit 02 on 11/16/2016.
 */
public class Route {
    private String start_longitude;
    private String end_longitude;
    private String start_latitude;
    private String end_latitude;
    private String routeName;
    private String number_traveled;
    private String routeId;

    public String getStart_longitude() {
        return start_longitude;
    }

    public void setStart_longitude(String start_longitude) {
        this.start_longitude = start_longitude;
    }

    public String getEnd_longitude() {
        return end_longitude;
    }

    public void setEnd_longitude(String end_longitude) {
        this.end_longitude = end_longitude;
    }

    public String getStart_latitude() {
        return start_latitude;
    }

    public void setStart_latitude(String start_latitude) {
        this.start_latitude = start_latitude;
    }

    public String getEnd_latitude() {
        return end_latitude;
    }

    public void setEnd_latitude(String end_latitude) {
        this.end_latitude = end_latitude;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getNumber_traveled() {
        return number_traveled;
    }

    public void setNumber_traveled(String number_traveled) {
        this.number_traveled = number_traveled;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public Route (String start_longitude, String end_longitude,
                  String start_latitude, String end_latitude, String routeName,
                  String number_traveled, String routeId) {
        this.start_longitude = start_longitude;
        this.end_longitude = end_longitude;
        this.start_latitude = start_latitude;
        this.end_latitude = end_latitude;
        this.routeName = routeName;
        this.number_traveled = number_traveled;
        this.routeId = routeId;
    }

    @Override
    public String toString() {
        return "Start Lat: " + start_latitude + "\n" +
                "End Lat: " + end_latitude + "\n" +
                "Start Long: " + start_longitude + "\n" +
                "End Long: " + end_longitude + "\n" +
                "Used: " + number_traveled;
    }


    public void incrementNumberTraveled() {
        number_traveled = Integer.toString(Integer.parseInt(number_traveled) + 1);
    }
}
