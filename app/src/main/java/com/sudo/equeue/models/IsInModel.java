package com.sudo.equeue.models;

import com.sudo.equeue.models.basic.PossibleError;

public class IsInModel extends PossibleError {

    private int status;

    public boolean getStatus() {
        return status == 1;
    }

    public void setStatus(boolean status) {
        this.status = status ? 1 : 0;
    }
}
