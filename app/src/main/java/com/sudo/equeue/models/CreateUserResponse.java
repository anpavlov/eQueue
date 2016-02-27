package com.sudo.equeue.models;

import com.sudo.equeue.models.basic.ResponseBase;
import com.sudo.equeue.models.basic.User;

/**
 * Created by orange on 24.02.16.
 */
public class CreateUserResponse extends ResponseBase {

    private User body;

    public User getBody() {
        return body;
    }

    public void setBody(User body) {
        this.body = body;
    }
}
