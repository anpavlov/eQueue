package com.sudo.equeueadmin.models;

import com.sudo.equeueadmin.models.basic.PossibleError;

import java.util.List;

/**
 * Created by orange on 24.02.16.
 */
public class Queue extends PossibleError {

    private int qid;
    private String name;
    private String alias;
    private String description;

    private String coords;
//    private List<Integer> users;
    private int users_quantity;

    public int getUsersQuantity() {
        return users_quantity;
    }

//    public List<Integer> getUserlist() {
//        return users;
//    }
//
//    public void setUserlist(List<Integer> userlist) {
//        this.users = userlist;
//    }

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

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
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
}
