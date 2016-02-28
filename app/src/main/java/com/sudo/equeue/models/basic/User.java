package com.sudo.equeue.models.basic;

import java.io.Serializable;

/**
 * Created by orange on 24.02.16.
 */
public class User extends PossibleError implements Serializable {

    private int uid;
    private String token;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
