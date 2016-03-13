package com.sudo.equeue.models;

import com.sudo.equeue.models.basic.PossibleError;

import java.io.Serializable;

/**
 * Created by orange on 24.02.16.
 */
public class User extends PossibleError {

    private int uid;
    private String token;
    private String email;
    private String username;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

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
