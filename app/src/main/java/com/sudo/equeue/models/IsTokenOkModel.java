package com.sudo.equeue.models;

import com.sudo.equeue.models.basic.PossibleError;

public class IsTokenOkModel extends PossibleError {

    private int is_valid;

    public boolean isValid() {
        return is_valid == 1;
    }
}
