package com.addah.tourplus;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Created by choxmi on 6/13/17.
 */

public class LocationDetails implements Serializable{
    private String loc_name;
    private double lat;
    private double lng;
    private int waiting_time;
    private String details;

    public String getLoc_name() {
        return loc_name;
    }

    public void setLoc_name(String loc_name) {
        this.loc_name = loc_name;
    }

    public int getWaiting_time() {
        return waiting_time;
    }

    public void setWaiting_time(int waiting_time) {
        this.waiting_time = waiting_time;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
