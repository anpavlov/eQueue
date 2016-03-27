package com.sudo.equeueadmin.models.basic;

import java.io.Serializable;

public class PossibleError implements Serializable {

    private String error;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
