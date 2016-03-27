package com.sudo.equeue.models;

import com.sudo.equeue.models.basic.PossibleError;

import java.util.List;

/**
 * Created by orange on 27.02.16.
 */
public class QueueList extends PossibleError {

    private List<Queue> queues;

    public List<Queue> getQueues() {
        return queues;
    }

    public void setQueues(List<Queue> queues) {
        this.queues = queues;
    }
}
