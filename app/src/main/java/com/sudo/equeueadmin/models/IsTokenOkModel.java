package com.sudo.equeueadmin.models;

import com.sudo.equeueadmin.models.basic.PossibleError;

public class IsTokenOkModel extends PossibleError {

    private int is_valid;

    public boolean isValid() {
        return is_valid == 1;
    }
}
