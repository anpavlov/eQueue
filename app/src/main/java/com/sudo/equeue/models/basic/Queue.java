package com.sudo.equeue.models.basic;

import java.io.Serializable;
import java.util.List;

/**
 * Created by orange on 24.02.16.
 */
public class Queue extends PossibleError implements Serializable {

    private int queueId;
    private String name;
    private String alias;
    private String description;
    private List<String> userlist;

    public List<String> getUserlist() {
        return userlist;
    }

    public void setUserlist(List<String> userlist) {
        this.userlist = userlist;
    }

    public int getQueueId() {
        return queueId;
    }

    public void setQueueId(int queueId) {
        this.queueId = queueId;
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
}
