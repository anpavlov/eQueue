package com.sudo.equeue.models;

import com.google.android.gms.maps.model.LatLng;
import com.sudo.equeue.models.basic.PossibleError;

import java.util.List;

public class Queue extends PossibleError {

    private int qid;
    private String name;
//    private String alias;
//    private boolean isIn;
    private String description;
    private int users_quantity;
    private String address;
    private int wait_time;
    private int in_front;
    private String coords;
    private boolean isPassed = false;

    public boolean isPassed() {
        return isPassed;
    }

    public void setPassed(boolean isPassed) {
        this.isPassed = isPassed;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getWaitTime() {
        return wait_time;
    }

    public void setWaitTime(int wait_time) {
        this.wait_time = wait_time;
    }

    public int getInFront() {
        return in_front;
    }

    public boolean IsIn() { return in_front != -1; }

    public void setInFront(int in_front) {
        this.in_front = in_front;
    }

    public int getUsersQuantity() {
        return users_quantity;
    }


    public void setUsersQuantity(int users_quantity) {
        this.users_quantity = users_quantity;
    }

    public int getQid() {
        return qid;
    }

    public void setQueueId(int queueId) {
        this.qid = queueId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getCoords() {
        return coords;
    }

    public void setCoords(String coords) {
        this.coords = coords;
    }

    public LatLng getLatLng() {
        if (this.coords != null && this.coords != "0,0") {
            String[] coords = this.coords.split(",");
            Float lat = Float.valueOf(coords[0]);
            Float lon = Float.valueOf(coords[1]);
            return new LatLng(lat, lon);
        } else
            return null;
    }
}
