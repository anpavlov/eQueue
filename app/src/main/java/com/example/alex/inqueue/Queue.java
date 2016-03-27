package com.example.alex.inqueue;


public class Queue {
    String name;
    String description;
    String location;
    int remaining;
    int totalInQueue;
    int frontOfYou;

    public Queue(String name, String description, String location, int remaining, int totalInQueue, int frontOfYou) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.remaining = remaining;
        this.totalInQueue = totalInQueue;
        this.frontOfYou = frontOfYou;
    }
}
