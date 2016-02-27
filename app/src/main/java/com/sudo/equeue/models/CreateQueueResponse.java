package com.sudo.equeue.models;

import com.sudo.equeue.models.basic.Queue;
import com.sudo.equeue.models.basic.ResponseBase;


public class CreateQueueResponse extends ResponseBase {

    private Queue body;

    public Queue getBody() {
        return body;
    }

    public void setBody(Queue body) {
        this.body = body;
    }
}
