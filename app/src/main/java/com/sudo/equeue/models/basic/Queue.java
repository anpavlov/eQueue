package com.sudo.equeue.models.basic;

import java.io.Serializable;
import java.util.List;

/**
 * Created by orange on 24.02.16.
 */
public class Queue extends PossibleError implements Serializable {

    private int qid;
    private String name;
    private String alias;
    private String description;
    private List<String> users;

    public List<String> getUserlist() {
        return users;
    }

    public void setUserlist(List<String> userlist) {
        this.users = userlist;
    }

    public int getQueueId() {
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
}
